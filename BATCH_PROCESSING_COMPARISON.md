# 배치 처리 방식 비교 분석

**작성일:** 2025-11-11
**비교 대상:** Cursor + Tasklet vs PagingReader + Writer

---

## 📊 요구사항 정리

### 기능 요구사항
1. **배치 단위 처리:** Reader에서 1000건을 읽은 후 Processor에 한번에 전달
2. **병렬 API 호출:** 1000건을 병렬 5개로 분할하여 동시 API 호출
3. **일괄 저장:** 1000건에 대한 모든 API 응답을 받은 후 Writer로 전달

### 비기능 요구사항
1. **데이터 볼륨:** 약 200만 로우 처리
2. **메모리 효율:** 대용량 데이터 처리 시 OOM 방지 필수
3. **처리 성능:** API 호출 병렬화로 처리 시간 단축
4. **파티셔닝:** 기존 15개 쓰레드 구조 유지

### ⚡ 병렬 처리 구조 (중요!)

**2단계 병렬 처리:**
```
1차 병렬 (Partitioner): 15개 파티션
  ↓
2차 병렬 (각 파티션 내부): 5개 병렬 API 호출
  ↓
총 동시 API 호출: 15 × 5 = 75개 🔥
```

**상세 구조:**
- Partitioner가 데이터를 15개로 분할 (MOD 연산)
- 각 파티션은 독립적인 쓰레드에서 실행 (wlessMain-thread-1~15)
- 각 파티션 내부에서 1000건씩 배치 처리
- 각 1000건 배치를 다시 5개로 분할하여 병렬 API 호출 (200건 × 5)
- **결과: 15개 파티션 × 5개 내부 병렬 = 최대 75개 동시 API 호출**

**API 서버 부하:**
- 동시 API 호출: 최대 75개
- API 서버는 75개 요청을 동시 처리할 수 있어야 함
- Rate Limiting 및 API 서버 용량 확인 필수 ⚠️

> 📖 상세 분석: [PARALLEL_PROCESSING_ANALYSIS.md](PARALLEL_PROCESSING_ANALYSIS.md) 참조

---

## 🎯 설계 방안 비교

### Option A: Cursor + Tasklet 방식

#### 구조
```
Tasklet {
  SqlSession.selectCursor() → Cursor 스트리밍
    ↓
  while(iterator.hasNext()) {
    1000건 버퍼링
      ↓
    병렬 5개로 API 호출
      ↓
    일괄 저장
      ↓
    buffer.clear()
  }
}
```

#### 장점
- ✅ **메모리 최적화:** 60MB (15 파티션 기준)
- ✅ **코드 가독성:** 로직이 한 곳에 집중, 이해 용이
- ✅ **완전한 제어:** 배치 크기, 병렬 수 직접 제어 가능
- ✅ **기존 패턴 일관성:** MyBatis Cursor 직접 사용 (표준 패턴)
- ✅ **디버깅 용이:** 단일 클래스에 로직 집중

#### 단점
- ❌ **재시작 기능:** 파티션 단위로만 가능 (청크 단위 불가)
- ❌ **프레임워크 제한:** Spring Batch의 Skip/Retry 기능 수동 구현 필요
- ❌ **메트릭 제한:** Read/Write count 자동 추적 안 됨

#### 메모리 사용량 (파티션당)
```
Cursor: 스트리밍 (0MB 상주)
1000건 버퍼: 2MB
병렬 처리: +2MB
Total: ~4MB per partition
15 partitions: 60MB
```

#### 코드 복잡도
- **라인 수:** ~250 라인 (Tasklet 클래스)
- **파일 수:** 2개 (Configuration + Tasklet)
- **가독성:** ⭐⭐⭐⭐⭐

---

### Option B: PagingReader + Writer 방식

