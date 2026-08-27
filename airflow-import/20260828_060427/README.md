# Airflow 반입판 20260828_060427

## 1. archive 무결성

반입 전 현재 디렉터리에서 checksum을 확인합니다.

```bash
shasum -a 256 -c SHA256SUMS.txt
```

- `airflow-bundle-airflow-2.6.3-20260828_060427.7z`
  - SHA-256: `ed69fd3cdeee80693a5523f698cf34399b08be239a1e2492496ad14908139b98`
- `airflow-bundle-airflow-3.2.1-20260828_060432.7z`
  - SHA-256: `0c98b5b518781597f6d23f168ed95cce16b4dd0be748dbc87aa2d554cf62d5d5`

### 해제

설치할 Airflow target 의 archive 를 해제합니다. **비밀번호는 이 파일에 없습니다** — 반입 담당자에게 별도로 전달됩니다.

먼저 배포 대상 서버의 Airflow 버전을 확인해 어느 archive 를 쓸지 정합니다.

```bash
# Airflow 컨테이너에서 직접 확인합니다(컨테이너 이름은 사내 구성에 맞춰 바꿉니다).
sudo podman exec --user airflow <airflow-scheduler-container> airflow version
# 2.x 로 나오면 2.6.3 archive, 3.x 로 나오면 3.2.1 archive 입니다.
```

```bash
7z x airflow-bundle-airflow-2.6.3-20260828_060427.7z        # 비밀번호 입력
7z x airflow-bundle-airflow-3.2.1-20260828_060432.7z        # 비밀번호 입력

# 위 명령이 풀어낸 tar 를 다시 해제하면 airflow-bundle/ 이 나옵니다.
tar -zxvf airflow-bundle-airflow-<target>.tar.gz
```

해제 결과는 `airflow-bundle/`(dags·plugins·config·scripts)과 같은 레벨의 참조 디렉터리(`docs/`, `batch-admin-reference/`, `sql/` 등)입니다.

## 2. 배치어드민 반영

배치어드민 운영자가 전달한 **승인된 원본 checkout 절대경로**를
`BATCH_ADMIN_SOURCE_DIR`로 export합니다. 이 값은 archive에 들어 있지 않습니다.
선택한 Airflow target archive를 전체 해제한 뒤 `batch-admin-reference/README.md`의
신규/수정 표를 따라 `src/**`, `mapper/`, `static/`, `templates/`, `sql/`을 그 checkout의
동일 경로에 반영합니다. 공유 host-owned 파일은 통째로 덮어쓰지 말고 명시된 변경점만 병합합니다.

> 경고: Java만 교체하면 css/js/html/mapper.xml 변경이 누락됩니다. 해당 경로를 함께 반영해야 합니다.

병합이 끝나면 다음 명령으로 실제 jar를 빌드합니다.

```bash
set -euo pipefail
: "${BATCH_ADMIN_SOURCE_DIR:?배치어드민 운영자에게 승인된 ui-batchadmin-main checkout 절대경로를 받아 export하세요}"
test -f "$BATCH_ADMIN_SOURCE_DIR/pom.xml"
cd "$BATCH_ADMIN_SOURCE_DIR"
mvn -Pdeploy package
test "$(find target -maxdepth 1 -type f -name '*.jar' | wc -l | tr -d ' ')" -ge 1
```

## 3. DB DDL 및 템플릿 seed

PostgreSQL 접속 상세는 archive나 README에서 만들지 않습니다. DB 운영자가 배포 호스트의
`$HOME/.pg_service.conf`와 `$HOME/.pgpass`에 승인된 접속을 설치하고, 그 service 이름만
`AIRFLOW_PG_SERVICE`로 전달합니다. 다음 블록은 그 source를 검증한 뒤 같은 연결로 DDL,
seed, 분모 확인을 수행합니다.

```bash
set -euo pipefail
: "${AIRFLOW_PG_SERVICE:?DB 운영자에게 승인된 PostgreSQL service 이름을 받아 export하세요}"
test -r "$HOME/.pg_service.conf"
test -r "$HOME/.pgpass"
export PGSERVICE="$AIRFLOW_PG_SERVICE"
psql -v ON_ERROR_STOP=1 -At -c 'select current_database(), current_user;'
psql -v ON_ERROR_STOP=1 -f batch-admin-reference/sql/airflow_control_ddl.sql
psql -v ON_ERROR_STOP=1 -f sql/seed_templates.sql

flow_templates="$(psql -v ON_ERROR_STOP=1 -At -c "
  select count(*)
    from sqmown.cm_airflow_apv_dtl d
    join sqmown.cm_apv_rqt p on p.apv_rqt_id = d.apv_rqt_id
   where p.apv_domain = 'AIRFLOW'
     and p.action_type = 'TEMPLATE'
     and p.rqtr_id = 'system'
     and d.dag_id like 'tpl_%'
     and d.template_kind = 'FLOW';")"
test "$flow_templates" -eq 15

parameter_templates="$(psql -v ON_ERROR_STOP=1 -At -c "
  select count(*)
    from sqmown.cm_airflow_apv_dtl d
    join sqmown.cm_apv_rqt p on p.apv_rqt_id = d.apv_rqt_id
   where p.apv_domain = 'AIRFLOW'
     and p.action_type = 'TEMPLATE'
     and p.rqtr_id = 'system'
     and d.dag_id = '__parameter_template__'
     and d.template_kind = 'PARAMETER';")"
test "$parameter_templates" -eq 11
```

