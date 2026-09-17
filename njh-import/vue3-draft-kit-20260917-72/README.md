# Vue 3 draft kit — 2026-09-17 (72th)

This directory holds one encrypted archive (`vue3-draft-kit-20260917-72.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260917-72.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- **`$parent` 사슬 계산(22단계 parent)**: `this.$parent.$parent.$parent.값` 같은 사슬은 Vue 2 가 **인스턴스를 만드는 부품**만 세며 올라간 결과라, Vuetify 3·4 에서는 다른 컴포넌트에 닿는다. Vuetify 2 부품이 슬롯 자식과 주인 사이에 끼우는 인스턴스 수를 실브라우저(Vue 2.7 + Vuetify 2.7)로 재 표로 두고(functional 인 v-container·v-row·v-col 등은 0, v-app·v-card 는 1, v-dialog 는 2), as-is 스냅샷 템플릿에서 그 컴포넌트를 쓰는 모든 자리의 도달 대상을 계산한다. 모든 자리에서 그곳이 값을 가진 가장 가까운 앱 컴포넌트일 때만 `vue2Ancestor(this, '값', n)` 로 바꾸고, 아니면 자리별 계산 결과(어느 자리에서 원래 값이 들어왔고 어느 자리에서 원래도 비어 있었는지)를 사람 확인 사유로 남긴다. 테스트가 표와 실측이 같은지 확인한다.
- **`$parent` 호환 부품 새 판**: 초안이 v-dialog 자리에 넣은 킷 공통 부품(다이얼로그 셸 등)은 Vue 2 에 없던 인스턴스라 건너뛴다(실브라우저 테스트: 공통 부품 안 팝업에서 화면 메서드 호출). 22 재실행이 호환 부품을 갱신한다. 템플릿에 쓰는 곳이 없는 팝업은 import·라우트·이름 문자열·동적 컴포넌트 사용 근거를 사유에 적는다.
- **8b 닫힌 다이얼로그**: 템플릿 루트가 다이얼로그인 화면을 단독으로 열면 닫힌 채다. Vuetify 3·4 는 다이얼로그 내용을 #app 밖(body 오버레이 컨테이너)으로 옮겨 그려 "빈 화면" 으로 보였다. 오버레이 내용도 화면 내용으로 세고, 열린 다이얼로그·예외가 없으면 "닫힌 다이얼로그" 로 따로 센다(배정·비교에서 뺀다 — 부모 화면에서 열어 확인). 실제 Vue 2·Vuetify 2 와 Vue 3·Vuetify 4 로 실브라우저 확인.
- **같은 원래 결함(개발 모드 차이)**: to-be 의 첫 예외와 같은 모양의 예외를 as-is 도 Vue 2 식으로 콘솔에 삼켰으면 "to-be 에서만 안 뜸" 이 아니라 따로 센다(부모 데이터 없이 연 팝업·data 에 없는 값 읽기). 브라우저 판마다 다른 오류 문구를 같은 모양으로 본다.
- **잔재 검사(09)**: 로그인처럼 보이지만 라우터 가드 허용 목록 밖인 라우트에서만 닿는 화면(템플릿에서 온 로그인 폼)의 `$store` 호출은 "참고 — 사용하지 않는 템플릿 화면(1차 제외 후보)" 으로 낮추고 근거를 적는다. `$parent` 사슬 처방은 22 의 자동 계산을 안내한다. 진행 표(00b) 22 행에 `$parent` 사슬.
- 게이트: Vue 2 인스턴스 표 실측 테스트, 호환 부품 실브라우저 5건, 스윕 실브라우저 9건(닫힌 다이얼로그 포함), 보고서 단위(같은 원래 결함), 실험 트리 22 parent 사슬 사유·쓰지 않는 팝업 근거, 09 로그인 템플릿 낮춤, 71차 모양에서 22 재실행(호환 부품만 커밋)·00b.
- 71차까지의 변경 포함.