#### 구조
```
MyBatisPagingItemReader (1000건씩 페이징)
  ↓
read() 호출 1000번 (내부 버퍼에서 1건씩 반환)
  ↓
Processor: pass-through (데이터만 전달)
  ↓
chunk(1000) 완료
  ↓
Writer: 1000건 수신
  ↓
병렬 5개로 API 호출
  ↓
일괄 저장
```

#### 장점
- ✅ **Spring Batch 표준:** 프레임워크 완전 활용
- ✅ **재시작 기능:** 청크 단위 정교한 재시작 지원
- ✅ **메트릭/모니터링:** Read/Write count 자동 추적
- ✅ **Skip/Retry:** 프레임워크 기능 활용 가능
- ✅ **트랜잭션 관리:** 자동 처리

#### 단점
- ❌ **Anti-pattern:** Writer에 비즈니스 로직 (API 호출)
- ❌ **Processor 무의미:** pass-through만 수행
- ❌ **메모리 사용:** 90MB (Tasklet 대비 50% 증가)
- ❌ **코드 의도 불명확:** 왜 Processor가 있는지 혼란

#### 메모리 사용량 (파티션당)
```
PagingReader 내부 버퍼: 2MB (1000건)
Chunk 컨텍스트: 2MB
Writer 병렬 처리: +2MB
Total: ~6MB per partition
15 partitions: 90MB
```

#### 코드 복잡도
- **라인 수:** ~300 라인 (Configuration + Writer + Processor)
- **파일 수:** 4개 (Configuration + Reader Bean + Processor + Writer)
- **가독성:** ⭐⭐⭐

---

## 📊 상세 비교표

| 항목 | Cursor + Tasklet (A) | PagingReader + Writer (B) |
|------|---------------------|---------------------------|
| **메모리 효율** | 60MB ⭐⭐⭐⭐⭐ | 90MB ⭐⭐⭐⭐ |
| **처리 속도** | 동일 (병렬 5개) | 동일 (병렬 5개) |
| **코드 가독성** | 매우 높음 ⭐⭐⭐⭐⭐ | 중간 ⭐⭐⭐ |
| **유지보수성** | 쉬움 ⭐⭐⭐⭐⭐ | 보통 ⭐⭐⭐ |
| **재시작 기능** | 파티션 단위 ⭐⭐⭐ | 청크 단위 ⭐⭐⭐⭐⭐ |
| **Skip/Retry** | 수동 구현 ⭐⭐ | 프레임워크 지원 ⭐⭐⭐⭐⭐ |
| **메트릭 추적** | 수동 로깅 ⭐⭐⭐ | 자동 추적 ⭐⭐⭐⭐⭐ |
| **배치 단위 처리** | 완벽 지원 ⭐⭐⭐⭐⭐ | 완벽 지원 ⭐⭐⭐⭐⭐ |
| **병렬 API 호출** | 완벽 지원 ⭐⭐⭐⭐⭐ | 완벽 지원 ⭐⭐⭐⭐⭐ |
| **디버깅** | 매우 쉬움 ⭐⭐⭐⭐⭐ | 보통 ⭐⭐⭐ |

---

## 🔬 실행 흐름 비교

### Cursor + Tasklet 실행 흐름

```
[파티션 1 시작 - Thread-1]
├─ SqlSession.selectCursor() 실행
│  └─ DB Cursor 오픈 (메모리: 0MB)
│
├─ Iterator.next() 반복
│  ├─ 1건 fetch → batch.add() (메모리: +2KB)
│  ├─ 2건 fetch → batch.add()
│  ├─ ...
│  └─ 1000건 도달
│
├─ processBatch(1000건) 시작 (메모리: 2MB)
│  ├─ 병렬 5개로 분할
│  │  ├─ Thread-A: 200건 API 호출
│  │  ├─ Thread-B: 200건 API 호출
│  │  ├─ Thread-C: 200건 API 호출
│  │  ├─ Thread-D: 200건 API 호출
│  │  └─ Thread-E: 200건 API 호출
│  │
│  ├─ 모든 API 완료 대기 (CompletableFuture.get())
│  ├─ 결과 수집 (메모리 피크: 4MB)
│  └─ batchInsert() 실행
│
├─ batch.clear() (메모리: 0MB)
│
├─ 다음 1000건 반복...
│
└─ Cursor 종료

[처리 완료]
처리 건수: 133,333건 (파티션당)
피크 메모리: 4MB
```

