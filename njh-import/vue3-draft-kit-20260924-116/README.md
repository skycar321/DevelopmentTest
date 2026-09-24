# Vue 3 draft kit — 2026-09-24 (116th)

This directory holds one encrypted archive (`vue3-draft-kit-20260924-116.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260924-116.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- 116차는 비활성 입력칸의 공통 수리다 — 전부 22 css-canon(설치된 V2/V4 CSS 기반 공통 스타일)으로 들어온다
- css-canon: V2 비활성 입력칸은 글자·선택값·라벨 .38 · 바깥선 .26 · 아이콘 .38 의 색만 바꿨다(설치본 vuetify.css). V4 는 칸 전체 불투명도 .38 이라 앱 윤곽까지 옅고 글자는 강조 불투명도와 곱해졌다 — 설치본 V2 색을 V4 자리로, 칸 불투명도는 1(앱 CSS 에는 진다). V2 색 규칙·V4 불투명도 규칙을 못 찾으면 두지 않고 사유. 랩: 비활성 날짜 칸 4개 불투명도 1·글자 .38 = as-is
- css-canon: V2 입력 루트의 폭 상한(.v-input max-width 100%)을 V4 같은 루트로 — 표 칸 안 style 250px 날짜 칸이 칸(171px)을 넘어 옆 칸을 덮던 것(0.38% → 0.01%). 윤곽·여백·배치 선언은 여전히 V4 가 그리고, 버린 입력 루트 선언은 사유로 남긴다
- 화면 하나에만 박아 두었던 비활성 날짜 칸 측정 복원(114차 css-restore) 은퇴 — 위 일반 규칙과 겹쳐 글자 여백이 두 번(12px, 2.92%)이었다. css-canon 이 일반 규칙을 쓰는 실행에서 킷이 넣은 블록만(바이트 그대로) 걷어내고, 고친 블록은 사람 확인. css-restore 는 시트가 덮으면 다시 넣지 않는다(22 전체 한 번으로 끝남·멱등). 2.92% → 0.00%
- 테스트: css-canon 70(비활성 색만·반례, 루트 폭 상한·99차 단언 이식) · 비활성 날짜 칸 은퇴 1(줄끝·멱등·고친 블록·이전 판) · css-restore·22 분류·순서 114 — 새 테스트 revert-red 확인. 게이트 116 블록(89 블록 as-is 픽스처 뒤 시트 일반 규칙·블록 은퇴·css-restore 재삽입 없음·테스트)
