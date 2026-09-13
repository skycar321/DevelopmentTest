# 병렬 처리 구조 상세 분석

**작성일:** 2025-11-11
**목적:** Partitioner + 내부 병렬 처리의 정확한 구조 파악

---

## 🎯 핵심 요약

### 병렬 처리 계층 구조

```
총 병렬 수: 15 (Partitioner) × 5 (내부 병렬) = 75개 동시 API 호출
```

---

## 📊 1차 병렬: Partitioner 레벨

### 구조
```java
@Bean
public Step StepManager() {
    return steps.get("StepManager")
        .partitioner(Slave().getName(), Partitioner())  // 15개 파티션 생성
        .partitionHandler(PartitionHandler())           // 15개 쓰레드로 실행
        .build();
}

@Bean
public TaskExecutor executor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(pool_size);      // 15
    executor.setMaxPoolSize(pool_size);       // 15
    executor.setThreadNamePrefix("wlessMain-thread-");
    return executor;
}
```

### 파티션 분할 방식
```java
// WlessMabcQatCplyPerpRangePartitioner.java
for(int i=1; i<=pool_size; i++) {  // i: 1~15
    ExecutionContext value = new ExecutionContext();
    value.putInt("threadNo", i);            // 1, 2, 3, ..., 15
    value.putString("partitionGbn", partitionGbn);
    value.put("paramSetMap", paramSetMap);
    result.put("partition" + i, value);
}
```

### 데이터 분할 쿼리
```sql
-- MyBatis Mapper
SELECT *
FROM TMP_PP_WLESS_abc_TXN_JOIN_CALSUM_#{tableNumber}
WHERE MOD(CAST(row_num AS INTEGER), #{pool_size}) = #{threadNo}
-- pool_size=15, threadNo=1~15
--
-- threadNo=1: MOD(row_num, 15) = 1 인 데이터
-- threadNo=2: MOD(row_num, 15) = 2 인 데이터
-- ...
-- threadNo=15: MOD(row_num, 15) = 15 인 데이터 ❌ (잘못됨!)
```

**⚠️ 주의:** threadNo는 1부터 시작하지만 MOD 연산은 0~14 결과를 반환합니다!
```
MOD(1, 15) = 1   ✅
MOD(2, 15) = 2   ✅
...
MOD(14, 15) = 14 ✅
MOD(15, 15) = 0  ❌ threadNo=15는 MOD 결과가 0인 데이터를 처리해야 함!
```

### 파티션 실행 흐름
```
[마스터 쓰레드]
├─ Partitioner.partition(15) 호출
│  └─ 15개 ExecutionContext 생성
│     ├─ partition1 (threadNo=1)
│     ├─ partition2 (threadNo=2)
│     ├─ ...
│     └─ partition15 (threadNo=15)
│
├─ TaskExecutorPartitionHandler
│  └─ ThreadPoolTaskExecutor(15)에 작업 분배
│
└─ 15개 워커 쓰레드 동시 실행
   ├─ wlessMain-thread-1 → Slave Tasklet (threadNo=1)
   ├─ wlessMain-thread-2 → Slave Tasklet (threadNo=2)
   ├─ wlessMain-thread-3 → Slave Tasklet (threadNo=3)
   ├─ ...
   └─ wlessMain-thread-15 → Slave Tasklet (threadNo=15)
```

### 1차 병렬 처리량
- **동시 실행 파티션:** 15개
- **각 파티션 처리 데이터:** 약 133,333건 (2,000,000 / 15)
- **각 파티션 실행 쓰레드:** 독립적 (wlessMain-thread-1 ~ 15)

---

## 🚀 2차 병렬: 각 파티션 내부 병렬 처리

### A 방법: Cursor + Tasklet 구조

```java
// WlessMabcCursorTasklet.java
public RepeatStatus execute(...) {

    // 각 파티션이 독립적으로 실행 (15개 파티션 중 하나)

    while (iterator.hasNext()) {
        // 1. 1000건 배치 버퍼링
        batch.add(iterator.next());

        if (batch.size() == 1000) {
            // 2. 1000건을 5개로 분할하여 병렬 처리
            processBatch(batch);  // 여기서 2차 병렬 발생
            batch.clear();
        }
    }
}

private void processBatch(List<Item> batch) {
    // 각 파티션 내부에서 추가로 5개 병렬 처리

    ExecutorService executor = Executors.newFixedThreadPool(5);  // ⚡ 중요!

    int subBatchSize = 1000 / 5 = 200건;

    for (int i = 0; i < 5; i++) {
        // 200건씩 5개로 분할
        List<Item> subBatch = batch.subList(i*200, (i+1)*200);

        futures.add(CompletableFuture.supplyAsync(() -> {
            // API 호출 (각 파티션 내에서 5개 병렬)
            return callRuleEngine(subBatch);
        }, executor));
    }

    // 5개 API 호출 완료 대기
    for (Future future : futures) {
        results.addAll(future.get());
    }

    executor.shutdown();
}
```

