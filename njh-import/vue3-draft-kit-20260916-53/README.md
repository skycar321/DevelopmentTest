# Vue 3 draft kit — 2026-09-16 (53th)

This directory holds one encrypted archive (`vue3-draft-kit-20260916-53.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260916-53.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.1** from this channel (`v1.6.1/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- **DevAI 모델 고정** `VUE3_DEVAI_MODEL="<provider>/<model>"`: DevAI 는 기본 모델을 자기 설정(`devai-code debug config` 의 `model`)에서 고르므로, Qwen 으로 못 박으려면 이 변수를 두고 `00d-devai-setup.sh` 를 다시 돌린다(TUI 기본 모델과 내장 에이전트 build·plan·general·explore·title·summary·compaction·vue3-fixer 의 model 까지 `.opencode/opencode.json` 에 기록 — 사내 설정엔 에이전트별 gpt/claude/gemini 고정이 있어 서브에이전트가 딴 모델로 갈 수 있다. 사내 기본값은 DevAI 의 기본 Qwen 모델(id 는 `devai-code debug config` 로 확인)). 19 DevAI 워커·연결 확인과 `19d` 비대화형은 같은 변수를 `--model` 로 넘긴다. 목록은 `devai-code models`. 추론 강도는 `VUE3_DEVAI_VARIANT=high|medium|low|none` → `--variant`.
- 사내 실측 반영: `devai-code run --help` 에 `--auto` 있음, `run --format json 'Reply exactly CLI_FIRST_RESPONSE_OK'` 정상(5토큰 답에 입력 35,071 토큰 = DevAI 기본 시스템 프롬프트 크기 — 유닛 수 × 35K 가 크레딧에 잡힌다는 안내를 README 에 추가).
- 52차까지의 변경 포함.
