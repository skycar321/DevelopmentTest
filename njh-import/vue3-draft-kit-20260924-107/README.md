# Vue 3 draft kit — 2026-09-24 (107th)

This directory holds one encrypted archive (`vue3-draft-kit-20260924-107.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260924-107.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- 106차 화면 픽셀 비교를 이어서 대화상자 그림자·입력칸 라벨 자리·Vue 2 와 3 템플릿 공백 규칙을 공통으로 옮김: 랩 68개 화면 as-is 와 다른 픽셀 평균 106차 0.16% → 0.10%, 중앙값 0.14% → 0.08%, 25개 화면 개선·나빠진 화면 0(로딩 표시가 도는 화면 1개는 찍은 순간 차이)
- 대화상자 그림자·모서리: V2 는 바깥 .v-dialog, V4 는 안쪽 .v-overlay__content — V2 CSS 가 .v-dialog(전체 화면 포함)에 준 값을 .v-dialog > .v-overlay__content 로(css-canon 구조 대응표 한 항목, 필터 전 전체 선언을 넘김)
- 입력칸 라벨 자리: V2 라벨·바깥선 틈 = 슬롯 여백 12px(enclosed) ↔ V4 --v-field-padding-start 16 + 떠 있는 라벨 4px — 모양별로 V2 슬롯 여백을 읽어 옮기고, 바깥선 떠 있는 라벨 4px 는 뺌(축약형 margin 시작값을 읽어 판정)
- 새 22 분류 ws-comment(vcol-legacy 바로 뒤): Vue 2 condense 는 요소와 주석 사이 같은 줄 공백을 공백 하나로 남기고 Vue 3 는 지움 → as-is 에서 V2 가 공백을 남긴 자리를 찾아 to-be 의 같은 주석(내용·순번 짝) 옆에 {{ ' ' }}. 초안이 주석을 다음 줄로 옮겨도 서식 유지, pre·textarea·여는 태그 바로 뒤·닫는 태그 앞 제외, 짝 없으면 영수증 사람 확인, 주석 두 개 사이 공백은 한 번만, 재실행 무변경(버튼 줄 화면 0.39% → 0.01%)
- 리더 템플릿 표지 ⑥: {{ ' ' }} 보간을 공백처럼 뺌 — ws-comment 뒤에도 15b 리더 핀이 그대로(105차 written ≥ 21 하한이 리허설에서 확인)
- 테스트: 주석 옆 공백 V2 자리·as-is 짝·멱등·CRLF·끝-대-끝, 리더 표지 ⑥, 대화상자 그림자·전체 화면 모서리, 라벨 자리 문맥별(전부 revert-red 확인). 게이트 107 블록(리허설 트리에서 ws-comment 커밋·{{ ' ' }} 실재)
