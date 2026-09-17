# Encrypted Airflow release 20260918_083926

## Archives

- `airflow-bundle-airflow-3.2.0-20260918_083926.7z`
  - SHA-256: `1b0f927648acdb7c4d101b202856aa9543add271fd2b2fba15c4cb9b40955659`
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
7z x airflow-bundle-airflow-3.2.0-20260918_083926.7z
cd dev
tar -zxvf airflow-bundle-airflow-3.2.0.tar.gz
```

## After extraction

Read `README.md` at the archive root first. Development steps are in `dev/AFTER-EXTRACTION.md`; production steps are in `prd/README_운영배포.md`.
