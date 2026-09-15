# Vue 3 draft kit — 2026-09-16 (41th)

This directory holds one encrypted archive (`vue3-draft-kit-20260916-41.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260916-41.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.1** from this channel (`v1.6.1/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- **Step 5 no longer leaves `main.js` on Vue 2 because of one screen.** The bootstrap composer (main, store root,
  login store, router, helpers) was all-or-nothing across every source file: a single screen outside the registered
  shape (for example one that reads `this.$store.getters` directly) refused the whole composition, so `main.js`
  kept `import Vue from "vue"` and `vite build` died with `MISSING_EXPORT` — the field run on 2026-09-16 and a lab
  reproduction with one added screen showed exactly this. Such screens are now refused **per file** (they show up in
  the hand-work list) and the rest is composed; the reproduction builds.
- Step 5 prints the infrastructure-stage refusal reasons (bootstrap / router / store) right under the screen
  receipts, with the file that caused each one.
- The pre-ship rehearsal now plants one such out-of-shape screen on the fixture and asserts that `main.js` is
  converted and that screen is refused per file.
- Includes the 40th kit (npm via `npm_invoke.mjs`, registry-only `.npmrc`, no driver receipts in the app repo).
- **Windows checkouts (CRLF) no longer change the outcome.** Git for Windows checks worktrees out with CRLF; the
  codemods compare statement shapes assuming LF, so with CRLF input five modules (`plugins/vuetify.js`,
  `utils/api/event-bus.js`, …) were refused and kept `import Vue from "vue"`, failing the build (lab reproduction
  with a CRLF fixture). Step 5 now normalises text input to LF once before any stage; the rehearsal fixture runs
  with `core.autocrlf=true` like Git for Windows.
- **New step 19 (`run/19-refused-screens.sh`)**: lists the files step 5 refused per file (`--list`) and repairs
  them one by one with njh-cli, one commit per file, functional-deletion guard, build check at the end. Reason-
  specific guidance lives in `prompts/refusal-guides.ko.json`; a paste-in version is
  `prompts/대화형/4-거부-화면-보수.txt`.
- Step 6 no longer carries the draft-only `.gitattributes` (`* -text`, which protects the draft baseline) into the
  app worktree. With it present, git stopped normalising line endings, so on a Windows (CRLF) checkout the app commit
  stored CRLF blobs — the rehearsal's new "committed blobs are LF" assertion caught this before it reached the field.
