# Vue 3 draft kit — 2026-09-15 (32nd)

This directory holds one encrypted archive (`vue3-draft-kit-20260915-32.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260915-32.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` inside the extracted folder, and follow
`docs/뷰3-초안-사내-런북.md` (or the matching `.html`) as the single source of truth for the
procedure. Step 0b (`run/00b-resume.sh`) prints which step to continue from — settings and
progress live outside the kit (`~/.vue3-draft/`), so unpacking this kit anywhere continues the
same run.

## Requires

- The njh-cli replacement kit **v1.6.0** from this channel (`v1.6.0/`). Step 18 refuses to run on
  a 1.5.x client: it relies on `/goal` refusing a "done" claim without a real file change and on
  continuing past the per-turn tool budget.

## What changed since the previous kit

- **New step 18 — per-finding completion driver** (`run/18-phase1-driver.sh`). Each detector
  finding becomes one bounded `/goal` run; after every finding the detector is re-run and a verdict
  is recorded (`KIT_SUFFICIENT`, `RESIDUE_REMAINS`, `REVIEW_ESCALATED`, `BLOCKED_STATED`,
  `TIMEOUT`, `NO_GUIDE`). Evidence: `~/.vue3-draft/phase1-driver/phase1-driver.jsonl` and a
  `phase1-summary.md` with an "Operator decisions" section. Edits land as one revertable commit.
- `REVIEW_ESCALATED` separates "the model left only a comment because the guide says stop and ask
  the operator" from real residue. Detector silence is still not proof of behaviour preservation —
  steps 8b (screen survival) and 17 (equivalence) remain the completion criteria.
- **Every detector code seen in the real queue now has a guide**, and a base-conversion document
  (`00-BASE-CONVERSION.md`, xlsx → exceljs) that all guides point to first. Six guides cover codes
  that were in the action table without a document; three cover codes that appear outside the table.
- Step 0b now reports the driver's verdict counts and whether operator decisions are pending.
- The bundled skill is taken from the njh-cli v1.6.0 line (same tree that ships inside the client
  kit), so the client and the kit agree on guides and driver.
- The runbook section for step 18 is in `README-사내-반입.md` under "2026-09-15 갱신", including
  what not to do (no whole-tree formatting on top of the driver commit; no phase-1 sign-off on
  `KIT_SUFFICIENT` counts alone).
