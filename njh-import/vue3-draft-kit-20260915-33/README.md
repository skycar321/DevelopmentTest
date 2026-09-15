# Vue 3 draft kit — 2026-09-15 (33rd)

This directory holds one encrypted archive (`vue3-draft-kit-20260915-33.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260915-33.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` inside the extracted folder, and follow
`docs/뷰3-초안-사내-런북.md` (or the matching `.html`) as the single source of truth for the
procedure. Step 0b (`run/00b-resume.sh`) prints which step to continue from.

## Requires

- The njh-cli replacement kit **v1.6.1** from this channel (`v1.6.1/`). Step 18 refuses to run on a
  1.5.x client.

## What changed since the 32nd kit

- **New step 0c — fresh start** (`run/00c-fresh-start.sh`). When the as-is branch has moved on and
  the Vue 3 branch carries little or no hand work, this moves the draft baseline to the latest `dev`
  instead of catching up: the old state directory, worktrees, draft and branch are kept under
  `-old-<previous-baseline-date>` names (nothing is deleted; the old baseline tag stays), then step 0
  is re-run for today's baseline with the same project path and registry. Without `--yes` it only
  prints the plan, including how many commits the Vue 3 branch has since the old baseline — above 20
  it recommends step 12 (catch-up) instead, which protects hand-edited screens.
- Steps 0, 0b and 2 now point at 0c in the message that used to say only "that is step 12".
- `README-사내-반입.md` gained a "2026-09-15 갱신(33차)" section with the decision table (0c vs 12) and
  the full order after a fresh start: 1 → 2 → 3 → 4 → 5 → 6 → 7 → 8a → 8b → 15 → 16 → 18 → 9 → 8b → 17.
- Everything from the 32nd kit (step 18 driver, `REVIEW_ESCALATED`, guides for every queue code,
  steps 15–17 assets) is unchanged.
