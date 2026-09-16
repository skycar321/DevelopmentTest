# Vue 3 draft kit — 2026-09-16 (52th)

This directory holds one encrypted archive (`vue3-draft-kit-20260916-52.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260916-52.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.1** from this channel (`v1.6.1/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- **세 경로 시험** (`run/19t-trial.sh`): 잔여 거부 화면을 경로별로 한 건씩 돌려 같은 표로 비교한다. `--list`(대기 파일 번호·A/B/C 추천) → `A <파일>`(njh `--yes -p` → DevAI 비대화형이 본 트리에서 보수 → 킷이 검증·파일별 커밋·원장) → `B <파일1> <파일2>`(njh 가 메인 DevAI 에게 두 파일을 `@vue3-fixer` 서브에이전트로 동시에 맡기게 함 → 회수) → `C <파일>`(DevAI TUI `/vue3-fix` → `/vue3-harvest` 안내, 끝나면 `C --done`) → `--report`(경로 | 파일 | 결과 | 소요 | 커밋 | 로그, `trial-report.md`). A·B 는 njh 1.6.7 DevAI 연결이 전제, C 는 51차 팩. B 의 실제 병렬 여부는 모델이 정하므로 확인 안 되면 보고서에 그렇게 적는다. `REFUSED_REPAIR_ONLY=<파일,...>` 로 19단계 대상을 특정 파일로 제한. `tools/trial-report.mjs`(+테스트 9), 게이트 `19t-trial` 단계.
- **같은 팩을 비대화형으로** (`run/19d-devai-run.sh <파일>|--next`): TUI 없이 `devai-code run --auto --command vue3-fix` 로 같은 `.opencode/` 명령·스킬을 쓰고 킷이 회수한다(배치·원격 셸). README 51차 절에 소절 추가, 게이트 `19d-devai-run` 단계(가짜 devai-code 가 명령 파일의 주입 명령을 실제로 돌린다).
- 51차까지의 변경 포함.
