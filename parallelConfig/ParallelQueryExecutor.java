package com.kt.sqms.job.util;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kt.sqms.batch.mapper.ParallelQueryMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * PostgreSQL 병렬 쿼리 실행기
 *
 * 목적:
 * - 병렬 워커 수 보장을 위한 재시도 로직 제공
 * - 목표 워커 수 미달 시 쿼리 취소 후 재시도
 * - 최소 임계값 이상의 워커 수로 실행 보장
 *
 * 동작 방식:
 * 1. 별도 스레드에서 쿼리 실행
 * 2. 모니터링 스레드에서 병렬 워커 수 체크
 * 3. 목표 미달 시 pg_cancel_backend() 호출 후 재시도
 * 4. 최대 재시도 후 최대 달성값으로 진행 (최소 임계값 이상)
 *
 * @author Claude Code
 * @since 2026-01-12
 */
@Slf4j
@Component
public class ParallelQueryExecutor {

    @Autowired
    private ParallelQueryMapper parallelQueryMapper;

    // 기본 설정값
    private static final long DEFAULT_INITIAL_WAIT_MS = 1000;      // 쿼리 시작 후 첫 모니터링까지 대기
    private static final long DEFAULT_MONITORING_INTERVAL_MS = 500; // 모니터링 간격
    private static final long DEFAULT_RETRY_DELAY_MS = 2000;       // 재시도 간 대기

    /**
     * 병렬 쿼리 실행 결과
     */
    public static class ParallelQueryResult<T> {
        private final T result;              // 쿼리 실행 결과
        private final int achievedWorkers;   // 달성한 병렬 워커 수
        private final int attemptCount;      // 시도 횟수
        private final boolean targetMet;     // 목표 워커 수 달성 여부

        public ParallelQueryResult(T result, int achievedWorkers, int attemptCount, boolean targetMet) {
            this.result = result;
            this.achievedWorkers = achievedWorkers;
            this.attemptCount = attemptCount;
            this.targetMet = targetMet;
        }

        public T getResult() { return result; }
        public int getAchievedWorkers() { return achievedWorkers; }
        public int getAttemptCount() { return attemptCount; }
        public boolean isTargetMet() { return targetMet; }
    }

    /**
     * 실행 상태 관리
     */
    private static class ExecutionState {
        final AtomicInteger currentAttempt = new AtomicInteger(0);
        final AtomicInteger maxWorkersAchieved = new AtomicInteger(0);
        final AtomicInteger currentWorkers = new AtomicInteger(0);
        volatile boolean queryCompleted = false;
        volatile boolean cancelled = false;
        volatile Integer queryPid = null;
    }

    /**
     * 병렬 쿼리 실행 (목표 워커 수 보장)
     *
     * @param querySupplier 실행할 쿼리 (Mapper 메서드 호출)
     * @param cleanupAction 실패 시 정리 작업 (예: DROP TABLE)
     * @param targetWorkers 목표 병렬 워커 수
     * @param minWorkers 최소 워커 수 (임계값)
     * @param maxRetries 최대 재시도 횟수
     * @return 쿼리 실행 결과와 메타정보
     */
    public <T> ParallelQueryResult<T> executeWithParallelGuarantee(
            Supplier<T> querySupplier,
            Runnable cleanupAction,
            int targetWorkers,
            int minWorkers,
            int maxRetries) {

        ExecutionState state = new ExecutionState();
        T lastResult = null;
        int lastAchievedWorkers = 0;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            state.currentAttempt.set(attempt);
            state.queryCompleted = false;
            state.cancelled = false;
            state.queryPid = null;

            log.info("[ParallelQuery] 시도 {}/{} 시작, 목표 워커: {}, 최소 워커: {}, 현재 최대 달성: {}",
                    attempt, maxRetries, targetWorkers, minWorkers, state.maxWorkersAchieved.get());

            try {
                // 쿼리 실행 결과를 저장할 Future
                CompletableFuture<T> queryFuture = CompletableFuture.supplyAsync(() -> {
                    // 쿼리 시작 전 현재 PID 조회
                    state.queryPid = parallelQueryMapper.selectCurrentPid();
                    log.info("[ParallelQuery] 쿼리 실행 중... PID: {}", state.queryPid);

                    // 실제 쿼리 실행
                    T result = querySupplier.get();
                    state.queryCompleted = true;
                    return result;
                });

                // 초기 대기 (쿼리가 병렬 워커를 생성할 시간 부여)
                Thread.sleep(DEFAULT_INITIAL_WAIT_MS);

                // 병렬 워커 모니터링
                int monitoredWorkers = monitorParallelWorkers(state, targetWorkers, queryFuture);

                // 쿼리가 완료되었는지 확인
                if (state.queryCompleted) {
                    lastResult = queryFuture.get(5, TimeUnit.SECONDS);
                    lastAchievedWorkers = monitoredWorkers;

                    // 목표 달성 또는 최대 달성값 재달성
                    if (monitoredWorkers >= targetWorkers) {
                        log.info("[ParallelQuery] 목표 달성! 워커 수: {}, 시도: {}/{}",
                                monitoredWorkers, attempt, maxRetries);
                        return new ParallelQueryResult<>(lastResult, monitoredWorkers, attempt, true);
                    } else if (monitoredWorkers >= state.maxWorkersAchieved.get() &&
                               monitoredWorkers >= minWorkers) {
                        log.info("[ParallelQuery] 최대 달성값 재달성: {}, 시도: {}/{}",
                                monitoredWorkers, attempt, maxRetries);
                        return new ParallelQueryResult<>(lastResult, monitoredWorkers, attempt, false);
                    }
                } else {
                    // 쿼리가 아직 실행 중이지만 목표 미달 - 취소
                    log.warn("[ParallelQuery] 목표 미달 ({}/{}), 쿼리 취소 및 재시도",
                            monitoredWorkers, targetWorkers);

                    cancelQueryExecution(state, queryFuture);

                    // 정리 작업 실행
                    if (cleanupAction != null) {
                        log.info("[ParallelQuery] 테이블 정리 실행");
                        cleanupAction.run();
                    }
                }

                // 재시도 전 대기
                if (attempt < maxRetries) {
                    log.info("[ParallelQuery] {}ms 대기 후 재시도...", DEFAULT_RETRY_DELAY_MS);
                    Thread.sleep(DEFAULT_RETRY_DELAY_MS);
                }

            } catch (InterruptedException e) {
                log.error("[ParallelQuery] 스레드 인터럽트 발생", e);
                Thread.currentThread().interrupt();
                break;
            } catch (ExecutionException e) {
                log.error("[ParallelQuery] 쿼리 실행 중 예외 발생", e.getCause());
                throw new RuntimeException("병렬 쿼리 실행 실패", e.getCause());
            } catch (TimeoutException e) {
                log.error("[ParallelQuery] 쿼리 실행 타임아웃", e);
                throw new RuntimeException("병렬 쿼리 타임아웃", e);
            } catch (Exception e) {
                log.error("[ParallelQuery] 예상치 못한 오류", e);
                throw new RuntimeException("병렬 쿼리 실행 오류", e);
            }
        }

