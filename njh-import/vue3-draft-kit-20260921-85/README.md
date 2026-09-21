# Vue 3 draft kit — 2026-09-21 (85th)

This directory holds one encrypted archive (`vue3-draft-kit-20260921-85.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260921-85.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- Adds a standalone worktree comparison launcher that creates paired as-is/to-be trees, installs
  dependencies, starts both servers and prints the two addresses. An existing folder name is never
  overwritten: the next free suffix is used, the pair keeps matching numbers, ports shift together,
  and list/remove commands are provided.
- Credentials are never accepted as command arguments. They are prompted without echo and stored only
  in the user-level npm configuration and the repository git configuration.
- Adds a per-file explanation command: for one source file it reports what changed between the two
  trees and why, using the transformation category vocabulary and naming the responsible adapter.
  Anything it cannot explain is reported as unclassified rather than given a guessed label.
- Adds a shared constraint skill for assistant use during screen conversion, covering settled syntax,
  library targets, shared-layer ownership and evidence rules, with routing to the detailed references.
- Adds Korean usage guides for the shared layer, the worktree comparison and the per-file explanation,
  linked from the kit entry document.
- Commit messages produced by the kit no longer carry assistant attribution, and a test prevents it
  from returning.
- The common-asset refresh keeps existing environment and configuration files untouched; a small
  reviewed allowlist of corrections is applied explicitly instead of a blanket overwrite.
- Windows runtime for the new launchers is NOT VERIFIED; an operator performs that check after import.
  Full phase-1 migration, spreadsheet dependency removal and complete behaviour parity remain open.