### 2차 병렬 실행 흐름

```
[파티션 1 - wlessMain-thread-1]
├─ Cursor 스트리밍으로 데이터 읽기
├─ 1000건 버퍼링
└─ processBatch(1000건) 호출
   ├─ ExecutorService(5) 생성
   └─ 5개 서브 쓰레드로 분할 실행
      ├─ pool-1-thread-1 → callRuleEngine(200건) 🔥 API 호출
      ├─ pool-1-thread-2 → callRuleEngine(200건) 🔥 API 호출
      ├─ pool-1-thread-3 → callRuleEngine(200건) 🔥 API 호출
      ├─ pool-1-thread-4 → callRuleEngine(200건) 🔥 API 호출
      └─ pool-1-thread-5 → callRuleEngine(200건) 🔥 API 호출

[파티션 2 - wlessMain-thread-2]
├─ Cursor 스트리밍으로 데이터 읽기
├─ 1000건 버퍼링
└─ processBatch(1000건) 호출
   ├─ ExecutorService(5) 생성
   └─ 5개 서브 쓰레드로 분할 실행
      ├─ pool-2-thread-1 → callRuleEngine(200건) 🔥 API 호출
      ├─ pool-2-thread-2 → callRuleEngine(200건) 🔥 API 호출
      ├─ pool-2-thread-3 → callRuleEngine(200건) 🔥 API 호출
      ├─ pool-2-thread-4 → callRuleEngine(200건) 🔥 API 호출
      └─ pool-2-thread-5 → callRuleEngine(200건) 🔥 API 호출

... (파티션 3 ~ 15 동일)

[파티션 15 - wlessMain-thread-15]
└─ processBatch(1000건) 호출
   ├─ ExecutorService(5) 생성
   └─ 5개 서브 쓰레드로 분할 실행
      ├─ pool-15-thread-1 → callRuleEngine(200건) 🔥 API 호출
      ├─ pool-15-thread-2 → callRuleEngine(200건) 🔥 API 호출
      ├─ pool-15-thread-3 → callRuleEngine(200건) 🔥 API 호출
      ├─ pool-15-thread-4 → callRuleEngine(200건) 🔥 API 호출
      └─ pool-15-thread-5 → callRuleEngine(200건) 🔥 API 호출
```

### B 방법: PagingReader + Writer 구조

```java
// Configuration
@Bean
public Step Slave() {
    return steps.get("Slave")
        .<Item, Item>chunk(1000)
        .reader(pagingReader())       // 1000건씩 페이징
        .processor(passthrough())     // pass-through
        .writer(batchWriter())        // 여기서 2차 병렬 처리
        .build();
}

// Writer
@Bean
public ItemWriter<Item> batchWriter() {
    return items -> {
        // items: 1000건 (chunk_size)

        // 각 파티션 내부에서 5개 병렬 처리
        ExecutorService executor = Executors.newFixedThreadPool(5);

        for (int i = 0; i < 5; i++) {
            List<Item> subBatch = items.subList(i*200, (i+1)*200);

            futures.add(CompletableFuture.supplyAsync(() -> {
                return callRuleEngine(subBatch);  // API 호출
            }, executor));
        }

        // 5개 완료 대기
        for (Future future : futures) {
            results.addAll(future.get());
        }

        executor.shutdown();
    };
}
```

---

## 🔥 총 병렬 처리 계산

### 동시 API 호출 수

```
1차 병렬 (Partitioner): 15개 파티션
2차 병렬 (내부 ExecutorService): 각 파티션당 5개

총 동시 API 호출 = 15 × 5 = 75개
```

### 시간별 API 호출 패턴

```
시간 T0: 배치 시작
├─ 15개 파티션 동시 시작
│
시간 T1: 각 파티션이 첫 1000건 배치 처리
├─ 파티션 1: 5개 API 호출 (200건 × 5)
├─ 파티션 2: 5개 API 호출 (200건 × 5)
├─ ...
└─ 파티션 15: 5개 API 호출 (200건 × 5)
   └─ 총 75개 API 동시 호출 🔥🔥🔥

시간 T2: 첫 배치 완료 후 다음 1000건 처리
└─ 다시 75개 API 동시 호출

시간 T3, T4, ... : 반복
```

