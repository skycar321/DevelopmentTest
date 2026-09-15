# Vue 3 draft kit — 2026-09-15 (35th)

This directory holds one encrypted archive (`vue3-draft-kit-20260915-35.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260915-35.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` inside the extracted folder — its new first
section "지금 바로 시작하기" is the exact command order for a fresh start — and follow
`docs/뷰3-초안-사내-런북.md` (or the matching `.html`) for the details of each step.

## Requires

- The njh-cli replacement kit **v1.6.1** from this channel (`v1.6.1/`).

## What changed since the 34th kit

- `README-사내-반입.md` opens with the fresh-start command order (source kit.sh → 0c plan → 0c --yes →
  re-source → 0b → 1…7 → 8a/8b → 15 → 16 → 18 (5 findings, then all) → 9 → 8b → 17) and the three
  pre-checks (client version 1.6.1, previous-round settings present, shell outside the folders that
  will be moved). Documentation only; scripts are the 34th kit unchanged.
- Reminder: the 33rd kit's step 0c does **not** rename the remote branch. If you extracted the 33rd
  kit and have not yet run `00c --yes`, switch to this kit (or the 34th) before running it.
