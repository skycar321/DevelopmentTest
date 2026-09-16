# Vue 3 draft kit — 2026-09-16 (55th)

This directory holds one encrypted archive (`vue3-draft-kit-20260916-55.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260916-55.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.1** from this channel (`v1.6.1/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- **export 없이 바로**: 킷 기본값에 `VUE3_DEVAI_MODEL`(사내 기본 Qwen 모델 id)·`VUE3_DEVAI_VARIANT=high` 를 박아(`run/_lib.sh`) `bash "$KIT/run/00d-devai-setup.sh"` 만 치면 TUI 기본·내장 에이전트 모델과 19 워커·19d 비대화형의 `--model/--variant` 가 정해진다. 바꾸려면 같은 이름의 환경변수를 먼저 두면 되고, 빈 문자열이면 DevAI 자체 기본.
- **잘못된 모델 이름을 설치 단계에서 막는다**: `00d-devai-setup.sh` 가 `<공급자>/<모델>` 형식이 아니면 멈추고 `Codi/<입력값>` 형태를 제안한다(사내에서 공급자 없이 모델 이름만 넣은 사례). DevAI 가 PATH 에 있으면 `devai-code models` 목록에 정확히 있는지도 확인하고, 없으면 비슷한 이름을 보여 준다(`VUE3_DEVAI_SKIP_MODEL_CHECK=1` 로 생략). 강도는 high|medium|low|none 만 받는다. 끝에 적용된 모델·강도와 확인 명령을 찍는다.
- njh 1.6.9 와 짝: DevAI 연결 안의 `reasoningEffort`(연결별 값) — 기본 설정 시드가 `high` 라 njh 쪽도 설정 없이 high.
- 54차까지의 변경 포함.
