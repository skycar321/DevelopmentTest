# Vue 3 draft kit — 2026-09-18 (73th)

This directory holds one encrypted archive (`vue3-draft-kit-20260918-73.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260918-73.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- **반입 전 사전 점검으로 한 번에 고친 판**: 사내에서 하나 확인하고 하나 고치는 반복을 줄이려고, 반입 전에 복제 환경(참조 소스로 만든 to-be + 합성 목 API)에서 as-is 개발·to-be 개발·to-be 운영 빌드의 라우트를 열고 조회·탭·다이얼로그·날짜 선택까지 눌러 비교했고, Windows + Git Bash(한글 경로·Node 22)와 남은 단계(15·17·18·12·21·00b)도 미리 돌렸다.
- **22단계 새 분류 5개**
  - `css`: 전체 화면 다이얼로그 전역 CSS 가 Vuetify 4 에서 화면 전체 오버레이 루트에 붙어 **닫힌 다이얼로그가 화면을 흰색으로 덮던 것**을 내용 상자(`> .v-overlay__content`)로 옮기고, 이름만 바뀐 Vuetify 2 클래스를 설치본 이름으로 바꾼다(postcss 토큰 단위, 구조가 바뀐 것은 사람 확인). 복제 환경 흰 막 화면 13→0(운영 빌드).
  - `error-handler`: Vue 3 개발 빌드가 훅 예외를 다시 던져 원래 결함이 있는 화면이 개발 서버에서만 멈추던 것 → Vue 2 처럼 콘솔에 남기고 계속 그리는 오류 처리기. 이 뒤 콘솔 오류 **줄 수는 늘어난다**(오류 종류는 같다).
  - `select-slots`(목록 슬롯 `on/attrs` → `props`), `router-link-tag`(Vue Router 4 가 없앤 `tag`), `common`(날짜 부품이 `false` 초기값을 받음).
  - 09 에 대응 규칙, 00b 22 행이 새 분류를 짚는다.
- **17 동작 동등성**: 사내에서 돌지 않던 랩 하네스 대신 08b 와 같은 조건(포트·세션·조회 전용 목록)으로 to-be·as-is 를 재고 조회·검색 버튼과 탭만 눌러 요청 모양·예외·표시를 비교한다(쓰기 버튼은 누르지 않고 요청 값은 남기지 않는다). 측정 못 하면 "미검증"으로 끝나고 검증 결과 파일을 남기지 않는다(00b 가 완료로 세지 않는다).
- **15 엑셀 축**: 20·22 뒤 자동 0 이던 원인(설정 파일 모양·이미 처리한 규칙이 관문에 걸림)을 분석 사본에서 풀고, 라이브러리 대조가 같은 화면만 쓴 뒤 빌드·커밋·완료 표시(실패하면 되돌림).
- **12 따라잡기**: 작업 브랜치 위에서 화면마다 3방향 병합 → 결정론 단계 재실행 → 빌드·커밋, 기준은 스크립트가 갱신(태그 push 는 선택).
- **08c** 는 유효한 as-is 비교가 있으면 "to-be 에서만 안 뜸" 만 보수, **18** 은 킷 소유 파일 제외·예상 시간 표시, **21** 은 킷 어댑터 제공 전역과 닿지 않는 파일을 따로 센다, **8b 보고서**는 옛 판 as-is 결과를 비교 무효로 보고 CORS 실패의 허용 출처 값을 적는다.
- **Windows**: DevAI 병렬 보수가 시작 직후 끝나던 것(detached), 중단 뒤 남은 작업자 정리, CR 을 뺀 기준 해시, `node_modules` 정션 먼저, 공백 경로 초기 거부.
- 게이트: 73차 단위·실브라우저 테스트(흰 막·오류 처리기·목록 슬롯·router-link·날짜 부품, 17 비교·15 분석 사본·12 병합·08c 대상·18 범위), 실험 트리 22 새 분류 적용·재실행 무변경·72차 모양에서 오류 처리기만 커밋, 17 측정 불가 시 미검증·00b, Windows 배선, 00-env 공백 경로.
- 72차까지의 변경 포함.
