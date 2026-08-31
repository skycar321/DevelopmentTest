# Encrypted Airflow release 20260831_121029

## Archives

- `airflow-bundle-airflow-2.6.3-20260831_121029.7z`
  - SHA-256: `1bd95cd47f88ec763ea0be5b1c8202b885c05534b7393e867ef45f199507a1cf`
  - Provenance: `provenance-airflow-2.6.3.md`
- `airflow-bundle-airflow-3.2.1-20260831_121029.7z`
  - SHA-256: `1804f4bce7f65fe2d0d761188a14460ff4c2b433dbc52e47f897037680e04f92`
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
    7z x airflow-bundle-airflow-2.6.3-20260831_121029.7z
    test -f airflow-bundle-airflow-2.6.3.tar.gz || { echo "ERROR: target tar is missing: airflow-bundle-airflow-2.6.3.tar.gz" >&2; exit 1; }
    tar -zxvf airflow-bundle-airflow-2.6.3.tar.gz
    ;;
  3.2.1)
    7z x airflow-bundle-airflow-3.2.1-20260831_121029.7z
    test -f airflow-bundle-airflow-3.2.1.tar.gz || { echo "ERROR: target tar is missing: airflow-bundle-airflow-3.2.1.tar.gz" >&2; exit 1; }
    tar -zxvf airflow-bundle-airflow-3.2.1.tar.gz
    ;;
  *) echo "ERROR: unsupported Airflow runtime: $AIRFLOW_RUNTIME_VERSION" >&2; exit 1 ;;
esac
# END AIRFLOW TARGET-BOUND EXTRACTION
```

## After extraction

Read `AFTER-EXTRACTION.md` for all operational instructions.
