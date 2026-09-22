# Vue 3 draft kit — 2026-09-22 (86th)

This directory holds one encrypted archive (`vue3-draft-kit-20260922-86.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260922-86.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- Search forms keep their original markup. Irregular layouts used to be serialized into descriptor
  objects, so an operator had to learn that format to move a single cell. The original table or column
  markup now stays in the screen template inside one layout slot and only the wrapper is added, so it
  can be edited directly. Priority is: declared fields for plain rectangular grids, original markup for
  everything else, descriptors only as a last resort. On the reference tree the descriptor route fell
  from 65 screens to 3, and those 3 contain no table tags. Conditional rendering directives inside the
  kept markup are preserved as written.
- The layout comment above each generated declaration now matches the route: for kept markup it says
  to edit the template, not the constant.
- Adds a kit-rule catch-up mode to the weekly catch-up step. Until now a common-asset refresh touched
  shared parts only, and the catch-up step merged only screens whose source had changed, so an
  improvement in how the kit converts a screen never reached screens that were already converted.
  The new mode regenerates the draft from the same source revision with the new kit and three-way
  merges only the screens whose draft output changed. It creates no new source tag, never overwrites a
  screen that was edited in the same place (those are listed for manual work with a draft-to-draft
  diff), and does nothing when the draft output is unchanged.
- Doughnut and pie charts are drawn at the original radius again. The newer chart library does not
  read the hover border width when sizing these charts and drew them 3px larger with no error or
  warning. The compatibility step now restores the earlier sizing for the two wrapper components,
  including wrappers produced by earlier kits. Charts that do not set a hover border width are
  unaffected. A 1px canvas-width difference caused by fractional container widths remains open.
- The runbook and the after-draft operations guide are rendered from their source documents again;
  both pairs had drifted, and the rendered runbook was missing four entry-point links. A test now
  fails when a rendered document differs from its source.
- The shared-layer guide gains a section classifying each composable-style module by what it really
  is (pure helper, factory, shared state) with when to call it, whether to keep the result, and who
  releases it. A two-way test keeps the table and the actual modules in step.
- Two test files that could not run without an environment variable now fall back to the kit's own
  modules instead of crashing, and two assertions that searched for a launcher name rather than the
  call itself were corrected.
- Spreadsheet handling is unchanged from the previous kit. Removing the legacy spreadsheet library,
  complete behaviour parity and the remaining chart width difference remain open.
  Windows runtime for the new catch-up mode is NOT VERIFIED; an operator performs that check after import.
- The route sweep again tells the operator that a port is occupied and how to free it when it refuses
  to reuse an existing reference server it cannot prove ownership of. The refusal itself is unchanged.
- The behaviour-comparison step again ends as "not verified" (its own exit status and a recorded reason)
  when it could not measure, instead of stopping with a generic error when only one side had been
  recorded. A fast test now guards this; previously only the full-chain rehearsal could detect it.
- Spreadsheet upload dialogs are accepted again: two shared components had been edited without moving
  the reviewed-provider pins, so every upload dialog screen was silently refused. The pins now match
  the shipped components, and a fast test fails as soon as they diverge.
