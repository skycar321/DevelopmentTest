# Vue 3 developer tools — 20260922-5

This directory holds one encrypted archive (`vue3-dev-tools-20260922-5.7z`, 7z AES-256 with encrypted headers, 23 files)
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

## What changed since the previous bundle (20260922-4)

- `--reuse` no longer rejects a registry that differs from the saved one only by a trailing slash (`저장된 쌍의 registry 와 다르다`);
  a genuinely different registry path is still refused with the pair left untouched.
- A pair whose installation failed part-way (npm exit code, failed postinstall) is retried on the next run instead of being treated
  as installed because `node_modules` exists.
- The existing clone's remote URL is used as it is, even when it embeds credentials; the value is never printed or stored, and
  authentication failures name the host only.
- `--nexus-password '<value>'` is accepted on the command line (the guide says it stays in shell history); hidden prompt, `NX_PW`
  and `--nexus-password-file` remain.
- Guides quote the exact messages a developer can hit (non-TTY password prompt, wrong password `E401`, wrong Git token, password
  file permissions) with the remedy command for each; the offline HTML has the same rows.
- Every isolated authentication test now runs (no skipped cases): smart Git HTTP with basic auth, a registry under a path prefix
  that returns 401 without credentials, CRLF checkout.

## Verify and extract

```bash
shasum -a 256 -c sha256.txt
7z x -p vue3-dev-tools-20260922-5.7z
cd vue3-dev-tools && cat README.md && bash bin/compare.sh --help
```

Requires Node.js 20+, Git, npm and Bash (Git Bash on Windows). The file reporter uses the Vue SFC and Babel
parsers of the installed converted project and never installs anything itself.

Windows runtime is NOT VERIFIED; an operator performs that check after import.
