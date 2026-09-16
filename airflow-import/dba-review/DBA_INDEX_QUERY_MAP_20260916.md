# Airflow 제어 테이블 인덱스 ↔ 업무 쿼리 대응표 (DBA 제출용, 2026-09-16)

- 대상 테이블: `sqmown.cm_airflow_apv_rqt`(승인요청, 부모) · `sqmown.cm_airflow_apv_dtl`(승인요청 상세, 자식 1:1) · `sqmown.cm_airflow_dag_task_mappg_info`(DAG-task 매핑)
- DDL: `sql/01_airflow_control_ddl.sql`. 쿼리 원문: 배치어드민 MyBatis 매퍼 `airflowApprovalMapper.xml` (아래 `[문장 id]` 표기).
- 인입량: 운영 일 0~5건 / 월 100건 이하(운영자가 화면에서 요청·승인할 때만 적재, 자동 스케줄 실행은 미적재). 개발 월 수백 건.
- 모든 쿼리는 `apv_domn_val = 'AIRFLOW'` 를 반드시 건다(다른 도메인이 같은 테이블을 쓸 경우의 격리 조건).

## 0. 결론 먼저 — 필수 / 유지 권장 / 생성 생략 가능

| 구분 | 인덱스 | 판단 |
|---|---|---|
| 필수(정합성) | `cm_airflow_apv_rqt_pk`, `cm_airflow_apv_dtl_pk`, `cm_airflow_dag_task_mappg_info_pk` | 기본키 |
| 필수(정합성) | `cm_airflow_apv_rqt_ux01` UNIQUE, `cm_airflow_apv_rqt_ux02` UNIQUE | 매퍼가 `ON CONFLICT (…)` 로 직접 참조 → 없으면 문장 실패. 업무 규칙(1행 보장)을 DB 가 지키는 장치 |
| 유지 권장 | `cm_airflow_apv_rqt_ix01` | 화면을 열 때마다 도는 승인 대기열·중복 요청 가드가 선두 컬럼(도메인, 상태) 을 쓴다 |
| **생성 생략 가능** | `cm_airflow_apv_rqt_ix02`, `cm_airflow_apv_rqt_ix03`, `cm_airflow_apv_dtl_ix01` | 쓰는 쿼리는 있으나 월 100건 규모에서 순차 탐색과 차이가 없다. DBA 가 인덱스 수를 줄이길 원하면 이 3개는 빼도 애플리케이션 수정이 필요 없다(매퍼는 인덱스 이름을 참조하지 않는다) |

- 크기 영향: 부모 테이블 행 폭 약 1KB, 연 1,200행 → 인덱스 6개를 다 만들어도 인덱스 총량은 연 수 MB 수준이다. 그래도 "필요한 것만" 원칙이면 위 3개를 빼고 **PK 3 + UNIQUE 2 + ix01** 의 6개(테이블 3개 기준) 로 가면 된다.
- 어느 쪽을 택해도 매퍼·소스 변경은 없다. 결정되면 DDL(01·04) 에서 해당 `CREATE INDEX` 줄만 빼서 다시 제출한다.

---

## 1. `cm_airflow_apv_rqt_ix01` (apv_domn_val, apv_rqt_sttus_val, apv_rqt_dt DESC)

**용도**: 승인관리 화면의 대기열(도메인·상태로 걸러 최근순), 같은 DAG 의 진행중 요청 유무(중복 요청 차단), 승인/반려 시 상태 CAS. 화면을 열 때·요청을 낼 때마다 실행되는 가장 잦은 경로.

