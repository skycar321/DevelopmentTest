# Vue 3 draft kit — 2026-09-16 (57th)

This directory holds one encrypted archive (`vue3-draft-kit-20260916-57.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260916-57.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.1** from this channel (`v1.6.1/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- **승인 없이 계속 진행(기본)**: `00d-devai-setup.sh` 가 `VUE3_DEVAI_PERMISSION=auto`(기본)로 편집·명령을 묻지 않게 설정하고, 킷이 맡는 커밋과 되돌리기 어려운 명령(`git commit/push/reset/checkout/clean/rebase/stash`, `rm -rf`)만 거부한다. 사내 주 에이전트가 자체 권한을 가져도 `devai-code agent list` 로 이름을 받아 같은 규칙을 얹는다(plan·내부 에이전트 제외). 반복 호출 감지(doom_loop)도 묻지 않는다. 묻게 하려면 `VUE3_DEVAI_PERMISSION=ask`. 랩 엔진에서 자체 ask 권한을 가진 주 에이전트에 규칙이 적용되는 것 확인.
- **셸 19 병렬 실행과 겹치지 않게**: 오케스트레이터가 시작 행에 pid 를 남기고, DevAI 도우미(`next-unit`·`harvest`·`goal-start`·`parallel-status`)가 셸에서 돌린 19 실행이 살아 있으면 오류로 멈춘다(pid 가 없는 옛 실행은 end 행 없음 + 원장 45분 안 갱신으로 판단, `VUE3_DEVAI_IGNORE_RUN=1` 로 무시). 56차의 "진행 중인 병렬 실행은 없다" 단정을 이 판정 결과로 바꿨다(사내에서 njh-cli 워커 실행이 도는 중에 DevAI 가 같은 작업 트리를 건드릴 뻔했다).
- 56차까지의 변경 포함.
