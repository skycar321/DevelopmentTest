# Vue 3 draft kit — 2026-09-23 (94th)

This directory holds one encrypted archive (`vue3-draft-kit-20260923-94.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260923-94.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- 화면 CSS 회귀 수리(사내 2026-09-23: 본문이 고정 머리글 아래로 올려붙어 잘리고 상단 메뉴 항목 CSS 가 안 먹음): 실체는 퍼블리싱 CSS 의 전역 리셋 `*{margin:0;padding:0}`(base.css)을 22 css-canon 이 계층 밖에 복사한 것(초안 05 는 app-reset 으로 감싸 두었다) — Vuetify 4 는 부품 스타일을 CSS 계층(@layer) 안에 두어 계층 밖 리셋이 특이도와 무관하게 .v-main padding-top(112→0)·.v-list-item padding 을 지운다. 22 css 가 클래스 없는 전역 규칙을 `@layer vuetify-core.base` 블록으로 옮기고(원래 순서·@media 유지·멱등), css-canon 도 전역 리셋을 같은 계층 안에 쓰고, index.html 의 초안 계층 순서 선언을 Vuetify 전 계층으로 늘린다(첫 선언이 순서를 못 박는다). 초안 코드모드가 @charset 뒤 첫 규칙의 * 리셋을 못 알아보던 가장자리 결함도 수리. 랩 실브라우저 실측: 겹침 as-is 0 / to-be 112px / 수리 뒤 0
- 22 error-handler 에 라우터 resolve 가드: WAS 메뉴에는 있는데 화면 라우트가 없는 이름을 Vue Router 4 가 던져 v-list-item 이 사라지던 것(사내 CommRatBlckAdm)을 Vue Router 3 처럼 경고 + '/' 로. errorHandler 를 이미 둔 트리에도 따로 들어간다
- 8b 스윕이 화면마다 고정 머리글 아래로 들어간 본문 px 와 라우트 없는 메뉴 이름을 찍고 요약 줄에 합산한다(머리글 겹침 N화면 · 라우트 없는 메뉴 이름 N개). 게이트에 as-is↔to-be 실브라우저 비교 테스트 추가
- 24 push 단계가 커밋이 많으면(사내 113개) `git log | head -15` SIGPIPE 로 push 앞에서 조용히 끝나던 것 수리(`git log -15`), 모든 단계가 set -e 로 죽을 때 파일:행·코드·명령을 찍는 종료 트랩, run/*.sh 의 같은 파이프 모양 금지 검사, bare 원격에 커밋 120개를 실제로 올리는 테스트
- 23 보고 머리 시각 UTC → 현지 시각