```sql
-- [selectActiveRequests] 승인관리 대기열: PENDING 전체 + 승인됐지만 아직 실행 안 된 실행요청
SELECT p.apv_rqt_id, p.apv_acton_type_val, d.dag_id, d.tgt_dag_run_id, d.exec_st_task_id, d.exec_fns_task_id,
       d.exec_conf_json_val, d.dag_scdul_expr_val, d.dag_desc_val, p.apv_rqt_sttus_val, p.apv_rqtr_id,
       p.apv_dcsn_trtr_id, p.apv_rqt_dt, p.apv_dcsn_dt, d.apv_exec_sttus_val, p.apv_rcess_cncl_why_val, d.ctlog_write_route_val
  FROM sqmown.cm_airflow_apv_rqt p
  JOIN sqmown.cm_airflow_apv_dtl d ON d.apv_rqt_id = p.apv_rqt_id
 WHERE p.apv_domn_val = 'AIRFLOW'
   AND p.apv_acton_type_val IN ('RUN','RERUN_FROM_TASK','CLEAR_TASKS','PAUSE','UNPAUSE','CHANGE_SCHEDULE','CHANGE_DESCRIPTION')
   AND ( p.apv_rqt_sttus_val = 'PENDING'
         OR ( p.apv_rqt_sttus_val = 'APPROVED'
              AND p.apv_acton_type_val IN ('RUN','RERUN_FROM_TASK','CLEAR_TASKS')
              AND d.apv_exec_sttus_val IS NULL ) )
 ORDER BY p.apv_rqt_id DESC
 LIMIT 500;
```

```sql
-- [countActiveForDag] 같은 DAG 에 진행중 요청이 있으면 새 요청을 거부(중복 요청 가드). 요청 1건마다 1회.
SELECT count(*)
  FROM sqmown.cm_airflow_apv_rqt p
  JOIN sqmown.cm_airflow_apv_dtl d ON d.apv_rqt_id = p.apv_rqt_id
 WHERE p.apv_domn_val = 'AIRFLOW'
   AND p.apv_acton_type_val IN ('RUN','RERUN_FROM_TASK','REGISTER_DAG','CLEAR_TASKS','PAUSE','UNPAUSE',
                                'CHANGE_SCHEDULE','CHANGE_DESCRIPTION','AUTOGEN_APPLY','AUTOGEN_OVERRIDE','EDIT_ENV_MANIFEST')
   AND d.dag_id = :dagId
   AND ( p.apv_rqt_sttus_val = 'PENDING'
         OR ( p.apv_rqt_sttus_val = 'APPROVED'
              AND p.apv_acton_type_val IN ('RUN','RERUN_FROM_TASK','CLEAR_TASKS')
              AND d.apv_exec_sttus_val IS NULL ) )
   AND p.apv_acton_type_val IN ('RUN','RERUN_FROM_TASK','CLEAR_TASKS');   -- 액션 그룹별로 바뀜
```

```sql
-- [claimRunRequest] 승인 확정 CAS: 동시에 둘이 승인해도 1회만 전이
UPDATE sqmown.cm_airflow_apv_rqt
   SET apv_rqt_sttus_val = 'APPROVED', apv_dcsn_trtr_id = :approvedBy, apv_dcsn_dt = NOW(), sys_recd_chg_dt = NOW()
 WHERE apv_rqt_id = :requestId AND apv_rqt_sttus_val = 'PENDING' AND apv_domn_val = 'AIRFLOW';
```

```sql
-- [selectExecutedRunHistory] 실행 이력 화면: 승인·실행이 끝난 실행요청을 실행일시 역순으로
SELECT p.apv_rqt_id, p.apv_acton_type_val, d.dag_id, d.tgt_dag_run_id, d.exec_st_task_id, d.exec_fns_task_id, d.exec_conf_json_val,
       p.apv_rqt_sttus_val, p.apv_rqtr_id, p.apv_dcsn_trtr_id, p.apv_rqt_dt, p.apv_dcsn_dt, d.apv_exec_rcv_dt,
       d.reslt_dag_run_id, d.apv_exec_sttus_val
  FROM sqmown.cm_airflow_apv_rqt p
  JOIN sqmown.cm_airflow_apv_dtl d ON d.apv_rqt_id = p.apv_rqt_id
 WHERE p.apv_domn_val = 'AIRFLOW'
   AND p.apv_acton_type_val IN ('RUN','RERUN_FROM_TASK','CLEAR_TASKS')
   AND p.apv_rqt_sttus_val = 'APPROVED'
   AND d.reslt_dag_run_id IS NOT NULL AND d.apv_exec_sttus_val IS NOT NULL AND d.apv_exec_rcv_dt IS NOT NULL
   AND d.apv_exec_rcv_dt >= :executedFrom AND d.apv_exec_rcv_dt < :executedUntil
 ORDER BY d.apv_exec_rcv_dt DESC NULLS LAST, p.apv_rqt_id DESC
 LIMIT :limit OFFSET :offset;
```

