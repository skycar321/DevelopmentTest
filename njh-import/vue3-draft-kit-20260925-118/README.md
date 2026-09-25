# Vue 3 draft kit — 2026-09-25 (118th)

This directory holds one encrypted archive (`vue3-draft-kit-20260925-118.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260925-118.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- 118차는 디스크 정리다 — 사내 PC 디스크가 가득 차 12 를 못 돌렸다. 화면 결과는 116·117차와 같다(117차 Windows 파일 잠금 재시도·이어하기·원인 출력 포함)
- 원인: 초안 생성기는 단계마다 입력 트리 전체를 사본(.tmp/draft-project/stages/*/proposal-*)으로 만들고 지우지 않았다 — 초안 폴더 하나에 33벌. 12 는 회차마다 초안 폴더를 하나 더 만들었고, 새 출발 백업(-old-<날짜>)도 남았다. 랩 실측: 초안 폴더 하나 3.1 GB 중 사본이 대부분
- 생성기: 단계 결과를 트리에 옮긴 뒤 그 단계의 사본을 바로 지운다(영수증은 남아 이어하기·검증 그대로), 도중에 죽은 준비의 고아 사본은 다음 준비가 지운다. 게시 전 영수증의 사본이 없으면 게시하지 않는다. 랩 실측: 초안 폴더 3.1 GB → 116 MB(사본 33 → 0), src 바이트 117차 초안과 같음, 생성 시간 163 → 158초
- 12 시작 때 자동 정리: 지난 회차 초안 폴더 중 지금 기준 초안과 그 회차의 기준(12 --replay 용)만 남기고 삭제, 새 출발 백업 삭제(워크트리는 git 으로 커밋 안 된 변경이 없을 때만), 남기는 초안은 끝난 단계 사본만 삭제(옛 킷이 만든 초안도 3.1 GB → 116 MB, src 그대로). 디스크 여유를 전후로 찍는다. 끄기 VUE3_KEEP_OLD_TREES=1
- 설치본 보호: 초안 폴더·옛 as-is 워크트리의 node_modules 는 설치본으로 가는 정션이다 — 정리 도구가 링크를 먼저 끊고 지우고, 보호 대상이 지울 폴더 안이면 거부, 지운 뒤 설치본이 비었으면 멈춘다
- 테스트: 트랜잭션 3(사본 삭제 뒤 이어하기·재적용, 고아 정리와 게시 전 사본 보존, 사본 없는 게시 거부) · 정리 도구 5(링크 너머 설치본 보존·보호 대상 거부·두 단계 링크·사본 정리·CLI) · 12 끝-대-끝 2(자동 정리 규칙·끄기) — revert-red 확인, 관련 테스트 21파일 116차와 실패 목록 동일. 게이트 118 블록(신선 사슬 초안 사본 0·영수증 남음)
