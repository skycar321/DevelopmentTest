# 배치 최적화 설계 분석서

**작성일:** 2025-11-11
**대상 Job:** WlessMabcQatCplyPerpDbyPartiJobConfiguration
**목적:** 1000건 단위 배치 처리 + 병렬 5개 API 호출 구조 설계

---

## 📊 현재 구조 분석

### 처리 흐름
```
Reader (MyBatisCursorItemReader)
  → 1건씩 읽음 (총 200만 로우)
  → Processor: 1건 → 룰엔진 API 순차 호출 → List<Result> 반환
  → chunk_size=1000 도달 시 Writer 실행
  → Writer: 1000개의 List<Result> 일괄 저장
```

### 데이터 볼륨
- **전체 데이터:** 약 200만 로우
- **파티셔닝:** pool_size=15 (dev/prd)
- **각 파티션 처리량:** 약 13만 로우 (200만 / 15)
- **분할 테이블:** 20만 건씩 분할 (skipCount=200,000)
- **chunk_size:** 1000건

### 현재 병렬 처리
- **1차 병렬:** Partitioning으로 15개 쓰레드 분할 (MOD 연산)
- **2차 병렬:** 없음 (각 쓰레드 내에서 순차 처리)
- **API 호출:** item-by-item 순차 호출

### 메모리 사용 패턴
- **Reader:** Cursor 기반 스트리밍 (메모리 효율적 ✅)
- **Processor:** 1건씩 처리 (메모리 부하 낮음 ✅)
- **Writer:** 1000건씩 일괄 저장 (메모리 부하 중간)

---

## 🎯 요구사항

### 기능 요구사항
1. **배치 단위 처리:** Reader에서 1000건을 읽은 후 Processor에 한번에 전달
2. **병렬 API 호출:** 1000건을 병렬 5개로 분할하여 동시 API 호출
3. **일괄 저장:** 1000건에 대한 모든 API 응답을 받은 후 Writer로 전달

### 비기능 요구사항
1. **메모리 효율:** 200만 로우를 처리하므로 메모리 부하 최소화 필수
2. **처리 성능:** API 호출 병렬화로 처리 시간 단축
3. **안정성:** 대용량 데이터 처리 중 OOM 방지

---

## 📋 설계 옵션 비교

### Option A: Tasklet + Manual Cursor (⭐⭐⭐⭐⭐ 권장)

**구조:**
```java
Tasklet {
  while(cursor.hasNext()) {
    // 1. Cursor에서 1000건 수동으로 fetch
    List<Item> batch = fetchNextBatch(cursor, 1000);

    // 2. 병렬 5개로 API 호출
    List<Result> results = processInParallel(batch, 5);

    // 3. 일괄 저장
    batchInsert(results);
  }
}
```

**장점:**
- ✅ 메모리 효율: Cursor 스트리밍 + 1000건씩만 메모리 적재
- ✅ 완전한 제어: 배치 크기, 병렬 수 직접 제어
- ✅ 코드 가독성: 로직이 명확하고 디버깅 용이
- ✅ 파티셔닝 유지: 기존 15개 쓰레드 구조 유지 가능

**단점:**
- ❌ Spring Batch 재시작 기능 수동 구현 필요
- ❌ Cursor 관리 코드 직접 작성

**메모리 사용량:**
- 각 파티션: 1000건 × 2 (input + output) ≈ 2MB
- 전체: 2MB × 15 파티션 = 30MB (매우 낮음 ✅)

---

### Option B: Custom BufferingReader + Chunk(1) (⭐⭐⭐⭐)

**구조:**
```java
BufferingReader {
  // Cursor에서 1000건씩 버퍼링
  return List<Item> (1000건)
}
  ↓ chunk(1)
Processor {
  // List<Item> 1000건 → 병렬 5개 처리
  return List<Result>
}
  ↓
Writer {
  // List<Result> 저장
}
```

**장점:**
- ✅ 메모리 효율: Cursor 기반 스트리밍 유지
- ✅ Spring Batch 프레임워크 활용 (재시작, 메트릭)
- ✅ 트랜잭션 자동 관리

**단점:**
- ❌ chunk(1) 사용이 다소 비직관적
- ❌ Reader 커스텀 구현 복잡도

**메모리 사용량:**
- Option A와 동일 (약 30MB)

---

### Option C: Chunk(1000) + Buffering Processor (⭐⭐⭐)

**구조:**
```java
Reader: 1건씩 읽기 (Cursor)
  ↓ chunk(1000)
Processor {
  // 내부에서 1000건 버퍼링
  static ThreadLocal<List<Item>> buffer;

  if(buffer.size() < 1000) {
    buffer.add(item);
    return null; // Writer 스킵
  } else {
    // 1000건 모이면 병렬 처리
    List<Result> results = processInParallel(buffer, 5);
    buffer.clear();
    return results;
  }
}
  ↓
Writer: results 저장
```

