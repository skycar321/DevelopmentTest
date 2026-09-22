# Vue 3 developer tools — 20260922-3

This directory holds one encrypted archive (`vue3-dev-tools-20260922-3.7z`, 7z AES-256 with encrypted headers, 23 files)
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
- `docs/시작-가이드.html` — **start here**: an offline, copy-first Korean guide (no external resources; opens by double-click).
  Variables at the top, one copy button per command block, the three password routes, a "where it stops → what to run →
  what to send" table.
- `docs/` — three more Korean usage guides (shared layer, working-tree comparison, per-file change report).

## What changed since the previous bundle

- First-run order: step 1 creates the working tree only (`--worktree-only`); installation and servers come with `--registry`
  and `--nexus-user` in step 2. The assistant is opened through `bin/skill-setup.sh --bin <assistant binary>`.
- Git: an existing clone supplies its own remote URL and credential helper; one targeted fetch, a clear message on an
  authentication failure, and no half-made working tree. `--credentials git` applies prompt/askpass before the first fetch.
- Credentials on reuse: `--reuse --nexus-user` refreshes the pair-local npmrc even when `node_modules` already exists;
  `--reuse --forget-credentials` deletes only that file.
- Skill verification parses the assistant's structured output; internal notes are stripped from executables.

## Verify and extract

```bash
shasum -a 256 -c sha256.txt
7z x -p vue3-dev-tools-20260922-3.7z
cd vue3-dev-tools && cat README.md && bash bin/compare.sh --help
```

Requires Node.js 20+, Git, npm and Bash (Git Bash on Windows). The file reporter uses the Vue SFC and Babel
parsers of the installed converted project and never installs anything itself.

Windows runtime is NOT VERIFIED; an operator performs that check after import.
