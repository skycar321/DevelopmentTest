# Encrypted Airflow release 20260909_165602

## Archives

- `airflow-bundle-airflow-2.6.3-20260909_165602.7z`
  - SHA-256: `f8991cc269bee983770b17e0343d5b84738bb7ea6ada2445762804792119f5d9`
  - Provenance: `provenance-airflow-2.6.3.md`
- `airflow-bundle-airflow-3.2.0-20260909_165602.7z`
  - SHA-256: `51653a0dabb697419fb2c10d3f86c0ee17567eb0860ceadd859209d53dfab1d9`
  - Provenance: `provenance-airflow-3.2.0.md`

## Verify

Run this command in the directory containing this file.

```bash
shasum -a 256 -c SHA256SUMS.txt
```

## Extract

The archive password is delivered out of band and is not stored in this file.

The following block reads the runtime and extracts only its matching archive.

```bash
# BEGIN AIRFLOW TARGET-BOUND EXTRACTION
: "${AIRFLOW_SCHEDULER_CONTAINER:?Set the deployment scheduler container name}"
AIRFLOW_RUNTIME_OUTPUT="$(sudo podman exec --user airflow "$AIRFLOW_SCHEDULER_CONTAINER" airflow version 2>&1)" || { echo "ERROR: failed to read Airflow runtime version" >&2; exit 1; }
AIRFLOW_RUNTIME_VERSION="$(printf '%s\n' "$AIRFLOW_RUNTIME_OUTPUT" | sed -nE 's/^[[:space:]]*([0-9]+\.[0-9]+\.[0-9]+)[[:space:]]*$/\1/p')"
case "$AIRFLOW_RUNTIME_VERSION" in
  2.6.3)
    7z x airflow-bundle-airflow-2.6.3-20260909_165602.7z
    test -f airflow-bundle-airflow-2.6.3.tar.gz || { echo "ERROR: target tar is missing: airflow-bundle-airflow-2.6.3.tar.gz" >&2; exit 1; }
    tar -zxvf airflow-bundle-airflow-2.6.3.tar.gz
    ;;
  3.2.0)
    7z x airflow-bundle-airflow-3.2.0-20260909_165602.7z
    test -f airflow-bundle-airflow-3.2.0.tar.gz || { echo "ERROR: target tar is missing: airflow-bundle-airflow-3.2.0.tar.gz" >&2; exit 1; }
    tar -zxvf airflow-bundle-airflow-3.2.0.tar.gz
    ;;
  *) echo "ERROR: unsupported Airflow runtime: $AIRFLOW_RUNTIME_VERSION" >&2; exit 1 ;;
esac
# END AIRFLOW TARGET-BOUND EXTRACTION
```

## After extraction

Read `AFTER-EXTRACTION.md` for all operational instructions.








cat > /tmp/afdiag.sh <<'SCRIPT'
#!/bin/bash
set -uo pipefail
RT="${RT:-sudo podman}"; PREFIX="${PREFIX:-ff}"
W="${PREFIX}_airflow-worker_1"; S="${PREFIX}_airflow-scheduler_1"
R="${PREFIX}_redis_1";          P="${PREFIX}_postgres_1"
CELERY_APP="airflow.providers.celery.executors.celery_executor.app"
hr(){ printf '\n=== %s ===\n' "$*"; }
sql(){ echo "$1" | $RT exec -i "$P" sh -c 'psql -U "$POSTGRES_USER" -d "$POSTGRES_DB"' 2>&1; }
wexec(){ $RT exec --user airflow "$W" "$@" 2>&1; }

hr "0. 컨테이너"
$RT ps --format '{{.Names}}\t{{.Status}}' 2>&1 | grep -E "${PREFIX}_|NAMES"

hr "1. 워커가 살아서 응답하나 (celery ping)"
wexec celery -A "$CELERY_APP" inspect ping --timeout 10 \
  || echo "  ping 실패 = 워커 프로세스가 죽었거나 브로커에 못 붙었다"

hr "2. 워커가 '어느 큐' 를 듣고 있나   <- 3번과 다르면 그게 원인"
wexec celery -A "$CELERY_APP" inspect active_queues --timeout 10

hr "3. 스케줄러가 '어느 큐' 에 넣나 + queued 타임아웃"
$RT exec --user airflow "$S" sh -c '
  echo -n "executor           = "; airflow config get-value core executor 2>/dev/null
  echo -n "default_queue      = "; airflow config get-value operators default_queue 2>/dev/null
  echo -n "celery task_queue  = "; airflow config get-value celery task_default_queue 2>/dev/null || echo "(미설정)"
  echo -n "queued_timeout(s)  = "; airflow config get-value scheduler task_queued_timeout 2>/dev/null || echo "(기본 600)"
  echo -n "worker_concurrency = "; airflow config get-value celery worker_concurrency 2>/dev/null' 2>&1

hr "4. 워커 슬롯이 꽉 찼나"
wexec celery -A "$CELERY_APP" inspect stats --timeout 10 | grep -iE "concurrency|pool" | head
echo "--- 지금 붙들고 있는 태스크 ---"
wexec celery -A "$CELERY_APP" inspect active --timeout 10 | head -40

hr "5. 브로커(redis) 적재량"
$RT exec "$R" sh -c 'for q in default celery  " "$q"; redis-cli llen "$q"; done; echo "---키 ---"; redis-cli keys "*" | head -20' 2>&1

hr "6. 메타DB — 막혀 있는 태스크"
sql "SELECT state, count(*) FROM task_instanc','running','scheduled') GROUP BY state ORDERBY 2 DESC;"
echo "--- 가장 오래 queued 10건 ---"
sql "SELECT dag_id, task_id, queued_dttm, hostname, queue, pool FROM task_instance WHERE state='queued' ORDER BY
queued_dttm LIMIT 10;"
echo "--- '워커가 안 집어간' 형태의 최근 실패 10건 ---"
sql "SELECT dag_id, task_id, queued_dttm, stask_instance WHERE state='failed' AND (hostname IS NULL OR hostname='') AND try_number>=1 ORDER BY end_date DESC NULLS LAST LIMIT 10;"

hr "7. 스케줄러 최근 오류"
$RT logs --tail 4000 "$S" 2>&1 | grep -iE "aled for too long|adopt|Executor reports|ERROR"| tail -25

hr "8. 워커 최근 오류"
$RT logs --tail 4000 "$W" 2>&1 | grep -iE "Trrror|ERROR|refused|timeout" | tail -25
SCRIPT
bash /tmp/afdiag.sh 2>&1 | tee /tmp/afdiag.ou
