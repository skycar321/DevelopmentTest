# Vue 3 draft kit — 2026-09-15 (37th)

This directory holds one encrypted archive (`vue3-draft-kit-20260915-37.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260915-37.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.1** from this channel (`v1.6.1/`).

## What changed since the 36th kit

- **Project `.npmrc` with the registry line is back (address only).** Step 4 writes `registry=<npm-group>`
  into the Vue 3 worktree's `.npmrc` — the team convention the as-is tree followed before the Nexus
  address changed. Credentials stay in the user's `~/.npmrc` (step 0-credentials); step 6 now refuses to
  commit only when the project `.npmrc` contains `_auth`/`_authToken`/password, instead of refusing any
  `.npmrc`.
- `README-사내-반입.md` gained a short section on where each value lives: registry address (env.sh +
  project `.npmrc`), Nexus credentials (`~/.npmrc` only), app modes (`.env`, `.env.development`,
  `.env.production`, `.env.local` — no secrets in a front-end `.env`), and the CI pattern for build
  agents.
- Scripts otherwise unchanged from the 36th kit (step 0c fresh start with remote branch rename via
  `ls-remote`).
