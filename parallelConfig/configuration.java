import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

@Autowired
private com.kt.sqms.job.util.ParallelQueryExecutor parallelQueryExecutor;



                        wlessMapper.configureParallelSettings();

						log.info("PreStep ::: 전처리 임시테이블 생성 (병렬 보장 모드)");

// 병렬 워커 보장 실행
com.kt.sqms.job.util.ParallelQueryExecutor.ParallelQueryResult<Integer> parallelResult =
        parallelQueryExecutor.executeWithParallelGuarantee(
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