비고: 목록 정렬은 `apv_rqt_id DESC`(PK, 시퀀스 채번이라 요청일시와 같은 순서) 라 셋째 컬럼 `apv_rqt_dt DESC` 는 보조 컬럼이다. `apv_rqt_id DESC` 로 바꾸거나 빼도 쿼리는 그대로 동작한다.

## 2. `cm_airflow_apv_rqt_ix02` (apv_rqtr_id, apv_rqt_dt DESC) — 생략 가능

**용도**: "내 요청" 조회(요청자 필터), 템플릿 목록의 시스템 템플릿 우선 정렬.

```sql
-- [selectRunRequests] 실행요청 목록 — 요청자 필터를 켰을 때(선택 필터). 최근 100건.
SELECT p.apv_rqt_id, p.apv_acton_type_val, d.dag_id, d.tgt_dag_run_id, d.exec_st_task_id, d.exec_fns_task_id, d.exec_conf_json_val,
       p.apv_rqt_sttus_val, p.apv_rqtr_id, p.apv_dcsn_trtr_id, p.apv_rqt_dt, p.apv_dcsn_dt, d.apv_exec_rcv_dt, d.reslt_dag_run_id,
       d.apv_exec_sttus_val, p.apv_rcess_cncl_why_val, d.airflow_api_rspn_val
  FROM sqmown.cm_airflow_apv_rqt p
  JOIN sqmown.cm_airflow_apv_dtl d ON d.apv_rqt_id = p.apv_rqt_id
 WHERE p.apv_domn_val = 'AIRFLOW' AND p.apv_acton_type_val IN ('RUN','RERUN_FROM_TASK','CLEAR_TASKS')
   AND p.apv_rqtr_id = :requestedBy
   AND p.apv_rqt_sttus_val = :requestStatus          -- '전체' 선택 시 이 조건 없음
 ORDER BY p.apv_rqt_id DESC
 LIMIT 100;
```

```sql
-- [selectTemplates] 빌더 템플릿 목록: 시스템 템플릿('system') 을 앞에, 나머지는 요청일시 역순. 최근 200건.
SELECT p.apv_rqt_id, d.dag_id, d.apv_item_disp_nm, d.apv_pyld_text_val, p.apv_rqtr_id, p.apv_rqt_dt
  FROM sqmown.cm_airflow_apv_rqt p
  JOIN sqmown.cm_airflow_apv_dtl d ON d.apv_rqt_id = p.apv_rqt_id
 WHERE p.apv_domn_val = 'AIRFLOW' AND p.apv_acton_type_val = 'TEMPLATE' AND p.apv_rqt_sttus_val = 'APPROVED'
   AND COALESCE(d.tmplt_type_val, 'FLOW') = 'FLOW'
 ORDER BY p.apv_rqt_dt DESC, p.apv_rqt_id DESC
 LIMIT 200;
```

생략 가능 이유: 요청자 필터는 선택 항목이고 대상 행이 월 100건이라 순차 탐색과 차이가 없다.

## 3. `cm_airflow_apv_rqt_ix03` (idem_rply_exp_dt, apv_rqt_id) — 생략 가능

**용도**: 만료된 멱등 재응답 행(24시간 보관) 의 기회적 정리. 새 마커를 넣기 직전에 행 수 제한을 두고 실행.

