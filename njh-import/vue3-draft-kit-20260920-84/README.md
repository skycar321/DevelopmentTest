# Vue 3 draft kit — 2026-09-20 (84th)

This directory holds one encrypted archive (`vue3-draft-kit-20260920-84.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260920-84.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- Guarded removal of two unused AG Grid options; actual column definitions, row data and original font styling are preserved.
- Custom option consumers, unknown clone chains and changed provider behavior remain review items instead of being overwritten.
- Cross-version invalid-property warnings are attributed consistently without hiding raw warnings or qualifying warned interactions as equivalent.
- Retains the prior dashboard alignment, missing-menu handling, Excel corrections, report freshness and unchanged inherited configuration support.
- For an existing kit83 tree, use the internal README's Kit84 continuation section, not a fresh start. Older kit80/81/82 trees use the full Kit83 continuation sequence with this kit.
- The observed grid/font comparisons use local synthetic rows. Full business-flow parity, all exports and spreadsheet-library removal remain incomplete.
- Provider updates and dependent option repairs are validated as one complete plan so a fresh migration does not defer repairs to a second run. Unknown custom provider behavior remains a review item.
