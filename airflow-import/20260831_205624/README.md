# Encrypted Airflow release 20260831_205624

## Archives

- `airflow-bundle-airflow-2.6.3-20260831_205624.7z`
  - SHA-256: `a32dbfcb8d50eb5c9b982244ac337543159efefc6149da5cb20b1aa66d58ff1f`
  - Provenance: `provenance-airflow-2.6.3.md`
- `airflow-bundle-airflow-3.2.0-20260831_205624.7z`
  - SHA-256: `2a3ec14498aa8dad6b0809765ab7ce0a164c49990188d588d9a0fa3fe023b2d8`
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
AIRFLOW_RUNTIME_VERSION="$(sudo podman exec --user airflow "$AIRFLOW_SCHEDULER_CONTAINER" airflow version)"
case "$AIRFLOW_RUNTIME_VERSION" in
  2.6.3)
    7z x airflow-bundle-airflow-2.6.3-20260831_205624.7z
    test -f airflow-bundle-airflow-2.6.3.tar.gz || { echo "ERROR: target tar is missing: airflow-bundle-airflow-2.6.3.tar.gz" >&2; exit 1; }
    tar -zxvf airflow-bundle-airflow-2.6.3.tar.gz
    ;;
  3.2.0)
    7z x airflow-bundle-airflow-3.2.0-20260831_205624.7z
    test -f airflow-bundle-airflow-3.2.0.tar.gz || { echo "ERROR: target tar is missing: airflow-bundle-airflow-3.2.0.tar.gz" >&2; exit 1; }
    tar -zxvf airflow-bundle-airflow-3.2.0.tar.gz
    ;;
  *) echo "ERROR: unsupported Airflow runtime: $AIRFLOW_RUNTIME_VERSION" >&2; exit 1 ;;
esac
# END AIRFLOW TARGET-BOUND EXTRACTION
```

## After extraction

Read `AFTER-EXTRACTION.md` for all operational instructions.
