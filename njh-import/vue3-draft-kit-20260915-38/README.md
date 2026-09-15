# Vue 3 draft kit — 2026-09-15 (38th)

This directory holds one encrypted archive (`vue3-draft-kit-20260915-38.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260915-38.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.1** from this channel (`v1.6.1/`).

## What changed since the 37th kit

- **Step 0c (fresh start) can be re-run after it stops halfway.** A field run on Windows moved the state
  folder and then failed to move the Vue 3 worktree folder (`mv: Device or resource busy` — a program held
  the folder). 0c now detects the already-moved state folder, continues with the remaining items, and
  moves the state folder last so a retry finds the config in place.
- **Linked worktrees are now detected by their `.git` file**, not by comparing paths with
  `git worktree list`. On Windows those paths never matched (`D:/…` vs `/d/…`), so 0c fell back to a
  plain `mv` and the following `git worktree prune` would have dropped the old worktree's registration.
  With `git worktree move` the registration travels with the folder.
- On a locked folder 0c prints what usually holds it on Windows (an editor, Explorer, another terminal,
  a leftover dev server or njh `node.exe`) and how to find the owner (Resource Monitor → CPU → handles).
- After the remote branch rename the stale `origin/<branch>` tracking ref is removed.
- Everything from the 37th kit (project `.npmrc` with the registry line only; credential layout section)
  is included unchanged.
