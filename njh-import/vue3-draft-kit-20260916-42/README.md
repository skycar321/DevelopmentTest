# Vue 3 draft kit — 2026-09-16 (42th)

This directory holds one encrypted archive (`vue3-draft-kit-20260916-42.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260916-42.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.1** from this channel (`v1.6.1/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- **Parallel execution: a main orchestrator with one worker per file, harvested first-finished-first.**
  `tools/parallel-orchestrator.mjs` takes a task list (file + njh prompt, or any command), creates an isolated git
  worktree per task, runs N workers concurrently, and as each finishes harvests only that task's files (stray edits
  discarded, functional deletions rejected), committing serially — one file, one commit. Ledger and report per run.
- Step 19 (`REFUSED_REPAIR_JOBS`) and step 18 (`PHASE1_JOBS`, driver `--only-file`) run on it. Worker worktrees
  inherit the project's `.njh/settings.json` (connection/model).
- Unit tests for the orchestrator (pool order, stray discard, deletion guard, serial commits, timeout, prompt tasks)
  run in the gate, and the gate exercises step 19 with three parallel workers (a fake njh) after step 6.
- Verified in the lab with real njh workers on a local OpenAI-format model (`qwen2.5-coder:14b` via Ollama `/v1`):
  see the README section "병렬 실행 — 검증 결과".
- Includes the 41st kit.
- **Harvest verifies before accepting.** `--verify "<command>"` runs in the worker's worktree after it finishes (steps
  18/19 pass `npm run build`); if it fails, nothing from that task is harvested and the ledger records
  `verify-failed`. Lab evidence for why: a 14B model "repaired" two small screens by replacing the `<template>` line
  with a comment and by reformatting — both passed the deletion guard and both broke the build. With verify on,
  such edits are rejected instead of committed.
