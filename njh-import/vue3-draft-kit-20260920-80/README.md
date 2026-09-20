# Vue 3 draft kit — 2026-09-20 (80th)

This directory holds one encrypted archive (`vue3-draft-kit-20260920-80.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260920-80.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- Restore measured native-button borders through structural ownership checks, preserving unrelated styles and refusing tampered blocks.
- Strengthen Excel upload error-display, generated dependencies, continuation, dataflow and review-screen coverage checks.
- Report unsupported source blocks and inferred usage as unverified instead of clean.
- Require compatible viewport metadata for route-sweep comparisons; retain warning and dialog observations.
- Register executable tests with the test runner and cover CRLF category output, idempotence and tamper refusal.
- Continue from kit 79 using the kit 80 instructions inside the encrypted archive. Regenerate both sweep reports; old reports are not proof of equivalence.
- Remaining Excel and behavior work is not claimed complete. Use the generated five-page report for operator feedback.
- Keep photo-report reopening instructions within the display-width contract even when the state directory has a long Unicode path.
