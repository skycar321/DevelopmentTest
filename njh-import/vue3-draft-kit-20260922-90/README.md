# Vue 3 draft kit — 2026-09-22 (90th)

This directory holds one encrypted archive (`vue3-draft-kit-20260922-90.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260922-90.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- Catch-up (`12 --kit`) now refreshes the kit-owned shared parts it depends on (session store, shared composables, compatibility
  shells) with a three-way merge before it merges screens, and a contract check verifies that every screen still resolves the
  members it imports; a missing member is reported with the file and symbol instead of surfacing later as a runtime error at login.
  `12 --replay <run directory>` reuses a finished draft run instead of regenerating it, so a retry after a fix does not repeat the
  long draft stage.
- Residual spreadsheet screens (`15b`): the per-screen admission prepared for this round was withdrawn before shipping — on the
  lab tree it recorded zero conversions because the stored reader profiles predate the shared search-form contract of the previous
  round; `15b` behaves as in the previous round (all-or-nothing with reasons in `excel-styled.json`) until the profiles are
  re-measured from source in the next round.
- Step-22 replay on Windows checkouts: the layout, pager and disabled-date restorations compared their owned blocks against CRLF
  text and reported them as "modified" (false operator items); they now normalise line endings like the other categories.
- Catch-up refuses to start when the previous draft folder is damaged (screens listed in the baseline hashes are missing), before
  the long draft stage, and prints the recovery with the previous kit; a damaged base otherwise turns every screen into hand work.
- Chart rule: converted screens keep the semantic defaults the previous chart library applied implicitly (axis ticks, legend
  placement, line tension) as explicit options, so v4 renders the same shape; owned option blocks are kept across re-runs.
- Manual-work items carry paired evidence (before/after capture names and the operator decision) so the review file states why a
  screen was left to hand work and what proves the outcome.
- Grid provider declarations that the earlier conversion erased are admitted again, and the CSS restoration keeps its owned repair
  blocks when a later category rewrites the same file.
- Developer tools bundle -4 shipped separately: path arguments accept a leading `~`, the first-run guide warns that `~` inside
  quotes is not expanded, the pre-checks print the reason when a path is wrong, and the guide notes `--branch` for a migration
  branch with a different name.
- Developer tools bundle -5 shipped separately: registry trailing-slash equivalence on reuse, retry after a failed installation,
  existing clone remotes with embedded credentials accepted (never printed), `--nexus-password` on the command line, exact-message
  remedies in both guides.