### PagingReader + Writer 실행 흐름

```
[파티션 1 시작 - Thread-1]
├─ MyBatisPagingItemReader 초기화
│
├─ Chunk 1 시작 (chunk_size=1000)
│  ├─ Reader.doReadPage() 실행
│  │  └─ SELECT * LIMIT 1000 OFFSET 0
│  │     (메모리: 2MB - 내부 버퍼에 1000건 로드)
│  │
│  ├─ Reader.read() 호출 1번 → item 1 반환
│  ├─ Processor.process(item 1) → pass-through
│  ├─ Reader.read() 호출 2번 → item 2 반환
│  ├─ Processor.process(item 2) → pass-through
│  ├─ ...
│  ├─ Reader.read() 호출 1000번 → item 1000 반환
│  ├─ Processor.process(item 1000) → pass-through
│  │  (메모리: 4MB - chunk context + processed items)
│  │
│  └─ Writer.write(List<1000건>) 호출
│     ├─ 병렬 5개로 분할
│     │  ├─ Thread-A: 200건 API 호출
│     │  ├─ Thread-B: 200건 API 호출
│     │  ├─ Thread-C: 200건 API 호출
│     │  ├─ Thread-D: 200건 API 호출
│     │  └─ Thread-E: 200건 API 호출
│     │
│     ├─ 모든 API 완료 대기
│     ├─ 결과 수집 (메모리 피크: 6MB)
│     └─ batchInsert() 실행
│
├─ Chunk 1 완료 (트랜잭션 커밋)
│  └─ Spring Batch 메타데이터 업데이트
│
├─ Chunk 2 시작
│  ├─ Reader.doReadPage() 실행
│  │  └─ SELECT * LIMIT 1000 OFFSET 1000
│  └─ ... (반복)
│
└─ 처리 완료

[처리 완료]
처리 건수: 133,333건 (파티션당)
피크 메모리: 6MB
Spring Batch read_count: 133,333
Spring Batch write_count: 133건 (청크 수)
```

---

## 🎯 선택 기준

### Cursor + Tasklet을 선택해야 하는 경우

1. ✅ **메모리 효율이 최우선**
   - 200만 로우 대용량 처리
   - 메모리 제약이 있는 환경

2. ✅ **코드 가독성과 유지보수성 중시**
   - 로직이 한 곳에 집중
   - 디버깅이 쉬워야 함

3. ✅ **배치 + 병렬 처리가 핵심 요구사항**
   - 1000건 단위 처리
   - 병렬 5개 API 호출

4. ✅ **파티션 단위 재시작으로 충분**
   - 청크 단위 정교한 재시작 불필요

5. ✅ **기존 Cursor 패턴 유지**
   - MyBatis Cursor 표준 패턴 사용

### PagingReader + Writer를 선택해야 하는 경우

1. ✅ **Spring Batch 표준 준수가 중요**
   - 엔터프라이즈 표준 패턴
   - 프레임워크 기능 완전 활용

2. ✅ **청크 단위 재시작이 필수**
   - 정확한 재시작 포인트 필요
   - 실패 지점부터 정밀 재개

3. ✅ **Skip/Retry 정책 활용**
   - 특정 item 스킵 필요
   - 자동 재시도 로직 필요

4. ✅ **메트릭/모니터링 중시**
   - Read/Write count 자동 추적
   - ItemReadListener/ItemWriteListener 활용

5. ✅ **메모리 차이가 문제 안 됨**
   - 90MB도 충분히 수용 가능한 환경

---

## 📈 성능 예측

