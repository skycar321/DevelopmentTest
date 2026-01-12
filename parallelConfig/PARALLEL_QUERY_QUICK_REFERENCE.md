# PostgreSQL 병렬 쿼리 재시도 로직 빠른 참조

## 신규 생성 파일

```
src/main/java/com/
├── batch/mapper/ParallelQueryMapper.java           # 병렬 워커 모니터링 Mapper
├── job/util/ParallelQueryExecutor.java             # 재시도 로직 유틸리티
└── batch/job/wless/calsum/
    └── JobConfiguration.java  # 수정됨

src/main/resources/mapper/myBatis/
└── ParallelQueryMapper.xml                          # PostgreSQL 모니터링 쿼리
```

---

## 핵심 설정

| 항목          | 값    | 설명                      |
| ------------- | ----- | ------------------------- |
| 목표 워커 수  | 4개   | PostgreSQL 병렬 워커 목표 |
| 최소 워커 수  | 2개   | 미달 시 Job 실패          |
| 최대 재시도   | 5회   | 재시도 횟수               |
| 재시도 대기   | 2초   | 재시도 간 대기 시간       |
| 모니터링 간격 | 0.5초 | 워커 수 체크 간격         |

---

## 사용법

```java
// 1. ParallelQueryExecutor 주입
@Autowired
private ParallelQueryExecutor parallelQueryExecutor;

// 2. 병렬 보장 실행
ParallelQueryResult<Integer> result = parallelQueryExecutor.executeWithParallelGuarantee(
    () -> wlessMapper.createTmpWlessMktQatCplyPerpTgtList(jobMap),  // 쿼리
    () -> wlessMapper.dropTmpWlessMktQatCplyPerpTgtList(),          // 정리
    4,   // 목표 워커
    2,   // 최소 워커
    5    // 최대 재시도
);

// 3. 결과 확인
if (result == null) {
    // 최소 워커 미달 - 실패 처리
    contribution.setExitStatus(ExitStatus.FAILED);
    return RepeatStatus.FINISHED;
}

int fullCount = result.getResult();
log.info("fullCount={}, 워커={}, 시도={}",
    fullCount, result.getAchievedWorkers(), result.getAttemptCount());
```

---

## 동작 흐름

```
시도 1:
  쿼리 실행 → 1초 대기 → 워커 수 체크
  ├─ 4개 이상 → ✓ 성공
  └─ 4개 미만 → pg_cancel_backend() → DROP TABLE → 2초 대기 → 재시도

시도 2~5:
  동일 프로세스 반복

최종:
  ├─ 최대 달성값 ≥ 2개 → 진행
  └─ 최대 달성값 < 2개 → 실패 (null)
```

---

## 주요 PostgreSQL 쿼리

```sql
-- 현재 세션 PID
SELECT pg_backend_pid();

-- 병렬 워커 수
SELECT COUNT(*) FROM pg_stat_activity WHERE leader_pid = ?;

-- 쿼리 취소
SELECT pg_cancel_backend(?);

-- 세션 종료 (폴백)
SELECT pg_terminate_backend(?);
```

---

## 로그 패턴

```
INFO  [ParallelQuery] 시도 1/5 시작, 목표 워커: 4, 최소 워커: 2
INFO  [ParallelQuery] 쿼리 실행 중... PID: 12345
INFO  [ParallelQuery] 목표 달성! 워커 수: 4
INFO  PreStep ::: fullCount=2500000, 달성워커=4, 시도횟수=1
```

---

## 빌드 및 실행

```bash
# 컴파일
mvn clean compile

# 패키징
mvn clean package

# 실행 (JVM 인자 필수)
java -Djasypt.encryptor.password=<password> \
     --add-opens java.base/java.io=ALL-UNNAMED \
     -jar target/batch-0.0.1-SNAPSHOT.jar \
     --spring.profiles.active=dev
```

---

## 트러블슈팅 빠른 가이드

| 증상           | 원인                    | 해결                                    |
| -------------- | ----------------------- | --------------------------------------- |
| 최소 워커 미달 | DB 자원 부족            | PostgreSQL 설정 확인, 배치 시간대 조정  |
| 쿼리 취소 실패 | 권한 부족               | `pg_terminate_backend()` 폴백 자동 실행 |
| 모니터링 오류  | `pg_stat_activity` 권한 | `GRANT SELECT ON pg_stat_activity`      |

---

## 상세 문서

- [상세 구현 가이드](./PARALLEL_QUERY_IMPLEMENTATION_GUIDE.md)
- [변경 이력](../MODIFY_HISTORY.md)
- [구현 계획](C:\Users\Nam.claude\plans\sprightly-floating-gizmo.md)