        // 최대 재시도 도달
        log.warn("[ParallelQuery] 최대 재시도 도달 ({}/{}), 최대 달성 워커: {}",
                maxRetries, maxRetries, state.maxWorkersAchieved.get());

        // 최소 임계값 체크
        if (state.maxWorkersAchieved.get() >= minWorkers && lastResult != null) {
            log.info("[ParallelQuery] 최소 임계값 충족 ({} >= {}), 마지막 결과 반환",
                    state.maxWorkersAchieved.get(), minWorkers);
            return new ParallelQueryResult<>(lastResult, lastAchievedWorkers, maxRetries, false);
        } else {
            log.error("[ParallelQuery] 최소 임계값 미달 ({} < {}), 실패 처리",
                    state.maxWorkersAchieved.get(), minWorkers);
            return null;
        }
    }

    /**
     * 병렬 워커 수 모니터링
     *
     * @param state 실행 상태
     * @param targetWorkers 목표 워커 수
     * @param queryFuture 쿼리 실행 Future
     * @return 모니터링 중 관측된 최대 워커 수
     */
    private int monitorParallelWorkers(ExecutionState state, int targetWorkers,
                                       CompletableFuture<?> queryFuture) {
        int maxObserved = 0;

        try {
            // 쿼리가 완료되거나 목표 달성까지 모니터링
            while (!state.queryCompleted && !state.cancelled) {
                if (state.queryPid != null) {
                    int workerCount = parallelQueryMapper.selectParallelWorkerCount(state.queryPid);
                    state.currentWorkers.set(workerCount);

                    if (workerCount > maxObserved) {
                        maxObserved = workerCount;
                        if (workerCount > state.maxWorkersAchieved.get()) {
                            state.maxWorkersAchieved.set(workerCount);
                        }
                    }

                    log.debug("[ParallelQuery] 모니터링: 현재 워커 수 = {}", workerCount);

                    // 목표 달성 시 모니터링 종료
                    if (workerCount >= targetWorkers) {
                        log.info("[ParallelQuery] 목표 워커 수 달성: {}/{}", workerCount, targetWorkers);
                        break;
                    }

                    // 쿼리가 완료되었는지 체크
                    if (queryFuture.isDone()) {
                        state.queryCompleted = true;
                        break;
                    }
                }

                Thread.sleep(DEFAULT_MONITORING_INTERVAL_MS);
            }
        } catch (InterruptedException e) {
            log.warn("[ParallelQuery] 모니터링 인터럽트", e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("[ParallelQuery] 모니터링 중 오류", e);
        }

        return maxObserved;
    }

    /**
     * 쿼리 실행 취소
     *
     * @param state 실행 상태
     * @param queryFuture 쿼리 실행 Future
     */
    private void cancelQueryExecution(ExecutionState state, CompletableFuture<?> queryFuture) {
        if (state.queryPid != null && !state.cancelled) {
            try {
                // pg_cancel_backend() 호출
                boolean cancelResult = parallelQueryMapper.cancelQuery(state.queryPid);
                state.cancelled = true;
                log.info("[ParallelQuery] 쿼리 취소 완료 (PID: {}, 결과: {})", state.queryPid, cancelResult);

                // Future도 취소
                queryFuture.cancel(true);

                // 취소 후 잠시 대기 (DB 자원 정리 시간 부여)
                Thread.sleep(500);

            } catch (Exception e) {
                log.error("[ParallelQuery] 쿼리 취소 중 오류 (PID: {})", state.queryPid, e);

                // 폴백: 세션 강제 종료 시도
                try {
                    boolean terminateResult = parallelQueryMapper.terminateSession(state.queryPid);
                    log.warn("[ParallelQuery] 세션 강제 종료 (PID: {}, 결과: {})",
                            state.queryPid, terminateResult);
                } catch (Exception ex) {
                    log.error("[ParallelQuery] 세션 강제 종료 실패 (PID: {})", state.queryPid, ex);
                }
            }
        }
    }
}