### 실제 API 호출 예시 (1개 건당 100ms 가정)

```
각 파티션이 200건씩 처리:
- 순차 처리: 200건 × 100ms = 20초
- 병렬 처리 안 하면: 1000건 × 100ms = 100초

1000건 배치의 실제 처리 시간:
- 5개 병렬: max(200건 × 100ms) = 20초 ✅

전체 2,000,000건 처리 시간:
- 각 파티션: 133,333건 / 1000 = 133배치
- 각 배치: 20초
- 총 시간: 133 × 20초 = 2,660초 = 44분

⚠️ 단, 15개 파티션이 동시에 실행되므로 API 서버는 동시에 75개 요청 처리 필요!
```

---

## 🎯 쓰레드 구조 상세

### 쓰레드 계층

```
[JVM Main Thread]
│
├─ [Spring Batch Main Thread]
│  ├─ Job 실행
│  ├─ PreStep
│  └─ StepManager (Partitioning)
│     │
│     ├─ [TaskExecutorPartitionHandler]
│     │  └─ ThreadPoolTaskExecutor(15)
│     │     │
│     │     ├─ [wlessMain-thread-1] 파티션 1
│     │     │  └─ Slave Tasklet 실행
│     │     │     └─ ExecutorService(5)
│     │     │        ├─ pool-1-thread-1 🔥 API 호출
│     │     │        ├─ pool-1-thread-2 🔥 API 호출
│     │     │        ├─ pool-1-thread-3 🔥 API 호출
│     │     │        ├─ pool-1-thread-4 🔥 API 호출
│     │     │        └─ pool-1-thread-5 🔥 API 호출
│     │     │
│     │     ├─ [wlessMain-thread-2] 파티션 2
│     │     │  └─ Slave Tasklet 실행
│     │     │     └─ ExecutorService(5)
│     │     │        ├─ pool-2-thread-1 🔥 API 호출
│     │     │        ├─ pool-2-thread-2 🔥 API 호출
│     │     │        ├─ pool-2-thread-3 🔥 API 호출
│     │     │        ├─ pool-2-thread-4 🔥 API 호출
│     │     │        └─ pool-2-thread-5 🔥 API 호출
│     │     │
│     │     ├─ ... (파티션 3~14)
│     │     │
│     │     └─ [wlessMain-thread-15] 파티션 15
│     │        └─ Slave Tasklet 실행
│     │           └─ ExecutorService(5)
│     │              ├─ pool-15-thread-1 🔥 API 호출
│     │              ├─ pool-15-thread-2 🔥 API 호출
│     │              ├─ pool-15-thread-3 🔥 API 호출
│     │              ├─ pool-15-thread-4 🔥 API 호출
│     │              └─ pool-15-thread-5 🔥 API 호출
│     │
│     └─ 모든 파티션 완료 대기
│
└─ AfterStep
```

### 쓰레드 수 계산

```
1. Partitioner 쓰레드: 15개
   - wlessMain-thread-1 ~ 15

2. 각 파티션 내부 쓰레드: 5개 × 15 = 75개
   - pool-1-thread-1~5
   - pool-2-thread-1~5
   - ...
   - pool-15-thread-1~5

3. 총 워커 쓰레드: 15 + 75 = 90개
   (단, 1+2는 계층이 다름)

4. 실제 동시 활성 쓰레드:
   - 파티션 레벨: 15개
   - API 호출 레벨: 75개 (15 × 5)
```

---

## ⚠️ API 서버 부하 고려사항

### 동시 API 호출: 75개

```
API 서버 요구사항:
- 동시 처리 용량: 최소 75개 요청
- 응답 시간: 100ms/요청 (가정)
- 초당 요청 수 (RPS): 75 / 0.1 = 750 RPS (이론상 최대)
```

### 실제 부하 패턴

```
시나리오: 각 API 호출이 100ms 소요

T0 (0초):
- 15개 파티션이 각각 첫 1000건 배치 시작
- 75개 API 동시 호출 시작

T1 (20초):
- 첫 배치 완료 (200건 × 5개 병렬 = 20초)
- 저장 완료 후 다음 1000건 배치 시작
- 다시 75개 API 동시 호출

T2 (40초):
- 두 번째 배치 완료
- 다시 75개 API 동시 호출

... 반복

부하 패턴:
- 20초마다 75개 요청이 동시에 발생 (burst)
- 각 요청은 순차적으로 처리 (각 파티션이 200건 처리)
- 평균 RPS: (200건 × 15파티션) / 20초 = 150 RPS
- 피크 동시 연결: 75개
```

