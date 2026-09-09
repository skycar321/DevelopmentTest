# Vue 3 draft kit — 2026-09-09 (encrypted, seventh cut: lab tip f591bd4e)

This directory publishes one archive, `vue3-draft-kit-20260909-7.7z`.

- Format: 7z, AES-256, **encrypted headers** (`-mhe=on`). Without the password neither the file list nor the contents can be read.
- Password: the same archive password as the `njh-cli` kits in this channel. It is delivered separately, never through this repository.
- Integrity: compare against `sha256.txt` before extracting.

```bash
shasum -a 256 -c sha256.txt        # or: certutil -hashfile vue3-draft-kit-20260909-7.7z SHA256
7z x -p vue3-draft-kit-20260909-7.7z # enter the password when prompted
```

After extraction, start with the Korean README inside the extracted folder. It explains the import order:
install the matching `njh-cli` kit, regenerate the library decision sheet, prepare the A/B trees, run the one-command draft, compare, and report gaps.

Contents (high level): the `vue3-migration` skill bundle at the lab integration tip named in `delivery-line.json`, the library decision documents,
the A/B dual-runtime procedure, the draft gap-report tool, and reference analysis lists.


Seventh cut of the same day; supersedes the sixth. Skill bundle unchanged (lab tip f591bd4e). Runbook change only: the draft is now generated from a clean snapshot of the remote tip (a detached worktree at the freeze tag) instead of the working checkout, so a dirty local Vue 2 tree neither blocks the procedure nor leaks into the draft; the freeze tag is created on `origin/<base>` without touching the local checkout. Both runbook copies (Markdown, self-contained HTML) and the to-be manifest are included.
