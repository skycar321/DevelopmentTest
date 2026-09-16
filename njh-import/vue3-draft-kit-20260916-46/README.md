# Vue 3 draft kit — 2026-09-16 (46th)

This directory holds one encrypted archive (`vue3-draft-kit-20260916-46.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260916-46.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.1** from this channel (`v1.6.1/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- **병렬 실행 중 진행 표시**: 작업 시작 시 `[시작] <id> (n/총)` 을 찍고, 1분마다 `… 진행 중 N개 · 끝남 k/총 · <id> <경과 분>/<로그 KB> …` 하트비트를 찍는다. DevAI 가 파일당 12~20분 걸릴 때 화면이 조용해 "도는 건가" 를 알 수 없던 문제(45차 사내). 로그 KB 가 늘면 DevAI 와 대화 중이라는 뜻. 간격은 `ORCH_HEARTBEAT_MS`. 테스트 6/6.
- **거부 지침 추가** `empty-name-refused:`(컴포넌트 name 누락 → 파일명 PascalCase 로 name 지정, script setup 은 defineOptions). 지침 17개.
- 45차에서 이어지는 사내 안내: 43차 Ctrl+C 로 회수 도중 죽어 V3 에 남은 미커밋 변경은 `git -C "$V3" checkout -- .` 로 버리고 재실행(재실행이 그 파일을 다시 처리). 44차부터는 중단이 회수 뒤에만 처리돼 재발하지 않는다.
