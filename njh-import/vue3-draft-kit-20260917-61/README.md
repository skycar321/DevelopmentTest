# Vue 3 draft kit — 2026-09-17 (61th)

This directory holds one encrypted archive (`vue3-draft-kit-20260917-61.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260917-61.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.1** from this channel (`v1.6.1/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- **DevAI 비대화형 호출 실패의 원인과 수정**: 사내 `19d-devai-run.sh --doctor` 결과 엔진 로그가 `default agent "<사내 기본 에이전트>" is a subagent` 였다. DevAI 설정의 기본 에이전트가 `devai-code run` 에서는 서브에이전트로 판정돼, 옵션 없는 호출부터 모델 전에 `UnknownError` 로 끝났다(TUI 는 이 검사를 거치지 않아 정상). 19d 와 19 병렬 DevAI 워커가 이제 `--agent <주 에이전트>` 를 붙인다. 기본은 `agent list` 의 `build`, 없으면 plan·내부 에이전트를 뺀 첫 주 에이전트이고 `VUE3_DEVAI_AGENT` 로 바꿀 수 있다. 로컬 OpenCode 1.18.25 에 같은 설정(기본 에이전트 = 서브에이전트)을 만들어 같은 오류를 재현하고, `--agent build` 로 인자·명령 파일·첨부·stdin 네 방식이 모두 통과함을 확인했다.
- **`--doctor` 7단계**: 1단계는 에이전트 지정 없이(DevAI 기본), 2단계부터 지정 에이전트로 부른다. 1단계만 실패하면 "기본 에이전트 문제이며 19d 는 영향 없음" 이라고 해석해 준다.
- **설치기 발견 검사 오판 수정**: `agent list`·`debug skill` 의 큰 출력을 파이프로 받으면 뒷부분이 잘리고 `printf | grep -q` 가 SIGPIPE 로 실패해, 스킬이 있는데도 "못 찾는다" 고 나왔다. 임시 파일로 받아 읽는다(로컬 실제 엔진에서 스킬·서브에이전트 둘 다 OK 확인).
- 게이트: 가짜 엔진이 `--agent` 없는 호출을 모두 실패시키는 흉내로 19 DevAI 워커 REPAIRED, 19d 보수, `--doctor` 해석을 검사한다.
- 60차까지의 변경 포함.
