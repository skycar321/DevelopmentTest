# Encrypted Airflow release 20260831_180024

## Archives

- `airflow-bundle-airflow-2.6.3-20260831_180024.7z`
  - SHA-256: `177d0d223da2f98e6af6d438701e7c99560a42df321479ff664bba825249058b`
  - Provenance: `provenance-airflow-2.6.3.md`
- `airflow-bundle-airflow-3.2.1-20260831_180024.7z`
  - SHA-256: `23e75ca4f6feb6fc966e41e71bf242a961030f7cd35a8c7c2400c72aaf893c3f`
  - Provenance: `provenance-airflow-3.2.1.md`

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
AIRFLOW_RUNTIME_VERSION="$(sudo podman exec --user airflow "$AIRFLOW_SCHEDULER_CONTAINER" airflow version)"
case "$AIRFLOW_RUNTIME_VERSION" in
  2.6.3)
    7z x airflow-bundle-airflow-2.6.3-20260831_180024.7z
    test -f airflow-bundle-airflow-2.6.3.tar.gz || { echo "ERROR: target tar is missing: airflow-bundle-airflow-2.6.3.tar.gz" >&2; exit 1; }
    tar -zxvf airflow-bundle-airflow-2.6.3.tar.gz
    ;;
  3.2.1)
    7z x airflow-bundle-airflow-3.2.1-20260831_180024.7z
    test -f airflow-bundle-airflow-3.2.1.tar.gz || { echo "ERROR: target tar is missing: airflow-bundle-airflow-3.2.1.tar.gz" >&2; exit 1; }
    tar -zxvf airflow-bundle-airflow-3.2.1.tar.gz
    ;;
  *) echo "ERROR: unsupported Airflow runtime: $AIRFLOW_RUNTIME_VERSION" >&2; exit 1 ;;
esac
# END AIRFLOW TARGET-BOUND EXTRACTION
```

## After extraction

Read `AFTER-EXTRACTION.md` for all operational instructions.
