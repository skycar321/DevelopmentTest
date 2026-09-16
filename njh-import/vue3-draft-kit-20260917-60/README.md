# Vue 3 draft kit — 2026-09-17 (60th)

This directory holds one encrypted archive (`vue3-draft-kit-20260917-60.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260917-60.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.1** from this channel (`v1.6.1/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- **DevAI 엔진 오류를 "수정 없음" 으로 적던 결함 수정**: 사내에서 `devai-code run` 이 모델 작업 전에 `UnknownError`("Unexpected server error")로 10여 초 만에 끝났는데, `19d-devai-run.sh --loop` 가 종료코드를 버리고 NO_CHANGE 로 기록한 뒤 다음 파일로 넘어갔다. 이제 (편집 없음 + 엔진 오류)는 `ENGINE_ERROR` 로 따로 적고, 서로 다른 파일에서 연속 3번이면 반복을 멈추고 원인 확인 명령을 안내한다. 커밋·작업 트리는 건드리지 않는다.
- **지시 전달 방식 자동 전환**: 기본이 팩의 명령 파일(`run --command vue3-fix <파일>`, 사내에서 모델 응답이 확인된 방식)이다. 엔진 오류면 같은 지시를 `--file` 첨부, 그다음 stdin 으로 다시 보내고, 통한 방식을 그 반복의 남은 파일에 고정한다. `VUE3_DEVAI_DELIVERY=command|file|stdin` 으로 하나만 쓰게 할 수 있다. 엔진 로그(WARN 이상)를 실행 로그에 같이 남겨 오류 번호의 원인 줄이 보인다.
- **`19d-devai-run.sh --doctor`**: 옵션 없는 호출·모델과 강도·19d 옵션·명령 파일·첨부 파일·stdin 을 짧게 한 번씩 불러 PASS/FAIL 과 오류 줄, 돌릴 명령을 찍는다(파일은 고치지 않는다).
- 게이트: 가짜 엔진이 사내 오류를 흉내 내어 전환(command→file 뒤 REPAIRED), 연속 3건 중단(커밋 0·작업 트리 깨끗), `--doctor` 여섯 단계를 검사한다. 로컬 OpenCode 1.18.25 실제 엔진으로 `--doctor` 여섯 단계 통과 확인.
- 59차까지의 변경 포함.
