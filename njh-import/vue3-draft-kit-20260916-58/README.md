# Vue 3 draft kit — 2026-09-16 (58th)

This directory holds one encrypted archive (`vue3-draft-kit-20260916-58.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260916-58.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.1** from this channel (`v1.6.1/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- **셸 19 실행 감지 오판 수정**: 57차는 병렬 실행 원장의 `end` 행만 종료로 봐서, Ctrl+C 로 멈춘 실행(`aborted`)이나 예외로 끝난 실행(`fatal`)을 45분간 "아직 도는 중" 으로 잡아 DevAI 보수를 막을 수 있었다(사내 실행이 `aborted` 로 끝남). 이제 세 표식 모두 종료로 본다. 오케스트레이터는 중단 때 진행 중인 작업이 없어도 실행 단위 `aborted` 행을 남긴다.
- 57차까지의 변경 포함.