### 병렬 수 조정 전략

#### 현재 설정: 15 × 5 = 75개

```java
// Partitioner
executor.setCorePoolSize(15);  // 1차 병렬

// 내부 병렬
ExecutorService executor = Executors.newFixedThreadPool(5);  // 2차 병렬
```

#### 보수적 설정: 15 × 3 = 45개

```java
// API 서버 용량이 부족하다면
ExecutorService executor = Executors.newFixedThreadPool(3);  // 2차 병렬 축소
```

#### 공격적 설정: 15 × 10 = 150개

```java
// API 서버 용량이 충분하다면
ExecutorService executor = Executors.newFixedThreadPool(10);  // 2차 병렬 증가
```

#### 동적 조정 설정

```java
// 시스템 상태에 따라 동적 조정
private int calculateOptimalParallelCount() {
    // CPU 사용률 체크
    // 메모리 사용률 체크
    // API 응답 시간 체크

    if (apiResponseTime > 500) {
        return 3;  // API 부하 시 병렬 감소
    } else if (apiResponseTime < 100) {
        return 10; // API 여유 시 병렬 증가
    } else {
        return 5;  // 기본값
    }
}
```

---

## 📊 성능 예측 (정확한 계산)

### 데이터 볼륨
```
전체 데이터: 2,000,000건
파티션 수: 15개
각 파티션: 2,000,000 / 15 = 133,333건
배치 크기: 1000건
각 파티션 배치 수: 133,333 / 1000 = 133배치 (나머지 333건)
```

### API 호출 시간 (1건당 100ms 가정)

#### 현재 구조 (item-by-item, 순차)
```
각 파티션:
- 133,333건 × 100ms = 13,333초 = 3.7시간

전체 (15개 파티션 동시):
- 3.7시간 (가장 느린 파티션 기준)
```

#### A/B 방법 (1000건 배치 + 5개 병렬)
```
각 파티션:
- 배치당 처리 시간: max(200건 × 100ms) = 20초
- 총 배치 수: 133배치
- 총 시간: 133 × 20초 = 2,660초 = 44분

전체 (15개 파티션 동시):
- 44분 (가장 느린 파티션 기준)

개선율: 3.7시간 → 44분 (80% 단축) ✅
```

### API 서버 부하

```
현재 구조:
- 동시 API 호출: 최대 15개 (각 파티션 1건씩)
- 부하: 낮음

A/B 방법:
- 동시 API 호출: 최대 75개 (15 파티션 × 5 병렬)
- 부하: 5배 증가 ⚠️

권장사항:
1. API 서버 용량 확인 필요
2. Rate Limiting 고려
3. 점진적 증가 (3개 → 5개 → 10개)
```

---

## 🔧 구현 코드 검증

### A 방법: Cursor + Tasklet

```java
// WlessMabcCursorTasklet.java
private void processBatch(List<PpWlessabcTxnItem> batch,
                         Map<String, Object> params) throws Exception {

    int PARALLEL_COUNT = 5;  // ⚡ 2차 병렬 수
    int subBatchSize = (int) Math.ceil((double) batch.size() / PARALLEL_COUNT);

    // 각 파티션 내부에서 5개 쓰레드 생성
    ExecutorService executor = Executors.newFixedThreadPool(PARALLEL_COUNT);
    List<CompletableFuture<List<RuleWlessChkResltItem>>> futures = new ArrayList<>();

    try {
        for (int i = 0; i < PARALLEL_COUNT; i++) {
            int start = i * subBatchSize;
            int end = Math.min(start + subBatchSize, batch.size());

            if (start >= batch.size()) break;

            List<PpWlessabcTxnItem> subBatch = batch.subList(start, end);
            final int partition = i;

            // 5개 병렬 API 호출
            futures.add(CompletableFuture.supplyAsync(() -> {
                log.debug("[Partition {} - Sub {}] Processing {} items",
                    params.get("threadNo"), partition, subBatch.size());
                return callRuleEngine(subBatch, params);  // 🔥 API 호출
            }, executor));
        }

        // 5개 모두 완료 대기
        List<RuleWlessChkResltItem> allResults = new ArrayList<>();
        for (CompletableFuture<List<RuleWlessChkResltItem>> future : futures) {
            allResults.addAll(future.get(5, TimeUnit.MINUTES));
        }

        // 일괄 저장
        if (!allResults.isEmpty()) {
            saveResults(allResults);
        }

    } finally {
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.MINUTES);
    }
}
```

