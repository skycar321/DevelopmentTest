# Vue 3 draft kit — 2026-09-22 (89th)

This directory holds one encrypted archive (`vue3-draft-kit-20260922-89.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260922-89.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- Adds a canonical shared stylesheet derived from the installed Vue 2 and Vue 3 framework CSS plus the application's own global CSS
  (buttons, tabs, compact fields, dialogs, grid headers, cards, containers, typography), installed by a new step-22 category with one
  static entry import; the generator records source hashes and line numbers, defers selectors it cannot map instead of guessing, and
  reports those deferrals as one informational line rather than as operator tasks. Offline source-computed defaults only: no browser
  parity claim is made; the runbook names the screens an operator must still measure.
- Shared-layer improvements ported from an independent review: dialog transport, search-form composables, file download cleanup, the
  Excel upload model/panel and export button now follow one canonical contract; every pinned shape accepts both the previous and the
  new form so trees converted by earlier kits keep working.
- The upload-planner test ships its own fixture (no retained lab tree needed), so it joins the packaging gate.
- Developer tools bundle: first-run guidance fixed (worktree-only start, assistant binary names), skill verification parses the
  assistant's structured output, internal notes stripped from executables, Git authentication ordering and credential refresh on reuse.
- Catch-up (`12 --kit`) now merges a conflicting screen hunk by hunk: non-overlapping hunks from the new draft are applied, overlapping
  hunks keep the in-house side, and the result is accepted only when it compiles with the Vue compiler; otherwise the whole file is kept
  as before. The summary and commit separate partial merges from whole-file keeps, and the review file lists the kept draft-side hunks.
  When the draft generator refuses, the step now prints the refusal code and failed stage on screen and names the JSON file to send
  (an in-house run stopped on a full disk and the previous message pointed at an empty log).
- Residual spreadsheet screens: on the lab tree (same screens) the staged conversion behind `15 --write` converted all 21 remaining
  screens (28 files, build passed, live legacy imports 21 → 0). In house the planners refuse screens whose structure differs from the
  measured profiles and record the reason; behaviour parity was NOT VERIFIED in the lab and remains the in-house step 17 proof.
- The shared-style category skips itself with one operator note when the original tree is not installed, instead of stopping step 22.
- Runbook: a short decision block at the top points a new operator to exactly one section (fresh tree, continue, stopped), the
  current continue section sits right below it, older round sections are kept under one archive heading, the fresh-start command
  block is 25 lines with per-step notes in a table, and `docs/README.md` indexes the guides. The continue section now includes the
  legacy spreadsheet package removal after the screen conversion (lab: removal, offline install and build passed).
- The shared stylesheet and the earlier native-button restoration now recognise each other as kit-owned output: the shared
  stylesheet owns framework-wide defaults, the restoration keeps the measured screen-specific declaration, and both category
  orders converge to the same tree.
- Developer tools bundle -3 shipped separately (offline start guide, first-run order, Git authentication, credential refresh).

