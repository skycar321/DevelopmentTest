# Erratum — njh-import v1.5.321 embedded capability documentation

## Read this with the extracted v1.5.321 archives

This erratum corrects **capability wording in documents embedded in the
v1.5.321 archives**. It does not report a damaged archive, a changed installer,
or a changed connection procedure.

- The v1.5.321 archive bytes are unchanged.
- The SHA-256 values distributed for v1.5.321 still identify those unchanged
  historical bytes and still verify normally.
- Do **not** download the archives again because of this erratum.
- Installation, local diagnostics, configuration, gateway routing, and the
  package-integrity evidence retain the scopes stated in their respective
  procedures.

The correction is that several embedded documents describe model capability
more broadly than the available evidence supports. Read the replacements below
instead of the identified passages.

## What remains valid

A matching archive digest, the supplied build manifests, source attestation,
and passing safety gate establish the received archive identity, listed package
contents, source identity, and configured safety-scan result. They do not
establish model correctness or general task completion.

A successful installation, local doctor, laptop verifier, gateway diagnosis, or
connection doctor establishes only its named local, configuration, runtime,
route, authentication, or HTTP predicate. It does not establish that a model
answer is correct or that a tool-using task will complete correctly.

You may continue to use v1.5.321 for package inspection, offline installation,
configuration and connection diagnostics, controlled failure reproduction, and
the exact bounded transformation described below when an independent oracle
checks its result.

## Correct model-capability statement

Read all three rows together.

| Boundary | Observed evidence | Recipient action |
| --- | --- | --- |
| Simplified two-level configuration merge on `qwen2.5-coder:7b` | An independent semantic oracle judged 100 of 100 outputs correct after Markdown-fence extraction on each of three non-corporate test machines. | This is a narrow capability for this exact task, model, and oracle. Re-run the independent oracle on the target before relying on an output. |
| Withdrawn strict raw-output criterion for that simplified merge | The previously reported 92-to-100-per-100 strict range is withdrawn as a measure of the model, not only as a host comparison or recipient rate. The 300 historical runs sent no seed, so they cannot support a host conclusion. More importantly, the 42-seed corpus found that all six strict failures were one JSON Markdown fence: `njh-cli`'s own `parseRepresentativeTaskJson` accepted all six, and the independent semantic result was 42 of 42. The strict boundary rejected bytes that the installed client already parses; it did not measure a model error. | Do not reject an output for its pre-parser raw shape. Verify the semantic result through the same NJH-CLI parsing path you use, with the independent oracle for the exact simplified task. |
| Original multi-rule representative task across two tested models | Semantic correctness was 0 of 40. Because the strict criterion above turned out to be a parsing artifact, this task was re-run as a separate 40-run `qwen2.5-coder:7b` cohort with the same `njh-cli` extraction applied and the independent semantic oracle. All 40 outputs parsed — 32 standalone JSON and 8 fenced — and semantic correctness was again 0 of 40, with the trace field left unremoved in 40 runs and the model list wrong in 38. This failure is the model, not the parsing boundary. | This task is functionally NO-GO. A plausible answer, exit code `0`, successful installation, green doctor, or successful connection does not make it pass. |

The 100-of-100 result does not qualify the original multi-rule task. The
0-of-40 result does not erase the narrow simplified-merge result. The 42-seed
result changes the acceptance boundary for that simplified task: validate
semantic output through the installed CLI parsing path and the independent
oracle, not through the pre-parser raw-byte shape. Neither result establishes a
generally correct task-completion system.

Do not use the bundled models to make or approve file changes, perform
autonomous or tool-using work, provide correctness-critical answers, or make
automated decisions. Treat other model output as untrusted diagnostic text
until a human, source of record, or independent checker validates it.

## Embedded passages to disregard and their replacements

The paths below are relative to the root of the stated extracted archive.
Where a row names more than one path, the same correction applies to every
listed copy.