```sql
-- [deleteExpiredIdempotencyReplays] 만료 행을 만료일시 순으로 n건만, 다른 인스턴스와 겹치지 않게(SKIP LOCKED) 삭제
WITH expired AS (
    SELECT apv_rqt_id
      FROM sqmown.cm_airflow_apv_rqt
     WHERE apv_domn_val = 'AIRFLOW'
       AND apv_acton_type_val = 'IDEMPOTENCY_REPLAY'
       AND idem_rply_exp_dt <= CURRENT_TIMESTAMP
     ORDER BY idem_rply_exp_dt, apv_rqt_id
     LIMIT :limit
     FOR UPDATE SKIP LOCKED
)
DELETE FROM sqmown.cm_airflow_apv_rqt replay
 USING expired
 WHERE replay.apv_rqt_id = expired.apv_rqt_id;
```

생략 가능 이유: 멱등 행은 요청 1건당 1행이 24시간만 머물다 지워지므로 상주 행이 수십 건을 넘지 않는다.

## 4. `cm_airflow_apv_rqt_ux01` UNIQUE (apv_domn_val, apv_rqtr_id, idem_route_scope_val, idem_rqt_key_val) — 필수

**용도**: 같은 사용자가 같은 API 경로에 같은 `Idempotency-Key` 로 재요청하면 행이 1개만 존재하게 보장 → 서비스가 첫 응답을 그대로 재생하고 Airflow 를 두 번 실행하지 않는다. 일반 승인 행은 `idem_*` 가 NULL 이라 충돌하지 않는다.

```sql
-- [selectIdempotencyReplay] 재응답 행을 잠그고 읽는다(유니크라 최대 1행)
SELECT apv_rqt_id, idem_rqt_sha256_val, idem_rply_sttus_val, idem_http_rspn_sttus_val, idem_http_rspn_json_val::text,
       CASE WHEN idem_rply_sttus_val = 'IN_PROGRESS' AND idem_lease_fns_dt > CURRENT_TIMESTAMP
              THEN LEAST(300, GREATEST(1, CEIL(EXTRACT(EPOCH FROM (idem_lease_fns_dt - CURRENT_TIMESTAMP)))::integer))
            WHEN idem_rply_sttus_val = 'IN_PROGRESS' AND idem_rply_exp_dt > CURRENT_TIMESTAMP
              THEN LEAST(86400, GREATEST(1, CEIL(EXTRACT(EPOCH FROM (idem_rply_exp_dt - CURRENT_TIMESTAMP)))::integer))
            ELSE 1 END AS retry_after_seconds
  FROM sqmown.cm_airflow_apv_rqt
 WHERE apv_domn_val = 'AIRFLOW'
   AND apv_acton_type_val = 'IDEMPOTENCY_REPLAY'
   AND apv_rqtr_id = :requestedBy
   AND idem_route_scope_val = :scope
   AND idem_rqt_key_val = :idempotencyKey
 FOR UPDATE;
```

```sql
-- [insertIdempotencyReplay] 재응답 마커 신규 — 같은 (요청자, 경로, 키) 가 이미 있으면 유니크 위반 → 기존 응답 재생
INSERT INTO sqmown.cm_airflow_apv_rqt
    (apv_rqt_id, apv_domn_val, apv_acton_type_val, apv_rqt_sttus_val, apv_rqtr_id, apv_rqt_dt,
     sys_trtr_id, sys_trt_org_id, sys_svc_id, sys_comp_id, sys_recd_cret_dt,
     idem_route_scope_val, idem_rqt_key_val, idem_rqt_sha256_val, idem_rply_sttus_val, idem_lease_fns_dt, idem_rply_exp_dt)
VALUES
    (nextval('sqmown.sq_cm_airflow_apv_rqt01'), 'AIRFLOW', 'IDEMPOTENCY_REPLAY', 'APPROVED', :requestedBy, CURRENT_TIMESTAMP,
     :requestedBy, 'SQMS', 'Airflow', 'BatchAdmin', CURRENT_TIMESTAMP,
     :scope, :idempotencyKey, :requestSha256, 'IN_PROGRESS', CURRENT_TIMESTAMP + INTERVAL '5 minutes', CURRENT_TIMESTAMP + INTERVAL '24 hours')
RETURNING apv_rqt_id;
```

