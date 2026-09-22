# Vue 3 draft kit — 2026-09-22 (88th)

This directory holds one encrypted archive (`vue3-draft-kit-20260922-88.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260922-88.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- Adds a public step that moves the shared Excel layer to ExcelJS: the two shared composables (export and upload) and
  their runtime helpers are replaced by the kit's verified versions, the runtime dependencies are declared and installed,
  the build is checked and the result committed. The two planners behind it refuse when the shared layer is not in the
  shape they know, and the step then changes nothing. Screens are untouched; after this step no file outside the screen
  folder imports the legacy spreadsheet library. Running it again reports "already applied". The retired copy of the
  upload composable asset is removed, and the target manifest declares the three runtime helpers explicitly.
- The step's place in the order is after the API-compatibility step: its boundary check needs the dev-server
  pre-bundling block that step writes, and the upload panel is judged in the shape that step produces. Run earlier, it
  stops and names the step to run first instead of a bare planner refusal (found in the packaging rehearsal, not in
  house). After applying, it re-runs the pre-bundling category once so the new spreadsheet engine is bundled at
  dev-server start.
- The kit-upgrade rehearsal now also runs this step on the aged replica after the catch-up, and the full-chain rehearsal
  runs it after the API-compatibility rounds, probes the early-run refusal, and checks the pre-bundling entry and
  idempotency.
- Catch-up hardening from an independent review: installation honours output paths containing spaces; the import
  scan for carried parts reads the converted tree's version of an existing file, accepts a space before the parenthesis
  in dynamic imports and requires, copies binary assets byte for byte, and refuses paths that escape the draft; the
  progress heartbeat survives a missing folder; a reused pair checks only the servers it actually started; a fresh
  original can be installed on reuse; an explicitly named assistant binary is resolved on PATH; a malformed assistant
  configuration is left untouched instead of overwritten; Korean file names are read unquoted from Git.
- Known limit stated in the runbook: a screen kept whole on the converted side does not receive that round's kit
  changes automatically; the recorded draft-side diff is the handoff.
- Spreadsheet screens (21 legacy-import screens), full behaviour parity and the 1px chart width difference remain
  open. Windows runtime for the new step is NOT VERIFIED; an operator performs that check after import.
