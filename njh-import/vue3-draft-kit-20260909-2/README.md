# Vue 3 draft kit — 2026-09-09 (encrypted, second cut: lab tip 9ee6a364)

This directory publishes one archive, `vue3-draft-kit-20260909-2.7z`.

- Format: 7z, AES-256, **encrypted headers** (`-mhe=on`). Without the password neither the file list nor the contents can be read.
- Password: the same archive password as the `njh-cli` kits in this channel. It is delivered separately, never through this repository.
- Integrity: compare against `sha256.txt` before extracting.

```bash
shasum -a 256 -c sha256.txt        # or: certutil -hashfile vue3-draft-kit-20260909-2.7z SHA256
7z x -p vue3-draft-kit-20260909-2.7z # enter the password when prompted
```

After extraction, start with the Korean README inside the extracted folder. It explains the import order:
install the matching `njh-cli` kit, regenerate the library decision sheet, prepare the A/B trees, run the one-command draft, compare, and report gaps.

Contents (high level): the `vue3-migration` skill bundle at the lab integration tip named in `delivery-line.json`, the library decision documents,
the A/B dual-runtime procedure, the draft gap-report tool, and reference analysis lists.

Second cut of the same day. Changes since `vue3-draft-kit-20260909`: the one-command draft now completes 129 of 152 screens fully automatically
(was 85) — Excel export adoption 20/36, chart wrappers, sign-in screens and the completion accounting for screens without a component assignment.
The remaining 23 screens carry precise refusal reasons and hand-work instructions in their receipts.
