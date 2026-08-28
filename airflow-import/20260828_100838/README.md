# Encrypted Airflow release 20260828_100838

## Archives

- `airflow-bundle-airflow-2.6.3-20260828_100838.7z`
  - SHA-256: `d8be8ef9a25d22a3d8741387c0818359056648c52f46654df195032a02b25b09`
  - Provenance: `provenance-airflow-2.6.3.md`
- `airflow-bundle-airflow-3.2.1-20260828_100852.7z`
  - SHA-256: `488e81f84c8a69e6637fe94a6081c6b837667e3f822fb161d3cda8faebdb927e`
  - Provenance: `provenance-airflow-3.2.1.md`

## Verify

Run this command in the directory containing this file.

```bash
shasum -a 256 -c SHA256SUMS.txt
```

## Extract

The archive password is delivered out of band and is not stored in this file.

Run the command pair for the archive you intend to use.

```bash
7z x airflow-bundle-airflow-2.6.3-20260828_100838.7z
tar -zxvf airflow-bundle-airflow-2.6.3.tar.gz
7z x airflow-bundle-airflow-3.2.1-20260828_100852.7z
tar -zxvf airflow-bundle-airflow-3.2.1.tar.gz
```

## After extraction

Read `AFTER-EXTRACTION.md` for all operational instructions.
