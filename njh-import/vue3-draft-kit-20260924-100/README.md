# Vue 3 draft kit — 2026-09-24 (100th)

This directory holds one encrypted archive (`vue3-draft-kit-20260924-100.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260924-100.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- 99차의 전 화면 요소별 CSS 비교(랩 68개 화면, 앱 안 이동, 127.0.0.1 만)를 이어 간 공통 수리: 99차 533건 → 100차 191건(98차 1073건 대비 −82%), 새로 생긴 차이 0. 남은 것은 대부분 V2·V4 입력칸 DOM 구조 차이(보이는 칸 크기는 같음)와 측정 잡음
- 그리드 머리글: AG Grid 31 이 새로 켠 동작 — 그룹 없는 열 머리글을 그룹 줄까지 늘리기, 그룹 머리글 글자를 스크롤에 붙이기 — 를 호환 부품이 25 처럼 끈다(열·그룹 기본값). 화면이 defaultColDef 를 속성으로 직접 넘겨도 그리드를 만들 때 25 기본값 위에 얹는다(25 도 속성이 이겼다). 실브라우저 테스트
- 새 라이브러리 명시도 역전: 계층 밖 AG Grid 31 규칙이 선택자를 더 구체적으로 바꿔 as-is 에서 이기던 앱 CSS 를 뒤집던 자리(머리글 윗선 색)를, as-is 에서 앱이 이기던 속성만 옮긴 앱 규칙 사본의 명시도를 올려 되돌린다(평상시 요소 규칙끼리 비교 — 의사 요소·상태 규칙 제외)
- 열 간격: V2 거터(열 padding·행 margin)와 V4 거터(행 gap)가 겹치던 것 — V2 모델을 옮기면 V4 gap 을 0 으로(dense·no-gutters 도 V4 이름으로)
- 색: 테마 색 버튼은 V2 규칙대로 흰 글씨(V4 는 배경 명도로 계산), 이름이 그대로 대응하는 입력칸 색(라벨)은 옮기고 V4 강조 불투명도가 걸린 대상은 불투명도 1(두 번 흐림 방지)
- 구조 대응 표: V2 선택 컨트롤 입력 상자(24×24 + 8px) → V4 크기 변수·wrapper 끝 여백(라디오 라벨 위치 일치). V2 글자 도우미 클래스(.display-2·.title·.headline·.body-1 등, 템플릿 사용·V4 에 없는 이름)도 원래 선택자·!important 그대로(제목 줄 높이 일치)
- 테스트: 거터 모델·명시도 역전 판정(옛 라이브러리에 지던 앱 규칙은 올리지 않음)·테마 색 버튼·입력칸 색·구조 대응·글자 도우미, 호환 부품 실브라우저 5건(속성 defaultColDef 포함), 검출기 핀. 게이트 100 블록
