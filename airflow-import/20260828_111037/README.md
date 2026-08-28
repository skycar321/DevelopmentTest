# Encrypted Airflow release 20260828_111037

## Archives

- `airflow-bundle-airflow-2.6.3-20260828_111037.7z`
  - SHA-256: `7dc5a7e83405761ce29ddc0169aca1434c473a149cad45add3e6a5433c01c9f5`
  - Provenance: `provenance-airflow-2.6.3.md`
- `airflow-bundle-airflow-3.2.1-20260828_111044.7z`
  - SHA-256: `43f388de865fe9a182215861e663722d9e600d1aff147e090fe8516a9c9242f2`
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
7z x airflow-bundle-airflow-2.6.3-20260828_111037.7z
tar -zxvf airflow-bundle-airflow-2.6.3.tar.gz
7z x airflow-bundle-airflow-3.2.1-20260828_111044.7z
tar -zxvf airflow-bundle-airflow-3.2.1.tar.gz
```

## After extraction

Read `AFTER-EXTRACTION.md` for all operational instructions.
