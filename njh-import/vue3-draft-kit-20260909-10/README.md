# Vue 3 draft kit — 2026-09-09 (encrypted, tenth cut: lab tip c98ab69d)

This directory publishes one archive, `vue3-draft-kit-20260909-10.7z`.

- Format: 7z, AES-256, **encrypted headers** (`-mhe=on`). Without the password neither the file list nor the contents can be read.
- Password: the same archive password as the `njh-cli` kits in this channel. It is delivered separately, never through this repository.
- Integrity: compare against `sha256.txt` before extracting.

```bash
shasum -a 256 -c sha256.txt        # or: certutil -hashfile vue3-draft-kit-20260909-10.7z SHA256
7z x -p vue3-draft-kit-20260909-10.7z # enter the password when prompted
```

After extraction, start with the Korean README inside the extracted folder. It explains the import order:
install the matching `njh-cli` kit, regenerate the library decision sheet, prepare the A/B trees, run the one-command draft, compare, and report gaps.

Contents (high level): the `vue3-migration` skill bundle at the lab integration tip named in `delivery-line.json`, the library decision documents,
the A/B dual-runtime procedure, the draft gap-report tool, and reference analysis lists.


Tenth cut — the one to import; earlier same-day cuts were removed from this channel because their to-be manifest still declared the enterprise grid package and the quarantined legacy test runner. Skill bundle at lab tip c98ab69d (delivery-manifest codemod applies the 2026-09-08 owner decisions: community-only grid, no mocha runner, no `test` script). The runbook now reads the skill tip from `delivery-line.json` instead of hard-coding it, and states that the to-be manifest carries neither the enterprise grid nor a test runner. To-be manifest: 22 runtime + 15 development dependencies.
