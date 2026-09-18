# Encrypted Airflow release 20260918_211611

## Archives

- `airflow-bundle-airflow-3.2.0-20260918_211611.7z`
  - SHA-256: `070de14c8dfa71c15a5a6b343c103befd49d8588785e67be57ecac6a8bd35f06`
  - Provenance: `provenance-airflow-3.2.0.md`

## Verify

Run this command in the directory containing this file.

```bash
shasum -a 256 -c SHA256SUMS.txt
```

## Extract

The archive password is delivered out of band and is not stored in this file.

Run the command pair for the archive you intend to use.

```bash
7z x airflow-bundle-airflow-3.2.0-20260918_211611.7z
cd dev
tar -zxvf airflow-bundle-airflow-3.2.0.tar.gz
```

## After extraction

Read `README.md` at the archive root first. Development steps are in `dev/AFTER-EXTRACTION.md`; production steps are in `prd/README_운영배포.md`.
