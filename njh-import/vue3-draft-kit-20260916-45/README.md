# Vue 3 draft kit — 2026-09-16 (45th)

This directory holds one encrypted archive (`vue3-draft-kit-20260916-45.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260916-45.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.1** from this channel (`v1.6.1/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- **워커를 프로세스 트리째 종료(Windows 고아 node.exe 결함)**: 워커는 `bash → njh.cmd → cmd.exe → node.exe` 로 손자까지 내려가는데, 시간 초과·중단 때 직계(bash)만 죽여 Windows 에서 node.exe 가 고아로 남아 DevAI 호출을 계속하며 메모리를 잡았다(43차 사내: TIMEOUT 3건 = node.exe 3개 잔존, Ctrl+C 뒤에도 5개). 이제 Windows 는 `taskkill /F /T /PID`, macOS/Linux 는 프로세스 그룹(detached + `kill(-pid)`)으로 트리째 죽인다. 테스트 5/5(손자 잔존 0 시나리오 추가).
- 남아 있는 고아 정리(43/44차로 돌린 뒤): Git Bash 에서 `powershell -Command "Get-CimInstance Win32_Process -Filter \"Name='node.exe'\" | Select ProcessId,CommandLine"` 로 njh 워커만 골라 `taskkill //F //T //PID <pid>`.
- 동시 수 안내: 워커는 독립 프로세스라 `REFUSED_REPAIR_JOBS` 를 10 까지 올려도 노트북 부담은 작다(대기 중엔 놀고, 회수 빌드는 한 번에 하나). 제약은 DevAI 의 동시 요청 허용치뿐 — 시작 직후 여러 건이 수십 초 만에 NO_CHANGE/REJECTED 로 끝나면 4 로 낮춘다. 권장: `REFUSED_REPAIR_MAX=<남은 수> REFUSED_REPAIR_JOBS=10 REFUSED_REPAIR_TIMEOUT_MS=2400000`.
