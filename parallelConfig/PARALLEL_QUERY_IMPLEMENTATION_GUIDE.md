# PostgreSQL 병렬 쿼리 실행 보장 구현 가이드

## 개요

PostgreSQL에서 `max_parallel_workers_per_gather` 설정에도 불구하고 실제 병렬 워커 수가 불안정하게 할당되는 문제를 해결하기 위한 재시도 로직 구현 가이드입니다.

작성일: 2026-01-12
작성자: Claude Code

---

## 1. 구현 배경

### 문제점

- PostgreSQL에서 `SET max_parallel_workers_per_gather=4` 설정 후에도 실제 병렬 워커가 1~5개로 랜덤하게 할당됨
- DB 서버 자원 상태, 쿼리 플래너 판단에 따라 병렬화 정도가 달라짐
- 대량 데이터 처리 시 성능 일관성이 떨어지는 문제 발생

### 해결 방안

- 쿼리 실행 시 실시간으로 병렬 워커 수 모니터링
- 목표 워커 수(4개) 미달 시 쿼리 취소 후 재시도
- 최대 5회 재시도, 최소 임계값(2개) 보장

---

## 2. 아키텍처

```
┌─────────────────────────────────────────────────────────┐
│ WlessMktQatCplyPerpCpntCalsumMmPartiJobConfiguration    │
│                                                           │
│  PreStep() {                                              │
│    configureParallelSettings() // SET max_parallel...    │
│    ↓                                                      │
│    ParallelQueryExecutor.executeWithParallelGuarantee()  │
│  }                                                        │
└────────────────┬────────────────────────────────────────┘
                 │
                 ↓
┌─────────────────────────────────────────────────────────┐
│ ParallelQueryExecutor (재시도 로직)                       │
│                                                           │
│  시도 1~5:                                                │
│    ├─ [Thread 1] 쿼리 실행 (querySupplier.get())        │
│    │                                                     │
│    ├─ [Thread 2] 병렬 워커 모니터링                      │
│    │    └─ 1초 대기 → 0.5초 간격 체크                    │
│    │                                                     │
│    ├─ 워커 ≥ 4개 → 성공                                 │
│    └─ 워커 < 4개 → pg_cancel_backend() → 재시도         │
│                                                           │
│  최종:                                                    │
│    ├─ 최대값 ≥ 2개 → 해당 값으로 진행                   │
│    └─ 최대값 < 2개 → 실패 (null 반환)                  │
└────────────────┬────────────────────────────────────────┘
                 │
                 ↓
┌─────────────────────────────────────────────────────────┐
│ ParallelQueryMapper (PostgreSQL 모니터링)                │
│                                                           │
│  - selectCurrentPid()           // pg_backend_pid()      │
│  - selectParallelWorkerCount()  // pg_stat_activity      │
│  - cancelQuery()                // pg_cancel_backend()   │
│  - terminateSession()           // pg_terminate_backend()│
└─────────────────────────────────────────────────────────┘
```

---

## 3. 주요 컴포넌트

### 3.1 ParallelQueryMapper

**파일**: `/batch/mapper/ParallelQueryMapper.java`

```java
@Mapper
public interface ParallelQueryMapper {
    Integer selectCurrentPid();                          // 현재 세션 PID
    int selectParallelWorkerCount(Integer leaderPid);    // 워커 수 조회
    boolean cancelQuery(Integer pid);                    // 쿼리 취소
    boolean terminateSession(Integer pid);               // 세션 종료
}
```

**주요 SQL** (`ParallelQueryMapper.xml`):

```sql
-- 병렬 워커 수 조회
SELECT COUNT(*) FROM pg_stat_activity WHERE leader_pid = #{leaderPid}

-- 쿼리 취소
SELECT pg_cancel_backend(#{pid})
```

### 3.2 ParallelQueryExecutor

**파일**: `/job/util/ParallelQueryExecutor.java`

