# Vue 3 draft kit — 2026-09-15 (39th)

This directory holds one encrypted archive (`vue3-draft-kit-20260915-39.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260915-39.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.1** from this channel (`v1.6.1/`).

## What changed since the 38th kit

- **Step 5 no longer refuses every draft.** The skill's pinned digest for
  `assets/original-preimages.json` was stale: a 2026-09-14 commit sanitized two path entries inside
  that manifest without moving the pin, so the integrity check threw
  `ONE_SHOT_BUNDLED_INPUT_INTEGRITY` and the driver reported `DRAFT_REFUSED` (field run, 2026-09-15).
  The pin now matches the shipped bytes; the unit test `vue3_portable_inputs.test.mjs` passes 5/5.
  Behaviour is unchanged otherwise — an in-house as-is never matches the lab original, so the driver
  continues in derived mode as before.
- Step 5 now stops right after a refusal instead of printing follow-up errors about `.kit-tip` and
  `.gitignore` in a draft folder that was never created.
- Everything from the 37th/38th kits (registry-only project `.npmrc`; resumable step 0c) is included.
