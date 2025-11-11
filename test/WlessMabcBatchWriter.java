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

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.batch.item.ItemWriter;

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
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * B 방법: 병렬 처리 Writer
 *
 * ⚠️ Anti-pattern 주의:
 * - Writer에 비즈니스 로직 (API 호출)이 들어감
 * - Spring Batch 표준 패턴에서 벗어남
 * - 하지만 1000건 배치 처리 + 병렬 호출을 위해 불가피
 *
 * 동작 방식:
 * 1. chunk_size(1000)건을 한번에 받음
 * 2. 1000건을 5개로 분할 (200건 × 5)
 * 3. ExecutorService(5)로 병렬 API 호출
 * 4. 결과 일괄 저장
 *
 * @author Claude Code
 * @since 2025-11-11
 * @version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class WlessMabcBatchWriter implements ItemWriter<PpWlessabcTxnItem> {

    private final BatchInsertDao batchInsertDao;
    private final WlessPartiMapper wlessMapper;
    private final SqlSessionFactory sqlSessionFactory;

    @Setter
    private String partitionGbn;

    @Setter
    private Map<String, String> recvMap;

    // 병렬 처리 수 (5개 병렬 API 호출)
    private static final int PARALLEL_COUNT = 5;

    // API 재시도 횟수
    private static final int MAX_RETRIES = 3;

    // API 타임아웃 (분)
    private static final int API_TIMEOUT_MINUTES = 5;

    private static int writeCount = 0;  // Writer 호출 횟수

    /**
     * Writer 메인 로직
     *
     * @param items chunk_size(1000)건의 아이템
     */
    @Override
    public void write(List<? extends PpWlessabcTxnItem> items) throws Exception {

        writeCount++;

        log.info("=================================================================");
        log.info("[B방법-Writer] Partition {} - Write #{} 시작: {}건",
            partitionGbn, writeCount, items.size());
        log.info("=================================================================");

        long startTime = System.currentTimeMillis();

        // ⚡ 1000건을 5개로 분할하여 병렬 처리
        List<RuleWlessChkResltItem> allResults = processInParallel(items);

        // 💾 결과 일괄 저장
        if (!allResults.isEmpty()) {
            saveResults(allResults);
        }

        long duration = System.currentTimeMillis() - startTime;

        log.info("=================================================================");
        log.info("[B방법-Writer] Partition {} - Write #{} 완료: {}건 → {}결과 ({}ms)",
            partitionGbn, writeCount, items.size(), allResults.size(), duration);
        log.info("=================================================================");
    }

    /**
     * 1000건을 5개로 분할하여 병렬 처리
     *
     * ⚡ 병렬 구조:
     * - 1000건 → 200건씩 5개로 분할
     * - ExecutorService(5) 사용
     * - CompletableFuture로 비동기 처리
     */
    private List<RuleWlessChkResltItem> processInParallel(List<? extends PpWlessabcTxnItem> items) throws Exception {

        int subBatchSize = (int) Math.ceil((double) items.size() / PARALLEL_COUNT);

        // ⚡ 병렬 5개로 API 호출
        ExecutorService executor = Executors.newFixedThreadPool(PARALLEL_COUNT);
        List<CompletableFuture<List<RuleWlessChkResltItem>>> futures = new ArrayList<>();

        try {
            for (int i = 0; i < PARALLEL_COUNT; i++) {
                int start = i * subBatchSize;
                int end = Math.min(start + subBatchSize, items.size());

                if (start >= items.size()) break;

                // 서브 배치 생성 (약 200건)
                List<PpWlessabcTxnItem> subBatch = new ArrayList<>(items.subList(start, end));
                final int partitionIndex = i;

                // 🔥 비동기 API 호출
                futures.add(CompletableFuture.supplyAsync(() -> {
                    long subBatchStart = System.currentTimeMillis();

                    log.debug("[Partition {} - Writer - Sub {}] API 호출 시작: {}건",
                        partitionGbn, partitionIndex, subBatch.size());

                    List<RuleWlessChkResltItem> results = callRuleEngineForBatch(subBatch);

                    long subBatchDuration = System.currentTimeMillis() - subBatchStart;
                    log.debug("[Partition {} - Writer - Sub {}] API 호출 완료: {}건 → {}결과 ({}ms)",
                        partitionGbn, partitionIndex, subBatch.size(), results.size(), subBatchDuration);

                    return results;

                }, executor));
            }

            // ⏳ 모든 API 호출 완료 대기
            List<RuleWlessChkResltItem> allResults = new ArrayList<>();
            for (CompletableFuture<List<RuleWlessChkResltItem>> future : futures) {
                try {
                    allResults.addAll(future.get(API_TIMEOUT_MINUTES, TimeUnit.MINUTES));
                } catch (Exception e) {
                    log.error("[Partition {}] 서브 배치 처리 실패", partitionGbn, e);
                    throw e;
                }
            }

            return allResults;

        } finally {
            executor.shutdown();
            if (!executor.awaitTermination(10, TimeUnit.MINUTES)) {
                log.warn("[Partition {}] ExecutorService 강제 종료", partitionGbn);
                executor.shutdownNow();
            }
        }
    }

    /**
     * 배치 단위로 룰엔진 API 호출
     * (서브 배치: 약 200건)
     */
    private List<RuleWlessChkResltItem> callRuleEngineForBatch(List<PpWlessabcTxnItem> items) {

        List<RuleWlessChkResltItem> batchResults = new ArrayList<>();

        for (PpWlessabcTxnItem item : items) {
            try {
                // 개별 아이템에 대해 룰엔진 호출 (재시도 포함)
                List<RuleWlessChkResltItem> itemResults = callRuleEngineWithRetry(item);
                batchResults.addAll(itemResults);

            } catch (Exception e) {
                log.error("[Partition {}] 아이템 처리 실패: svcContId={}",
                    partitionGbn, item.getSvcContId(), e);

                // 실패 건 별도 처리 (선택사항)
                // saveFailedItem(item, e);

                // 계속 진행 (다음 아이템 처리)
            }
        }

        return batchResults;
    }

    /**
     * 룰엔진 API 호출 (재시도 포함)
     */
    private List<RuleWlessChkResltItem> callRuleEngineWithRetry(PpWlessabcTxnItem item) {

        int retryCount = 0;
        Exception lastException = null;

        while (retryCount < MAX_RETRIES) {
            try {
                return callRuleEngine(item);

            } catch (Exception e) {
                lastException = e;
                retryCount++;

                if (retryCount < MAX_RETRIES) {
                    log.warn("[Partition {}] API 재시도 {}/{}: svcContId={}",
                        partitionGbn, retryCount, MAX_RETRIES, item.getSvcContId());

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
            partitionGbn, MAX_RETRIES, item.getSvcContId(), lastException);

        // 빈 결과 반환 (또는 예외 throw)
        return new ArrayList<>();
    }

    /**
     * 룰엔진 API 호출 (실제 InnoRules 호출)
     *
     * 기존 WlessMabcQatCplyPerpProcessor.ruleCall() 로직과 동일
     */
    private List<RuleWlessChkResltItem> callRuleEngine(PpWlessabcTxnItem paramItem) throws Exception {

        List<RuleWlessChkResltItem> rstList = new ArrayList<>();

        try {
            RuleInterface intf = null;
            RuleReq req = new RuleReq();
            ResultSet rs;
            Item item;
            ResultSetMetaData rsmd;
            int iColCnt;
            int iRuleCodeType = Constants.CODETYPE_ALIAS;

            // API ID split
            String apiId = recvMap.get("apiId");
            if (apiId == null || apiId.trim().isEmpty()) {
                log.warn("apiId가 null 또는 비어있음");
                return rstList;
            }

            String[] apiArr = apiId.split(",");

            for (String api : apiArr) {
                req.setRuleCode(api.trim());
                req.setDate(recvMap.get("batchExecDt"));
                req.resetItems();

                // ⚡ InnoRules API 파라미터 설정
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

                        // 컬럼 매핑
                        mapResultToItem(rstItem, colNm, item, paramItem);
                    }

                    rstList.add(rstItem);
                }
            }

        } catch (RulesException e) {
            log.error("[Partition {}] InnoRules API 오류: svcContId={}",
                partitionGbn, paramItem.getSvcContId(), e);
            throw e;
        } catch (Exception e) {
            log.error("[Partition {}] 룰엔진 호출 오류: svcContId={}",
                partitionGbn, paramItem.getSvcContId(), e);
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
        // ... 나머지 모든 필드 (기존 Processor와 동일)
        // 실제 구현 시 WlessMabcQatCplyPerpProcessor의 모든 필드 설정 로직 복사
    }

    /**
     * 결과 매핑
     */
    private void mapResultToItem(RuleWlessChkResltItem rstItem,
                                  String colNm,
                                  Item item,
                                  PpWlessabcTxnItem paramItem) {
        // 기존 Processor의 매핑 로직 이관
        // 실제 구현 시 WlessMabcQatCplyPerpProcessor의 매핑 로직 복사
    }

    /**
     * 결과 일괄 저장
     */
    private void saveResults(List<RuleWlessChkResltItem> results) {

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

            log.debug("[Partition {}] 결과 저장 완료: {}건", partitionGbn, results.size());

        } catch (Exception e) {
            log.error("[Partition {}] 결과 저장 실패: {}건", partitionGbn, results.size(), e);
            throw e;
        }
    }
}
