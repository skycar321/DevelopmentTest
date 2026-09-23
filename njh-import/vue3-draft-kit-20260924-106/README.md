# Vue 3 draft kit — 2026-09-24 (106th)

This directory holds one encrypted archive (`vue3-draft-kit-20260924-106.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260924-106.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- Vuetify 2 와 4 의 간격·크기 단위가 다른 자리를 css-canon 이 공통으로 옮김: 랩 68개 화면 as-is 와 다른 픽셀 평균 105차 0.31% → 0.16%, 중앙값 0.28% → 0.14%, 60개 화면 개선·나빠진 화면 0
- 도구 막대 시작 쪽: V2 내용 여백 16·첫 아이콘 버튼 −12·그 뒤 제목 +20(제목 여백 없음) ↔ V4 첫 버튼 4·제목 20 — 앞에 보이는 버튼이 없는 머리글 제목이 4px 오른쪽이던 것(거의 모든 화면). 두 설치본에서 값을 읽고 끝 쪽은 같아서 두지 않음
- 입력 글자 좌우 자리: V2 = 슬롯 여백(enclosed 12, 밑줄 0) + 이긴 input 여백(V2 input 규칙 vs 앱의 클래스 없는 input 규칙 — 명시도·순서로 문맥별 재현), V4 = --v-field-padding-start·end 하나 → 값이 다를 때만 안쪽 아이콘 없는 칸에(글자 시작 어긋난 칸 48 → 2)
- 바깥 아이콘 여백: V2 prepend-outer·append-outer 9px ↔ V4 prepend·append 16px — 구조 대응표 한 항목
- 아이콘 크기: V2 px 고정(SIZE_MAP 12·16·24·36·40, .v-icon.v-icon 24px) ↔ V4 부모 글꼴 em — 크기 클래스를 배수 변수를 살린 px 로(버튼·칩은 revert-layer 로 V4 그대로), 직접 쓴 v-icon 마크업은 리셋 계층 24px(크기 다른 아이콘 9 → 0)
- 테스트: 도구 막대 값 읽기·보류, 입력 문맥별 캐스케이드(조밀 밑줄 0·앱 규칙 없음·순서), 바깥 아이콘 RTL 중복 없음, 아이콘 크기 설치본 읽기(전부 revert-red 확인). 게이트 106 블록
