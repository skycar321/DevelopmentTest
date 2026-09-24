# Vue 3 draft kit — 2026-09-24 (110th)

This directory holds one encrypted archive (`vue3-draft-kit-20260924-110.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260924-110.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- 109차 화면 픽셀 비교를 이어서 Vuetify 2 와 4 의 입력칸·선택칸 차이를 공통으로 옮김: 랩 68개 화면 as-is 와 다른 픽셀 평균 0.08%(유지), 중앙값 0.06% → 0.05%, 입력칸이 많은 화면 0.15% → 0.05%·0.14% → 0.09%, 나빠진 화면 0
- css-canon: V2 regular(밑줄) 입력칸의 입력 요소 상자 = 라벨 자리 P 아래 높이 MH(V2 CSS 에서 읽음) → V4 input.v-field__input 에 바깥 여백 P·위아래 안쪽 여백(MH 안 글자 한 줄 가운데)·min/max-height MH. textarea·select 의 div 는 제외(input 요소만)
- css-canon: V2 입력 라벨 줄 높이(.v-input .v-label 20px) → V4 쉬고 있는 라벨(V4 는 물려받은 24px 라 글자가 2px 아래). 영수증 출처 vuetify2-input-label
- css-canon: V2 solo 칸은 높이 H 의 한가운데 글자 → V4 입력 위·아래 여백 변수를 H/2 − 줄 높이/2 로(V4 .v-field__input 최소 높이 식에서 줄 높이 rem 을 읽음 — 높이 변수만 주면 52·56px 로 커짐)
- 22 theme: V2 선택칸 목록은 칸을 덮으며 칸 위쪽에서 열렸다(설치본 VSelect defaultMenuProps 에 offsetY 없음) → 킷 defaults 한 줄의 VSelect 에 menuProps(location top start · origin overlap, 표지 KIT110_V2_SELECT_MENU). 자동완성·콤보는 V2 도 아래라 그대로, 화면이 menu-props 를 주면 그 값이 이김. 목록 자리·높이 48 as-is 와 일치(랩 실측)
- 테스트: css-canon 56·vuetify2-theme 16(새 판정 전부 revert-red 확인). 게이트 110 블록(리허설 트리 parity 시트의 라벨 줄 높이·입력 상자 규칙, as-is 픽스처에 V2 VSelect 설치본을 넣고 22 theme 목록 자리·멱등)
