# Vue 3 draft kit — 2026-09-22 (87th)

This directory holds one encrypted archive (`vue3-draft-kit-20260922-87.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260922-87.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- The kit-rule catch-up mode (`12 --kit`) works on a tree that was drafted by an older kit and then advanced by
  many later step-22 rounds, which is the real in-house shape. Its first in-house run failed in two ways: merged
  screens imported shared parts that the tree did not have yet, so the build could not load them, and screens
  whose in-house edits overlapped the new draft were pushed to a manual list of twenty. Now the missing parts are
  carried from the new draft (transitively, never overwriting an existing file), overlapping screens are kept as a
  whole on the to-be side with the draft-side diff recorded for review, and the abandon hint no longer mentions
  tag deletion in kit mode. Nothing blocks the next round.
- A kit-upgrade rehearsal is now a packaging gate: a lab replica shaped like the in-house tree (an older kit's
  draft plus later step-22 work) receives the new kit's catch-up and must build with zero manual work. This is
  the check that would have caught the defect above before shipping.
- Long silent phases (draft generation, builds) print progress every thirty seconds: elapsed time, screen files
  written and whether the diagnostic build has started. The draft log stays empty while work is in progress and
  only records errors; the heartbeat says so.
- Worktree comparison: the original side is now the developer's existing clone exactly as it is (source,
  installed dependencies and registry file untouched); only the converted side is a new worktree tracking the
  remote migration branch. Without registry information the command still creates the worktree and wires the
  assistant skill; with a registry and account it installs, builds and serves. The registry falls back to the
  target branch's `.npmrc` line. The account password is never a command argument (hidden prompt, environment
  variable or a mode-600 file) and is written only to a pair-private npm configuration file.
- The shared rules skill has a single source (the kit copy); the developer bundle is generated from it with the
  kit-only reference blocks removed and bundle references appended. The skill now includes CSS parity,
  evidence, the commit/collaboration convention and what to do when blocked. A helper writes the assistant
  configuration into a working tree and checks with the assistant CLI that the skill is actually listed.
- A test that runs another test file as a child no longer inherits the parent test runner's context, which made
  it report a serialized stream instead of TAP under Node 25.
- Spreadsheet handling is unchanged from the previous kit. Removing the legacy spreadsheet library, complete
  behaviour parity and the remaining 1px chart width difference remain open. Windows runtime for the new
  catch-up behaviour and the comparison launcher is NOT VERIFIED; an operator performs that check after import.
