# Vue 3 draft kit — 2026-09-23 (93th)

This directory holds one encrypted archive (`vue3-draft-kit-20260923-93.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260923-93.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- 12 --kit 한 회차의 vite 빌드를 15번 → 3번으로: 22 가 분류마다 빌드하던 것을 모든 분류 뒤 한 번으로(합친 빌드가 깨질 때만 분류별로 다시 돌아 범인만 되돌림), 20·22 가 빌드로 검증한 커밋을 기록해 12 가 같은 트리를 다시 빌드하지 않는다 — 빌드 한 번이 메모리 정점(실제 앱 1.5~3 GB)이라 정점 횟수를 줄였다(2026-09-23 사내 12 실행 중 PC 정지 사례)
- 초안 생성기를 준비/빌드 두 프로세스로 분리: 변환 프로세스(158화면 1.9 GB)를 닫고 새 프로세스(약 1.0 GB)가 영수증을 이어받아 진단 빌드만 한다(강제 GC 로는 RSS 가 줄지 않음을 실측). 생성기 힙 상한 1.5 GB. 진행 줄에 여유 메모리 표시(Windows PowerShell·macOS·Linux)
- 의존성 취약점 실측 단계 run/26-dep-audit.sh 와 결정서 6절: to-be 잠금 6건 → qs 6.16.0·uuid override(^11.1.1) 뒤 3건(남은 3건은 xlsx 계열 = 15b·21b 로 사라지는 것), as-is 156건. 사내 Nexus 가 audit 엔드포인트를 안 주면 "조회 불가" 로 끝나고 격리 목록 대조로 판정
- 12 가 멈췄을 때 이어서 하는 법(README Kit 93): 반쯤 쓴 병합은 checkout 으로 버리고 다시, 커밋 뒤 멈춤은 --finish
- 22 common: 킷 부품 사본을 손 수정으로 오판하던 것 수리 — 이전 킷 판 목록에 85·86 판 추가, 파일 git 이력이 전부 킷 커밋(미커밋 변경 없음)이면 새 판 채택(사내 AppDatePicker 사례)
- 12 --transplant-receipts 가 Windows 에서 ERR_UNSUPPORTED_ESM_URL_SCHEME 으로 죽던 것 수리(분석기 동적 import 를 file:// URL 로) + 절대 경로 동적 import 금지 검사. 92차로 이 단계가 '옮길 화면 없음·손작업' 으로 끝났다면 미실행 — 93차로 15 전에 다시

