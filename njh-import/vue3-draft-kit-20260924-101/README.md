# Vue 3 draft kit — 2026-09-24 (101th)

This directory holds one encrypted archive (`vue3-draft-kit-20260924-101.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260924-101.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- 요소 비교가 못 보는 배치(줄바꿈·상자 높이·위치)를 as-is·to-be 화면 스크린샷 픽셀 비교(랩 68개 화면)로 찾아 공통 수리: 98차 대비 as-is 와 다른 픽셀 평균 1.17% → 0.71%, 중앙값 0.88% → 0.27%, 좋아진 화면 36·나빠진 화면 1(0.08%p)
- 흰 상자 입력칸: V2 solo·solo-inverted·filled 불리언 속성을 V4 variant 로(스킬 Vuetify 코드모드, 22 vuetify). V4 는 그 속성을 무시해 기본 모양으로 그렸다. flat·rounded·single-line 은 V4 도 같은 속성이라 그대로
- 버튼 줄바꿈: css-canon 이 V2 열 클래스를 설치된 V4 이름(v-col--cols-*)으로 옮긴다 — V2 .col { width: 100% } 를 덮던 .col-auto { width: auto } 를 못 옮겨 cols="auto" 열이 줄 전체 폭이던 것
- 구조 대응 표: V2 선택 컨트롤 루트 위 여백(16+4px)·체크박스 높이(입력 상자 24px)·solo 높이(48px, 조밀 38px)를 V4 루트·크기 변수·필드 입력 높이 변수로
- 테스트: 코드모드 픽스처(solo·solo-inverted·filled, 라벨 값 보호), css-canon 구조 대응·열 이름. 게이트 101 블록
