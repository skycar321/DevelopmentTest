# Vue 3 draft kit — 2026-09-20 (82th)

This directory holds one encrypted archive (`vue3-draft-kit-20260920-82.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260920-82.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- Restore centered grouped headers on the synthesis statistics screen, including horizontal scrolling.
- Restore the two static disabled date fields' text position, size and disabled color on that screen.
- Keep restoration scoped to measured template shapes and installed CSS prerequisites; changed
  inputs, owned styles and edited generated blocks remain for review instead of being overwritten.
- Preserve generated block position when another transformation appends unrelated styles; repeated
  CSS/layout stages are checked for no additional writes.
- Retain all kit81 Excel corrections. No spreadsheet package is removed by this update.
- Local verification uses synthetic fixtures, two viewport widths and both statistics tabs.
  Other CSS/behavior differences, legacy XLS support and full phase1 migration remain open.
