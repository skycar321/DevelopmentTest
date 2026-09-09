# Vue 3 draft kit — 2026-09-09 (encrypted, fifth cut: lab tip f591bd4e)

This directory publishes one archive, `vue3-draft-kit-20260909-5.7z`.

- Format: 7z, AES-256, **encrypted headers** (`-mhe=on`). Without the password neither the file list nor the contents can be read.
- Password: the same archive password as the `njh-cli` kits in this channel. It is delivered separately, never through this repository.
- Integrity: compare against `sha256.txt` before extracting.

```bash
shasum -a 256 -c sha256.txt        # or: certutil -hashfile vue3-draft-kit-20260909-5.7z SHA256
7z x -p vue3-draft-kit-20260909-5.7z # enter the password when prompted
```

After extraction, start with the Korean README inside the extracted folder. It explains the import order:
install the matching `njh-cli` kit, regenerate the library decision sheet, prepare the A/B trees, run the one-command draft, compare, and report gaps.

Contents (high level): the `vue3-migration` skill bundle at the lab integration tip named in `delivery-line.json`, the library decision documents,
the A/B dual-runtime procedure, the draft gap-report tool, and reference analysis lists.


Fifth cut of the same day; supersedes the fourth. The skill bundle is unchanged (lab tip f591bd4e; 129 of 152 screens fully automatic). The in-house runbook in `docs/` was reworked so it is easier to follow: the four paths are written once into an `env.sh` that every later step sources, the draft lands on the `vue3-draft` branch as a single commit, optional work (Nexus survey, decision sheet) moved behind the gap report, a step for importing later kits (common-part updates only, without touching developer-owned screens) was added, and the HTML copy's checklist layout was fixed. The to-be manifest (`assets/package.tobe.json`, 40 packages) is included as in the fourth cut.
