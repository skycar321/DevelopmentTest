# Vue 3 draft kit — 2026-09-24 (109th)

This directory holds one encrypted archive (`vue3-draft-kit-20260924-109.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260924-109.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- 108차는 리허설에서 멈춤(게이트가 as-is 미설치 신선 사슬에서 새 분류 aggrid-css 의 커밋을 요구 — 킷 동작이 옳고 검사가 틀림) → 업로드 없이 109차에 합침. 게이트는 89 블록처럼 as-is 픽스처에 AG Grid 25 설치본을 넣고 aggrid-css 만 다시 돌려 검증하도록 고침
- 107차 화면 픽셀 비교를 이어서 AG Grid 25 와 31 이 조용히 다르게 하는 일과 바깥선 입력칸 틈을 공통으로 옮김: 랩 68개 화면 as-is 와 다른 픽셀 평균 107차 0.10% → 0.08%, 중앙값 0.08% → 0.06%, 합계 행 화면 0.34% → 0.08%·0.26% → 0.03%·0.11% → 0.00%, 나빠진 화면 0(회전 아이콘·로딩 표시는 찍은 순간 차이)
- 새 22 분류 aggrid-css(layout-restore 바로 뒤) ①: AG Grid 25 CSS 는 --ag-* 를 정의하지 않았다(읽기만) → 앱의 var(--ag-x, 값) 은 늘 그 값. 31 은 테마가 정의 → as-is(25 설치본 + 앱 CSS·SFC·스크립트 setProperty)가 정의하지 않고 31 이 정의하는 변수만 값으로 고정(중첩 대체값까지, 대체값 없는 var 는 사람 확인)
- aggrid-css ②: 열 구분선 가상 요소 25 .ag-header-cell::after ↔ 31 ::before(두 설치본에서 구분선 색 변수를 쓰는 규칙으로 판정) → 앱이 ::after 에 준 색·숨김(width: 0)을 같은 규칙의 ::before 에도(옛 선택자 유지, 앱이 ::before 를 이미 쓰면 사람 확인, scss·less 는 ① 만)
- aggrid-css ③: 31 은 선택할 행이 없으면 머리 전체 선택 체크박스를 끄고 불투명도 0.5 — 25 는 끈 적 없음(진입 파일 cbSelectAll.setDisabled 유무·31 CSS 로 판정) → main 의 마지막 AG Grid 스타일 import 뒤에 compat/aggrid-v25-parity.css 한 번(표지 KIT108_AGGRID25_PARITY, 동작은 31 그대로)
- 호환 부품 ag-grid-vue3-legacy.js: 25 는 고정(합계) 행 영역 높이에 1px 을 더했다(설치본 setFloatingHeights) → 격자 생성 뒤·pinnedRowDataChanged 마다 고정 행 모델(행 높이 합) + 1 을 다시 씀. 31 API 리스너는 비동기라 DOM 값에 더하면 +2px(실브라우저 테스트가 잡음). 새 파일 가져오기 없음. 엑셀 묶음 검출기 부품 핀 갱신
- css-canon: V2 바깥선 틈(legend) = fieldset 테두리 1 + 왼쪽 여백 8 자리, 폭 = 라벨 + 6(VTextField JS) → V4 선 시작 칸 9px·떠 있는 라벨 앞 3·뒤 3(107차는 글자 자리만 맞춰 틈이 글자에 붙었다). 둥근 칸은 V4 그대로, 못 읽으면 107차대로
- 테스트: aggrid-css 판정·변환·SFC·끝-대-끝·체크박스 판정·main 삽입, 고정 행 높이 실브라우저(AG Grid 31 UMD)·설치본 근거, 바깥선 틈 판독·보류(전부 revert-red 확인). 73차 분류 순서 계약 갱신. 게이트 108 블록(리허설 트리에서 aggrid-css 커밋·main 표지·부품 판)
- aggrid-css: as-is 에 AG Grid 25 설치본이 없으면 조용히 넘기지 않고 사람 확인(css-canon 과 같이 — as-is 폴더에서 npm install 뒤 다시)
- 22 vuetify: V2 solo·solo-inverted 는 single-line(VTextField isSingle = isSolo || singleLine || fullWidth — 라벨이 뜨지 않고 값·포커스가 있으면 숨음, 입력은 라벨 자리 없이 가운데) → 라벨 있는 정적 variant solo·solo-inverted 에 single-line(가려진 variant 문자열도 복원해 판정 — 이미 변환된 트리도 재실행으로). 픽스처 대조 테스트
- css-canon: 앱이 V2 입력 밑줄(.v-input__slot::before·::after)에 준 규칙 → V4 밑줄(.v-field .v-field__outline::before·::after), 색을 주면 V4 선 불투명도(0.38) 대신 1 — 이름만 옮기던 .v-field:before 는 V4 가 그리지 않는다(랩 날짜 칸 밑줄 rgb 214 실측 일치)
- css-canon: 100차 명시도 올리기가 31 끌기 유령 요소 규칙 때문에 앱 .ag-theme-balham 12px 를 (0,3,0) 으로 올려 as-is 에서 이기던 앱 .ag-db.ag-theme-balham 14px 를 이겼다 → 같은 대상·속성의 앱 규칙이 원래 규칙을 이기고 올린 명시도 이하이면 그 속성만 원래 명시도로 나눠 싣고 영수증 deferred(대시보드 0.12% → 0.03%)
- 랩 68개 화면(오늘 날짜 as-is 로 다시 찍음, 촬영은 웹 글꼴 로드 대기): 평균 107차 0.10% → 0.08%, 중앙값 0.08% → 0.06%, 나빠진 화면 0. 남은 큰 몫인 격자 머리 글자 1px 는 계산 스타일·좌표가 같고 한 화면씩 새로 열면 차이 0 — 연속 이동 뒤 래스터화 이력 차이로 판정, 옮기지 않음
- 게이트 109 블록(리허설 트리에서 solo single-line·parity 밑줄 규칙, 코드모드 픽스처·css-canon·aggrid-css 테스트)
