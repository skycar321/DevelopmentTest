# Encrypted Airflow release 20260902_234139

## Archives

- `airflow-bundle-airflow-2.6.3-20260902_234139.7z`
  - SHA-256: `60f5111a5be36c2146b3c7d5251308255b1ecbec941d6b513bc71998bee5f374`
  - Provenance: `provenance-airflow-2.6.3.md`
- `airflow-bundle-airflow-3.2.0-20260902_234139.7z`
  - SHA-256: `169d203fc579c28196e4c5c4025e6358152eea329a931f8859d704a5be6ed47a`
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
    7z x airflow-bundle-airflow-2.6.3-20260902_234139.7z
    test -f airflow-bundle-airflow-2.6.3.tar.gz || { echo "ERROR: target tar is missing: airflow-bundle-airflow-2.6.3.tar.gz" >&2; exit 1; }
    tar -zxvf airflow-bundle-airflow-2.6.3.tar.gz
    ;;
  3.2.0)
    7z x airflow-bundle-airflow-3.2.0-20260902_234139.7z
    test -f airflow-bundle-airflow-3.2.0.tar.gz || { echo "ERROR: target tar is missing: airflow-bundle-airflow-3.2.0.tar.gz" >&2; exit 1; }
    tar -zxvf airflow-bundle-airflow-3.2.0.tar.gz
    ;;
  *) echo "ERROR: unsupported Airflow runtime: $AIRFLOW_RUNTIME_VERSION" >&2; exit 1 ;;
esac
# END AIRFLOW TARGET-BOUND EXTRACTION
```

## After extraction

Read `AFTER-EXTRACTION.md` for all operational instructions.
