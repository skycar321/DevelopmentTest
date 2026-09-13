package com.abc.batch.job.test;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;

import com.abc.batch.BatchHelper;
import com.abc.batch.BatchUtil;
import com.abc.batch.job.wless.WlessMabcQatCplyPerpRangePartitioner;
import com.abc.batch.mapper.WlessPartiMapper;
import com.abc.batch.job.wless.main.WlessMabcQatCplyPerpItemManager;
import com.abc.job.util.JobBuilderFactory;
import com.abc.job.util.StepBuilderFactory;
import com.abc.job.util.BatchInsertDao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * A 방법: Cursor + Tasklet 구현
 *
 * 특징:
 * - MyBatis Cursor 스트리밍으로 메모리 효율 최적화
 * - 1000건씩 배치 단위 처리
 * - 병렬 5개로 API 호출
 * - 단일 Tasklet에 로직 집중
 *
 * @author Claude Code
 * @since 2025-11-11
 * @version 1.0.0
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class WlessMabcCursorTaskletJobConfiguration {

	private final JobBuilderFactory jobs;
	private final StepBuilderFactory steps;

	@Autowired
	private SqlSessionFactory sqlSessionFactory;

	@Autowired
	private WlessPartiMapper wlessMapper;

	@Autowired
	private BatchInsertDao batchInsertDao;

	private static final String job_name = "wlessMabcCursorTaskletJob";

	@Value("${batch-job-thread-count}")
	private int pool_size;

	private String cronExprs = "";
	private Map<String, String> srchMap = null;

	private String execRst = "";

	private int tableNumber = 0;
	private int tableCount = 20;
	private int skipCount = 200000;

	private Map<String, Object> params = new HashMap<>();

	@Autowired
	WlessMabcQatCplyPerpItemManager itemManager;

	@Value("${spring.datasource.first.jdbc-url}")
	private String url;

	@Value("${spring.datasource.first.username}")
	private String usr;

	@Value("${spring.datasource.first.password}")
	private String pw;

	private ArrayList<String> vacuumTableList = new ArrayList<String>();

	/**
	 * Trigger
	 */
	@Bean(name = job_name+"Trigger")
	public CronTriggerFactoryBean Trigger() throws Exception {

		log.info(" === [A 방법 - Cursor + Tasklet] job_name ::: [{}] start === ", job_name);
		log.info("pool_size={}", pool_size);

		cronExprs = BatchUtil.getCronExprsVal(job_name);

		log.info("cronExprs={}", cronExprs);

		return BatchHelper.cronTriggerFactoryBeanBuilder()
				.cronExpression(cronExprs)
				.jobDetailFactoryBean(Schedule())
				.build();
	}

	/**
	 * Schedule
	 */
	@Bean(name = job_name+"Schedule")
	public JobDetailFactoryBean Schedule() throws Exception {
		return BatchHelper.jobDetailFactoryBeanBuilder()
				.job(Job())
				.build();
	}

	/**
	 * Job
	 *
	 * 플로우:
	 *   PreStep → vacuumStep → StepManager → BrmsInsertStep → AfterStep
	 *   PreStep 실패 → NotCompletedStep → AfterStep
	 */
	@Bean(name = job_name)
	public Job Job() throws Exception {

		return jobs.get(job_name)
				.preventRestart()
				.start(PreStep()).on("COMPLETED").to(vacuumStep()).on("*").to(StepManager())
				.from(PreStep()).on("*").to(NotCompletedStep()).on("*").to(AfterStep()).on("*").end()
					.from(StepManager()).on("COMPLETED").to(BrmsInsertStep()).on("*").to(AfterStep())
					.from(StepManager()).on("*").to(NotCompletedStep()).on("*").to(AfterStep())
				.end()
				.build();
	}

	/**
	 * PreStep - 전처리
	 *
	 * 작업:
	 * 1. 임시테이블 DROP
	 * 2. 기존 데이터 삭제
	 * 3. 전처리 타겟 임시테이블 생성
	 * 4. 20만건씩 분할 테이블 생성
	 */
	@Bean(name = job_name+"PreStep")
	public Step PreStep() throws Exception {

		return steps.get(job_name+"PreStep")
				.tasklet((contribution, chunkContext) -> {

					vacuumTableList.clear();

					log.info("PreStep ::: 전처리 임시테이블 DROP");
					wlessMapper.dropTmpWlessMabcQatCplyPerpTgtList();

					log.info("PreStep ::: 접점합산 추가 집계 임시테이블 DROP");
					wlessMapper.dropTmpPpabcCpntSumTxnGroup();

					log.info("PreStep ::: 분할 임시테이블 DROP");
					for (int i = 0; i < tableCount; i++) {
						tableNumber = i;
						params.put("tableNumber", tableNumber);
						wlessMapper.dropTmpPpabcPartition(params);
						wlessMapper.dropTmpPpabcJoinCalsumPartition(params);
					}

					tableNumber = 0;
					tableCount = 0;

					itemManager.itemMap = new HashMap<>();

					Map<String, String> jobMap = BatchUtil.getInitSet(job_name);
					srchMap = jobMap;

					log.info("PreStep ::: param1={}", jobMap.get("param1"));
					log.info("PreStep ::: batchId={}", jobMap.get("batchId"));
					log.info("PreStep ::: chkScopeVal={}", jobMap.get("chkScopeVal"));

					String param1 = jobMap.get("param1");
					String rexePosblYn = jobMap.get("rexePosblYn");

					BatchUtil.insertBatchWrkHst(jobMap.get("batchId"), jobMap.get("param1"), 0, "S", jobMap.get("batchId")+" 시작 [A방법-Cursor+Tasklet]");

					if(!"".equals(param1) && "N".equals(rexePosblYn)) {
						execRst = "Fail";
						contribution.setExitStatus(ExitStatus.FAILED);
					} else {
						execRst = "";
						BatchUtil.updateJobExecReslt(job_name, jobMap.get("batchId"), "Processing");

						wlessMapper.deleteWlessMabcQatCplyPerp(jobMap);

						wlessMapper.dropTmpRuleWlessChkReslt();
						wlessMapper.createTmpRuleWlessChkReslt();

						log.info("PreStep ::: 접점합산 추가집계 임시테이블 생성");
						int groupCount = wlessMapper.createTmpPpabcCpntSumTxnGroup(jobMap);
						log.info("PreStep ::: groupCount={}", groupCount);

						log.info("PreStep ::: 전처리 타겟 임시테이블 생성");
						int fullCount = wlessMapper.createTmpWlessMabcQatCplyPerpTgtList(jobMap);
						log.info("PreStep ::: fullCount={}", fullCount);

						vacuumTableList.add("abcBAT.TMP_PP_WLESS_abc_TXN_RULE_TGT");

						if(fullCount == 0) {
							log.info("PreStep ::: 타겟 데이터 0건");
							execRst = "Fail";
							contribution.setExitStatus(ExitStatus.FAILED);
						}

						tableCount = fullCount / skipCount;

						if(fullCount > tableCount * skipCount) {
							tableCount += 1;
						}

						log.info("PreStep ::: tableCount={}", tableCount);
						log.info("PreStep ::: skipCount={}", skipCount);

						jobMap.put("skipCount", String.valueOf(skipCount));

						for (int i = 0; i < tableCount; i++) {
							tableNumber = i;
							jobMap.put("tableNumber", String.valueOf(tableNumber));

							log.info("PreStep ::: create partition tableNumber={}", jobMap.get("tableNumber"));

							wlessMapper.createTmpPpabcPartition(jobMap);
							wlessMapper.createTmpPpabcJoinCalsum(jobMap);

							vacuumTableList.add("abcBAT.TMP_PP_WLESS_abc_TXN_JOIN_CALSUM_"+i);
						}
						tableNumber = 0;
					}

					return RepeatStatus.FINISHED;
				})
				.build();
	}

	/**
	 * VacuumStep - 임시테이블 vacuum 처리
	 */
	@Bean(name = job_name+"VacuumStep")
	public Step vacuumStep() {
		return steps.get(job_name+"VacuumStep")
				.tasklet((contribution, chunkContext) -> {

					log.info("VacuumStep ::: vacuumTableList={}", vacuumTableList);

					BatchUtil.vacuumTable(url, usr, pw, vacuumTableList);

					vacuumTableList.clear();
					log.info("VacuumStep ::: vacuumTableList 클리어={}", vacuumTableList);

					tableNumber = 0;

					return RepeatStatus.FINISHED;
				})
				.build();
	}

	/**
	 * StepManager - Partitioning 관리
	 *
	 * pool_size 만큼 파티션을 생성하여 병렬 처리
	 */
	@Bean(name = job_name+"StepManager")
	public Step StepManager() throws Exception {
		return steps.get(job_name+"StepManager")
				.partitioner(Slave().getName(), Partitioner())
				.partitionHandler(PartitionHandler())
				.build();
	}

	/**
	 * Partitioner
	 */
	@Bean(name = job_name+"Partitioner")
	public WlessMabcQatCplyPerpRangePartitioner Partitioner() {
		return new WlessMabcQatCplyPerpRangePartitioner(job_name, pool_size);
	}

	/**
	 * PartitionHandler
	 */
	@Bean(name = job_name+"PartitionHandler")
	public TaskExecutorPartitionHandler PartitionHandler() throws Exception {
		TaskExecutorPartitionHandler partitionHandler = new TaskExecutorPartitionHandler();
		partitionHandler.setStep(Slave());
		partitionHandler.setTaskExecutor(executor());
		partitionHandler.setGridSize(pool_size);

		try {
			partitionHandler.afterPropertiesSet();
		} catch(Exception e) {
			e.printStackTrace();
		}

		return partitionHandler;
	}

	/**
	 * TaskExecutor
	 */
	@Bean(name = job_name+"TaskPool")
	public TaskExecutor executor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(pool_size);
		executor.setMaxPoolSize(pool_size);
		executor.setThreadNamePrefix("cursor-tasklet-thread-");
		executor.setWaitForTasksToCompleteOnShutdown(Boolean.TRUE);
		executor.initialize();
		return executor;
	}

	/**
	 * Slave Step - 실제 데이터 처리 (Tasklet 방식)
	 *
	 * 핵심:
	 * - MyBatis Cursor로 스트리밍
	 * - 1000건씩 배치 처리
	 * - 병렬 5개로 API 호출
	 */
	@Bean(name = job_name+"Slave")
	public Step Slave() throws Exception {

		log.info(" =============== called Slave (Cursor Tasklet) ================ ");

		return steps.get(job_name+"Slave")
				.tasklet(new WlessMabcCursorTasklet(
						sqlSessionFactory,
						batchInsertDao,
						wlessMapper,
						tableNumber
				))
				.build();
	}

	/**
	 * BrmsInsertStep - 룰 결과 본 테이블에 인서트
	 */
	@Bean(name = job_name+"BrmsInsertStep")
	public Step BrmsInsertStep() {
		return steps.get(job_name+"BrmsInsertStep")
				.tasklet((contribution, chunkContext) -> {

					log.info("BrmsInsertStep ::: srchMap={}", srchMap);

					log.info("BrmsInsertStep ::: {}", "Brms 무선 룰 결과 임시테이블에서 본테이블로 인서트");
					wlessMapper.insertRuleWlessChkReslt(srchMap);

					log.info("BrmsInsertStep ::: {}", "Brms 무선 룰 결과 임시테이블 드랍");
					wlessMapper.dropTmpRuleWlessChkReslt();

					return RepeatStatus.FINISHED;
				})
				.build();
	}

	/**
	 * NotCompletedStep - 실패 시 상태 업데이트
	 */
	@Bean(name = job_name+"NotCompletedStep")
	public Step NotCompletedStep() {
		return steps.get(job_name+"NotCompletedStep")
				.tasklet((contribution, chunkContext) -> {

					log.info("NotCompletedStep ::: 상태 값 업데이트 execRst={}", execRst);
					execRst = "Fail";
					log.info("NotCompletedStep ::: execRst={}", execRst);

					return RepeatStatus.FINISHED;
				})
				.build();
	}

	/**
	 * AfterStep - 후처리
	 *
	 * 작업:
	 * 1. 결과 건수 조회
	 * 2. 배치 실행 결과 업데이트
	 * 3. 작업 이력 생성
	 * 4. SMS 발송 (실패 시)
	 */
	@Bean(name = job_name+"AfterStep")
	public Step AfterStep() {
		return steps.get(job_name+"AfterStep")
				.tasklet((contribution, chunkContext) -> {

					log.info("AfterStep ::: batchId={}", srchMap.get("batchId"));
					log.info("AfterStep ::: param1={}", srchMap.get("param1"));
					log.info("AfterStep ::: chkScopeVal={}", srchMap.get("chkScopeVal"));
					log.info("AfterStep ::: rexePosblYn={}", srchMap.get("rexePosblYn"));

					String batchId = srchMap.get("batchId");
					String param1 = srchMap.get("param1");
					String chkScopeVal = srchMap.get("chkScopeVal");
					String rexePosblYn = srchMap.get("rexePosblYn");
					String updRexePosblYn = "";
					int rstCnt = 0;
					String rstCntComma = "";

					if(!"Fail".equals(execRst)) {
						if(!"".equals(param1) && "N".equals(rexePosblYn)) {
							execRst = "Fail";
							updRexePosblYn = rexePosblYn;
						} else {
							execRst = "Success";

							Map<String, String> params = new HashMap<>();
							params.put("param1", param1);
							params.put("batchId", batchId);
							params.put("chkScopeVal", chkScopeVal);
							rstCnt = wlessMapper.selectWlessMabcQatCplyPerpCnt(params);
						}

						BatchUtil.updateRexePosblYn(job_name, batchId, updRexePosblYn);
					}

					BatchUtil.updateJobParam(job_name, batchId, "", "", execRst);

					Map<String, String> smsParams = new HashMap<>();
					DecimalFormat df = new DecimalFormat("###,###,###");
					rstCntComma = df.format(rstCnt);
					smsParams.put("rstCnt", rstCntComma);
					smsParams.put("jobName", job_name);
					smsParams.put("jobNameKR", "무선abc 일별 [A방법-Cursor+Tasklet]");
					smsParams.put("param1", param1);

					if("Fail".equals(execRst)) {
						BatchUtil.insertBatchWrkHst(batchId, param1, 0, "E", "기준일자(작업월) 또는 재수행여부 확인 필요");
						smsParams.put("successYN", "N");
					} else {
						BatchUtil.insertBatchWrkHst(batchId, param1, rstCnt, "F", batchId+" 종료 [A방법-Cursor+Tasklet]");
						if(rstCnt == 0) {
							smsParams.put("successYN", "N");
						} else {
							smsParams.put("successYN", "Y");
						}
					}

					if("N".equals(smsParams.get("successYN"))) {
						log.info("일배치 실패시 SMS발송 진입 : successYN={}", smsParams.get("successYN"));
						wlessMapper.insertWlessMabcQatCplyPerpSms(smsParams);
					}

					if("01".equals(srchMap.get("param1").substring(6)) || "02".equals(srchMap.get("param1").substring(6))) {
						log.info("param1이 1,2일 인 경우 BRMS월배치 재수행여부 업데이트 param1={}, batchExeDt={}",
								srchMap.get("param1").substring(6), srchMap.get("batchExecDt").substring(6));
						wlessMapper.updateRexePosblYn();
					}

					itemManager.itemMap = new HashMap<>();

					log.info("=====================================================");
					log.info("[A방법-Cursor+Tasklet] 등록 건 수 = {}", rstCnt);
					log.info("=====================================================");

					return RepeatStatus.FINISHED;
				})
				.build();
	}
}
