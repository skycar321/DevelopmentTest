# Vue 3 draft kit — 2026-09-16 (47th)

This directory holds one encrypted archive (`vue3-draft-kit-20260916-47.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260916-47.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.1** from this channel (`v1.6.1/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- **njh 대화형 안에서 병렬 보수(19단계)**: `bash run/19-refused-screens.sh --njh-parallel` 이 거부 화면 프롬프트를 유닛 JSON(`<state>/refused-parallel.json`, 유닛 = 파일 하나, concurrency = REFUSED_REPAIR_JOBS 최대 8)으로 쓰고, njh(**1.6.3 이상**)를 V3 에서 띄워 `/parallel start --watch --file <경로>` 로 동시에 돌린다. TUI 에서 진행·유닛별 출력(`/parallel output`)·취소(`/parallel cancel`)를 그대로 쓴다. 끝나면 `--harvest` 가 본 트리에서 오케스트레이터와 같은 규칙으로 회수한다(대상 외 폐기 — 미추적은 `strays/` 보존, 기능 삭제 거부, `npm run build` 실패 시 차단 파일만 되돌리고 최대 6회 재검증, 파일 하나 = 커밋 하나, 원장 REPAIRED 기록 → 다음 `19 --list` 가 건너뜀). 새 도구 `tools/harvest-tree.mjs` + 테스트 1/1(게이트 편입). README "대화형 병렬" 절과 상단 순서에 추가.
- 셸 경로(`REFUSED_REPAIR_JOBS=N …`)와 원장 형식이 같아 섞어 써도 되지만 **동시에 돌리지는 말 것**(같은 파일을 두 곳에서 고친다).
- 46차까지의 변경(하트비트·트리 종료·건너뛰기·지침 17개) 포함.
