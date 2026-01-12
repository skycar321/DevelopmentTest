  계획 파일을 작성했습니다. 이제 ExitPlanMode를 호출하여 사용자의 승인을 받아야 합니다.

● 계획을 완성했습니다. 요약해드리겠습니다.

  📋 구현 계획 요약

  핵심 설정값
  ┌────────────────┬────────────────────┐
  │      항목      │         값         │
  ├────────────────┼────────────────────┤
  │ 목표 워커 수   │ 4개                │
  ├────────────────┼────────────────────┤
  │ 최소 임계값    │ 2개 (미달 시 실패) │
  ├────────────────┼────────────────────┤
  │ 최대 재시도    │ 5회                │
  ├────────────────┼────────────────────┤
  │ 재시도 간 대기 │ 2초                │
  └────────────────┴────────────────────┘
  파일 변경 범위 (최소화)

  신규 생성 (3개):
  1. ParallelQueryMapper.java - PostgreSQL 모니터링 쿼리
  2. ParallelQueryMapper.xml - SQL 매퍼
  3. ParallelQueryExecutor.java - 재시도 로직 유틸리티

  수정 (1개, 약 10줄):
  4. WlessMktQatCplyPerpCpntCalsumMmPartiJobConfiguration.java

  동작 흐름

  시도 1~5:
    ├─ 별도 스레드에서 쿼리 실행
    ├─ 1초 후 pg_stat_activity로 워커 수 체크
    ├─ 워커 ≥ 4개 → 성공
    └─ 워커 < 4개 → pg_cancel_backend() → DROP TABLE → 2초 대기 → 재시도

  5회 완료 후:
    ├─ 최대 달성값 ≥ 2 → 해당 값으로 진행
    └─ 최대 달성값 < 2 → 실패 처리

  계획 승인 후 구현을 진행하겠습니다.

────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────── Ready to code?

 Here is Claude's plan:
╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌ PostgreSQL 병렬 쿼리 실행 보장을 위한 재시도 로직 구현 계획

 요구사항 요약
 ┌────────────────┬────────────────────────────────────────────────────┐
 │      항목      │                         값                         │
 ├────────────────┼────────────────────────────────────────────────────┤
 │ 목표 워커 수   │ 4개                                                │
 ├────────────────┼────────────────────────────────────────────────────┤
 │ 최소 임계값    │ 2개 (미달 시 실패 처리)                            │
 ├────────────────┼────────────────────────────────────────────────────┤
 │ 최대 재시도    │ 5회                                                │
 ├────────────────┼────────────────────────────────────────────────────┤
 │ 재시도 간 대기 │ 2초                                                │
 ├────────────────┼────────────────────────────────────────────────────┤
 │ 성공 조건      │ 4개 달성 OR 5회 시도 후 최대 달성값(≥2개)으로 진행 │
 └────────────────┴────────────────────────────────────────────────────┘
 구현 전략: Wrapper 메서드 방식

 소스 수정 최소화를 위해 Wrapper 메서드 방식 채택:
 - 신규 유틸리티 클래스 1개 추가
 - Mapper 인터페이스 1개 + XML 1개 추가
 - 기존 Job 설정 파일 약 10줄 수정

 ---
 파일 변경 목록

 신규 생성 (3개)

 1. src/main/java/com/kt/sqms/job/util/ParallelQueryExecutor.java
 2. src/main/java/com/kt/sqms/batch/mapper/ParallelQueryMapper.java
 3. src/main/resources/mapper/myBatis/ParallelQueryMapper.xml

 수정 (1개)

 4. src/main/java/com/kt/sqms/batch/job/wless/calsum/WlessMktQatCplyPerpCpntCalsumMmPartiJobConfiguration.java (약 10줄)

 ---
 상세 구현 계획

 1단계: ParallelQueryMapper 생성

 ParallelQueryMapper.java
 @Mapper
 public interface ParallelQueryMapper {
     Integer selectCurrentPid();                              // pg_backend_pid()
     int selectParallelWorkerCount(Integer leaderPid);        // 워커 수 조회
     boolean cancelQuery(Integer pid);                        // pg_cancel_backend()
 }

 ParallelQueryMapper.xml
 <select id="selectCurrentPid" resultType="java.lang.Integer">
     SELECT pg_backend_pid()
 </select>

 <select id="selectParallelWorkerCount" resultType="int">
     SELECT COUNT(*) FROM pg_stat_activity WHERE leader_pid = #{leaderPid}
 </select>

 <select id="cancelQuery" resultType="boolean">
     SELECT pg_cancel_backend(#{pid})
 </select>

 2단계: ParallelQueryExecutor 구현

 핵심 로직 흐름:
 시도 1~5:
   ├─ 별도 스레드에서 쿼리 실행 시작
   ├─ 모니터링 스레드에서 1초 대기 후 워커 수 체크 (0.5초 간격)
   ├─ 워커 ≥ 4개 → 성공, 쿼리 완료까지 대기
   ├─ 워커 < 4개 & 쿼리 실행 중 →
   │    ├─ 최대 워커 수 기록
   │    ├─ pg_cancel_backend() 호출
   │    ├─ DROP TABLE IF EXISTS 실행 (부분 생성 테이블 정리)
   │    └─ 2초 대기 후 재시도
   └─ 5회 완료 후:
        ├─ 최대 달성값 ≥ 2 → 해당 워커 수로 진행
        └─ 최대 달성값 < 2 → 실패 처리

 주요 메서드:
 public class ParallelQueryExecutor {

     // 메인 메서드
     public <T> ParallelQueryResult<T> executeWithParallelGuarantee(
         Supplier<T> querySupplier,     // 실행할 쿼리 (Mapper 호출)
         Runnable cleanupAction,         // 실패 시 정리 작업 (DROP TABLE)
         int targetWorkers,              // 목표 워커 수 (4)
         int minWorkers,                 // 최소 워커 수 (2)
         int maxRetries                  // 최대 재시도 (5)
     );

     // 결과 클래스
     public static class ParallelQueryResult<T> {
         T result;              // 쿼리 결과값
         int achievedWorkers;   // 달성 워커 수
         int attemptCount;      // 시도 횟수
         boolean targetMet;     // 목표 달성 여부
     }
 }

 3단계: Job 설정 파일 수정

 변경 전 (라인 246-250):
 wlessMapper.configureParallelSettings();
 log.info("PreStep ::: 전처리 임시테이블 생성");
 int fullCount = wlessMapper.createTmpWlessMktQatCplyPerpTgtList(jobMap);
 log.info("PreStep ::: fullCount={}", fullCount);

 변경 후:
 wlessMapper.configureParallelSettings();
 log.info("PreStep ::: 전처리 임시테이블 생성 (병렬 보장 모드)");

 // 병렬 워커 보장 실행
 ParallelQueryResult<Integer> parallelResult = parallelQueryExecutor.executeWithParallelGuarantee(
     () -> wlessMapper.createTmpWlessMktQatCplyPerpTgtList(jobMap),  // 쿼리
     () -> wlessMapper.dropTmpWlessMktQatCplyPerpTgtList(),          // 정리 작업
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

 추가 필요 (클래스 상단):
 @Autowired
 private ParallelQueryExecutor parallelQueryExecutor;

 ---
 동작 시나리오

 시나리오 1: 첫 시도에 4개 달성

 [시도 1] 쿼리 시작 → 1초 후 모니터링 → 워커 4개 확인 → 성공
 결과: attemptCount=1, achievedWorkers=4, targetMet=true

 시나리오 2: 3번째 시도에 4개 달성

 [시도 1] 워커 2개 → 취소 → DROP → 2초 대기
 [시도 2] 워커 3개 → 취소 → DROP → 2초 대기
 [시도 3] 워커 4개 → 성공
 결과: attemptCount=3, achievedWorkers=4, targetMet=true

 시나리오 3: 5회 시도 후 최대 3개 달성

 [시도 1] 워커 2개 → 취소, maxAchieved=2
 [시도 2] 워커 3개 → 취소, maxAchieved=3
 [시도 3] 워커 2개 → 취소, maxAchieved=3
 [시도 4] 워커 3개 → 취소, maxAchieved=3
 [시도 5] 워커 3개 → 최대값 달성, 진행
 결과: attemptCount=5, achievedWorkers=3, targetMet=false

 시나리오 4: 5회 시도 후 최대 1개 (실패)

 [시도 1~5] 최대 워커 1개 → 최소 임계값(2) 미달
 결과: null 반환 → Job 실패 처리

 ---
 로깅 출력 예시

 INFO  [ParallelQuery] 시도 1/5 시작, 목표 워커: 4, 최소 워커: 2
 INFO  [ParallelQuery] 쿼리 실행 중... PID: 12345
 INFO  [ParallelQuery] 모니터링: 현재 워커 수 = 2
 INFO  [ParallelQuery] 목표 미달 (2/4), 쿼리 취소 및 재시도
 WARN  [ParallelQuery] 쿼리 취소 완료, 테이블 정리 실행
 INFO  [ParallelQuery] 2초 대기 후 재시도...

 INFO  [ParallelQuery] 시도 2/5 시작, 현재 최대 달성: 2
 INFO  [ParallelQuery] 쿼리 실행 중... PID: 12350
 INFO  [ParallelQuery] 모니터링: 현재 워커 수 = 4
 INFO  [ParallelQuery] 목표 달성! 워커 수: 4
 INFO  [ParallelQuery] 결과: 성공, 시도=2, 워커=4

 ---
 검증 계획

 1. 단위 테스트

 - ParallelQueryMapper 쿼리 정상 동작 확인
 - ParallelQueryExecutor 재시도 로직 테스트 (Mock 사용)

 2. 통합 테스트

 - 실제 DB 환경에서 병렬 워커 모니터링 확인
 - 쿼리 취소 및 테이블 정리 동작 확인

 3. 성능 테스트

 - 재시도로 인한 총 실행 시간 측정
 - 병렬 워커 수에 따른 쿼리 성능 비교

 4. 실행 검증 명령

 # 빌드
 mvn clean compile

 # 로그 확인하며 배치 실행
 # application.yaml에서 해당 Job 활성화 후 실행

 ---
 리스크 및 대응
 ┌────────────────────┬─────────────────────────────┐
 │       리스크       │            대응             │
 ├────────────────────┼─────────────────────────────┤
 │ 쿼리 취소 실패     │ pg_terminate_backend() 폴백 │
 ├────────────────────┼─────────────────────────────┤
 │ 모니터링 연결 실패 │ 경고 로그 후 쿼리 계속 진행 │
 ├────────────────────┼─────────────────────────────┤
 │ 부분 생성 테이블   │ DROP TABLE IF EXISTS로 정리 │
 ├────────────────────┼─────────────────────────────┤
 │ DB 자원 부족       │ 최소 임계값(2개)으로 보호   │
 └────────────────────┴─────────────────────────────┘
 ---
 구현 순서

 1. ParallelQueryMapper.java 인터페이스 생성
 2. ParallelQueryMapper.xml SQL 매퍼 생성
 3. ParallelQueryExecutor.java 유틸리티 클래스 구현
 4. WlessMktQatCplyPerpCpntCalsumMmPartiJobConfiguration.java 수정
 5. 컴파일 및 테스트











---------


  ---
  ✅ 구현 완료 요약

  📁 신규 생성 파일 (3개)

  1. src/main/java/com/kt/sqms/batch/mapper/ParallelQueryMapper.java
    - PostgreSQL 병렬 워커 모니터링 Mapper 인터페이스
    - 주요 메서드: selectCurrentPid(), selectParallelWorkerCount(), cancelQuery(), terminateSession()
  2. src/main/resources/mapper/myBatis/ParallelQueryMapper.xml
    - PostgreSQL 모니터링 SQL 쿼리 정의
    - pg_backend_pid(), pg_stat_activity, pg_cancel_backend() 등
  3. src/main/java/com/kt/sqms/job/util/ParallelQueryExecutor.java
    - 병렬 쿼리 재시도 로직의 핵심 유틸리티 클래스 (약 280줄)
    - 쿼리 실행/모니터링 스레드 분리, 자동 재시도, 워커 수 보장

  📝 수정 파일 (1개)

  4. src/main/java/com/kt/sqms/batch/job/wless/calsum/WlessMktQatCplyPerpCpntCalsumMmPartiJobConfiguration.java
    - 라인 70-71: ParallelQueryExecutor 의존성 주입
    - 라인 249-272: PreStep 메서드 수정 (약 27줄)
    - 기존 단순 호출을 executeWithParallelGuarantee() 래핑

  📚 문서 파일 (3개)

  5. MODIFY_HISTORY.md (업데이트)
    - 변경 이력 기록 (2026-01-12 18:24:02 KST)
  6. docs/PARALLEL_QUERY_IMPLEMENTATION_GUIDE.md (신규)
    - 상세 구현 가이드 (아키텍처, 사용법, 시나리오, 트러블슈팅)
  7. docs/PARALLEL_QUERY_QUICK_REFERENCE.md (신규)
    - 빠른 참조 가이드 (핵심 설정, 사용법, 로그 패턴)

  ---
  🎯 핵심 기능

  동작 방식

  시도 1~5:
    ├─ 쿼리 실행 (별도 스레드)
    ├─ 1초 대기 후 워커 수 모니터링 (0.5초 간격)
    ├─ 워커 ≥ 4개 → 성공
    └─ 워커 < 4개 → pg_cancel_backend() → DROP TABLE → 2초 대기 → 재시도

  최종:
    ├─ 최대값 ≥ 2개 → 해당 값으로 진행
    └─ 최대값 < 2개 → 실패

  설정값
  ┌─────────────┬─────┐
  │    항목     │ 값  │
  ├─────────────┼─────┤
  │ 목표 워커   │ 4개 │
  ├─────────────┼─────┤
  │ 최소 워커   │ 2개 │
  ├─────────────┼─────┤
  │ 최대 재시도 │ 5회 │
  ├─────────────┼─────┤
  │ 재시도 대기 │ 2초 │
  └─────────────┴─────┘
  ---
  💡 사용 예시

  // 기존 코드 (3줄)
  wlessMapper.configureParallelSettings();
  int fullCount = wlessMapper.createTmpWlessMktQatCplyPerpTgtList(jobMap);
  log.info("PreStep ::: fullCount={}", fullCount);

  // 변경 후 (약 20줄)
  wlessMapper.configureParallelSettings();

  ParallelQueryResult<Integer> result = parallelQueryExecutor.executeWithParallelGuarantee(
      () -> wlessMapper.createTmpWlessMktQatCplyPerpTgtList(jobMap),
      () -> wlessMapper.dropTmpWlessMktQatCplyPerpTgtList(),
      4, 2, 5
  );

  if (result == null) {
      contribution.setExitStatus(ExitStatus.FAILED);
      return RepeatStatus.FINISHED;
  }

  int fullCount = result.getResult();
  log.info("fullCount={}, 워커={}, 시도={}",
      fullCount, result.getAchievedWorkers(), result.getAttemptCount());

  ---
  🔍 검증 방법

  # 1. 컴파일 확인
  mvn clean compile

  # 2. 패키징
  mvn clean package

  # 3. 로그 확인
  # application.yaml에서 logging.level.com.kt.sqms.job.util=DEBUG 설정
  # 배치 실행 후 다음 로그 패턴 확인:
  # [ParallelQuery] 시도 1/5 시작, 목표 워커: 4
  # [ParallelQuery] 모니터링: 현재 워커 수 = 4
  # [ParallelQuery] 목표 달성! 워커 수: 4

  ---
  📊 예상 효과

  1. 성능 일관성: 병렬 워커 4개 보장으로 쿼리 실행 시간 안정화
  2. 자동 복구: DB 자원 일시 부족 시에도 재시도로 자동 복구
  3. 안전성: 최소 임계값(2개) 미달 시 명확한 실패 처리
  4. 가시성: 상세한 로그로 병렬 워커 할당 과정 추적 가능

  ---
  ★ 인사이트 ─────────────────────────────────────
  1. 병렬 처리 불확실성 해결: PostgreSQL의 병렬 워커 할당이 확정적이지 않은 문제를 런타임 모니터링과 재시도로 해결
  2. 최소 수정 원칙: 기존 코드 구조를 최대한 유지하면서 Wrapper 패턴으로 기능 확장 (약 30줄 수정)
  3. 관심사 분리: 재시도 로직을 별도 유틸리티로 분리하여 다른 Job에도 재사용 가능
  ─────────────────────────────────────────────────

  모든 파일이 생성되고 문서화가 완료되었습니다! 🎉
