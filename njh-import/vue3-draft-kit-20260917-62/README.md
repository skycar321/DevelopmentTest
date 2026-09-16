# Vue 3 draft kit — 2026-09-17 (62th)

This directory holds one encrypted archive (`vue3-draft-kit-20260917-62.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260917-62.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.1** from this channel (`v1.6.1/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- **08c 화면 보수와 18 소견 드라이버를 DevAI 로도 돌린다**: 실행기 선택 `VUE3_AI_WORKER=auto`(기본: `devai-code` 가 있으면 DevAI) | `devai` | `njh` (단계별 `SCREEN_REPAIR_WORKER`·`PHASE1_WORKER`). 08c 는 화면마다 DevAI 비대화형 run 한 번으로 보수하고(주 에이전트 지정, 엔진 오류는 "막힘" 과 구분해 연속 3번이면 멈춤), 18 은 드라이버의 njh 자리에 새 어댑터 `tools/devai-as-njh.mjs` 를 넣어 소견마다 DevAI run 한 번으로 처리한다. 드라이버 판정(GOAL_DONE / `BLOCKED: <이유>`)과 증거 형식은 그대로다.
- **18 드라이버 Windows 결함 수정**: 드라이버가 node 를 셸로 띄워, 목표문이 공백마다 쪼개지고 `BLOCKED: <이유>` 의 `<이유>` 가 cmd 입력 리디렉션으로 해석됐다(njh 실행기로도 한 번도 제대로 못 돌았을 가능성이 크다). 셸 없이 띄운다.
- **njh-cli 1.6.10 과 짝**: njh 의 DevAI 백엔드도 주 에이전트 `build` 를 기본으로 넘긴다. 1.6.9 이하는 에이전트 없이 불러 사내 DevAI 에서 모델 전에 죽는다.
- **19d·08c 엔진 오류 판정 보강**: 긴 로그에서 `grep -q` 가 먼저 끝나 판정이 뒤집히지 않게 읽는 방식을 바꿨다.
- 게이트: 08c(가짜 실패 화면 1개 → DevAI 보수 커밋, 에이전트 없는 호출은 죽는 흉내), 18(어댑터 테스트 + 실험 트리 소견 1건이 어댑터→DevAI→드라이버 판정까지). 로컬 OpenCode 1.18.25 에 사내와 같은 기본 에이전트 설정을 만들어 어댑터 동작 확인.
- 61차까지의 변경 포함.
