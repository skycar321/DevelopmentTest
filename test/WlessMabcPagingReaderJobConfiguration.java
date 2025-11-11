package com.abc.batch.job.test;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.mybatis.spring.batch.builder.MyBatisPagingItemReaderBuilder;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
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
import com.abc.batch.domain.PpWlessabcTxnItem;
import com.abc.batch.job.wless.WlessMabcQatCplyPerpRangePartitioner;
import com.abc.batch.job.wless.main.WlessMabcQatCplyPerpItemManager;
import com.abc.batch.mapper.WlessPartiMapper;
import com.abc.job.util.BatchInsertDao;
import com.abc.job.util.JobBuilderFactory;
import com.abc.job.util.StepBuilderFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * B 방법: PagingReader + Writer 구현
 *
 * 특징:
 * - MyBatisPagingItemReader로 1000건씩 페이징
 * - Processor는 pass-through (데이터만 전달)
 * - Writer에서 병렬 5개로 API 호출
 * - Spring Batch 표준 패턴 (재시작, 메트릭 지원)
 *
 * @author Claude Code
 * @since 2025-11-11
 * @version 1.0.0
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class WlessMabcPagingReaderJobConfiguration {

	private final JobBuilderFactory jobs;
	private final StepBuilderFactory steps;

	@Autowired
	private SqlSessionFactory sqlSessionFactory;

	@Autowired
	private WlessPartiMapper wlessMapper;

	@Autowired
	private BatchInsertDao batchInsertDao;

	private static final String job_name = "wlessMabcPagingReaderJob";
	private static final int chunk_size = 1000;

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

		log.info(" === [B 방법 - PagingReader + Writer] job_name ::: [{}] start === ", job_name);
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
	 * PreStep - 전처리 (A 방법과 동일)
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

					BatchUtil.insertBatchWrkHst(jobMap.get("batchId"), jobMap.get("param1"), 0, "S", jobMap.get("batchId")+" 시작 [B방법-PagingReader+Writer]");

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
	 * VacuumStep
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
		executor.setThreadNamePrefix("paging-reader-thread-");
		executor.setWaitForTasksToCompleteOnShutdown(Boolean.TRUE);
		executor.initialize();
		return executor;
	}

	/**
	 * Slave Step - Chunk 기반 처리 (Reader → Processor → Writer)
	 *
	 * 핵심:
	 * - MyBatisPagingItemReader로 1000건씩 페이징
	 * - Processor는 pass-through
	 * - Writer에서 병렬 5개 처리
	 */
	@Bean(name = job_name+"Slave")
	public Step Slave() throws Exception {

		log.info(" =============== called Slave (PagingReader + Writer) ================ ");

		return steps.get(job_name+"Slave")
				.<PpWlessabcTxnItem, PpWlessabcTxnItem>chunk(chunk_size)
				.reader(SlaveReader(null, null, null))
				.processor(SlaveProcessor(null))
				.writer(SlaveWriter(null, null))
				.build();
	}

	/**
	 * SlaveReader - MyBatisPagingItemReader
	 *
	 * ⚡ 페이징 방식:
	 * - pageSize = 1000 (한 번에 1000건 조회)
	 * - 내부적으로 LIMIT/OFFSET 사용
	 * - 메모리 효율적 (1000건씩만 로드)
	 */
	@Bean(name = job_name+"SlaveReader")
	@StepScope
	public MyBatisPagingItemReader<PpWlessabcTxnItem> SlaveReader(
			@Value("#{stepExecutionContext[threadNo]}") Integer threadNo,
			@Value("#{stepExecutionContext[partitionGbn]}") String partitionGbn,
			@Value("#{stepExecutionContext[paramSetMap]}") Map<String, String> paramSetMap) throws Exception {

		log.info(" ==== called SlaveReader_TBL_"+tableNumber+" pool_size="+pool_size+" threadNo="+threadNo+" partitionGbn="+partitionGbn+" ==== ");

		srchMap = paramSetMap;

		srchMap.put("jobName", job_name);

		Map<String, Object> params = new HashMap<>();
		params.put("param1", srchMap.get("param1"));
		params.put("chkScopeVal", srchMap.get("chkScopeVal"));
		params.put("threadNo", threadNo);
		params.put("pool_size", pool_size);
		params.put("tableNumber", tableNumber);

		// MyBatisPagingItemReader 빌더로 생성
		return new MyBatisPagingItemReaderBuilder<PpWlessabcTxnItem>()
				.sqlSessionFactory(sqlSessionFactory)
				.queryId("com.abc.batch.mapper.WlessPartiMapper.selectWlessMabcQatCplyPerpTgtList")
				.parameterValues(params)
				.pageSize(chunk_size)  // ⚡ 1000건씩 페이징
				.build();
	}

	/**
	 * SlaveProcessor - Pass-through
	 *
	 * ⚠️ Anti-pattern: Processor가 의미 없음
	 * - 단순히 데이터를 그대로 전달
	 * - 실제 비즈니스 로직은 Writer에서 처리
	 */
	@Bean(name = job_name+"SlaveProcessor")
	@StepScope
	public ItemProcessor<PpWlessabcTxnItem, PpWlessabcTxnItem> SlaveProcessor(
			@Value("#{stepExecutionContext[partitionGbn]}") String partitionGbn) throws Exception {

		log.info(" ==== called SlaveProcessor_TBL_"+tableNumber+" partitionGbn="+partitionGbn+"  ==== ");

		// Pass-through: 데이터를 그대로 반환
		return item -> item;
	}

	/**
	 * SlaveWriter - 병렬 처리 Writer
	 *
	 * ⚡ 핵심 로직:
	 * - chunk_size(1000)건을 받음
	 * - 1000건을 5개로 분할 (200건 × 5)
	 * - ExecutorService(5)로 병렬 API 호출
	 * - 결과 일괄 저장
	 */
	@Bean(name = job_name+"SlaveWriter")
	@StepScope
	public ItemWriter<PpWlessabcTxnItem> SlaveWriter(
			@Value("#{stepExecutionContext[partitionGbn]}") String partitionGbn,
			@Value("#{stepExecutionContext[paramSetMap]}") Map<String, String> paramSetMap) throws Exception {

		log.info(" ==== called SlaveWriter_TBL_"+tableNumber+" partitionGbn="+partitionGbn+"  ==== ");

		// WlessMabcBatchWriter 인스턴스 생성
		WlessMabcBatchWriter writer = new WlessMabcBatchWriter(
				batchInsertDao,
				wlessMapper,
				sqlSessionFactory
		);
		writer.setPartitionGbn(partitionGbn);
		writer.setRecvMap(srchMap);

		return writer;
	}

	/**
	 * BrmsInsertStep
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
	 * NotCompletedStep
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
	 * AfterStep
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
					smsParams.put("jobNameKR", "무선abc 일별 [B방법-PagingReader+Writer]");
					smsParams.put("param1", param1);

					if("Fail".equals(execRst)) {
						BatchUtil.insertBatchWrkHst(batchId, param1, 0, "E", "기준일자(작업월) 또는 재수행여부 확인 필요");
						smsParams.put("successYN", "N");
					} else {
						BatchUtil.insertBatchWrkHst(batchId, param1, rstCnt, "F", batchId+" 종료 [B방법-PagingReader+Writer]");
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
					log.info("[B방법-PagingReader+Writer] 등록 건 수 = {}", rstCnt);
					log.info("=====================================================");

					return RepeatStatus.FINISHED;
				})
				.build();
	}
}
