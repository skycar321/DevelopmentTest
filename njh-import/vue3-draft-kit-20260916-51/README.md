# Vue 3 draft kit — 2026-09-16 (51th)

This directory holds one encrypted archive (`vue3-draft-kit-20260916-51.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260916-51.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.1** from this channel (`v1.6.1/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- **DevAI 안에서 이어서 하기** (`run/00d-devai-setup.sh` → `cd "$V3" && devai`): njh 가 막혀도 DevAI TUI 한 세션에서 잔여 작업을 끝낸다. 킷이 `.opencode/` 팩(스킬 `vue3-migration`, 명령 `/vue3-status`·`/vue3-refused [N]`·`/vue3-fix [파일]`·`/vue3-harvest`, 서브에이전트 `@vue3-fixer`)을 Vue 3 워크트리에 설치한다(git 에는 안 들어감). `/vue3-status` 가 어디까지 했는지와 다음 명령 한 줄을 찍고, `/vue3-refused 3` 은 킷 오케스트레이터를 DevAI 워커 3개로 뒤에서 돌리며 진행을 보고하고, `/vue3-fix` 는 세션 안에서 파일 하나를 직접 고친 뒤 `/vue3-harvest` 로 검증·커밋한다. 도우미 `tools/devai-brief.mjs`(+테스트), 게이트 `00d-devai` 단계, README 절 "DevAI 안에서 이어서 하기 (51차)", 프롬프트 `prompts/대화형/6-devai-에서-이어서.txt`.
- 실제 OpenCode 1.18.25 엔진(DevAI 와 같은 버전)으로 랩 검증: 명령·스킬·서브에이전트 로드, `/vue3-fix` 편집, 비대화형 `run` 의 권한 동작(아래 보고서 참조).
- 50차까지의 변경 포함(19단계 DevAI 워커 모드).
