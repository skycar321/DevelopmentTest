# Vue 3 draft kit — 2026-09-17 (59th)

This directory holds one encrypted archive (`vue3-draft-kit-20260916-59.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260916-59.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.1** from this channel (`v1.6.1/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- **명령 파일에서 절대 경로 제거**: 사내 TUI 에서 작업 트리 경로의 `@`(예: `D:/@...`)를 DevAI 가 파일 첨부로 해석해 `/vue3-goal` 안의 도우미 인자가 `[file not found: ...]` 로 깨졌다(모델이 경로를 직접 복원해 넘어갔다). 설치기가 프로젝트에 `.opencode/vue3-brief.mjs` 를 만들고, 모든 명령·스킬은 `node .opencode/vue3-brief.mjs <명령>` 만 부른다. 경로는 그 파일 안에만 있다. 게이트가 명령 파일에 절대 경로 호출이 남았는지와 실행 파일 동작을 검사한다.
- **이미 커밋된 보수는 대기 목록에서 제외**: 원장에 REPAIRED 가 없는(옛 실행 등) 파일이 대기 목록에 남아 목표 반복이 한 바퀴를 헛돌았다. 19단계가 `거부 화면 보수` 커밋 제목의 id 도 보수 완료로 본다(커밋은 검증 통과분만 생긴다).
- **끝까지 돌리기 안내**: `--loop N`·`/vue3-goal N` 은 N건에서 멈춘다. 남은 전부는 셸 `19d-devai-run.sh --loop 100`(한 건마다 DevAI 새로 기동, 대기 없으면 자동 종료)이 가장 확실하다.
- 58차까지의 변경 포함.