`seed_templates.sql` 적용 뒤 플로우 템플릿 15개와 파라미터 템플릿 11개를 모두 확인해야 합니다.

## 4. Airflow 배포

**선행 조건**: 아래 4-1 의 설정 블록을 배포 호스트의 `~/.bashrc` 에 먼저 넣어 두어야 합니다.
이 절의 명령은 그 값을 읽어 쓰므로, 4-1 을 건너뛰면 첫 줄에서 멈춥니다.

선택한 archive 안의 tar.gz 를 Airflow 서버에 올려 해제한 뒤 실행합니다. 이름은 반입판에 따라
`airflow-bundle.tar.gz` 또는 타깃별 `airflow-bundle-airflow-<버전>.tar.gz` 입니다.

```bash
set -eo pipefail
tar -zxvf airflow-bundle*.tar.gz
cd airflow-bundle
# 이 블록을 실행하기 전에 아래 4-1 의 설정 블록을 ~/.bashrc 에 먼저 넣어야 합니다.
# 순서를 지키지 않으면 여기서 멈춥니다.
test -r "$HOME/.bashrc" || { echo "ERROR: $HOME/.bashrc에서 AIRFLOW_SECRET_* export를 읽을 수 없습니다." >&2; exit 1; }
set +u
# shellcheck disable=SC1090
. "$HOME/.bashrc"
set -u
./scripts/deploy.sh --check-python-batch-source dev
./scripts/deploy.sh dev
```

위 블록은 승인된 배포 호스트의 `~/.bashrc`에서 `AIRFLOW_SECRET_*`를 현재 shell로 먼저
가져온 뒤, 실제 배포와 동일한 python batch manifest source 사전검사를 통과해야 배포합니다.

### 4-1. 아카이브만 풀면 DAG 가 다 생기지 않습니다

python 배치 manifest 는 tar 에 들어 있지 않고 배포 시점에 서버36 원격 소스 또는 로컬 `pp` 에서
생성됩니다. 그래서 **아카이브만 해제해 배포하면 최종 68개 중 16개 DAG, 1,009개 중 55개 task만
파싱**됩니다(2026-08-27 실측; loader 생성 전 raw tree는 12개 DAG).
전량을 올리려면 배포 전에 manifest 원본을 붙여야 합니다.

```bash
# 아래 둘 중 승인된 source 하나를 ~/.bashrc에 설정한다.
# remote: python batch 운영자가 host/port/token/scan-dir 값을 별도 전달한다.
export PYBATCH_MANIFEST_SOURCE=remote
export AIRFLOW_SECRET_PYTHON_BATCH_HOST='<운영자가 전달한 서버36 host>'
export PYBATCH_AGENT_PORT='<운영자가 전달한 port>'
export AIRFLOW_SECRET_PYTHON_BATCH_AGENT_TOKEN='<운영자가 전달한 token>'
export PYBATCH_REMOTE_SCAN_DIR='<운영자가 전달한 scan-dir>'

# local: 승인된 pp tree를 Airflow host의 $BASE_DIR/pp에 배치한다.
# export PYBATCH_MANIFEST_SOURCE=local
# export BASE_DIR='<Airflow host-mount root>'
# test -d "$BASE_DIR/pp"

# 값을 저장한 뒤 새 shell에서 실제 배포와 같은 source를 확인한다.
# 없는 remote 설정이나 local 경로는 여기서 rc=3으로 거부된다.
set +u
. "$HOME/.bashrc"
set -u
case "$PYBATCH_MANIFEST_SOURCE" in
  remote)
    : "${AIRFLOW_SECRET_PYTHON_BATCH_HOST:?remote host가 없습니다}"
    : "${PYBATCH_AGENT_PORT:?remote port가 없습니다}"
    : "${AIRFLOW_SECRET_PYTHON_BATCH_AGENT_TOKEN:?remote token이 없습니다}"
    : "${PYBATCH_REMOTE_SCAN_DIR:?remote scan-dir가 없습니다}"
    ;;
  local)
    : "${BASE_DIR:?local manifest용 Airflow host-mount root가 없습니다}"
    test -d "$BASE_DIR/pp"
    ;;
  *)
    echo "ERROR: PYBATCH_MANIFEST_SOURCE는 remote 또는 local이어야 합니다" >&2
    exit 3
    ;;
esac
./scripts/deploy.sh --check-python-batch-source dev
```

배포 뒤에는 DAG 수를 반드시 눈으로 확인하세요. `68/68` 이 아니면 manifest 소스가 붙지 않은 것입니다.