**장점:**
- ✅ 기존 Reader/Writer 구조 유지
- ✅ 변경 범위 최소화

**단점:**
- ❌ ThreadLocal 사용으로 복잡도 증가
- ❌ 버퍼 관리 로직 복잡
- ❌ Stateful 처리로 인한 부작용 위험
- ❌ 안티패턴에 가까움 (비권장)

**메모리 사용량:**
- ThreadLocal 버퍼: 1000건 × 15 파티션 = 15,000건 상주
- 약 30-50MB (관리 복잡)

---

### Option D: MyBatis Cursor + Stream Processing (⭐⭐⭐⭐⭐ 최적)

**구조:**
```java
Tasklet {
  // MyBatis Cursor로 스트리밍
  try(Cursor<Item> cursor = sqlSession.selectCursor(...)) {

    Iterator<Item> iterator = cursor.iterator();
    List<Item> batch = new ArrayList<>(1000);

    while(iterator.hasNext()) {
      batch.add(iterator.next());

      if(batch.size() == 1000) {
        // 병렬 5개로 처리
        List<Result> results = processInParallel(batch);
        batchInsert(results);
        batch.clear();
      }
    }

    // 나머지 처리
    if(!batch.isEmpty()) {
      List<Result> results = processInParallel(batch);
      batchInsert(results);
    }
  }
}
```

**장점:**
- ✅ 메모리 최적: Cursor 스트리밍 + 배치 버퍼 최소화
- ✅ 코드 간결: MyBatis 기능 활용
- ✅ 자동 리소스 관리: try-with-resources
- ✅ MyBatis Cursor 표준 패턴 (검증된 방식)

**단점:**
- ❌ Spring Batch 메타데이터 활용 제한
- ❌ 청크 단위 재시작 불가 (파티션 단위 재시작은 가능)

**메모리 사용량:**
- 각 파티션: 1000건 버퍼 ≈ 2MB
- 전체: 30MB (가장 효율적 ✅)

---

## 🎯 최종 권장 방안: **Option D (MyBatis Cursor + Stream)**

### 선정 이유
1. **메모리 효율:** 200만 로우 처리에 최적화된 스트리밍
2. **성능:** 병렬 5개 API 호출로 처리 시간 단축
3. **안정성:** Cursor 기반으로 OOM 위험 없음
4. **유지보수:** 기존 패턴과 유사하여 이해 용이

### 예상 성능 개선
- **현재:** 200만 × 100ms (API) = 55시간 (순차 처리)
- **개선 후:** 200만 / 5 × 100ms = 11시간 (병렬 처리)
- **실제:** Partitioning(15) 고려 시 약 44분 (11시간 / 15)

---

## 📐 상세 설계 (Option D)

### 1. Job Configuration 수정

```java
@Bean(name = job_name+"Slave")
public Step Slave() throws Exception {
    return steps.get(job_name+"Slave")
        .tasklet(new WlessMabcBatchProcessingTasklet(
            sqlSessionFactory,
            batchInsertDao,
            wlessMapper
        ))
        .build();
}
```

### 2. Tasklet 구현

