# Vue 3 draft kit — 2026-09-23 (92th)

This directory holds one encrypted archive (`vue3-draft-kit-20260923-92.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260923-92.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- First real Windows rehearsal of the continue chain (12 → 22 → 14b → 16 → 15 → 15 --write) on a Windows 11 / Git Bash / Node 22 machine
  with a tree shaped like the in-house one (older draft plus catch-up). Every step passed after the fixes below; the staged
  spreadsheet conversion wrote 18 screens and skipped 3 with reasons, the build passed.
- Staged spreadsheet conversion (15b) failed on Windows at its first line: linking the shadow tree's dependencies used a directory
  symlink, which an ordinary Windows account cannot create (EPERM). It now uses a junction, then `mklink /J`, and names the error.
  With the previous kit this step cannot run on an in-house Windows PC; run 15 and 15 --write again with this kit.
- Catch-up (`12 --kit`) proves a partially merged screen against the first-stage lint's undefined-identifier rule as well as the
  compiler: a search-form conversion is three hunks (template binding, computed getter, module constant) and when only the
  declaration hunk fell to a conflict, the compile passed and step 16 reported `searchFields is not defined` (15 screens on the
  Windows tree). Such a merge is now refused whole. `12 --repair-undef` restores the missing declarations on trees merged by
  earlier kits from the round's own draft (Windows tree: 15 screens repaired, 35 lint errors → 0, build passed).
- `12 --transplant-receipts` carries the spreadsheet caller receipts (the transform's record of the original click site) from the
  new draft into screens that an earlier kit converted without them. The step-15 analyzer only admits an `ExcelExportButton`
  caller with that receipt, which is why the in-house run reported "no observed caller" / "template reference review" for
  ~20 screens and converted none automatically. A screen is written only when the analyzer replays the receipt on it, including
  the receiver part's identity; otherwise it is listed with the reason.
- Kit-owned shared parts that conflict in catch-up are adopted whole when the operator's copy differs from the previous draft only
  in comments, whitespace or line endings (structural comparison); real code edits are still kept whole and the operator's
  difference is written to `parts-hand-diff/` in the round folder so it can be moved before a replay.
- The dialog adoption no longer serializes `:message-props` / `:card-props` as entity-escaped JSON; it emits a readable object
  literal, omits the shell defaults (cols/lg 12) and omits empty props. Reader profiles and the upload-dialog provider check treat
  both generations as the same object, so previously converted screens keep matching; the region markers stay (they carry the
  caller-region contract).
- Step 16 `--fix-headers` adds the kit's header comment to kit-installed files that predate the comment convention (60-odd files
  in house were reported as missing headers; only comments are added, in one commit).
- Steps 12 and 22 now end by pointing at step 24 (safety branch, forbidden-file check, tracking ref) instead of a hand `git push`.
- Windows: Node 24 makes the Vite build exit silently (0xC0000409); use Node 22 LTS on the Windows PC.
