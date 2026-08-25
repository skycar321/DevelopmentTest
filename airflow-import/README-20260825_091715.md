# Airflow 제어 반입판 — 2026-08-25 (**DML 정본 운영 반영 + 노드 파라미터 시각화**)

- 번들: `airflow-bundle-20260825_091715.7z` (1166849 bytes)
- sha256: `1a1c003556b0c89312d3936cd7b9a0710107e1f074fa49621c9c63962ae777f2`
- source commit: `663eeb1dd`
- 직전 판 `20260821_070715` 이후의 변경을 모두 포함합니다.

---

## 이번 판의 핵심 — 운영을 DML 로 관리합니다

git 관리를 하지 않기로 한 결정에 맞춰, **테이블이 DAG 정의의 정본**이 됩니다.
운영에 DML 을 넣으면 YAML 과 DAG 가 따라옵니다.

### 무엇이 달라지나

| | 이전 | 이번 판 |
|---|---|---|
| 신규 DAG | 배포로만 | 정의 marker 행 INSERT → 10분 안에 DAG(paused) |
| 수정 | 배포로만 | 같은 행 UPDATE |
| 내림 | 파일 삭제 | `use_yn='N'` → YAML 을 `.disabled` 로 이동(**삭제하지 않음**) |

### 정의 marker 행

    full_task_id = '__definition__',  task_type = '__DAG_DEF__'
    def_yaml  TEXT     — flow 카탈로그 YAML 전문(정본)
    use_yn    CHAR(1)  — 'N'=내림. NULL/'Y'=활성

`__schedule__` 선례를 그대로 따랐습니다 — **새 테이블 0개, 컬럼 2개 추가**뿐입니다.

### 반입 전에 하실 일 하나

`cm_airflow_dag_task_mappg_info` 에 컬럼 2개를 추가해야 합니다. 멱등이라 여러 번 실행해도 안전합니다.

```bash
psql -v ON_ERROR_STOP=1 -h <DB host> -U <user> -d <db> \
  -f spring-batch-reference/sql/dag_task_map.sql
```

`-v ON_ERROR_STOP=1` 을 반드시 주세요. psql 기본값은 중간 문장이 실패해도 종료코드 0 으로 끝납니다.

⚠ 이 테이블은 **DROP 금지**입니다. `sched_json`(운영 스케줄)과 `def_yaml`(DAG 정의)은
DagBag 에서 재현할 수 없고, git 관리를 하지 않으므로 이 테이블 밖에 사본이 없습니다.

### 동작 방식

- 상시: `cm_ops_sync_catalog_from_db`(10분 주기 ops DAG). `{"dry_run": true}` conf 로 미리보기 가능.
- 배포 시 1회: `deploy.sh [1c/5]`. 최초 배포 직후 한 주기 공백을 없앱니다. `MATERIALIZE_CATALOG_DEF=0` 으로 끌 수 있습니다.

### 안전장치

- **신규 DAG 은 paused 로 생성됩니다.** DML 한 줄이 사람 손을 안 거치고 배치를 돌리지 않게 합니다
  (`CATALOG_FROM_DB_PAUSE_NEW=0` 으로 해제 가능).
- **번들이 반입한 같은 이름의 카탈로그는 덮지 않습니다.** 충돌로 보고하고 멈춥니다.
  이 경로가 쓴 파일에는 `# managed-by:` 머리말이 붙어 구분됩니다.
- **깨진 정의 한 건이 살아 있는 파일을 건드리지 못합니다.** 건별 격리 후 태스크는 실패로 끝납니다.
- 구조가 틀린 정의(예: 서로 연결되지 않은 스텝)는 **파일이 되기 전에** 막힙니다.
  막지 않으면 스케줄러 로그 한 줄로만 새어 "DML 넣었는데 DAG 가 안 보인다"만 남습니다.

### 실증

실 PostgreSQL 14 + 실 Airflow 2.6.3 으로 전 구간을 확인했습니다.

- DML INSERT → YAML 생성 → **DAG 등록(paused), tasks 정상, import_errors=0**
- UPDATE 로 스텝 추가 → 파일 반영 / `use_yn='N'` → `.disabled` 이동(원본 보존) / `'Y'` → 복구
- 재실행 멱등(파일 바이트 동일)
- pytest 435 통과, 계약 반례 8종 양방향 확인

---

## 함께 들어간 것

### 노드 안에 파라미터·시작시각 표시

DAG 편집/신규등록 화면의 노드에 `🧷 param_N=...` 과 `⏰ 시작: ...` 배지가 붙습니다.
`_by_day` 값은 평일/주말로 묶어 보여주고, 전 요일이 같으면 `전체=` 로 축약합니다.
값이 길면 말줄임으로 자르고 전문은 툴팁으로 볼 수 있습니다.

### 소형 모달 가운데정렬 정리

`.af-modal-sm` 이 컨테이너에 걸던 `text-align:center` 를 제거했습니다.
확인창 제목·본문·버튼은 각자 정렬을 선언하고 있어 **외형은 그대로**이고,
그동안 JS 로 주입되던 내용(입력창·대화상자)만 가운데로 쏠리던 것이 정상화됩니다.

---

## ⚠ 이 판의 검증 한계 — 그대로 적습니다

- **화면 육안 검수가 끝나지 않았습니다.** 위 두 UI 변경은 정적 검증까지만 했고
  1920 실렌더 기하는 측정하지 못했습니다(측정 장비 쪽 사정). 지난 배지 잘림 사고와 같은 계열이라
  **반입 테스트용으로 보시고, 화면은 직접 확인**해 주세요.
- 계약 원장의 근거 해시 재핀이 진행 중이라 커버리지 장치 하나가 적색입니다.
  번들 자체 검증(`verify_bundle` PASS 25 / FAIL 0)과 패키징 게이트는 통과했습니다.

## 압축 해제

```bash
7z x airflow-bundle-20260825_091715.7z   # 비밀번호 입력
tar -zxvf airflow-bundle.tar.gz          # → airflow-bundle/ (dags/plugins/config/scripts)
```
