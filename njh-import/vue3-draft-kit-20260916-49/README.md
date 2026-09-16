# Vue 3 draft kit — 2026-09-16 (49th)

This directory holds one encrypted archive (`vue3-draft-kit-20260916-49.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260916-49.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.1** from this channel (`v1.6.1/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- **대화형 병렬 보수 유닛에 쓰기 권한** (njh **1.6.5** 이상 필수): `19 --njh-parallel` 이 만드는 유닛 파일에 `agent_type: implementer`, `profile: worker`, `required_tools`(읽기·검색·쓰기·편집·실행)를 싣는다. 48차 + 1.6.4 조합에서는 기본 서브에이전트가 읽기 전용이라 유닛이 파일을 못 고치고 전부 NO_CHANGE 로 끝났다(랩 재현). 셸 경로(`REFUSED_REPAIR_JOBS=N …`)는 영향 없음.
- 랩 검증(로컬 14B): "유닛 3개를 동시에 돌려줘. 끝나면 회수 명령을 실행하고 집계 한 줄만 답해" → 모델이 `parallel_run`(file·concurrency 인자 정확) → 회수 명령 → 채팅으로 집계 보고까지 자율 진행. 스킬 지침에 njh 1.6.5 요구 명시.
- 48차까지의 변경 포함.