### 병렬 처리 확인 로그

```
[wlessMain-thread-1] Partition 1 - Processing batch 1 of 1000 items
  └─ [pool-1-thread-1] Partition 1 - Sub 0 - Processing 200 items 🔥
  └─ [pool-1-thread-2] Partition 1 - Sub 1 - Processing 200 items 🔥
  └─ [pool-1-thread-3] Partition 1 - Sub 2 - Processing 200 items 🔥
  └─ [pool-1-thread-4] Partition 1 - Sub 3 - Processing 200 items 🔥
  └─ [pool-1-thread-5] Partition 1 - Sub 4 - Processing 200 items 🔥

[wlessMain-thread-2] Partition 2 - Processing batch 1 of 1000 items
  └─ [pool-2-thread-1] Partition 2 - Sub 0 - Processing 200 items 🔥
  └─ [pool-2-thread-2] Partition 2 - Sub 1 - Processing 200 items 🔥
  └─ [pool-2-thread-3] Partition 2 - Sub 2 - Processing 200 items 🔥
  └─ [pool-2-thread-4] Partition 2 - Sub 3 - Processing 200 items 🔥
  └─ [pool-2-thread-5] Partition 2 - Sub 4 - Processing 200 items 🔥

... (파티션 3~15 동일)

총 동시 API 호출: 75개 (15 파티션 × 5 병렬)
```

---

## 📈 모니터링 지표

### 추적해야 할 메트릭

```
1. 파티션 레벨:
   - 각 파티션 처리 건수
   - 각 파티션 처리 시간
   - 파티션 간 처리 시간 편차

2. 배치 레벨:
   - 배치당 처리 시간 (1000건)
   - 배치당 API 호출 시간
   - 배치 처리 실패 건수

3. API 레벨:
   - 동시 API 호출 수
   - API 응답 시간 (평균/최대/최소)
   - API 실패율
   - API 타임아웃 건수

4. 시스템 레벨:
   - CPU 사용률
   - 메모리 사용률
   - 쓰레드 수
   - DB 커넥션 수
```

### 모니터링 코드 예시

```java
private void processBatch(List<Item> batch, Map<String, Object> params) {
    long startTime = System.currentTimeMillis();

    log.info("[Partition {}] Batch processing started: {} items",
        params.get("threadNo"), batch.size());

    // 병렬 처리
    List<Result> results = processInParallel(batch, params);

    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;

    log.info("[Partition {}] Batch completed in {}ms: {} items → {} results",
        params.get("threadNo"), duration, batch.size(), results.size());

    // 메트릭 기록
    recordMetric("batch.processing.time", duration);
    recordMetric("batch.size", batch.size());
    recordMetric("batch.result.size", results.size());
}

private List<Result> callRuleEngine(List<Item> items, Map<String, Object> params) {
    long apiStartTime = System.currentTimeMillis();

    try {
        // API 호출
        List<Result> results = actualApiCall(items);

        long apiEndTime = System.currentTimeMillis();
        long apiDuration = apiEndTime - apiStartTime;

        log.debug("[Partition {} - Sub thread] API call completed in {}ms: {} items",
            params.get("threadNo"), apiDuration, items.size());

        // API 메트릭 기록
        recordMetric("api.call.time", apiDuration);
        recordMetric("api.call.success", 1);

        return results;

    } catch (Exception e) {
        log.error("[Partition {}] API call failed", params.get("threadNo"), e);
        recordMetric("api.call.failure", 1);
        throw e;
    }
}
```

---

## 🎯 결론

### 병렬 처리 구조 확정

```
✅ 1차 병렬 (Partitioner): 15개
✅ 2차 병렬 (내부 ExecutorService): 각 5개
✅ 총 동시 API 호출: 15 × 5 = 75개
```

### 주의사항

1. **API 서버 용량 확인 필수**
   - 75개 동시 요청 처리 가능 여부 확인
   - 필요시 Rate Limiting 적용

2. **점진적 증가 권장**
   - 초기: 15 × 3 = 45개
   - 안정화 후: 15 × 5 = 75개
   - 여유 있으면: 15 × 10 = 150개

3. **모니터링 필수**
   - API 응답 시간 추적
   - 실패율 모니터링
   - 시스템 리소스 체크

4. **장애 대응 준비**
   - API 타임아웃 설정 (5분)
   - 재시도 로직 구현
   - Circuit Breaker 고려

---

**작성자:** Claude Code
**검토일:** 2025-11-11
**상태:** 병렬 처리 구조 확정