### 현재 구조 (item-by-item)
```
처리 방식: 1건씩 순차 API 호출
API 호출 시간: 100ms/건 (가정)
총 API 호출: 2,000,000건
순차 처리 시간: 2,000,000 × 100ms = 200,000초 = 55시간
파티셔닝(15): 55시간 / 15 = 3.7시간

동시 API 호출 수: 최대 15개 (각 파티션 1건씩)
```

### Cursor + Tasklet (개선 후)
```
데이터 분할:
- 전체: 2,000,000건
- 파티션당: 2,000,000 / 15 = 133,333건
- 배치 크기: 1000건
- 파티션당 배치 수: 133,333 / 1000 = 133배치

처리 방식: 1000건씩 → 병렬 5개
- 1000건을 5개로 분할: 200건 × 5
- API 호출 시간: 100ms/건

배치당 처리 시간:
- 200건 순차 처리: 200 × 100ms = 20,000ms = 20초
- 5개 병렬이므로: max(20초) = 20초

각 파티션 처리 시간:
- 133배치 × 20초 = 2,660초 = 44분

전체 처리 시간 (15개 파티션 동시):
- 44분 (가장 느린 파티션 기준)

개선율: 3.7시간 → 44분 (80% 단축) ✅

⚡ 동시 API 호출 수: 최대 75개 (15 파티션 × 5 병렬) 🔥
   - API 서버 부하: 현재 대비 5배 증가
   - API 서버 동시 처리 용량 확인 필수!
```

### PagingReader + Writer (개선 후)
```
처리 방식: 동일 (1000건씩 → 병렬 5개)
성능: Cursor + Tasklet과 동일
총 처리 시간: 44분

차이점:
- Chunk 오버헤드: +2% (Spring Batch 메타데이터 업데이트)
- 예상 시간: 약 45분

⚡ 동시 API 호출 수: 동일 (최대 75개)
```

### ⚠️ API 서버 부하 주의사항

```
현재 구조 vs 개선 후:
- 현재: 최대 15개 동시 호출
- 개선: 최대 75개 동시 호출 (5배 증가)

API 서버 요구사항:
- 동시 처리 용량: 최소 75개 요청
- 응답 시간 유지: 100ms 이내 (가정)
- 평균 RPS: (200 × 15) / 20초 = 150 RPS
- 피크 동시 연결: 75개

권장 조치:
1. API 서버 용량 확인
2. Rate Limiting 고려
3. 점진적 증가 (3개 → 5개 → 10개)
4. 모니터링 강화 (응답 시간, 실패율)
```

---

## 🔧 구현 상세 차이

### Cursor + Tasklet 주요 코드

```java
@Override
public RepeatStatus execute(StepContribution contribution,
                            ChunkContext chunkContext) throws Exception {

    Map<String, Object> params = extractParams(chunkContext);
    SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.SIMPLE);

    try (Cursor<PpWlessabcTxnItem> cursor =
            sqlSession.selectCursor(
                "com.abc.batch.mapper.WlessPartiMapper.selectWlessMabcQatCplyPerpTgtList",
                params)) {

        Iterator<PpWlessabcTxnItem> iterator = cursor.iterator();
        List<PpWlessabcTxnItem> batch = new ArrayList<>(1000);

        while (iterator.hasNext()) {
            batch.add(iterator.next());

            if (batch.size() == 1000) {
                processBatch(batch, params);
                batch.clear();
            }
        }

        if (!batch.isEmpty()) {
            processBatch(batch, params);
        }

    } finally {
        sqlSession.close();
    }

    return RepeatStatus.FINISHED;
}
```

### PagingReader + Writer 주요 코드

