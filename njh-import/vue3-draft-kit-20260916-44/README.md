# Vue 3 draft kit — 2026-09-16 (44th)

This directory holds one encrypted archive (`vue3-draft-kit-20260916-44.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260916-44.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.1** from this channel (`v1.6.1/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- **병렬 오케스트레이터 중단 처리**: Ctrl+C·kill 이 오면 진행 중 워커를 죽이고 그 워크트리를 지운 뒤 원장에 `aborted` 로 남기고 130 으로 끝난다(회수된 커밋은 그대로). 시작할 때는 이전 실행이 남긴 `wt-*` 워크트리(강제 종료 뒤 남는 것)를 `git worktree prune` + 제거로 정리한다. 테스트 4/4(중단·잔여 정리 시나리오 추가).
- **시간 초과 표기**: 제한 시간에 걸린 워커의 편집도 빌드 검증을 통과하면 회수·커밋한다(부분 진행 보존, 재실행이 그 지점부터 이어감). 그런 줄은 `[TIMEOUT·부분회수]` 로 찍어 커밋 해시가 붙은 이유를 보여 준다(42차 사내 문의). 원장 status 는 그대로 `TIMEOUT` 이라 19 재실행 대상이다.
- **19 `--list` 안내**: 끝에 실제 남은 거부 수를 넣은 명령(`REFUSED_REPAIR_MAX=<남은 수> REFUSED_REPAIR_JOBS=2 …`)을 찍는다. 상한을 안 주면 20개씩 나눠서 하고 재실행은 보수된 파일을 건너뛴다.
- 42/43차 사내 실측(참고): DevAI 로 파일당 12~20분, 동시 2 에서 5건 중 3건이 20분 제한에 걸림. 재실행 권장값: `REFUSED_REPAIR_TIMEOUT_MS=2400000 REFUSED_REPAIR_JOBS=3`.