| Extracted archive and passage to disregard | Replace it with this statement |
| --- | --- |
| C5 `docs/사용자/02-설치-및-업데이트.md`, steps 9–10: `마지막 줄의 🔌 연결: 표시가 사용 가능이면 바로 쓸 수 있습니다.` | A green connection result means only that the documented settings, transport, TLS, authentication, tags, and selected-model predicates passed. It is not a correctness or task-acceptance result. Apply the **Correct model-capability statement** above before using model output. |
| C5 `docs/사용자/03-기본-사용법.md`, `산출물 완료와 도구 인자 교정`, `장시간·어려운 작업 처방`, and the `PASS 2/6 → 3/6` / `품질 비열화 없음` table | Do not treat retries, artifact checks, a completed tool call, or those historical figures as evidence that local 7B can correctly create or edit files, perform tool work, or complete a general task. The only current demonstrated positive capability is the exact oracle-checked simplified merge; the original multi-rule task is NO-GO. |
| C5 `docs/사용자/04-문제-해결.md`, `첫 실제 작업에서 파일을 읽고 바꾸지 못할 때`, including the `edit_file` retry and v1.5.260 recovery-success wording | These are controlled failure and recovery observations, not an approved file-editing capability. Do not accept a model-directed file change merely because a tool ran or a local check passed. A person or an independent checker must validate every change before use. |
| C5 `docs/사용자/06-post-import-first-30-minutes.md`, `Acceptance verdict` and the local-7B `6 of 6` representative-task discussion | Keep its distinction between structural/install/round-trip checks and multi-step work. Replace its current model-evidence discussion with the three rows in **Correct model-capability statement**. In particular, report neither a strict raw-format rate nor a general usable-import verdict from the smoke task; for the simplified task, verify semantic output through the installed CLI parsing path and the independent oracle. |
| C5 `docs/사용자/index.html`, opening claims that NJH-CLI directly reads and edits files, executes commands, and automatically repeats work as a `직접 일하는 동료` | NJH-CLI can be installed and can expose tools and diagnostics, but this delivery does not demonstrate correct autonomous file work, command work, or general task completion by the bundled models. Do not use those claims as permission for such work. |
| C5 `docs/import-guide.md` and Gateway `import-guide.md`, `실제 사용 가능 판정`, `VERDICT: USABLE — 로컬 모델을 지금 쓸 수 있습니다.`, and `Recipient gateway acceptance` | These verdicts mean only the named model-runtime, health, route, and bounded request predicates. They are not semantic model-correctness or representative-task acceptance. |
| C5 `docs/폐쇄망-배포-운영-가이드.md` and Gateway `docs/field-operations-guide.md`, `VERDICT: USABLE — 로컬 모델을 지금 쓸 수 있습니다.` and `✓ 결론: 지금 njh 를 실행하면 연결됩니다.` | Read these as backend availability and connection readiness only. A ready model endpoint is not evidence that the model can complete a correct task. |
| C5 `docs/로컬LLM-통합-운영문서.html`, claims that `njh-cli가 실제 작업`, automatically searches, reads, edits, runs, verifies, and completes work, and the current-looking 7B artifact/completion figures | These are historical/reference claims, not current v1.5.321 recipient acceptance evidence. Do not treat them as a product promise. The current capability boundary is the paired simplified-merge and original-task result above. |
| C5 `docs/njh-cli-소개-발표.html`, `docs/njh-cli-소개-발표-amber.html`, `docs/njh-cli-소개-발표-cyan.html`, and `docs/njh-cli-소개-발표-violet.html`, shared claims that the CLI edits files, runs commands, confirms results, and ends work after a passing test | A command or test result is not a semantic acceptance of the model’s task. These presentations do not establish a generally correct agent workflow for the bundled models. Use only the narrow capability statement in this erratum. |
| C5 `docs/presentation/njh-2slide-static.html`, `docs/presentation/njh-2slide-rich.html`, and `docs/presentation/njh-2slide-ppt2013-safe.html`, `답변만 하는 AI가 아니라, njh-cli 는 실제 작업까지 합니다` and `읽고, 고치고 검증` | Do not read these short capability slogans as a demonstrated general-work promise. The delivery is NO-GO for general correct task completion. |
| C5 `docs/njh-cli-도입검토-발표.html`, `상한 확인` figures of `98% 결과물 통과율` and `90~95%`, plus claims that the execution layer performs real work | Those aggregate historical figures are not the v1.5.321 delivery capability result. They do not override the current original-task 0-of-40 semantic result or establish general task correctness. |
| C5 `docs/로컬LLM-임베딩OCR-슬라이드.html`, `무인 자동화` claims, including nightly report drafting and model-created files for later review | Deterministic pipeline mechanics may be evaluated separately, but this delivery does not demonstrate correct unattended model-generated reporting, file creation, or tool work. Human or independent validation remains required. |
| Gateway `설치가이드-통합.html`, checklist requirements for `VERDICT: USABLE — 로컬 모델을 지금 쓸 수 있습니다.` and `VERDICT: YES` | These are infrastructure results for residency, health, routing, and HTTP-level checks. They do not authorize a general functional rollout or correct model task. |
| Gateway `docs/README.md`, its final measurement categories `대화형 사용 가능`, `비동기 작업 전용`, and `사용 불가`, and its wording about whether an install is usable | Interpret these categories as gateway transport and performance availability only. They do not measure semantic correctness of an answer or task completion. |
| Gateway `docs/FIELD-TEST-CHECKLIST.md`, the expected-result statement that exit `0` means `대화형 사용 가능` | Exit `0` establishes only the checklist’s measured response, timing, routing, and HTTP predicates. It is not a model-correctness or task-acceptance verdict. |

## Laptop archive clarification

In the Laptop archive, `README.md` and `laptop-guide.html` describe seven local
runtime/configuration/one-turn checks. A passing result means only those seven
named checks passed. It does not establish general instruction following,
correctness, tool use, or multi-step task reliability. No archive replacement is
required to continue using the laptop setup and diagnostic procedure.

## What to do now

1. Keep the v1.5.321 archives, installed files, and matching checksum record.
2. Use installation, local-doctor, laptop, and gateway results only for their
   stated structural, runtime, and connection scopes.
3. Place this erratum with the extracted documentation or delivery record so a
   later reader does not rely on a superseded embedded capability claim.
4. Do not approve general task completion, autonomous action, file changes,
   tool-using work, or correctness-critical answers from the bundled models.
5. Await the separately versioned corrective release for archive-embedded
   documentation that incorporates this correction.