```java
// Reader
@Bean
@StepScope
public MyBatisPagingItemReader<PpWlessabcTxnItem> pagingReader(...) {
    MyBatisPagingItemReader<PpWlessabcTxnItem> reader =
        new MyBatisPagingItemReader<>();
    reader.setSqlSessionFactory(sqlSessionFactory);
    reader.setQueryId("mapper.selectWlessMabcQatCplyPerpTgtList");
    reader.setPageSize(1000);
    reader.setParameterValues(params);
    return reader;
}

// Processor (pass-through)
@Bean
@StepScope
public ItemProcessor<PpWlessabcTxnItem, PpWlessabcTxnItem> processor() {
    return item -> item;  // 단순 전달
}

// Writer (실제 비즈니스 로직)
@Bean
@StepScope
public ItemWriter<PpWlessabcTxnItem> batchWriter() {
    return items -> {
        // items는 1000건
        List<RuleWlessChkResltItem> results = processInParallel(items, 5);
        batchInsertDao.batchInsert("table", results, ...);
    };
}
```

---

## 🏆 최종 권장사항

### 🥇 현재 프로젝트에 최적: **Cursor + Tasklet (Option A)**

**선정 이유:**
1. ✅ 메모리 효율 33% 향상 (60MB vs 90MB)
2. ✅ 코드 가독성 및 유지보수성 우수
3. ✅ 요구사항 완벽 충족 (1000건 배치 + 병렬 5개)
4. ✅ MyBatis Cursor 표준 패턴 사용 (검증된 방식)
5. ✅ 디버깅 및 문제 해결 용이

**적합한 상황:**
- 200만 로우 대용량 처리
- 메모리 최적화가 중요
- 배치 + 병렬 처리가 핵심
- 파티션 단위 재시작으로 충분

---

### 🥈 엔터프라이즈 표준 중시: **PagingReader + Writer (Option B)**

**선정 이유:**
1. ✅ Spring Batch 표준 패턴
2. ✅ 프레임워크 기능 완전 활용
3. ✅ 청크 단위 정교한 재시작
4. ✅ Skip/Retry 자동 지원
5. ✅ 메트릭/모니터링 자동 추적

**적합한 상황:**
- 엔터프라이즈 표준 준수 필요
- 청크 단위 재시작 필수
- Skip/Retry 정책 활용
- 메모리 차이(30MB)가 문제 안 됨

---

## 📊 의사결정 매트릭스

| 우선순위 | Cursor + Tasklet | PagingReader + Writer |
|---------|------------------|----------------------|
| 메모리 효율 | 10점 | 8점 |
| 코드 가독성 | 10점 | 7점 |
| 유지보수성 | 10점 | 7점 |
| 재시작 기능 | 7점 | 10점 |
| 표준 준수 | 7점 | 10점 |
| 디버깅 용이성 | 10점 | 7점 |
| **총점** | **54점** | **49점** |

---

## 🎓 참고 자료

### 기존 코드
- `WlessMabcQatCplyPerpDbyPartiJobConfiguration.java` - 기존 구조
- `BrmsCursorItemReader.java` - Cursor 패턴 참조
- `BatchInsertDao.java` - 일괄 저장 유틸

### Spring Batch 문서
- [Tasklet Processing](https://docs.spring.io/spring-batch/docs/current/reference/html/step.html#taskletStep)
- [MyBatisPagingItemReader](https://mybatis.org/spring/batch.html)
- [Partitioning](https://docs.spring.io/spring-batch/docs/current/reference/html/scalability.html#partitioning)

---

## 📝 결론

**200만 로우 대용량 처리를 위한 최적 솔루션:**

**Cursor + Tasklet (Option A)** 방식이 현재 프로젝트에 가장 적합합니다.

- 메모리 효율성 ⭐⭐⭐⭐⭐
- 코드 품질 ⭐⭐⭐⭐⭐
- 요구사항 충족도 ⭐⭐⭐⭐⭐
- 유지보수성 ⭐⭐⭐⭐⭐

**다만, 엔터프라이즈 표준 준수가 더 중요하다면 PagingReader + Writer 방식을 선택하는 것도 합리적입니다.**

---

**작성자:** Claude Code
**검토일:** 2025-11-11
**상태:** 구현 준비 완료
