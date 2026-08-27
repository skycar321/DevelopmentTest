# Airflow 반입판 20260827_185932

## 1. archive 무결성

반입 전 현재 디렉터리에서 checksum을 확인합니다.

```bash
shasum -a 256 -c SHA256SUMS.txt
```

- `airflow-bundle-airflow-2.6.3-20260827_185932.7z`
  - SHA-256: `b6de2fe887faa1373871a5e06eae5c99dc3f178aeb973167a8bc93813e942f1c`
- `airflow-bundle-airflow-3.2.1-20260827_185939.7z`
  - SHA-256: `af7c84103d3db3cbb89f433c1a9966e61bdeea03b6d19a6bc09eee8dc5721221`

## 2. 배치어드민 반영

선택한 Airflow target archive를 전체 해제한 뒤 `batch-admin-reference/src/**`, `mapper/`,
`static/`, `templates/`, `sql/`을 사내 `ui-batchadmin-main` 원본 경로에 함께 반영하고 jar를 재빌드합니다.

> 경고: Java만 교체하면 css/js/html/mapper.xml 변경이 누락됩니다. 해당 경로를 함께 반영해야 합니다.

## 3. DB DDL 및 템플릿 seed

```bash
set -euo pipefail
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
     and d.template_kind is null;")"
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

선택한 archive 안의 tar.gz 를 Airflow 서버에 올려 해제한 뒤 실행합니다. 이름은 반입판에 따라
`airflow-bundle.tar.gz` 또는 타깃별 `airflow-bundle-airflow-<버전>.tar.gz` 입니다.

```bash
set -eo pipefail
tar -zxvf airflow-bundle*.tar.gz
cd airflow-bundle
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
# 실제 배포와 동일한 조건으로 source를 확인한다. 없는 local 경로도 여기서 rc=3으로 거부된다.
./scripts/deploy.sh --check-python-batch-source dev
```

배포 뒤에는 DAG 수를 반드시 눈으로 확인하세요. `68/68` 이 아니면 manifest 소스가 붙지 않은 것입니다.
```
