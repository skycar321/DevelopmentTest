# Vue 3 developer tools — 20260923-2

This directory holds one encrypted archive (`vue3-dev-tools-20260923-2.7z`, 7z AES-256 with encrypted headers, 24 files)
and its checksum. The password is the same as the other kits and is delivered separately.

A small, self-contained bundle for a developer who owns one screen. The migration kit is **not** required.

## What is inside

- `bin/compare.sh` — keeps the existing Vue 2 clone as the original side (source and `node_modules` untouched),
  creates one new converted working tree tracking the remote migration branch, and starts both dev servers
  side by side. Without registry information it still creates the working tree and wires the assistant skill;
  with `--registry` and `--nexus-user` it installs, builds and serves. The password is never a command
  argument: hidden prompt, `NX_PW`, or a mode-600 file. `--list` / `--remove` / `--reuse` are provided.
  `--init-config` writes an empty, commented settings file (`~/.vue3-dev-tools/config.env`: clone path, pair
  folder, registry, user, optional password with mode 600); once the values are filled in, the three tools read
  them and the command-line arguments can be omitted (explicit arguments always win).
- `bin/skill-setup.sh` — writes the assistant configuration into a working tree and checks that the
  assistant CLI actually lists the skill (`debug skill`, captured to a file).
- `bin/file-changes.sh` — for one source file, reports what changed between the two trees and why, using the
  transformation category vocabulary; anything it cannot explain is reported as unclassified.
- `skills/vue3-common-rules/SKILL.md` — the shared rules skill: settled syntax, library targets, shared-layer
  ownership, CSS parity, evidence, and the commit/collaboration convention.
- `docs/` — three Korean usage guides (shared layer, working-tree comparison, per-file change report).

## Verify and extract

```bash
shasum -a 256 -c sha256.txt
7z x -p vue3-dev-tools-20260923-2.7z
cd vue3-dev-tools && cat README.md && bash bin/compare.sh --help
```

Requires Node.js 20+, Git, npm and Bash (Git Bash on Windows). The file reporter uses the Vue SFC and Babel
parsers of the installed converted project and never installs anything itself.

Windows runtime is NOT VERIFIED; an operator performs that check after import.
