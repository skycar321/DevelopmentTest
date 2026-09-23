# Vue 3 draft kit — 2026-09-24 (105th)

This directory holds one encrypted archive (`vue3-draft-kit-20260924-105.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260924-105.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- 104차는 배송 전 리허설에서 멈춤(새 22 vcol-legacy 가 서식 엑셀 15b 의 리더 템플릿 핀을 바꿔 21 → 8화면) — 105차가 수리와 104차 내용을 함께 싣는다. 랩 68개 화면 as-is 와 다른 픽셀 평균 103차 0.43% → 0.31%, 차트 대시보드 3.8% → 0.3%, 나빠진 화면 0
- 리더 템플릿 표지에서 <v-col> 정적 격자 크기(cols·sm·md·lg·xl) 제외(레이아웃 전용, 92차 셸 기본값과 같은 원칙) + 저장된 핀 14개를 같은 규칙으로 이식 — vcol-legacy 전·후 트리 모두 14/14. 게이트에 15b written 하한(103차 기준선 21)
- 22 chart 눈금 자리: 104차 padding (V2 padding + lineSpace)/2 는 글자를 2.88px 밀었다 → padding 은 V2 값 그대로, 남는 두께·가로축 좌우 3 은 축 afterFit(두 설치본 상수, 104차 표지는 재실행으로 올림)
- 22 chart 축 선: V2 는 gridLines 첫 색·굵기로(격자·drawBorder 를 끄면 없이) — as-is 짝의 V2 축 옵션에서 읽어 border 가 없는 축에만
- 폴라·레이더: V2 는 눈금 배경 절반을 위쪽에서만 비웠다(V4 네 방향 — 반지름 4px 작고 중심 4px 위) → 래퍼 4세대 + 호환 부품 chart-radial-geometry(afterSetDimensions)
- main 에 Chart.js 2 캔버스 크기(부모 clientWidth 반올림 — V4 는 소수 폭 내림 407 → 406px)·autoPadding 없음(선 점 크기 가장자리) — 호환 부품 chart-v2-layout, 앱 시작에서 한 번
- 104차 내용: 22 vcol-legacy(V2 v-col xsN·lgN 효과 없던 크기 되돌림), css-canon V2 리셋 빈 곳·도우미 !important 우선순위, 22 theme 겹침 부품 앱 루트(V2 [data-app])
- 테스트: 실제 chart.js 로 방사형 반지름·중심·혼합 차트 chartArea 측정, afterFit 보정량, 축 선 짝·멱등, 리더 표지 격자 크기(전부 revert-red 확인). 게이트 105 블록