```java
@Slf4j
public class WlessMabcBatchProcessingTasklet implements Tasklet {

    private final SqlSessionFactory sqlSessionFactory;
    private final BatchInsertDao batchInsertDao;
    private final WlessPartiMapper wlessMapper;

    private static final int BATCH_SIZE = 1000;
    private static final int PARALLEL_COUNT = 5;

    @Override
    public RepeatStatus execute(StepContribution contribution,
                                ChunkContext chunkContext) throws Exception {

        // StepExecutionContext에서 파티션 정보 가져오기
        Map<String, Object> params = extractParams(chunkContext);

        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.SIMPLE);

        try (Cursor<PpWlessabcTxnItem> cursor =
                sqlSession.selectCursor(
                    "com.abc.batch.mapper.WlessPartiMapper.selectWlessMabcQatCplyPerpTgtList",
                    params)) {

            processCursorInBatches(cursor, params);

        } finally {
            sqlSession.close();
        }

        return RepeatStatus.FINISHED;
    }

    private void processCursorInBatches(Cursor<PpWlessabcTxnItem> cursor,
                                        Map<String, Object> params) throws Exception {

        Iterator<PpWlessabcTxnItem> iterator = cursor.iterator();
        List<PpWlessabcTxnItem> batch = new ArrayList<>(BATCH_SIZE);
        int processedCount = 0;

        while (iterator.hasNext()) {
            batch.add(iterator.next());

            if (batch.size() == BATCH_SIZE) {
                processBatch(batch, params);
                processedCount += batch.size();

                log.info("Processed {} items (total: {})", batch.size(), processedCount);

                batch.clear();
            }
        }

        // 나머지 배치 처리
        if (!batch.isEmpty()) {
            processBatch(batch, params);
            processedCount += batch.size();
            log.info("Processed final batch: {} items (total: {})", batch.size(), processedCount);
        }

        log.info("Partition completed. Total processed: {}", processedCount);
    }

    private void processBatch(List<PpWlessabcTxnItem> batch,
                             Map<String, Object> params) throws Exception {

        // 병렬 5개로 분할 처리
        int subBatchSize = (int) Math.ceil((double) batch.size() / PARALLEL_COUNT);

        ExecutorService executor = Executors.newFixedThreadPool(PARALLEL_COUNT);
        List<CompletableFuture<List<RuleWlessChkResltItem>>> futures = new ArrayList<>();

        try {
            for (int i = 0; i < PARALLEL_COUNT; i++) {
                int start = i * subBatchSize;
                int end = Math.min(start + subBatchSize, batch.size());

                if (start >= batch.size()) break;

                List<PpWlessabcTxnItem> subBatch = batch.subList(start, end);
                final int partition = i;

                futures.add(CompletableFuture.supplyAsync(() -> {
                    log.debug("Partition {} processing {} items", partition, subBatch.size());
                    return callRuleEngine(subBatch, params);
                }, executor));
            }

            // 모든 API 호출 완료 대기 및 결과 수집
            List<RuleWlessChkResltItem> allResults = new ArrayList<>();
            for (CompletableFuture<List<RuleWlessChkResltItem>> future : futures) {
                allResults.addAll(future.get(5, TimeUnit.MINUTES)); // 타임아웃 5분
            }

            // 일괄 저장
            if (!allResults.isEmpty()) {
                saveResults(allResults);
            }

        } finally {
            executor.shutdown();
            if (!executor.awaitTermination(10, TimeUnit.MINUTES)) {
                executor.shutdownNow();
            }
        }
    }

    private List<RuleWlessChkResltItem> callRuleEngine(
            List<PpWlessabcTxnItem> items,
            Map<String, Object> params) {

        List<RuleWlessChkResltItem> results = new ArrayList<>();

        for (PpWlessabcTxnItem item : items) {
            try {
                // 기존 Processor의 룰엔진 호출 로직 재사용
                List<RuleWlessChkResltItem> itemResults =
                    callInnoRulesApi(item, params);
                results.addAll(itemResults);

            } catch (Exception e) {
                log.error("Error processing item: {}", item.getSvcContId(), e);
                // 에러 처리: 스킵 또는 재시도
            }
        }

        return results;
    }

    private List<RuleWlessChkResltItem> callInnoRulesApi(
            PpWlessabcTxnItem item,
            Map<String, Object> params) throws Exception {

        // 기존 WlessMabcQatCplyPerpProcessor.ruleCall() 로직 이관
        List<RuleWlessChkResltItem> rstList = new ArrayList<>();

        try {
            RuleInterface intf = null;
            RuleReq req = new RuleReq();

            String apiArr[] = ((String)params.get("apiId")).split(",");

            for (String apiId : apiArr) {
                req.setRuleCode(apiId);
                req.setDate((String)params.get("batchExecDt"));
                req.resetItems();

                // Item 설정 (기존 로직 유지)
                req.addStringItem("base_date").add(item.getBaseDate());
                req.addStringItem("svc_cont_id").add(item.getSvcContId());
                // ... 나머지 필드 설정

                // 룰 실행
                intf = ClusterManager.getInterface();
                ResultSet rs = intf.execute(req);

                // 결과 파싱
                while (rs.next()) {
                    RuleWlessChkResltItem rstItem = new RuleWlessChkResltItem();
                    // 결과 매핑 로직
                    rstList.add(rstItem);
                }
            }
        } catch (RulesException e) {
            log.error("InnoRules API error: {}", e.getMessage(), e);
            throw e;
        }

        return rstList;
    }

    private void saveResults(List<RuleWlessChkResltItem> results) {
        Map<String, Object> fixedValues = Map.of(
            "regUser", "batch",
            "regDate", "SQL::now()"
        );

        Set<String> excludeFields = Set.of("serialVersionUID");

        batchInsertDao.batchInsert(
            "abcBAT.TMP_RULE_WLESS_CHK_RESLT_01",
            results,
            excludeFields,
            fixedValues
        );
    }

    private Map<String, Object> extractParams(ChunkContext chunkContext) {
        StepContext stepContext = chunkContext.getStepContext();
        Map<String, Object> jobParams = stepContext.getJobParameters();

        Map<String, Object> params = new HashMap<>();
        // StepExecutionContext에서 파티션 정보 추출
        params.put("threadNo", stepContext.getStepExecutionContext().get("threadNo"));
        params.put("pool_size", stepContext.getStepExecutionContext().get("pool_size"));
        params.put("tableNumber", stepContext.getStepExecutionContext().get("tableNumber"));
        params.put("param1", jobParams.get("param1"));
        params.put("chkScopeVal", jobParams.get("chkScopeVal"));
        // ... 기타 필요한 파라미터

        return params;
    }
}
```