```java
@Component
public class ParallelQueryExecutor {

    public <T> ParallelQueryResult<T> executeWithParallelGuarantee(
        Supplier<T> querySupplier,     // 실행할 쿼리
        Runnable cleanupAction,         // 실패 시 정리 작업
        int targetWorkers,              // 목표 워커 수
        int minWorkers,                 // 최소 워커 수
        int maxRetries                  // 최대 재시도
    )
}
```

**결과 클래스**:

```java
public static class ParallelQueryResult<T> {
    T result;              // 쿼리 결과
    int achievedWorkers;   // 달성 워커 수
    int attemptCount;      // 시도 횟수
    boolean targetMet;     // 목표 달성 여부
}
```

---

## 4. 사용 예시

### 4.1 기존 코드

```java
wlessMapper.configureParallelSettings();
log.info("PreStep ::: 전처리 임시테이블 생성");
int fullCount = wlessMapper.createTmpWlessMktQatCplyPerpTgtList(jobMap);
log.info("PreStep ::: fullCount={}", fullCount);
```

### 4.2 변경 후 코드

```java
wlessMapper.configureParallelSettings();
log.info("PreStep ::: 전처리 임시테이블 생성 (병렬 보장 모드)");

// 병렬 워커 보장 실행
ParallelQueryResult<Integer> parallelResult =
    parallelQueryExecutor.executeWithParallelGuarantee(
        () -> wlessMapper.createTmpWlessMktQatCplyPerpTgtList(jobMap),  // 쿼리
        () -> wlessMapper.dropTmpWlessMktQatCplyPerpTgtList(),          // 정리
        4,   // 목표 워커 수
        2,   // 최소 워커 수
        5    // 최대 재시도
    );

if (parallelResult == null) {
    log.error("PreStep ::: 병렬 쿼리 실행 실패 (최소 워커 수 미달)");
    execRst = "Fail";
    contribution.setExitStatus(ExitStatus.FAILED);
    return RepeatStatus.FINISHED;
}

int fullCount = parallelResult.getResult();
log.info("PreStep ::: fullCount={}, 달성워커={}, 시도횟수={}",
        fullCount, parallelResult.getAchievedWorkers(), parallelResult.getAttemptCount());
```

---

## 5. 동작 시나리오

### 시나리오 1: 첫 시도에 4개 달성 (최선의 경우)

```
[시도 1/5]
├─ 쿼리 시작 (PID: 12345)
├─ 1초 대기
├─ 모니터링: 워커 4개 확인
└─ ✓ 목표 달성! 쿼리 완료까지 대기

결과: attemptCount=1, achievedWorkers=4, targetMet=true
소요 시간: 약 5초 (쿼리 실행 시간)
```

### 시나리오 2: 3번째 시도에 4개 달성

```
[시도 1/5]
├─ 쿼리 시작 (PID: 12345)
├─ 모니터링: 워커 2개
├─ ✗ 목표 미달 (2/4)
├─ pg_cancel_backend(12345)
├─ DROP TABLE IF EXISTS...
└─ 2초 대기

[시도 2/5]
├─ 쿼리 시작 (PID: 12350)
├─ 모니터링: 워커 3개
├─ ✗ 목표 미달 (3/4)
├─ pg_cancel_backend(12350)
├─ DROP TABLE IF EXISTS...
└─ 2초 대기

[시도 3/5]
├─ 쿼리 시작 (PID: 12355)
├─ 모니터링: 워커 4개
└─ ✓ 목표 달성!

결과: attemptCount=3, achievedWorkers=4, targetMet=true
소요 시간: 약 15초 (재시도 2회 × 2초 + 쿼리 시간)
```

### 시나리오 3: 5회 시도 후 최대 3개 달성

