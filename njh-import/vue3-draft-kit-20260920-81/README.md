# Vue 3 draft kit — 2026-09-20 (81th)

This directory holds one encrypted archive (`vue3-draft-kit-20260920-81.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260920-81.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- Correct the report upload identifier policy from the synthetic `c0` header to the actual report schema,
  with scoped UI/save mapping checks and refusal of edited contracts.
- Block Save after a failed replacement upload or during a current read; reject stale read completions.
  A successful new read restores Save. Prior displayed rows do not authorize saving a failed new file.
- Apply the correction to already-generated kit 80 report uploads, not only newly converted screens.
  Current code is left unchanged; edited policies, guards or session runtimes are retained for review.
- Keep source and session-runtime updates in the same conflict-aware backup/rollback transaction.
- Add bounded grouped export real-engine/actual-method comparisons without promoting unverified screen
  equivalence to a completed migration claim.
- Local browser evidence uses synthetic fixtures and a simulated DRM completion callback. Corporate DRM
  and company services were not contacted. Legacy `.xls` support and full phase 1 migration remain open.
