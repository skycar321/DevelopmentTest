# Vue 3 draft kit — 2026-09-11 (27th)

This directory holds one encrypted archive (`vue3-draft-kit-20260911-27.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260911-27.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` inside the extracted folder, and follow
`docs/뷰3-초안-사내-런북.md` (or the matching `.html`) as the single source of truth for the
procedure. Step 0b (`run/00b-resume.sh`) prints which step to continue from.

## What changed since the previous kit

- **New in this kit: four survey prompts plus a runner** (`prompts/*.md`, `run/13-survey.sh`).
  They ask the CLI to measure the draft against its own target and write a shareable report:
  how much of the planned scope is converted; which remaining Vue 2 syntax is mandatory versus
  optional; why a page renders blank even though the dev server answers; and a one-page status
  summary for a team briefing. Reports land in `~/.vue3-draft/survey-<number>.md`.
- Residue findings now use project-relative paths, so reports no longer carry absolute home
  paths. A unit test covers it.
- Two more shared components and seven more codemods were folded in from the lab integration tip.
- The runbook gained a step 13 for the surveys above and notes that a model unable to issue tool
  calls returns a tool-call format error instead of a report — that is a connection problem, not
  a prompt problem.

## Interactive demo (new in this kit)

The four prompts above run headless (`njh -p`) and write a report file — useful, but the terminal
UI is not visible. Three more prompts under `prompts/대화형/` are written to be pasted into the
**interactive** session instead, so the sticky header, the status bar and the file-reading steps
are all on screen while the CLI works. `run/14-demo.sh <number>` prints one wrapped in copy
markers, and names where the report will land. The model writes the report itself; if the
connected model cannot call tools, `/export` in the session saves the whole conversation as the
fallback, so a live demo does not stall.

## Requirements

Node.js 22+, 7-Zip, git, and a reachable npm registry for the Vue 3 dependency set. Everything
else the kit needs is inside the archive. No outbound network call is made by the kit itself.
