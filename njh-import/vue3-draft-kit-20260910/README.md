# Vue 3 draft kit — 2026-09-09 (encrypted, 2026-09-10 cut: lab tip 72d84cf5)

This directory publishes one archive, `vue3-draft-kit-20260910.7z`.

- Format: 7z, AES-256, **encrypted headers** (`-mhe=on`). Without the password neither the file list nor the contents can be read.
- Password: the same archive password as the `njh-cli` kits in this channel. It is delivered separately, never through this repository.
- Integrity: compare against `sha256.txt` before extracting.

```bash
shasum -a 256 -c sha256.txt        # or: certutil -hashfile vue3-draft-kit-20260910.7z SHA256
7z x -p vue3-draft-kit-20260910.7z # enter the password when prompted
```

After extraction, start with the Korean README inside the extracted folder. It explains the import order:
install the matching `njh-cli` kit, regenerate the library decision sheet, prepare the A/B trees, run the one-command draft, compare, and report gaps.

Contents (high level): the `vue3-migration` skill bundle at the lab integration tip named in `delivery-line.json`, the library decision documents,
the A/B dual-runtime procedure, the draft gap-report tool, and reference analysis lists.


This is the cut to import; it replaces every earlier same-week kit, which are no longer published here.

Change since the previous cut: the draft driver now applies the Vuetify 4 cascade-layer reset boundary itself. Earlier cuts carried the finding as a reference document only, so a generated draft still shipped an unlayered blanket reset (`* { margin: 0; padding: 0 }`). Under Vuetify 4 that unlayered rule outranks component-owned layout CSS regardless of specificity, which pushed the main content under a fixed-height app bar and swallowed clicks at the top of every page: in the measured migration, 76 routes mounted before, 40 mounted and 36 failed on Vuetify 4, and 76 / 0 after the boundary. The diagnostic build passes in both states, which is why this only appears once a browser opens the app.

A new stage wraps reset-shaped rule blocks in an `app-reset` cascade layer and declares that layer before the component layer in the entry document. It never edits a declaration, never reaches inside an at-rule, refuses instead of guessing when there is no entry document, and is idempotent. Its receipt states explicitly that a passing build does not prove the fix — confirm mounted/failed route counts and a visual audit in a browser. A derived-mode replay of the whole project on this tip converts 129 of 152 screens, unchanged from the previous cut.
