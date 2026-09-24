# Vue 3 draft kit — 2026-09-24 (113th)

This directory holds one encrypted archive (`vue3-draft-kit-20260924-113.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260924-113.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- 113차 화면 비교를 열린 대화상자(팝업)까지 넓힘: 화면마다 이름이 dialog·popup·modal·visible 인 상태 값을 하나씩 켜 대화상자를 열고 as-is 와 창 영역을 비교(약 230개) — 열린 상태 평균 0.58% → 0.04%, 1% 넘는 것 1개(남은 것은 114차로 넘긴 엑셀 업로드 패널). 정적 68개 화면 22개 개선·나빠진 화면 0(평균 0.07% → 0.06%, 중앙값 0.03% → 0.01%)
- css-canon: 바깥선 칸의 떠 있는 라벨 — V2 라벨은 입력 슬롯 끝까지 닿았다(최대 폭 133% × 배율 0.75) → V4 틈 상한을 칸 폭 − (슬롯 끝 여백 + 틈 시작 − 라벨 뒤 여백)으로, V2 입력 라벨 자간 normal(V4 .v-label 0.009375em), V2 라벨 중심은 윗선보다 1.5px 아래 — 날짜 칸 포커스 1.77% → 0.04%, 4.19% → 0.04%
- css-canon: V2 대화상자 카드는 블록이라 화면이 준 높이를 지켰다(V4 는 flex: 1 1 100% 가 덮었다) → 카드 flex-basis auto, V2 가 카드·form 을 flex 로 둔 것은 스크롤 대화상자뿐 → 스크롤 아닌 대화상자의 카드·form 은 block(V2 CSS 에 그 규칙이 있을 때만)
- css-canon: V2 요소 규칙(.v-application p 아래 여백 16px · ul/ol 왼쪽 여백 24px · code · kbd)은 V4 에 짝이 없다 → 원래 선택자·명시도로 계층 밖에(V4 부품 내부 요소는 :where 로 제외) — 대화상자 안 설명 문단 아래가 16px 짧았다(7.5% → 0.3%)
- css-canon: V2 rounded 입력칸은 슬롯 좌우 여백 24px·밑줄 없음 → V4 밑줄 rounded 칸에 같은 여백·선 숨김(바깥선·solo·filled 는 V4 입력 여백 변수가 따로 있어 두지 않음), V2 루트 위 여백 4px 은 조밀에도 그대로 → V4 조밀 밑줄 칸 루트에 — 전체 화면 대화상자 1.65% → 0.05%
- 22 css: 앱이 입력 부품에만 붙이는 클래스 뒤에 V2 내부 요소(fieldset·.v-input__slot)로 준 규칙은 V4 에 그 요소가 없어 효과가 없었다 → 그 클래스가 입력 부품에만 쓰일 때 V4 자리로(fieldset → 외곽선 조각, 슬롯 여백·최소 높이 → V4 필드 변수, 쉬는 라벨 top → 가운데 변환 없이). 표지 KIT113_V2_INPUT_CONTEXT, 멱등 — 엑셀 업로드 대화상자 3.4~4.1% → 0.06~0.14%
- 22 z-scale: 킷 복원 블록(layout-restore)의 z-index 는 이미 V4 척도 — 옮기지 않고, 예전에 옮긴 표지가 있으면 as-is 값으로 되돌린다(전체 화면 로딩 막이 대화상자를 덮던 것 11.5% → 0.02%)
- 22 common: AppDatePicker 칸 뒤 슬롯(field-append)을 칸 안쪽(V4 append-inner)으로 — V2 입력칸 append 슬롯은 안쪽이었다. 111~112차 판은 이전 판으로 알아본다(kit111 픽스처·공급자 묶음)
- 22 chart: vue-chartjs 3 차트 부품은 캔버스에 width·height 속성(기본 400)을 붙였고 Chart.js 2 는 그 비율로 크기를 정했다 → 생성된 얇은 래퍼에 width·height prop(기본 400, 표지 KIT113_CHART_CANVAS_SIZE, 손으로 고친 래퍼는 그대로·툴팁 부품 충돌이면 통째 손작업). compat/chart-v2-layout.js 는 비율을 지키는 차트를 Chart.js 2 처럼 처음 캔버스 비율로(도넛·파이·폴라·레이더의 Chart.js 4 기본 비율 1 을 넘는다) — 대화상자 안 선 차트 16.8% → 0.22%
- 보류(114차): 엑셀 업로드 패널 채택 수정(파일 입력·읽기 버튼을 머리 상자 밖으로 빼지 않기)은 14b 소스 프로필·15b 리더 프로필이 그 화면 템플릿을 핀해 되돌렸다. 초안이 격리한 화면 1개(부품 prop 에 v-model — Vue 3 컴파일 거부)는 초안 단계의 일반 변환이 필요하다
- 테스트: css-canon 67(떠 있는 라벨·대화상자 카드·요소 규칙·rounded·조밀 루트 여백), vuetify2-css 15(입력 문맥), vuetify-z-scale 7(복원 블록), chart-tooltip-wrapper 15·chart-v2-layout 5(캔버스 크기·Chart.js 2 비율 실제 chart.js), commons-reviewed-updates 7 — 전부 revert-red 확인. 게이트 113 블록(킷 표지·리허설 트리 parity 규칙·content.css 입력 문맥·z-scale 복원 블록·22 chart 캔버스 크기·멱등·22 common 111 → 새 판·테스트)
