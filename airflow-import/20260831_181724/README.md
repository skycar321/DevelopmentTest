# Encrypted Airflow release 20260831_181724

## Archives

- `airflow-bundle-airflow-2.6.3-20260831_181724.7z`
  - SHA-256: `d28d4745764cf93caaff77a41d78464807b02f62f0ab46a46fb3f17a4c684a10`
  - Provenance: `provenance-airflow-2.6.3.md`
- `airflow-bundle-airflow-3.2.1-20260831_181724.7z`
  - SHA-256: `8f7bc2a0c344fd776fa72b7105001812883344f7150c50c31b2c62bd115a9ec4`
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
    7z x airflow-bundle-airflow-2.6.3-20260831_181724.7z
    test -f airflow-bundle-airflow-2.6.3.tar.gz || { echo "ERROR: target tar is missing: airflow-bundle-airflow-2.6.3.tar.gz" >&2; exit 1; }
    tar -zxvf airflow-bundle-airflow-2.6.3.tar.gz
    ;;
  3.2.1)
    7z x airflow-bundle-airflow-3.2.1-20260831_181724.7z
    test -f airflow-bundle-airflow-3.2.1.tar.gz || { echo "ERROR: target tar is missing: airflow-bundle-airflow-3.2.1.tar.gz" >&2; exit 1; }
    tar -zxvf airflow-bundle-airflow-3.2.1.tar.gz
    ;;
  *) echo "ERROR: unsupported Airflow runtime: $AIRFLOW_RUNTIME_VERSION" >&2; exit 1 ;;
esac
# END AIRFLOW TARGET-BOUND EXTRACTION
```

## After extraction

Read `AFTER-EXTRACTION.md` for all operational instructions.
