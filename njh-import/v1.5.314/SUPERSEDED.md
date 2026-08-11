# SUPERSEDED — do not use `njh-import/v1.5.314/` for a new delivery

`njh-import/v1.5.314/` is superseded by
[`njh-import/v1.5.321/`](../v1.5.321/). Download, verify, and install v1.5.321
instead. Keep v1.5.314 only when an historical incident or archive must be
examined.

## What was wrong with the v1.5.314 README

The v1.5.314 README described this directory as the final delivery and said the
three archives were sufficient to use together. That statement is no longer
valid. Its safety-gate, archive-execution, version, installation, local doctor,
and connection statements can establish only the predicates they name. They do
not establish that the bundled model correctly completes representative work.

The old README therefore gave an obsolete capability frame: a successful CLI
version command, local doctor, connection check, or package safety check must
not be treated as functional acceptance of the model.

The current evidence that must replace that frame is:

- the exact simplified two-level configuration merge on `qwen2.5-coder:7b` was
  semantically correct in 100 of 100 observations after Markdown-fence
  extraction, with an independent oracle;
- the original multi-rule representative task was semantically correct in 0 of
  40 observations across two tested models; and
- no numerical strict raw-output rate is available. The previously cited
  92-to-100-per-100 comparison used unseeded runs with unrecoverable random
  seed values, so it is void as a rate and as a host comparison. Check raw
  format on every output instead.

The first result is a narrow oracle-checked capability. It does not qualify the
second task or turn NJH into a generally correct task-completion product.

## If you already acted on v1.5.314

1. Withdraw any statement that v1.5.314 is final, generally functionally
   accepted, or proven correct by installation, doctor, connection, archive
   extraction, version output, or a safety gate.
2. Do not use its archives for a new deployment or a correctness-critical task.
3. Obtain v1.5.321 and verify its `SHA256SUMS.txt`, three build manifests,
   `source-commit-attestation.json`, and `safety-gate.json` before installing.
4. Re-run the applicable installation and connection checks on v1.5.321. Those
   checks remain structural or connection evidence, not model-correctness
   acceptance.

v1.5.321 is structurally verifiable and remains functionally NO-GO for general
correct task completion with the bundled models. Read its `README.md` before
using any archive.
