# njh-import v1.5.321 — canonical delivery

## Read this before installation

This delivery is **structurally verifiable but functionally NO-GO for work that
requires correct general task completion** with its bundled models.

NO-GO does **not** mean that the archives are corrupt or that the CLI cannot be
installed. It means that package integrity, installation, connection, and a
basic request/response path do not demonstrate that the bundled model completes
representative work correctly. Do not approve a general functional rollout,
automated action, file change, tool-using task, or correctness-critical answer
on the strength of this delivery.

You may use this delivery for package inspection, offline installation,
configuration and connection diagnostics, controlled failure reproduction, and
the exact bounded transformation described in [Model capability and limit](#model-capability-and-limit)
when an independent oracle checks the result.

> Do not use `njh-import/v1.5.316/`. It lacks the evidence required to verify
> canonical build contents and source provenance. Use v1.5.321 only.

## Delivered files

| File | Intended role | Required | Verified size |
| --- | --- | --- | ---: |
| `njh-cli-v1.5.321.7z` | Personal PC or laptop CLI | Yes | 19,433,234 bytes |
| `njh-win-laptop-v1.5.321.7z` | Windows laptop-local LLM helper | For the laptop-local role | 1,726,816 bytes |
| `njh-gateway-v1.5.321.7z` | Gateway operator package | For the gateway role only | 1,972,194 bytes |

The archive password is delivered through an approved separate channel. Do not
put it in a command line, shell history, ticket, repository, or this directory.

The following evidence files must remain beside the three archives:

- `SHA256SUMS.txt`
- `c5-build.json`
- `gateway-build.json`
- `laptop-build.json`
- `source-commit-attestation.json`
- `safety-gate.json`

## 1. Verify before extracting

### 1.1 Verify archive digests

macOS, Linux, or Git Bash:

```bash
shasum -a 256 njh-cli-v1.5.321.7z njh-gateway-v1.5.321.7z njh-win-laptop-v1.5.321.7z
cat SHA256SUMS.txt
```

Windows PowerShell:

```powershell
Get-FileHash .\njh-cli-v1.5.321.7z, .\njh-gateway-v1.5.321.7z, .\njh-win-laptop-v1.5.321.7z -Algorithm SHA256
Get-Content .\SHA256SUMS.txt
```

Each computed digest must exactly match the line for the same filename in
`SHA256SUMS.txt`. Do not extract or install an archive with a mismatch; obtain a new
copy through the approved delivery channel.

### 1.2 Verify the supplied build evidence

Node.js is already a required CLI prerequisite, so the following command needs
no network access or extra package:

```bash
node <<'NODE'
const fs = require('fs');
const c5 = JSON.parse(fs.readFileSync('c5-build.json', 'utf8'));
const gateway = JSON.parse(fs.readFileSync('gateway-build.json', 'utf8'));
const laptop = JSON.parse(fs.readFileSync('laptop-build.json', 'utf8'));
const attestation = JSON.parse(fs.readFileSync('source-commit-attestation.json', 'utf8'));
const safety = JSON.parse(fs.readFileSync('safety-gate.json', 'utf8'));
console.log(JSON.stringify({
  c5ManifestEntries: c5.files.length,
  gatewayInnerEntries: gateway.files.length,
  laptopManifestEntries: laptop.files.length,
  sourceCommit: attestation.source_commit_short,
  safetyStatus: safety.status,
  leakHits: safety.packages.map(({ artifact, leak_hits }) => ({ artifact, leak_hits }))
}, null, 2));
NODE
```

For this delivery, the result must show:

- `c5ManifestEntries: 312`
- `gatewayInnerEntries: 37`
- `laptopManifestEntries: 18`
- `sourceCommit: "ddd1cad593cf"`
- `safetyStatus: "pass"`
- `leakHits: 0` for all three archives

These facts mean the supplied build evidence identifies the received archive,
records its listed contents and source commit, and records a passing configured
safety scan. They do **not** demonstrate model correctness or general task
completion.

## 2. Install the CLI package

### 2.1 Prerequisites

- Node.js 22.13.0 or later
- An approved 7-Zip command available as `7z`
- The separately delivered archive password
- Git Bash on Windows, or a standard shell on macOS or Linux

### 2.2 Extract the C5 archive

```bash
mkdir -p "$HOME/njh-cli-v1.5.321"
7z x njh-cli-v1.5.321.7z -o"$HOME/njh-cli-v1.5.321"
cd "$HOME/njh-cli-v1.5.321"
test -f ./setup.sh
test -f ./dist/njh-cli.js
test -f ./README.md
```

Enter the password only at the 7-Zip prompt. The C5 archive places files
**directly** in the selected extraction directory; it does not create an inner
`njh-cli-v1.5.321/` wrapper. For v1.5.321, the C5 manifest has 312 entries and
25 distinct top-level path components. `./setup.sh` must be present directly in
the directory selected above.

### 2.3 Run setup and confirm the local installation

macOS, Linux, or Git Bash:

```bash
bash ./setup.sh
"$HOME/.njh-cli/bin/njh" --version
"$HOME/.njh-cli/bin/njh" --doctor --local --json
```

Windows PowerShell:

```powershell
.\setup.ps1
& "$HOME\.njh-cli\bin\njh.cmd" --version
& "$HOME\.njh-cli\bin\njh.cmd" --doctor --local --json
```

The version must be `1.5.321`. The local doctor must exit with code `0` and
report `summary.fail` as `0`. This verifies the installed local CLI and local
configuration checks. It does not verify a live gateway, corporate credentials,
or model-task correctness.

Do not run an older `njh` from another extracted directory. If the command
resolves to an old version, open a new terminal and use the installed path shown
above before diagnosing a model or connection issue.

## 3. Optional role packages

### 3.1 Windows laptop-local LLM helper

Extract the laptop archive to the directory you intend to operate from. It has
18 manifest-listed files at that selected root and places `setup-local-llm.sh`,
`verify-local-llm.sh`, and `start-local-llm.sh` directly there.

```bash
mkdir -p /c/njh-local-llm
7z x njh-win-laptop-v1.5.321.7z -o/c/njh-local-llm
cd /c/njh-local-llm
test -f ./setup-local-llm.sh
test -f ./verify-local-llm.sh
test -f ./start-local-llm.sh
bash ./setup-local-llm.sh
bash ./verify-local-llm.sh
bash ./start-local-llm.sh
```

Run these commands in Git Bash. `setup-local-llm.sh` reports what approved local
model artifact it can find and stops with guidance if none is available; it does
not download a model from the network. The local model result is not a general
correctness acceptance.

### 3.2 Gateway operator package

The gateway archive has one outer member,
`njh-gateway-v1.5.321.tar.gz`. Extract that member, then extract the tarball into
the directory selected for the gateway role. `setup-gateway.sh` must land
directly at that second directory.

```bash
mkdir -p "$HOME/njh-gateway-v1.5.321-outer" "$HOME/njh-gateway-v1.5.321"
7z x njh-gateway-v1.5.321.7z -o"$HOME/njh-gateway-v1.5.321-outer"
tar -xzf "$HOME/njh-gateway-v1.5.321-outer/njh-gateway-v1.5.321.tar.gz" -C "$HOME/njh-gateway-v1.5.321"
test -f "$HOME/njh-gateway-v1.5.321/setup-gateway.sh"
bash "$HOME/njh-gateway-v1.5.321/setup-gateway.sh" --help
```

Use the displayed `setup-gateway.sh` syntax only with the approved three backend
addresses and client CIDR for the target environment. The 37-entry gateway
manifest and a successful gateway setup diagnose package and route state; they
do not establish correctness of a model response.

## 4. Connection and first-use checks

To configure an approved gateway connection, run:

```bash
njh onboard gateway
bash "$HOME/.njh-cli/scripts/njh-connect-doctor.sh"
```

Enter the approved gateway address and token only in the interactive prompt. If
the target uses an internal CA, configure the approved `caBundle`; do not disable
TLS certificate verification. A successful connection doctor proves only the
stated connection predicates.

The first model request can remain silent while it is processed. Do not call it
failed from elapsed time alone. Follow `docs/사용자/04-문제-해결.md` for the
180-second observation boundary, CPU or partial-output checks, and the one-time
cancel rule. Waiting does not repair the known model-capability limit below.

## Model capability and limit

The following results must be read together. They are measurements from the
non-corporate test machines `OM`, `mac-new-tb`, and `oracle`, not results from a
recipient target. The semantic and original-task results below remain valid
observations. The earlier strict-rate comparison does not: its historical host
labels record where unseeded runs occurred, not a controlled host comparison.

| Task and boundary | Evidence | Recipient decision |
| --- | --- | --- |
| Simplified two-level configuration merge on `qwen2.5-coder:7b` | An independent semantic oracle judged 100 of 100 outputs correct after Markdown-fence extraction on each of the three test machines. | This is a narrow, plausibly hardware-independent capability for this exact task, model, and oracle. Verify the result with the independent oracle on the target before use. |
| Same simplified merge, strict raw-output contract | The previously reported 92-to-100-per-100 range is **void as a comparison and as a rate**. None of the 300 historical harness runs sent a seed. Ollama 0.32.1 applied `Seed -1`, which the backend used as a random RNG seed, and the backend did not return the actual seed. Those seeds cannot be recovered. | Take no numerical strict-pass expectation and no host conclusion from the withdrawn figure. Check the required raw format on every output and do not use an output that fails it. A future strict-rate claim requires a new controlled, seed-recorded study. |
| Original multi-rule representative task across two tested models | Semantic correctness was 0 of 40. | This task is functionally NO-GO. A plausible response, exit code `0`, successful installation, green local doctor, or successful connection does not make it pass. |

The 100-of-100 result does not qualify the original multi-rule task, and the
0-of-40 result does not erase the narrow demonstrated 7B capability. Neither
result establishes that the product is a generally correct task-completion
system. The withdrawn strict figure changes neither semantic observation because
neither depends on comparing the three hosts.

Do not use the bundled models to make or approve file changes, perform
autonomous or tool-using work, provide correctness-critical answers, or make an
automated decision. Treat all other model output as untrusted diagnostic text
until a human, source of record, or independent checker validates it.

## What is verified and what is not

| Statement | Status | Scope |
| --- | --- | --- |
| Archive digest matches `SHA256SUMS.txt` | Verifiable by recipient | Identifies the received archive bytes only. |
| Build manifests list 312 C5, 37 gateway, and 18 laptop entries | Verifiable by recipient | Records package contents and extraction-layout checks. |
| Source attestation names `ddd1cad593cf` | Verifiable by recipient | Records the source commit named by build evidence. |
| `safety-gate.json` is `pass` with zero leak hits for all three packages | Verifiable by recipient | Records archive identity, extraction, version-marker, and configured leak-scan checks. |
| CLI installs and local doctor passes | Verifiable on the target | Establishes local installation and local diagnostic predicates only. |
| Gateway connection doctor passes | Verifiable on the target when configured | Establishes the named connection predicates only. |
| General correct completion of representative work | Not demonstrated | The original multi-rule task was semantically correct in 0 of 40 observed runs. The separate simplified 7B configuration merge was semantically correct in 100 of 100 observed runs after fence extraction; that narrow result does not qualify the original task. |

Authenticated Codi calls, internal SQMS access, and gateway fleet acceptance are
not established by the supplied package evidence. If an internal dependency
cannot be reached, record `BLOCKED_INTERNAL_NETWORK`; do not record PASS.

## Business-unit review document included in the CLI package

The C5 package contains `docs/타겟점검-AI-고도화-검토보고.html`. Extract the C5
archive and open that file in a browser; it references no external CDN, font, or
image, so it renders on a closed network.

The document opens with a summary section titled 「사업부서 요청 — 그래서 어떻게
처리하는가」, which is also the first table-of-contents entry. Everything below
that section is the evidence for it. The summary states where the business-unit
proposal and the requester proposal are each correct and where each is wrong,
why `실판매자` is the one field an LLM is needed for, the measured throughput of
the 10,000-per-day pilot, the boundary of what is and is not being committed to,
and the five items that must come back from the business unit before the work
can proceed.

This document records a technical review. It is not affected by the model
capability limit above, which concerns the bundled models rather than the review.

## Help inside the extracted CLI package

| Need | Open this file after C5 extraction |
| --- | --- |
| First 30 minutes after import | `docs/사용자/06-post-import-first-30-minutes.md` |
| Installation or update | `docs/사용자/02-설치-및-업데이트.md` |
| Connection, TLS, or no-output troubleshooting | `docs/사용자/04-문제-해결.md` |
| Gateway connection check | `docs/사용자/게이트-연결-테스트.md` |
| Package evidence and layout verification | `docs/사용자/05-배포-번들-검증.md` |

## Recipient decision

Accept this delivery for the structural, installation, diagnostic, and narrowly
oracle-checked purposes stated above. Do **not** accept it as a generally
functionally correct model product. Any broader acceptance requires new,
model-specific evidence for the exact model, version, task, oracle, and target
environment.
