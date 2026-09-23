# Vue 3 draft kit — 2026-09-24 (102th)

This directory holds one encrypted archive (`vue3-draft-kit-20260924-102.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260924-102.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- 101차의 화면 픽셀 비교를 이어서, 라이브러리 척도·기본값이 바뀐 자리를 공통 수리: 랩 68개 화면에서 as-is 와 다른 픽셀 평균 101차 1.1% → 0.5%, 101차보다 좋아진 화면 17·나빠진 화면 0
- 새 분류 22 z-scale: 앱 z-index 를 Vuetify 4 겹침 척도로. V2 는 머리글 5·대화상자 202, V4 는 레이아웃 1000+·오버레이 2000+ (inline). 경계는 두 설치본에서 읽고 전역 CSS·SFC style·정적 style 을 한 단조 사상으로(앱 값끼리 순서 유지), 표지 주석으로 재실행 멱등. 전체 화면 덮개가 머리글을, 로딩 덮개가 대화상자를 다시 덮는다
- 22 css: 부품과 한 복합의 theme--light 는 "그 테마 안" 문맥 :is(.v-theme--이름, .v-theme--이름 *) 으로(V4 는 테마 클래스를 제공자에만 붙인다 — 탭 배경 규칙이 죽어 흰 막대). 명시도 같음, 이미 옮긴 트리도 재실행으로 수리
- css-canon: V2 오버레이 막 기본값(VOverlay prop #212121 · 0.46)을 설치본에서 읽어 V4 막(#000 · 0.32)에, V2 에서 모양 없이 폭만 있던 테두리(.v-card border-width)는 옮기지 않는다(V4 카드 바탕 solid 로 1px 테두리가 살아나 화면 전체 1px 밀림)
- 테스트: z-scale 경계·단조 사상·주석/문자열 보존·끝-대-끝·실설치본, 테마 문맥·재실행 수리, 오버레이 막·효과 없던 폭(revert-red 확인). 게이트 102 블록