## 5. `cm_airflow_apv_rqt_ux02` UNIQUE (apv_domn_val, apv_uniq_key_val) — 필수

**용도**: 마커 행의 유일성. 알림 "모두 확인" 은 사용자별 1행(`'NOTIF_SEEN:<사용자>'`), 자동생성 동기화 시스템 이벤트는 이벤트별 1행(`'SYSTEM_EVENT:AUTOGEN_SYNC:<이벤트>'`). 매퍼가 이 인덱스를 `ON CONFLICT` 대상으로 직접 지정한다. 일반 승인 행은 `apv_uniq_key_val` 이 NULL.

```sql
-- [upsertNotificationSeen] 알림 확인 시각 UPSERT(사용자별 1행)
INSERT INTO sqmown.cm_airflow_apv_rqt
    (apv_rqt_id, apv_domn_val, apv_acton_type_val, apv_rqt_sttus_val, apv_rqtr_id, apv_rqt_dt, notif_last_conf_dt,
     sys_trtr_id, sys_trt_org_id, sys_svc_id, sys_comp_id, sys_recd_cret_dt, apv_uniq_key_val)
VALUES
    (nextval('sqmown.sq_cm_airflow_apv_rqt01'), 'AIRFLOW', 'NOTIF_SEEN', 'APPROVED', :userId,
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, :userId, 'SQMS', 'Airflow', 'BatchAdmin', CURRENT_TIMESTAMP,
     'NOTIF_SEEN:' || :userId)
ON CONFLICT (apv_domn_val, apv_uniq_key_val)
DO UPDATE SET notif_last_conf_dt = GREATEST(cm_airflow_apv_rqt.notif_last_conf_dt, EXCLUDED.notif_last_conf_dt),
              sys_trtr_id = EXCLUDED.sys_trtr_id, sys_recd_chg_dt = CURRENT_TIMESTAMP;
```

```sql
-- [upsertSystemEvent] 자동생성 동기화 이벤트 마커(이벤트별 1행, 재실행 멱등)
INSERT INTO sqmown.cm_airflow_apv_rqt
    (apv_rqt_id, apv_domn_val, apv_acton_type_val, apv_rqt_sttus_val, apv_rqtr_id, apv_rqt_dt, apv_ev_occ_dt,
     sys_trtr_id, sys_trt_org_id, sys_svc_id, sys_comp_id, sys_recd_cret_dt, apv_uniq_key_val)
VALUES
    (nextval('sqmown.sq_cm_airflow_apv_rqt01'), 'AIRFLOW', 'SYSTEM_EVENT', 'APPROVED', :actor,
     COALESCE(:occurredAt, CURRENT_TIMESTAMP), COALESCE(:occurredAt, CURRENT_TIMESTAMP),
     :actor, 'SQMS', 'AUTOGEN_SYNC', :eventIdentity, CURRENT_TIMESTAMP,
     'SYSTEM_EVENT:AUTOGEN_SYNC:' || :eventIdentity)
ON CONFLICT (apv_domn_val, apv_uniq_key_val)
DO UPDATE SET sys_recd_chg_dt = sqmown.cm_airflow_apv_rqt.sys_recd_chg_dt
RETURNING apv_rqt_id;
```

## 6. `cm_airflow_apv_dtl_ix01` (dag_id) — 생략 가능

**용도**: DAG 하나를 기준으로 상세를 고르는 조회(최신 정의 YAML, 그 DAG 의 진행중 요청).

