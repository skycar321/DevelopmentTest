# Vue 3 developer tools — 20260922-4

This directory holds one encrypted archive (`vue3-dev-tools-20260922-4.7z`, 7z AES-256 with encrypted headers, 23 files)
and its checksum. The password is the same as the other kits and is delivered separately.

A small, self-contained bundle for a developer who owns one screen. The migration kit is **not** required.

## What is inside

- `bin/compare.sh` — keeps the existing Vue 2 clone as the original side (source and `node_modules` untouched),
  creates one new converted working tree tracking the remote migration branch, and starts both dev servers
  side by side. Without registry information it still creates the working tree and wires the assistant skill;
  with `--registry` and `--nexus-user` it installs, builds and serves. The password is never a command
  argument: hidden prompt, `NX_PW`, or a mode-600 file. `--list` / `--remove` / `--reuse` are provided.
- `bin/skill-setup.sh` — writes the assistant configuration into a working tree and checks that the
  assistant CLI actually lists the skill (`debug skill`, captured to a file).
- `bin/file-changes.sh` — for one source file, reports what changed between the two trees and why, using the
  transformation category vocabulary; anything it cannot explain is reported as unclassified.
- `skills/vue3-common-rules/SKILL.md` — the shared rules skill: settled syntax, library targets, shared-layer
  ownership, CSS parity, evidence, and the commit/collaboration convention.
- `docs/` — three Korean usage guides (shared layer, working-tree comparison, per-file change report).

## What changed since the previous bundle (20260922-3)

- Path arguments: a leading `~` in `--repo`, `--output` or `--nexus-password-file` is expanded to the home directory by the
  tool itself, so a quoted `"~/..."` no longer creates a folder literally named `~`.
- First-run guide (README and the offline HTML): the three path lines now say that `~` inside quotes is **not** expanded by
  the shell (`"$HOME/Desktop/..."` or a full path instead), and the pre-checks print `TOOLS OK` / `ASIS OK` or the reason
  (`TOOLS 경로가 틀렸다: ...`) instead of failing silently. The troubleshooting table gained a row for
  `bash: .../bin/compare.sh: No such file or directory`.
- The guide notes `--branch <name>` for a migration branch that is not called `vue3-migration`, and states that the
  existing clone's checked-out branch is left as it is.
- README is Korean-only; duplicated English sentences were removed.

## Verify and extract

```bash
shasum -a 256 -c sha256.txt
7z x -p vue3-dev-tools-20260922-4.7z
cd vue3-dev-tools && cat README.md && bash bin/compare.sh --help
```

Requires Node.js 20+, Git, npm and Bash (Git Bash on Windows). The file reporter uses the Vue SFC and Babel
parsers of the installed converted project and never installs anything itself.

Windows runtime is NOT VERIFIED; an operator performs that check after import.
