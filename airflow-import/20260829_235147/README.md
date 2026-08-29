# Encrypted Airflow release 20260829_235147

## Archives

- `airflow-bundle-airflow-2.6.3-20260829_235147.7z`
  - SHA-256: `3f4e43c78d87f7fddd2213fbfd7f8dcff8da9ad8b5ce4d1ddc26e56c0893d9b2`
  - Provenance: `provenance-airflow-2.6.3.md`
- `airflow-bundle-airflow-3.2.1-20260829_235147.7z`
  - SHA-256: `1f0f22d4998b16af97d233c33b8c49033acb45ffa5b650950224a4f439688b24`
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
    7z x airflow-bundle-airflow-2.6.3-20260829_235147.7z
    test -f airflow-bundle-airflow-2.6.3.tar.gz || { echo "ERROR: target tar is missing: airflow-bundle-airflow-2.6.3.tar.gz" >&2; exit 1; }
    tar -zxvf airflow-bundle-airflow-2.6.3.tar.gz
    ;;
  3.2.1)
    7z x airflow-bundle-airflow-3.2.1-20260829_235147.7z
    test -f airflow-bundle-airflow-3.2.1.tar.gz || { echo "ERROR: target tar is missing: airflow-bundle-airflow-3.2.1.tar.gz" >&2; exit 1; }
    tar -zxvf airflow-bundle-airflow-3.2.1.tar.gz
    ;;
  *) echo "ERROR: unsupported Airflow runtime: $AIRFLOW_RUNTIME_VERSION" >&2; exit 1 ;;
esac
# END AIRFLOW TARGET-BOUND EXTRACTION
```

## After extraction

Read `AFTER-EXTRACTION.md` for all operational instructions.
