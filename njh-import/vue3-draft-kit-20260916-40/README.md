# Vue 3 draft kit — 2026-09-16 (40th)

This directory holds one encrypted archive (`vue3-draft-kit-20260916-40.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260916-40.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.1** from this channel (`v1.6.1/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- **Step 6 no longer commits the draft driver's receipts.** `analysis/draft-project/{result,conversion,plan}.json`
  (about 340k lines) were being copied into the Vue 3 worktree and committed with the app; the rehearsal of the
  39th kit showed them as 35 of the 36 k changed lines. They stay in the draft folder only (removed from the
  worktree when the base tag never had them).
- **Step 6 appends agent-artifact rules to the as-is `.gitignore`** (`.njh/`, `.tmp/`, `ai-repair-rejected/`,
  `analysis/draft-project/`) instead of only refusing when they appear. The as-is file is otherwise untouched.
  Later steps (18 driver, 8c repair) run njh inside the worktree, so without the rules the 2026-09-11 `.njh/` commit
  would repeat.
- The pre-ship rehearsal now also plants `.env*` files and `.njh/` folders in the fixture and asserts after step 6:
  no artifacts tracked, every `.env*` still tracked and byte-identical, `.npmrc` is the registry line only,
  `.gitignore` carries the rules.
- Everything from the 39th kit (preimages pin fix, registry-only `.npmrc`, resumable 0c) is included.
- `tools/verify-parts.mjs` gained `--ledger <result.json>`; step 6 passes the draft folder's ledger so the
  part-landing check keeps using the driver receipt after the worktree copy is removed (the first 40th rehearsal
  caught exactly this: without the ledger the check fell back to path rules and misjudged one composable as absent).
- **Step 5 (and 18) no longer die on Windows before the build starts.** Node 22 refuses to spawn `npm.cmd`
  without a shell (EINVAL, the CVE-2024-27980 mitigation); the draft driver's build gate caught that as
  `DRAFT_BUILD_GATE_FAILED` with no build log (field runs on 2026-09-10 and 2026-09-15). The three spawn sites
  now go through one helper (`scripts/npm_invoke.mjs`): on Windows, node runs `node_modules\\npm\\bin\\npm-cli.js`
  beside `node.exe` directly (no batch file, no shell); only if that file is missing does it fall back to `npm.cmd`
  under a shell. The gate records the exception detail and which path was used (`npmVia`), and the rehearsal lint
  refuses any npm spawn outside the helper.
