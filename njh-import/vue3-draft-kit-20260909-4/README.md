# Vue 3 draft kit — 2026-09-09 (encrypted, fourth cut: lab tip f591bd4e)

This directory publishes one archive, `vue3-draft-kit-20260909-4.7z`.

- Format: 7z, AES-256, **encrypted headers** (`-mhe=on`). Without the password neither the file list nor the contents can be read.
- Password: the same archive password as the `njh-cli` kits in this channel. It is delivered separately, never through this repository.
- Integrity: compare against `sha256.txt` before extracting.

```bash
shasum -a 256 -c sha256.txt        # or: certutil -hashfile vue3-draft-kit-20260909-4.7z SHA256
7z x -p vue3-draft-kit-20260909-4.7z # enter the password when prompted
```

After extraction, start with the Korean README inside the extracted folder. It explains the import order:
install the matching `njh-cli` kit, regenerate the library decision sheet, prepare the A/B trees, run the one-command draft, compare, and report gaps.

Contents (high level): the `vue3-migration` skill bundle at the lab integration tip named in `delivery-line.json`, the library decision documents,
the A/B dual-runtime procedure, the draft gap-report tool, and reference analysis lists.


Fourth cut of the same day. The skill bundle is unchanged (lab tip f591bd4e; 129 of 152 screens fully automatic, alert part 90 of 90). What changed is the kit around it:

- `docs/` now carries an ordered in-house runbook (Markdown and a self-contained HTML page with the same content): measure the current web project, freeze the Vue 2 tree, create the sibling Vue 3 worktree and branch, install the to-be dependencies, run the one-command draft, publish the draft branch, run A/B, configure njh-cli for the DevAI connection (v1.5.564), start per-screen hand work with ready-made prompts, and send the gap report.
- The to-be dependency manifest (`assets/package.tobe.json`, 40 packages) that the previous kits' README referred to is now actually included.
- The kit README points to the runbook first and names njh-cli v1.5.564 as the matching client kit.
