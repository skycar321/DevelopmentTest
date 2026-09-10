# Vue 3 draft kit — 2026-09-10 rev 3 (encrypted, lab tip 72d84cf5)

This directory publishes one archive, `vue3-draft-kit-20260910-3.7z`. It replaces rev 2, which had defects found by a four-axis audit of the guide.

- Format: 7z, AES-256, **encrypted headers** (`-mhe=on`). Without the password neither the file list nor the contents can be read.
- Password: the same archive password as the client kits in this channel. It is delivered separately, never through this repository.
- Integrity: compare against `sha256.txt` before extracting.

```bash
shasum -a 256 -c sha256.txt          # or: certutil -hashfile vue3-draft-kit-20260910-3.7z SHA256
7z x -p vue3-draft-kit-20260910-3.7z # enter the password when prompted
```

## What changed in rev 3

Each numbered step is now a single script under `run/`, so the operator runs one command per step instead of pasting a block. The scripts fail fast, assert they are in the intended worktree and branch, and were exercised end to end against a real Vue 2 source tree (152 single-file components) before publication: dependency install, one-command draft, publish commit and gap report all completed, and the produced lock file is present in the commit.

Defects fixed since rev 2:

- The publish step deleted the dependency lock file that the install step had just produced, because the draft tree never contains one. Every developer other than the lock owner would have been unable to install. The copy now excludes the lock and asserts it survived.
- The one-command draft has hard preconditions on the source manifest (runtime versions and the exact three build script commands). They were undocumented, so a deviation stopped the run mid-way with an unexplained refusal. A new check reports them before anything is created.
- The registry address was derived from the legacy project configuration. The dependencies for the new stack come from a different repository, so the derivation was wrong; the address is now entered once and passed on the command line, never written into a file that would be committed.
- A follow-up kit's parts update skipped plugins, styles, shared utilities and configuration, and could overwrite locally corrected parts without warning. It now covers every generated path and prints the diff before committing.
- The catch-up procedure judged "has a developer edited this screen" against a commit tag; replacing an untouched screen moved it away from that tag, so from the second round every screen read as edited. It now compares against a recorded hash of the generated draft, refreshed each round. It also lists operational changes outside the screen directory, which were previously dropped silently.
- The kit carried two descriptions of the same procedure that had drifted apart. There is now one, and the offline HTML is generated from it rather than maintained by hand.
- The library decision sheet still listed packages that the manifest no longer ships. It was regenerated from the current manifest.

Contents (high level): the migration skill bundle at the lab integration tip named in `delivery-line.json`, the step scripts, the library decision documents, the dual-runtime procedure, the draft gap-report tool, and reference analysis lists.
