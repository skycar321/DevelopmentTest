package com.abc.batch.job.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.abc.batch.domain.PpWlessabcTxnItem;
import com.abc.batch.domain.RuleWlessChkResltItem;
import com.abc.batch.mapper.WlessPartiMapper;
import com.abc.job.util.BatchInsertDao;
import com.innoexpert.rulesclient.ClusterManager;
import com.innoexpert.rulesclient.Constants;
import com.innoexpert.rulesclient.Item;
import com.innoexpert.rulesclient.ResultSet;
import com.innoexpert.rulesclient.ResultSetMetaData;
import com.innoexpert.rulesclient.RuleInterface;
import com.innoexpert.rulesclient.RuleReq;
import com.innoexpert.rulesclient.RulesException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * A 방법: Cursor + Tasklet 방식 실제 처리 Tasklet
 *
 * 핵심 로직:
 * 1. MyBatis Cursor로 데이터 스트리밍
 * 2. 1000건씩 버퍼링
 * 3. 1000건을 5개로 분할하여 병렬 API 호출
 * 4. 결과 일괄 저장
 * 5. 반복
 *
 * 병렬 구조:
 * - 외부: Partitioner 15개 (이 Tasklet이 15개 파티션에서 실행)
 * - 내부: ExecutorService 5개 (이 Tasklet 내부에서 병렬 처리)
 * - 총: 15 × 5 = 75개 동시 API 호출
 *
 * @author Claude Code
 * @since 2025-11-11
 * @version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class WlessMabcCursorTasklet implements Tasklet {

    private final SqlSessionFactory sqlSessionFactory;
    private final BatchInsertDao batchInsertDao;
    private final WlessPartiMapper wlessMapper;
    private int tableNumber;

    // 배치 크기 (1000건씩 처리)
    private static final int BATCH_SIZE = 1000;

    // 병렬 처리 수 (5개 병렬 API 호출)
    private static final int PARALLEL_COUNT = 5;

    // API 재시도 횟수
    private static final int MAX_RETRIES = 3;

    // API 타임아웃 (분)
    private static final int API_TIMEOUT_MINUTES = 5;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        // StepExecutionContext에서 파티션 정보 추출
        Map<String, Object> params = extractParams(chunkContext);

        log.info("=================================================================");
        log.info("[A방법-Cursor+Tasklet] Partition {} 시작", params.get("threadNo"));
        log.info("[A방법-Cursor+Tasklet] tableNumber={}, pool_size={}",
            params.get("tableNumber"), params.get("pool_size"));
        log.info("=================================================================");

        // MyBatis SqlSession 생성 (SIMPLE executor)
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.SIMPLE);

        try (Cursor<PpWlessabcTxnItem> cursor =
                sqlSession.selectCursor(
                    "com.abc.batch.mapper.WlessPartiMapper.selectWlessMabcQatCplyPerpTgtList",
                    params)) {

            // Cursor 스트리밍 처리
            processCursorInBatches(cursor, params);

            log.info("[A방법-Cursor+Tasklet] Partition {} 완료", params.get("threadNo"));

        } catch (Exception e) {
            log.error("[A방법-Cursor+Tasklet] Partition {} 실패", params.get("threadNo"), e);
            throw e;
        } finally {
            sqlSession.close();
        }

        return RepeatStatus.FINISHED;
    }

    /**
     * Cursor를 1000건씩 배치로 처리
     */
    private void processCursorInBatches(Cursor<PpWlessabcTxnItem> cursor,
                                        Map<String, Object> params) throws Exception {

        java.util.Iterator<PpWlessabcTxnItem> iterator = cursor.iterator();
        List<PpWlessabcTxnItem> batch = new ArrayList<>(BATCH_SIZE);
        int processedCount = 0;
        int batchCount = 0;

        long startTime = System.currentTimeMillis();

        while (iterator.hasNext()) {
            batch.add(iterator.next());

            // 1000건 모이면 처리
            if (batch.size() == BATCH_SIZE) {
                batchCount++;
                processBatch(batch, params, batchCount);
                processedCount += batch.size();

                // 메모리 해제
                batch.clear();

                // 진행률 로깅 (10배치마다)
                if (batchCount % 10 == 0) {
                    long elapsed = (System.currentTimeMillis() - startTime) / 1000;
                    log.info("[Partition {}] 진행 중... 처리: {}건, 배치: {}개, 경과: {}초",
                        params.get("threadNo"), processedCount, batchCount, elapsed);
                }
            }
        }

        // 나머지 배치 처리 (1000건 미만)
        if (!batch.isEmpty()) {
            batchCount++;
            processBatch(batch, params, batchCount);
            processedCount += batch.size();
            batch.clear();
        }

        long totalTime = (System.currentTimeMillis() - startTime) / 1000;

        log.info("=================================================================");
        log.info("[Partition {}] 처리 완료: 총 {}건, {}배치, {}초 소요",
            params.get("threadNo"), processedCount, batchCount, totalTime);
        log.info("=================================================================");
    }

    /**
     * 1000건 배치를 5개로 분할하여 병렬 처리
     *
     * ⚡ 병렬 구조:
     * - 각 파티션 내부에서 5개 쓰레드로 병렬 API 호출
     * - 1000건 → 200건씩 5개로 분할
     * - ExecutorService(5) 사용
     */
    private void processBatch(List<PpWlessabcTxnItem> batch,
                             Map<String, Object> params,
                             int batchNumber) throws Exception {

        long batchStartTime = System.currentTimeMillis();

        log.debug("[Partition {} - Batch {}] 처리 시작: {}건",
            params.get("threadNo"), batchNumber, batch.size());

        // 1000건을 5개로 분할 (각 200건)
        int subBatchSize = (int) Math.ceil((double) batch.size() / PARALLEL_COUNT);

        // ⚡ 병렬 5개로 API 호출
        ExecutorService executor = Executors.newFixedThreadPool(PARALLEL_COUNT);
        List<CompletableFuture<List<RuleWlessChkResltItem>>> futures = new ArrayList<>();

        try {
            for (int i = 0; i < PARALLEL_COUNT; i++) {
                int start = i * subBatchSize;
                int end = Math.min(start + subBatchSize, batch.size());

                if (start >= batch.size()) break;

                // 서브 배치 생성 (200건)
                List<PpWlessabcTxnItem> subBatch = new ArrayList<>(batch.subList(start, end));
                final int partitionIndex = i;

                // 🔥 비동기 API 호출
                futures.add(CompletableFuture.supplyAsync(() -> {
                    long subBatchStart = System.currentTimeMillis();

                    log.debug("[Partition {} - Batch {} - Sub {}] API 호출 시작: {}건",
                        params.get("threadNo"), batchNumber, partitionIndex, subBatch.size());

                    List<RuleWlessChkResltItem> results = callRuleEngineForBatch(subBatch, params);

                    long subBatchDuration = System.currentTimeMillis() - subBatchStart;
                    log.debug("[Partition {} - Batch {} - Sub {}] API 호출 완료: {}건 → {}결과 ({}ms)",
                        params.get("threadNo"), batchNumber, partitionIndex,
                        subBatch.size(), results.size(), subBatchDuration);

                    return results;

                }, executor));
            }

            // ⏳ 모든 API 호출 완료 대기 (최대 5분)
            List<RuleWlessChkResltItem> allResults = new ArrayList<>();
            for (CompletableFuture<List<RuleWlessChkResltItem>> future : futures) {
                try {
                    allResults.addAll(future.get(API_TIMEOUT_MINUTES, TimeUnit.MINUTES));
                } catch (Exception e) {
                    log.error("[Partition {} - Batch {}] 서브 배치 처리 실패",
                        params.get("threadNo"), batchNumber, e);
                    throw e;
                }
            }

            // 💾 일괄 저장
            if (!allResults.isEmpty()) {
                saveResults(allResults, params);
            }

            long batchDuration = System.currentTimeMillis() - batchStartTime;
            log.debug("[Partition {} - Batch {}] 처리 완료: {}건 → {}결과 ({}ms)",
                params.get("threadNo"), batchNumber, batch.size(), allResults.size(), batchDuration);

        } finally {
            executor.shutdown();
            if (!executor.awaitTermination(10, TimeUnit.MINUTES)) {
                log.warn("[Partition {}] ExecutorService 강제 종료", params.get("threadNo"));
                executor.shutdownNow();
            }
        }
    }

    /**
     * 배치 단위로 룰엔진 API 호출
     * (서브 배치: 약 200건)
     */
    private List<RuleWlessChkResltItem> callRuleEngineForBatch(
            List<PpWlessabcTxnItem> items,
            Map<String, Object> params) {

        List<RuleWlessChkResltItem> batchResults = new ArrayList<>();

        for (PpWlessabcTxnItem item : items) {
            try {
                // 개별 아이템에 대해 룰엔진 호출 (재시도 포함)
                List<RuleWlessChkResltItem> itemResults = callRuleEngineWithRetry(item, params);
                batchResults.addAll(itemResults);

            } catch (Exception e) {
                log.error("[Partition {}] 아이템 처리 실패: svcContId={}",
                    params.get("threadNo"), item.getSvcContId(), e);

                // 실패 건 별도 처리 (선택사항)
                // saveFailedItem(item, e, params);

                // 계속 진행 (다음 아이템 처리)
            }
        }

        return batchResults;
    }

    /**
     * 룰엔진 API 호출 (재시도 포함)
     */
    private List<RuleWlessChkResltItem> callRuleEngineWithRetry(
            PpWlessabcTxnItem item,
            Map<String, Object> params) {

        int retryCount = 0;
        Exception lastException = null;

        while (retryCount < MAX_RETRIES) {
            try {
                return callRuleEngine(item, params);

            } catch (Exception e) {
                lastException = e;
                retryCount++;

                if (retryCount < MAX_RETRIES) {
                    log.warn("[Partition {}] API 재시도 {}/{}: svcContId={}",
                        params.get("threadNo"), retryCount, MAX_RETRIES, item.getSvcContId());

                    try {
                        // Exponential backoff
                        Thread.sleep(1000L * retryCount);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }

        log.error("[Partition {}] API 최종 실패 (재시도 {}회): svcContId={}",
            params.get("threadNo"), MAX_RETRIES, item.getSvcContId(), lastException);

        // 빈 결과 반환 (또는 예외 throw)
        return new ArrayList<>();
    }

    /**
     * 룰엔진 API 호출 (실제 InnoRules 호출)
     *
     * 기존 WlessMabcQatCplyPerpProcessor.ruleCall() 로직 이관
     */
    private List<RuleWlessChkResltItem> callRuleEngine(
            PpWlessabcTxnItem paramItem,
            Map<String, Object> params) throws Exception {

        List<RuleWlessChkResltItem> rstList = new ArrayList<>();

        try {
            RuleInterface intf = null;
            RuleReq req = new RuleReq();
            ResultSet rs;
            Item item;
            ResultSetMetaData rsmd;
            int iColCnt;
            int iRuleCodeType = Constants.CODETYPE_ALIAS;

            // API ID split (여러 API 호출 가능)
            String apiId = (String) params.get("apiId");
            if (apiId == null || apiId.trim().isEmpty()) {
                log.warn("apiId가 null 또는 비어있음");
                return rstList;
            }

            String[] apiArr = apiId.split(",");

            for (String api : apiArr) {
                req.setRuleCode(api.trim());
                req.setDate((String) params.get("batchExecDt"));
                req.resetItems();

                // ⚡ InnoRules API 파라미터 설정 (모든 필드)
                setRuleRequestParams(req, paramItem);

                // 룰 실행
                intf = ClusterManager.getInterface();
                rs = intf.execute(req);

                // 결과 파싱
                while (rs.next()) {
                    RuleWlessChkResltItem rstItem = new RuleWlessChkResltItem();

                    rsmd = rs.getMetaData();
                    iColCnt = rsmd.getColumnCount();

                    for (int i = 1; i <= iColCnt; i++) {
                        item = rs.getItem(i);
                        String colNm = rsmd.getColumnName(i, iRuleCodeType);

                        // 컬럼 매핑 로직 (필요시 확장)
                        mapResultToItem(rstItem, colNm, item, paramItem, params);
                    }

                    rstList.add(rstItem);
                }
            }

        } catch (RulesException e) {
            log.error("[Partition {}] InnoRules API 오류: svcContId={}",
                params.get("threadNo"), paramItem.getSvcContId(), e);
            throw e;
        } catch (Exception e) {
            log.error("[Partition {}] 룰엔진 호출 오류: svcContId={}",
                params.get("threadNo"), paramItem.getSvcContId(), e);
            throw e;
        }

        return rstList;
    }

    /**
     * InnoRules API 파라미터 설정
     * (기존 Processor 로직과 동일)
     */
    private void setRuleRequestParams(RuleReq req, PpWlessabcTxnItem paramItem) {
        req.addStringItem("base_date").add(paramItem.getBaseDate());
        req.addStringItem("wrkjob_ym").add(paramItem.getWrkjobYm());
        req.addStringItem("base_ym").add(paramItem.getBaseYm());
        req.addStringItem("svc_cont_id").add(paramItem.getSvcContId());
        req.addStringItem("ev_occ_dt").add(paramItem.getEvOccDt());
        req.addStringItem("sbsc_div_cd").add(paramItem.getSbscDivCd());
        req.addStringItem("svc_cont_div_cd").add(paramItem.getSvcContDivCd());
        req.addStringItem("chk_scope_val").add(paramItem.getWrkjobScope());
        req.addStringItem("adm_org_id").add(paramItem.getAdmOrgId());
        req.addStringItem("cpnt_id").add(paramItem.getCpntId());
        req.addNumberItem("same_nfl_vqnt_circuit_cnt").add(paramItem.getSameNflVqntCircuitCnt());
        req.addNumberItem("same_nfl_mysh_vqnt_circuit_cnt").add(paramItem.getSameNflMyshVqntCircuitCnt());
        req.addStringItem("cust_bthday_date").add(paramItem.getCustBthdayDate());
        req.addStringItem("crclt_sho_nflr_yn").add(paramItem.getCrcltShoNflrYn());
        req.addStringItem("new_icg_dt").add(paramItem.getNewIcgDt());
        req.addNumberItem("npay_tmscnt").add(paramItem.getNpayTmscnt());
        req.addNumberItem("npay_amt").add(paramItem.getNpayAmt());
        // ... 나머지 필드들 (기존 Processor와 동일하게 모두 추가)
        // 실제 구현 시 WlessMabcQatCplyPerpProcessor의 모든 필드 설정 로직을 복사
    }

    /**
     * 결과 매핑 (RuleWlessChkResltItem에 매핑)
     */
    private void mapResultToItem(RuleWlessChkResltItem rstItem,
                                  String colNm,
                                  Item item,
                                  PpWlessabcTxnItem paramItem,
                                  Map<String, Object> params) {

        // 기존 Processor의 매핑 로직 이관
        // 컬럼명에 따라 rstItem의 필드에 값 설정
        // 실제 구현 시 WlessMabcQatCplyPerpProcessor의 매핑 로직 복사
    }

    /**
     * 결과 일괄 저장 (BatchInsertDao 사용)
     */
    private void saveResults(List<RuleWlessChkResltItem> results,
                            Map<String, Object> params) {

        try {
            Map<String, Object> fixedValues = new HashMap<>();
            fixedValues.put("regUser", "batch");
            fixedValues.put("regDate", "SQL::now()");
            fixedValues.put("updUser", "batch");
            fixedValues.put("updDate", "SQL::now()");

            Set<String> excludeFields = Set.of("serialVersionUID");

            // 💾 BatchInsertDao로 일괄 저장
            batchInsertDao.batchInsert(
                "abcBAT.TMP_RULE_WLESS_CHK_RESLT_01",
                results,
                excludeFields,
                fixedValues
            );

            log.debug("[Partition {}] 결과 저장 완료: {}건",
                params.get("threadNo"), results.size());

        } catch (Exception e) {
            log.error("[Partition {}] 결과 저장 실패: {}건",
                params.get("threadNo"), results.size(), e);
            throw e;
        }
    }

    /**
     * StepExecutionContext에서 파티션 정보 추출
     */
    private Map<String, Object> extractParams(ChunkContext chunkContext) {
        StepContext stepContext = chunkContext.getStepContext();
        Map<String, Object> stepExecutionContext = stepContext.getStepExecutionContext();

        Map<String, Object> params = new HashMap<>();

        // Partitioner에서 전달된 파라미터
        params.put("threadNo", stepExecutionContext.get("threadNo"));
        params.put("partitionGbn", stepExecutionContext.get("partitionGbn"));

        @SuppressWarnings("unchecked")
        Map<String, String> paramSetMap = (Map<String, String>) stepExecutionContext.get("paramSetMap");

        if (paramSetMap != null) {
            params.put("param1", paramSetMap.get("param1"));
            params.put("batchId", paramSetMap.get("batchId"));
            params.put("chkScopeVal", paramSetMap.get("chkScopeVal"));
            params.put("batchExecDt", paramSetMap.get("batchExecDt"));
            params.put("apiId", paramSetMap.get("apiId"));
        }

        // 추가 파라미터
        params.put("pool_size", stepExecutionContext.get("pool_size"));
        params.put("tableNumber", tableNumber);

        return params;
    }

    /**
     * 메모리 사용량 로깅 (디버깅용)
     */
    @SuppressWarnings("unused")
    private void logMemoryUsage(String phase, Map<String, Object> params) {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory() / 1024 / 1024;
        long freeMemory = runtime.freeMemory() / 1024 / 1024;
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = runtime.maxMemory() / 1024 / 1024;

        log.debug("[Partition {} - Memory] {} - Used: {}MB, Free: {}MB, Total: {}MB, Max: {}MB",
            params.get("threadNo"), phase, usedMemory, freeMemory, totalMemory, maxMemory);
    }
}
