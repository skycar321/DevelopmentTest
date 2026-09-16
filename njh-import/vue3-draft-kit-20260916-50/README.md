# Vue 3 draft kit — 2026-09-16 (50th)

This directory holds one encrypted archive (`vue3-draft-kit-20260916-50.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260916-50.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.1** from this channel (`v1.6.1/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- **워커를 DevAI 로 바꿔 끼우기** (`REFUSED_REPAIR_WORKER=devai`): njh-cli 가 프록시에서 차단돼도 19단계(거부 화면 병렬 보수)가 돈다. DevAI 는 OpenCode 엔진(`devai-code`)의 사내 래퍼라 비대화형 `devai-code run --format json --dir <워크트리>` 에 같은 프롬프트를 stdin 으로 넘긴다. 격리 워크트리·회수(대상 외 폐기·기능 삭제 거부·빌드 검증·파일별 커밋)·원장·재실행 건너뛰기는 njh 워커와 동일. 연결 확인도 DevAI 로. 실행 파일명은 `REFUSED_REPAIR_DEVAI_BIN`(기본 `devai-code`, 보통 `~/.opencode/bin`), 아예 다른 CLI 는 `REFUSED_REPAIR_WORKER='<명령 틀>'`(자리표시자 `{WT}` `{PROMPT}` `{V3}` `{RUN}`). 오케스트레이터 `--worker` 옵션, 테스트 7/7, 게이트에 가짜 `devai-code` 로 19 DevAI 모드 검증 단계 추가. README "워커를 DevAI 로" 절.
- 한계: 18단계 드라이버와 njh 대화형 경로는 njh 전용. njh 가 막힌 동안은 19 만 DevAI 로 돌린다.
- **문서 정정**: 대화형 절·상단 순서·`prompts/대화형/5-거부-화면-병렬.txt`·19 안내를 "49차 + njh 1.6.5 이상"으로(48차+1.6.4 조합은 유닛이 읽기 전용).
- 랩 검증 결과 반영(로컬 14B, v1.6.5 코드): njh 안에서 한 문장으로 시키자 `parallel_run` → 유닛 3개 파일 편집 → 회수기가 검증 뒤 파일당 커밋 → 채팅 집계(REPAIRED 3)까지 자율 진행.
- 49차까지의 변경 포함.
