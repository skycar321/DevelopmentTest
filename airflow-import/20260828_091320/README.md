# Encrypted Airflow release 20260828_091320

## Archives

- `airflow-bundle-airflow-2.6.3-20260828_091320.7z`
  - SHA-256: `e3d92cb8f972a5ee5a15da4caf51cb791fb8df280c5e0cfdc89324dc3c9ba98a`
  - Provenance: `provenance-airflow-2.6.3.md`
- `airflow-bundle-airflow-3.2.1-20260828_091330.7z`
  - SHA-256: `296cffad410aa13e1bd7216570f9bdf9eab26509b661e1c1f738681111b8ba76`
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
7z x airflow-bundle-airflow-2.6.3-20260828_091320.7z
tar -zxvf airflow-bundle-airflow-2.6.3.tar.gz
7z x airflow-bundle-airflow-3.2.1-20260828_091330.7z
tar -zxvf airflow-bundle-airflow-3.2.1.tar.gz
```

## After extraction

Read `AFTER-EXTRACTION.md` for all operational instructions.
