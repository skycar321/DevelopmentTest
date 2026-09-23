# Vue 3 draft kit — 2026-09-24 (103th)

This directory holds one encrypted archive (`vue3-draft-kit-20260924-103.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260924-103.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- 기본 모양(regular → V4 underlined) 입력칸의 상자 크기를 as-is 와 같게: 랩 68개 화면 as-is 와 다른 픽셀 평균 102차 0.51% → 0.43%, 표 칸에 날짜 칸이 있는 화면 3.5% → 0.8%, 나빠진 화면 0, 요소 스윕 191 → 170(늘어난 것 0)
- css-canon: V2 regular 입력칸 상자(루트 위 4·라벨 자리 12·입력 여백 8·줄 20·최대 높이 32·아이콘 4)를 두 설치본 CSS 에서 읽어 V4 underlined 변수로 — 필드 44·글자 중심·아이콘 위치 같게(rem 은 calc). 값을 못 읽으면 옮기지 않는다
- css-canon: 입력칸 계열이 V2 에서 물려받던 글꼴 속성(굵기·줄 높이·자간) — V4 가 직접 정한 것 중 V2 짝이 정하지 않은 것은 inherit(표 머리 칸 안 입력 글자 굵게)
- 22 theme: 전역 VTextField 에 V2 기본 size 20(V4 는 input size 1 — 표 칸 날짜 칸이 184 → 147px). 호환 부품 src/compat/vuetify2-input-size.js, 인스턴스·ref·v-model·defaults 그대로, 화면이 준 size 가 이긴다. 세 가지를 합쳐야 as-is 폭이 된다(실측)
- 테스트: regular 상자 유도식·못 읽으면 보류, 물려받는 글꼴, size 20 설치본 SSR 실행 증명·theme 끝-대-끝(revert-red 확인). 게이트 103 블록