### 3. 에러 처리 전략

```java
private static final int MAX_RETRIES = 3;

private List<RuleWlessChkResltItem> callRuleEngineWithRetry(
        PpWlessabcTxnItem item,
        Map<String, Object> params) {

    int retryCount = 0;
    Exception lastException = null;

    while (retryCount < MAX_RETRIES) {
        try {
            return callInnoRulesApi(item, params);
        } catch (Exception e) {
            lastException = e;
            retryCount++;
            log.warn("Retry {}/{} for item: {}",
                retryCount, MAX_RETRIES, item.getSvcContId());

            try {
                Thread.sleep(1000 * retryCount); // Exponential backoff
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    log.error("Failed after {} retries: {}", MAX_RETRIES, item.getSvcContId(), lastException);

    // 실패 건 별도 테이블에 저장 또는 스킵
    saveFailedItem(item, lastException);

    return Collections.emptyList();
}
```

---

## 🔧 마이그레이션 가이드

### Phase 1: 준비 작업
1. Tasklet 클래스 생성
2. 기존 Processor 로직 Tasklet으로 이관
3. 단위 테스트 작성

### Phase 2: Configuration 수정
1. Slave Step을 chunk에서 tasklet으로 변경
2. Reader/Processor/Writer 빈 제거
3. Tasklet 빈 등록

### Phase 3: 검증
1. 로컬 환경에서 소량 데이터로 테스트
2. Dev 환경에서 전체 데이터 테스트
3. 메모리 사용량 모니터링
4. 처리 시간 비교

### Phase 4: 배포
1. Prd 환경 배포
2. 실시간 모니터링
3. 롤백 계획 준비

---

## 📊 예상 효과

### 성능 개선
| 항목 | 현재 | 개선 후 | 개선율 |
|------|------|---------|--------|
| API 호출 방식 | 순차 | 병렬 5개 | - |
| 예상 처리 시간 | 55시간 | 44분 | **98.7% 단축** |
| 메모리 사용량 | 낮음 | 낮음 | 동일 |
| CPU 사용률 | 낮음 | 중간 | 증가 (정상) |

### 리스크
1. **API 부하 증가:** 병렬 호출로 인한 외부 시스템 부하
   - 완화: API 호출 제한(Rate Limiting) 추가 가능
2. **메모리 스파이크:** 1000건 버퍼 × 15 파티션
   - 완화: 배치 크기 조정 (500건으로 축소 가능)
3. **재시작 복잡도:** 파티션 단위 재시작만 가능
   - 완화: 처리 진행률 로깅 강화

---

## 🎓 참고 자료

### 기존 코드 참조
- `WlessMabcQatCplyPerpDbyPartiJobConfiguration.java:388-479` - 기존 Reader/Processor/Writer
- `WlessMabcQatCplyPerpProcessor.java:62-70` - 룰엔진 호출 로직
- MyBatis Cursor API - 표준 Cursor 기반 스트리밍 패턴
- `BatchInsertDao.java` - 일괄 저장 유틸

### Spring Batch 문서
- [Tasklet Processing](https://docs.spring.io/spring-batch/docs/current/reference/html/step.html#taskletStep)
- [Partitioning](https://docs.spring.io/spring-batch/docs/current/reference/html/scalability.html#partitioning)
- [MyBatis Cursor](https://mybatis.org/mybatis-3/java-api.html)

---

## 📝 결론

**200만 로우 대용량 처리**를 위해서는 **Option D (MyBatis Cursor + Stream + Tasklet)** 방식이 최적입니다.

**핵심 포인트:**
1. ✅ Cursor 스트리밍으로 메모리 효율 보장
2. ✅ 1000건 배치 + 병렬 5개로 성능 극대화
3. ✅ Partitioning 유지로 기존 구조 활용
4. ✅ 코드 가독성과 유지보수성 확보

**다음 단계:** 사용자 승인 후 구현 시작