```
[시도 1/5] 워커 2개 → 취소, maxAchieved=2
[시도 2/5] 워커 3개 → 취소, maxAchieved=3
[시도 3/5] 워커 2개 → 취소, maxAchieved=3
[시도 4/5] 워커 3개 → 취소, maxAchieved=3
[시도 5/5] 워커 3개 → 최대값 재달성, 진행

결과: attemptCount=5, achievedWorkers=3, targetMet=false
소요 시간: 약 20초 (재시도 4회 × 2초 + 쿼리 시간)
```

### 시나리오 4: 5회 시도 후 최대 1개 (실패)

```
[시도 1~5] 모든 시도에서 최대 워커 1개

결과: null 반환 → Job 실패 처리
로그: "병렬 쿼리 실행 실패 (최소 워커 수 미달)"
ExitStatus: FAILED
```

---

## 6. 로그 출력 예시

### 성공 케이스

```
INFO  [ParallelQuery] 시도 1/5 시작, 목표 워커: 4, 최소 워커: 2, 현재 최대 달성: 0
INFO  [ParallelQuery] 쿼리 실행 중... PID: 12345
DEBUG [ParallelQuery] 모니터링: 현재 워커 수 = 2
DEBUG [ParallelQuery] 모니터링: 현재 워커 수 = 3
DEBUG [ParallelQuery] 모니터링: 현재 워커 수 = 4
INFO  [ParallelQuery] 목표 워커 수 달성: 4/4
INFO  [ParallelQuery] 목표 달성! 워커 수: 4, 시도: 1/5
INFO  PreStep ::: fullCount=2500000, 달성워커=4, 시도횟수=1
```

### 재시도 케이스

```
INFO  [ParallelQuery] 시도 1/5 시작, 목표 워커: 4, 최소 워커: 2, 현재 최대 달성: 0
INFO  [ParallelQuery] 쿼리 실행 중... PID: 12345
DEBUG [ParallelQuery] 모니터링: 현재 워커 수 = 2
WARN  [ParallelQuery] 목표 미달 (2/4), 쿼리 취소 및 재시도
INFO  [ParallelQuery] 쿼리 취소 완료 (PID: 12345, 결과: true)
INFO  [ParallelQuery] 테이블 정리 실행
INFO  [ParallelQuery] 2000ms 대기 후 재시도...

INFO  [ParallelQuery] 시도 2/5 시작, 목표 워커: 4, 최소 워커: 2, 현재 최대 달성: 2
INFO  [ParallelQuery] 쿼리 실행 중... PID: 12350
DEBUG [ParallelQuery] 모니터링: 현재 워커 수 = 4
INFO  [ParallelQuery] 목표 달성! 워커 수: 4, 시도: 2/5
INFO  PreStep ::: fullCount=2500000, 달성워커=4, 시도횟수=2
```

---

## 7. 적용 체크리스트

### 빌드 및 테스트

- [ ] `mvn clean compile` 실행하여 컴파일 오류 확인
- [ ] ParallelQueryMapper 쿼리 정상 동작 확인 (pg_backend_pid 등)
- [ ] ParallelQueryExecutor 단위 테스트 (Mock 사용)
- [ ] 실제 Job 실행 테스트 (dev 환경)

### 성능 검증

- [ ] 병렬 워커 4개 달성 시 쿼리 실행 시간 측정
- [ ] 재시도로 인한 추가 소요 시간 측정
- [ ] 로그 레벨 조정 (운영에서는 DEBUG 비활성화)

### 운영 배포

- [ ] application.yaml 설정 확인 (데이터베이스 연결 정보)
- [ ] Quartz 스케줄 설정 확인
- [ ] 배치 실행 시간대 조정 (재시도 고려)
- [ ] 모니터링 알림 설정 (워커 수 미달 시)

---

## 8. 설정 조정

### 파라미터 조정 가능 값

```java
parallelQueryExecutor.executeWithParallelGuarantee(
    querySupplier,
    cleanupAction,
    targetWorkers,    // 목표 워커 수 (기본: 4)
    minWorkers,       // 최소 워커 수 (기본: 2)
    maxRetries        // 최대 재시도 (기본: 5)
);
```

