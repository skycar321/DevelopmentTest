# Vue 3 draft kit — 2026-09-20 (83th)

This directory holds one encrypted archive (`vue3-draft-kit-20260920-83.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260920-83.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- Restore the dashboard report-publication counter for the field label variant, including already partially upgraded trees.
- Keep access denial intact while handling the empty menu left by a cleared session.
- Read actual schema9 rendering observations in the photo report, including the observations already collected by step17; avoid obligatory standalone08b duplication.
- Refuse ambiguous freshness, incompatible comparison conditions and missing baseline evidence rather than claiming equivalence.
- Preserve baseline-identical inherited environment configuration during branch publication; reject new or changed configuration in outgoing history without printing values.
- Retain kit81 and kit82 fixes. Full phase1 migration, spreadsheet dependency removal, broader grid/chart behavior and remaining field findings are not complete.