```sql
-- [selectLatestDagYaml] DAG 정의(REGISTER_DAG) 최신본 — 승인본 우선
SELECT d.apv_pyld_text_val
  FROM sqmown.cm_airflow_apv_rqt p
  JOIN sqmown.cm_airflow_apv_dtl d ON d.apv_rqt_id = p.apv_rqt_id
 WHERE p.apv_domn_val = 'AIRFLOW' AND p.apv_acton_type_val = 'REGISTER_DAG'
   AND p.apv_rqt_sttus_val IN ('APPROVED', 'PENDING')
   AND d.dag_id = :dagId AND d.apv_pyld_text_val IS NOT NULL
 ORDER BY CASE WHEN p.apv_rqt_sttus_val = 'APPROVED' THEN 0 ELSE 1 END, p.apv_rqt_id DESC
 LIMIT 1;
-- [countActiveForDag] (1절 참조) 도 d.dag_id = :dagId 로 상세를 고른다.
```

생략 가능 이유: 상세 행 수 = 부모 행 수(월 100건). 조인 후 `dag_id` 로 걸러도 비용 차이가 없다.

## 7. `cm_airflow_dag_task_mappg_info` — PK(dag_id, full_task_id) 외 인덱스 없음

- DAG 하나를 보는 문장은 PK 선두 `dag_id` 를 쓴다(`selectScheduleMarker`, `selectDefinitionYaml`: `WHERE dag_id = :dagId AND full_task_id = '__schedule__'`).
- DAG 목록 화면이 한 번 부르는 `selectScheduleMarkers`/`selectDescriptionMarkers` 는 `WHERE full_task_id = '__schedule__' AND dag_meta_json_val IS NOT NULL` 처럼 `full_task_id` 만으로 마커 행을 고르므로 PK 를 못 타고 테이블(행 수 = 전체 task 수, 수천 행)을 훑는다. 현재 규모에서는 문제 없어 인덱스를 요청하지 않았다. 커지면 `(full_task_id)` 단일 인덱스 1개를 추가 요청한다.

## 8. 인덱스별 사용 문장 수 (매퍼 전체 66문장 분류: SELECT 27 · INSERT 12 · UPDATE 23 · DELETE 4)

분류 기준 = 그 문장이 행을 **찾는 데** 쓰는 접근 경로(WHERE/JOIN/ON CONFLICT 선두 컬럼). 같은 문장이 두 테이블을 조인하면 각 테이블의 경로에 한 번씩 셈한다.
인덱스 **유지 비용**(INSERT/DELETE 는 그 테이블의 모든 인덱스를, UPDATE 는 바뀌는 컬럼의 인덱스만 갱신) 은 표 아래에 따로 적었다.

