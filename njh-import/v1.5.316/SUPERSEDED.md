# SUPERSEDED — do not use `njh-import/v1.5.316/` for delivery or verification

`njh-import/v1.5.316/` is superseded by
[`njh-import/v1.5.321/`](../v1.5.321/). Do not extract, install, deploy, or
accept v1.5.316 as a release. Keep it only as an historical artifact when a
specific incident requires it.

## What was wrong with v1.5.316

This directory is not a canonical build:

- it has no `c5-build.json`, `gateway-build.json`, `laptop-build.json`, or
  `source-commit-attestation.json`, so a recipient cannot verify the claimed
  build contents or source commit from this directory;
- the CLI archive contains a target-inspection document from the unlanded branch
  `cdx/target-inspection-doc-bundle-0811`, so that document was not canonical
  release documentation at the time this directory was published. The document
  itself has since landed on the main branch and ships in v1.5.321, so obtain it
  from there rather than from this directory; and
- the README's `309`-entry and zero-leak statements are not recipient-verifiable
  package facts without the missing canonical build evidence. A checksum can
  identify received bytes only; it cannot establish their source or contents.

The old README also predates the current model-capability verdict. Do not treat a
successful installation, CLI version output, local doctor, connection check, or
claimed safety result as proof that the bundled model completes work correctly.

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

The simple-task result is narrow and does not qualify the original task or a
general task-completion product.

## If you already acted on v1.5.316

1. Withdraw any release, source-provenance, contents-count, leak-scan, or
   general functional-acceptance claim based on this directory.
2. Do not rely on the unlanded target-inspection document as current canonical
   release documentation, and do not pair this directory with another old kit
   to fill missing files.
3. Obtain v1.5.321 and verify its `SHA256SUMS.txt`, three build manifests,
   `source-commit-attestation.json`, and `safety-gate.json` before installing.
4. Re-run the applicable installation and connection checks on v1.5.321. Those
   checks establish only their stated structural or connection predicates.

v1.5.321 is structurally verifiable and remains functionally NO-GO for general
correct task completion with the bundled models. Read its `README.md` before
using any archive.
