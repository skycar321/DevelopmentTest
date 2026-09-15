# Vue 3 draft kit — 2026-09-15 (36th)

This directory holds one encrypted archive (`vue3-draft-kit-20260915-36.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260915-36.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.1** from this channel (`v1.6.1/`).

## What changed since the 35th kit

- **Step 0c now asks the remote directly** (`git ls-remote --heads origin vue3-migration`) instead of
  looking for a local tracking ref. In a single-branch clone
  (`remote.origin.fetch = +refs/heads/dev:refs/remotes/origin/dev`) the tracking ref never exists
  even after the branch was pushed, so the 34th/35th kits printed no "원격 …" line and skipped the
  rename although the remote branch was there (observed in-house 2026-09-15). Before renaming, 0c
  fetches the branch with an explicit refspec so the rename works regardless of the fetch config.
  Verified against a single-branch clone of a bare origin.
- Step 6's rejected-push hint includes the same explicit fetch line.
- If you extracted the 35th kit and have not run `00c --yes` yet, switch to this kit; the plan screen
  must show the line `원격 origin/vue3-migration → origin/vue3-migration-old-<옛기준일>` before you
  confirm.
