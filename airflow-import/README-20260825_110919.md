# Airflow 제어 반입판 — 2026-08-25 (**DML 정본 운영 반영 + 노드 파라미터 시각화**)

- 번들: `airflow-bundle-20260825_110919.7z` (1173329 bytes)
- sha256: `74670d0efab71a1dbaf0115b0ff24cf308dd73f9ec687cdaaeec77e58e1ac409`
- source commit: `ff26ade96`
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

## 함께 들어간 것 — 브랜치 머지 런북

`docs/GIT-BRANCH-MERGE-RUNBOOK.md` + `docs/branch_report.sh`

**"두 브랜치가 같은 파일을 각자 고쳤다"** 상황을 안전하게 푸는 절차입니다. 특정 파일에 매여
있지 않습니다 — 스크립트가 파일명을 받지 않고 스스로 찾아 분류합니다.

```bash
bash branch_report.sh --help          # 사용법
bash branch_report.sh                 # 기본: main dev airflow
bash branch_report.sh main dev        # 브랜치를 인자로
REMOTE=upstream bash branch_report.sh main dev
FOCUS="pom.xml" bash branch_report.sh
OUT_DIR=/tmp    bash branch_report.sh
```

파일 하나만 복사해 두면 됩니다. 의존성도 설정도 없고, 저장소 안이면 어느 하위 디렉토리에서
실행해도 됩니다. **읽기 전용입니다** — 원격을 fetch 만 하고 작업 트리·브랜치·커밋을 건드리지 않습니다.

실행하면 파일을 열지 않아도 화면에 결론이 바로 나옵니다:

```
────── 요약 ──────
충돌 후보 파일: 동일 1건 / 상이 1건 / 한쪽만 0건
  ↳ 손봐야 할 파일:
      상이   app.txt
머지 예행:
  --- main 를 dev 에 머지하면 ---
    충돌 발생 — 아래 경로들:
      app.txt
같은 내용/다른 SHA 로 이미 양쪽에 있는 커밋: 2건  (cherry-pick 하지 말고 머지할 것)
──────────────────
```

리포트가 답하는 것:

| 섹션 | 답 |
|---|---|
| [4] 충돌 후보 | 양쪽이 함께 건드린 파일을 **동일 / 상이 / 한쪽만** 으로 판정 |
| [5] 머지 예행 | `git merge-tree` 로 **워크트리를 건드리지 않고** 충돌을 미리 계산 |
| [6] cherry-mark | 같은 내용을 서로 다른 커밋으로 갖고 있는지 `=` 로 표시 |

핵심은 **`동일` 과 `=`** 입니다. 같은 수정이 양쪽에 이미 있으면 3-way 머지가 조용히 통과시킵니다.
그걸 모르고 cherry-pick 하면 중복 변경이 되어 나중에 유령 충돌이 납니다.

런북에는 안전망(백업 브랜치), 충돌 판단 원칙, 검증 절차, IntelliJ 대응표가 함께 들어 있습니다.
**공유 브랜치 rebase 와 cherry-pick 은 쓰지 않습니다.**

---

## ⚠ 이 판의 검증 한계 — 그대로 적습니다

- **화면 육안 검수가 끝나지 않았습니다.** 위 두 UI 변경은 정적 검증까지만 했고
  1920 실렌더 기하는 측정하지 못했습니다(측정 장비 쪽 사정). 지난 배지 잘림 사고와 같은 계열이라
  **반입 테스트용으로 보시고, 화면은 직접 확인**해 주세요.
- 계약 원장의 근거 해시 재핀이 진행 중이라 커버리지 장치 하나가 적색입니다.
  번들 자체 검증(`verify_bundle` PASS 25 / FAIL 0)과 패키징 게이트는 통과했습니다.

## 압축 해제

```bash
7z x airflow-bundle-20260825_110919.7z   # 비밀번호 입력
tar -zxvf airflow-bundle.tar.gz          # → airflow-bundle/ (dags/plugins/config/scripts)
```