**권장 설정**:

- `targetWorkers`: 4 (PostgreSQL `max_parallel_workers_per_gather` 설정과 동일)
- `minWorkers`: 2 (목표의 50%, 성능 최소 보장선)
- `maxRetries`: 5 (총 소요 시간 약 10~20초 이내)

**대기 시간** (코드 수정 필요):

```java
private static final long DEFAULT_INITIAL_WAIT_MS = 1000;      // 첫 모니터링 대기
private static final long DEFAULT_MONITORING_INTERVAL_MS = 500; // 모니터링 간격
private static final long DEFAULT_RETRY_DELAY_MS = 2000;       // 재시도 대기
```

---

## 9. 트러블슈팅

### 문제 1: "병렬 쿼리 실행 실패 (최소 워커 수 미달)"

**증상**: 5회 재시도 후에도 최소 워커 2개 미달

**원인**:

- DB 서버 자원 부족 (CPU, 메모리)
- `max_parallel_workers` 전역 설정 부족
- 다른 배치 작업과 동시 실행

**해결**:

1. PostgreSQL 설정 확인:
   ```sql
   SHOW max_parallel_workers;          -- 전역 최대 워커
   SHOW max_parallel_workers_per_gather;  -- 쿼리당 최대 워커
   ```
2. DB 서버 자원 모니터링 (CPU, 메모리 사용률)
3. 배치 실행 시간대 조정 (다른 작업과 분리)

### 문제 2: 쿼리 취소 실패

**증상**: `pg_cancel_backend()` 호출 후에도 쿼리 계속 실행

**원인**:

- 쿼리가 이미 완료됨
- 취소 권한 부족

**해결**:

- 코드에서 폴백으로 `pg_terminate_backend()` 자동 호출됨
- PostgreSQL 사용자 권한 확인

### 문제 3: 모니터링 오류

**증상**: `selectParallelWorkerCount()` 실패

**원인**:

- `pg_stat_activity` 뷰 접근 권한 부족
- PID가 null

**해결**:

```sql
-- 권한 부여
GRANT SELECT ON pg_stat_activity TO batch_user;
```

---

## 10. 향후 개선 사항

### 10.1 설정 외부화

현재는 하드코딩된 값을 `application.yaml`로 이동:

```yaml
parallel-query:
  target-workers: 4
  min-workers: 2
  max-retries: 5
  retry-delay-ms: 2000
```

### 10.2 메트릭 수집

```java
@Component
public class ParallelQueryMetrics {
    private final AtomicLong totalExecutions = new AtomicLong(0);
    private final AtomicLong successfulExecutions = new AtomicLong(0);
    private final AtomicLong failedExecutions = new AtomicLong(0);

    // Micrometer 연동
}
```

### 10.3 다른 Job에 적용

유사한 대량 데이터 처리 Job에도 동일한 패턴 적용 가능:

- `WlessMktQatCplyPerpMainPartiJob` (무선 메인 배치)
- `WrlinMktQatCplyPerpMainPartiJob` (유선 메인 배치)

---

## 11. 참고 자료

### PostgreSQL 문서

- [Parallel Query](https://www.postgresql.org/docs/current/parallel-query.html)
- [pg_stat_activity](https://www.postgresql.org/docs/current/monitoring-stats.html#MONITORING-PG-STAT-ACTIVITY-VIEW)
- [Administrative Functions](https://www.postgresql.org/docs/current/functions-admin.html#FUNCTIONS-ADMIN-SIGNAL)

### 프로젝트 문서

- [구현 계획](C:\Users\Nam.claude\plans\sprightly-floating-gizmo.md)
- [변경 이력](../MODIFY_HISTORY.md)

---

**문의**: 문제 발생 시 로그 파일과 함께 문의 부탁드립니다.
