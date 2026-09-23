# Vue 3 draft kit — 2026-09-24 (99th)

This directory holds one encrypted archive (`vue3-draft-kit-20260924-99.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260924-99.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- 사내 97차 화면 사진(날짜·셀렉트 칸이 규격과 다르게 밀림, 메뉴 글씨가 선택 전 파랑·선택 뒤 검정)에서 시작해, 랩에서 as-is(Vue 2·원본 CSS)와 to-be 를 68개 화면 전부 요소 하나하나 짝지어 계산 스타일을 비교(앱 안 이동, 127.0.0.1 만)한 결과의 공통 수리: 98차 기준 차이 1073건 → 533건, 새로 생긴 차이 0. 화면별 예외 없이 설치본(Vuetify 2·4, AG Grid 25·31)에서 규칙을 뽑아 22단계가 판정한다
- 입력칸: V2 `<fieldset>` 테두리 규칙을 V4 외곽선 조각(`__start`·`__notch`·`__end`)으로 나눠 두 겹 외곽선·글자 붙음·줄 밀림 해소(22 css·css-canon, 이미 변환된 트리도 멱등). 라이브러리 입력칸 내부 규칙은 옮기지 않고 루트 글꼴만. 날짜 부품이 빈 문자열로 초기화한 화면에서 달력이 열린 채 뜨던 것 수리(공통 부품 새 판 + 이전 판 목록)
- 링크·테마 색: V2 가 실행 중 주입하던 링크 색(`.v-application a`)과 색 도우미를 as-is 가 실제로 쓴 테마로 되살림(css-canon). 새 22 분류 `theme`: as-is 가 `theme` 바로 아래에 둔 색은 V2 가 무시했으므로 V2 병합 규칙(기본 밝은 테마 ⊕ themes.light)으로 V4 색을 만든다(to-be 만 primary 가 초록이던 것), 입력칸 기본 모양 V2 regular = V4 underlined, 초안이 filled 로 옮긴 regular 날짜 칸 수리. 초안 코드모드도 같은 바이트로 수렴
- 버튼: css-canon 이 계층 밖으로 옮긴 V2 기본 규칙을 V2 에서 덮던 라이브러리 문맥 규칙도 V2 순서로 함께 옮김(상단 메뉴 버튼 모서리 0 → 4px 회귀 126 → 0). V2 밝은 테마 부품 색(버튼 배경 등)을 vuetify-overrides 계층으로, 팔레트 색 버튼의 흰 글씨를 V2 처럼(테마 색만 흰 글씨), 어두운 문맥 제외
- 그리드: 새 22 분류 `grid-inert` — as-is ag-grid-vue 2.x 가 prop 으로 받지 않아 한 번도 불리지 않던 `:gridReady`·`:gridSizeChanged` 핸들러를 초안이 이벤트로 살려 to-be 에서만 열이 0.76 배로 눌리던 것을, as-is 설치본 prop 목록으로 판정해 되돌림(16개 화면). 호환 부품이 AG Grid 25 열 기본값(정렬·크기 조절 꺼짐)을 되살림(31 기본값 변경으로 머리글이 눌리고 손가락 커서 159 → 0), 화면·열의 명시 값은 그대로
- 17 이 비교 뒤 같은 짝으로 8b 담당자 명단의 as-is 구분을 다시 가른다(08b --asis 를 따로 안 돌려도 된다)
- 테스트: V2 실효 테마·입력칸 기본 모양·플러그인 수렴·Vuetify 4 실행 증명, 캐스케이드 친척·부품 색·팔레트 글자색, 그리드 콜백 판정(실제 as-is 설치본)·끝-대-끝, 호환 부품 열 기본값 실브라우저 4건. 게이트 99 블록
