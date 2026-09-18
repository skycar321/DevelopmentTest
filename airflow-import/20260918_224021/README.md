# Encrypted Airflow release 20260918_224021

## Archives

- `airflow-bundle-airflow-3.2.0-20260918_224021.7z`
  - SHA-256: `a4fcebfd9028701d0c3c2935a63634d52df26f61a245400cac6acf87e6afec49`
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
7z x airflow-bundle-airflow-3.2.0-20260918_224021.7z
cd dev
tar -zxvf airflow-bundle-airflow-3.2.0.tar.gz
```

## After extraction

Read `README.md` at the archive root first. Development steps are in `dev/AFTER-EXTRACTION.md`; production steps are in `prd/README_운영배포.md`.