| 인덱스 | SELECT | INSERT | UPDATE | DELETE | 문장 id |
|---|---|---|---|---|---|
| `cm_airflow_apv_rqt_pk` (apv_rqt_id) | 4 | 0 | 14 | 2 | S: selectRunRequest, selectDagRequest, selectTemplate, selectActionRequest · U: claimRunRequest, claimRunExecution, cancelReadyRunRequest, updateRunResult, rejectRunRequest, updateRunFinalStatus, updateTemplate, reclaimExpiredIdempotencyReplay, completeIdempotencyReplay, claimDagRequest, reconcileDelegatedDagWriteFailure, reconcileDelegatedDagReadback, rejectDagRequest, reconcileDelegatedCatalogEdit · D: deleteTemplateParent, deleteTemplateChild(EXISTS 부모) |
| `cm_airflow_apv_rqt_ix01` (도메인, 상태, 요청일시) | 11 (+상태 필터 시 3) | 0 | 0 | 0 | selectActiveRequests, countActiveForDag, selectExecutedRunHistory, countExecutedRunHistory, selectTemplates, selectParameterTemplates, selectLatestDagYaml, selectDerivedNotifications, selectDerivedNotification, countVisibleDerivedNotification, selectVisibleDerivedNotificationKeys · 상태 필터를 켰을 때: selectRunRequests, selectDagRequests, selectActionHistory |
| `cm_airflow_apv_rqt_ix02` (요청자, 요청일시) | 8 | 0 | 0 | 0 | 요청자 필터: selectRunRequests, selectDagRequests · 알림 "확인 마커" LEFT JOIN(`seen.apv_rqtr_id = :userId`): selectDerivedNotifications, selectDerivedNotification, countVisibleDerivedNotification, selectVisibleDerivedNotificationKeys, selectNotificationState, selectNotificationStates |
| `cm_airflow_apv_rqt_ix03` (만료일시, id) | 0 | 0 | 0 | 1 | deleteExpiredIdempotencyReplays |
| `cm_airflow_apv_rqt_ux01` UNIQUE (도메인, 요청자, 경로, 멱등키) | 1 | 1 | 0 | 1 | S: selectIdempotencyReplay(FOR UPDATE) · I: insertIdempotencyReplay(유일성 검사) · D: deleteExpiredIdempotencyReplay |
| `cm_airflow_apv_rqt_ux02` UNIQUE (도메인, 유일키) | 0 | 2 | 0 | 0 | upsertNotificationSeen, upsertSystemEvent (둘 다 `ON CONFLICT (apv_domn_val, apv_uniq_key_val)`) |
| `cm_airflow_apv_dtl_pk` (apv_rqt_id) | 20 | 0 | 16 | 1 | 부모-상세 조인 `d.apv_rqt_id = p.apv_rqt_id` 를 쓰는 모든 SELECT · 상세 갱신 16(claimRunExecution … reconcileDelegatedCatalogEdit) · deleteTemplateChild |
| `cm_airflow_apv_dtl_ix01` (dag_id) | 2 | 0 | 0 | 0 | selectLatestDagYaml, countActiveForDag |
| `cm_airflow_dag_task_mappg_info_pk` (dag_id, full_task_id) | 2 | 3 | 0 | 0 | S: selectScheduleMarker, selectDefinitionYaml · I(UPSERT `ON CONFLICT (dag_id, full_task_id)`): upsertScheduleMarker, upsertDefinitionYaml, upsertDescriptionMarker |
| (인덱스 없음 — 순차 탐색) | 4 | 6 | 1 | 0 | S: selectScheduleMarkers, selectDescriptionMarkers(`full_task_id` 만으로 필터, 7절), selectNotificationState, updateNotificationHandle(`apv_rqt_id::text = split_part(:key, ':', 2)` — 컬럼에 캐스트가 걸려 PK 를 못 탄다, 행 수가 작아 영향 없음) · I: insertRunRequest, insertDagRequest, insertTemplate, insertParameterTemplate, insertActionRequest(조건 없는 신규 행), insertStopHistoryForRun(다른 테이블 sq_batch_wrk_hst) · lockIdempotencyReplay, lockActiveRequestGuard 는 advisory lock 만(테이블 접근 없음) |

유지 비용 쪽에서 보면:
- `cm_airflow_apv_rqt` INSERT 8문장·DELETE 3문장은 그 테이블의 인덱스 6개(PK+ix01~03+ux01~02) 를 모두 갱신한다. UPDATE 23문장 가운데 상태(`apv_rqt_sttus_val`) 를 바꾸는 6문장(claimRunRequest, rejectRunRequest, cancelReadyRunRequest, updateRunFinalStatus, claimDagRequest, rejectDagRequest) 만 ix01 을 갱신하고, 나머지는 인덱스 컬럼을 건드리지 않는다.
- 위 표의 "생략 가능" 3개(ix02·ix03·dtl_ix01) 를 빼면 INSERT/DELETE 마다 갱신하는 인덱스가 부모 6→4, 상세 2→1 로 줄고, 읽기 쪽에서 잃는 것은 월 100건 테이블의 순차 탐색 몇 건이다.
