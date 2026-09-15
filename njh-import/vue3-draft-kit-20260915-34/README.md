# Vue 3 draft kit — 2026-09-15 (34th)

This directory holds one encrypted archive (`vue3-draft-kit-20260915-34.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260915-34.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` inside the extracted folder, and follow
`docs/뷰3-초안-사내-런북.md` (or the matching `.html`) as the single source of truth for the
procedure. Step 0b (`run/00b-resume.sh`) prints which step to continue from.

## Requires

- The njh-cli replacement kit **v1.6.1** from this channel (`v1.6.1/`).

## What changed since the 33rd kit

- **Step 0c also renames the remote branch.** A fresh start used to rename only the local
  `vue3-migration`; the remote copy kept the old draft history, so the next round's step 6 `git push`
  would be rejected as non-fast-forward. Step 0c now pushes the old history to
  `origin/vue3-migration-old-<previous-baseline-date>` and then frees the original name — nothing is
  deleted. If that fails (credentials or a protected branch) it prints the two commands to run by hand.
  Verified end to end against a bare origin: old branch renamed, new baseline tagged on `origin/dev`,
  new worktrees created, new branch pushed.
- Step 6 now distinguishes a rejected push (remote has a different history) from an authentication
  failure and prints the fix for the former.
- `README-사내-반입.md` 33rd-kit section explains the remote rename.
- Everything else is the 33rd kit unchanged.
