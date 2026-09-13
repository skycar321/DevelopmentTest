# Encrypted Airflow release 20260910_124730

## Archives

- `airflow-bundle-airflow-2.6.3-20260910_124730.7z`
  - SHA-256: `397e05a356c338b91a9cbbdd71ed635dd537d83a5b455829d17a1cc5b06232e7`
  - Provenance: `provenance-airflow-2.6.3.md`
- `airflow-bundle-airflow-3.2.0-20260910_124730.7z`
  - SHA-256: `85704270351e104c2359842d1e2f7a584bc9756339aed369f46f2a212d97f17b`
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
    7z x airflow-bundle-airflow-2.6.3-20260910_124730.7z
    test -f airflow-bundle-airflow-2.6.3.tar.gz || { echo "ERROR: target tar is missing: airflow-bundle-airflow-2.6.3.tar.gz" >&2; exit 1; }
    tar -zxvf airflow-bundle-airflow-2.6.3.tar.gz
    ;;
  3.2.0)
    7z x airflow-bundle-airflow-3.2.0-20260910_124730.7z
    test -f airflow-bundle-airflow-3.2.0.tar.gz || { echo "ERROR: target tar is missing: airflow-bundle-airflow-3.2.0.tar.gz" >&2; exit 1; }
    tar -zxvf airflow-bundle-airflow-3.2.0.tar.gz
    ;;
  *) echo "ERROR: unsupported Airflow runtime: $AIRFLOW_RUNTIME_VERSION" >&2; exit 1 ;;
esac
# END AIRFLOW TARGET-BOUND EXTRACTION
```

## After extraction

Read `AFTER-EXTRACTION.md` for all operational instructions.
