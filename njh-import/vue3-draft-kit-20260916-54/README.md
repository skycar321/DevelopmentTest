# Vue 3 draft kit — 2026-09-16 (54th)

This directory holds one encrypted archive (`vue3-draft-kit-20260916-54.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260916-54.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.1** from this channel (`v1.6.1/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- **njh 를 DevAI 위에서 (njh 1.6.7)**: README 새 절 "njh 를 DevAI 위에서 — 프록시가 njh 를 막아도". `.njh/settings.json` 의 `connection.active: devai`(`executable: devai-code`, `surface: run`, `model`, `autoApprove: true`, `timeoutMs`, `maxSessions`, `agents`) + 최상위 `reasoningEffort`(보수 작업은 `high` 권장). njh 1.6.8 기본 설정 시드에도 비활성 `devai` 연결 항목이 들어가 `active` 만 바꾸면 된다. 달라지는 점 표: 18단계 드라이버는 DevAI 경유로 동작, 19 셸 경로는 njh 워커·devai 워커 둘 다, **19 "말로 시키기"는 불가 → `--njh-parallel` 뒤 `/parallel start --watch --file`**, 승인은 DevAI 권한 체계, 첨부·토큰 집계 미지원, 턴마다 DevAI 기본 프롬프트 약 35K 토큰.
- 19 `--njh-parallel` 안내와 `prompts/대화형/5-…` 에 DevAI 연결일 때의 직접 명령 경로 추가. 07 실패 안내에 DevAI 연결 힌트.
- 53차까지의 변경 포함.
