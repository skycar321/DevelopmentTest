# Vue 3 draft kit — 2026-09-17 (67th)

This directory holds one encrypted archive (`vue3-draft-kit-20260917-67.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260917-67.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- **새 22단계 — Vue 3 API 결정론 전환**(`run/22-vue3-api-compat.sh`, 모델 호출 없음): 분류마다 전환 → 빌드 확인 → 커밋 하나(빌드가 깨지면 그 분류만 되돌림).
  - `vuetify`: 초안이 그리드·달력이 든 화면을 Vuetify 호환 변환에서 뺐고, 스킬 코드모드는 월 달력 태그 하나로 파일 전체 변환을 버렸다. 파일마다 적용하되 월 달력 태그만 가렸다가 되돌리고, 스킬이 다루지 않는 이름(v-subheader·v-simple-checkbox 등)과 메뉴 activator(`{ on }` → `{ props }`)를 더 바꾼다.
  - `aggrid`: 그리드 라이브러리 새 버전은 `gridOptions.api`·`columnApi` 를 붙이지 않아 화면 mounted 에서 TypeError·라우트 이동 중단이 났다. 그리드 생성 시점에 둘을 다시 붙이는 호환 부품(`src/compat/ag-grid-vue3-legacy.js`)으로 import 를 바꾸고, 없어진 메서드 이름·숫자 열 키를 대응한다(실브라우저 테스트로 전후 확인). colDef 의 무의미한 `tooltip: true` 제거.
  - `chart`: 차트 라이브러리 새 버전은 옛 옵션 모양(축 배열·최상위 범례/툴팁/제목·도넛 두께)을 조용히 무시한다. 축 객체·plugins 아래로 옮기고 도넛 두께 이름을 바꾼다(실브라우저 테스트로 옛 모양 무시·새 모양 적용 확인).
  - `router-view`(transition 안 → v-slot), `env`(`process.env.VUE_APP_*` → `import.meta.env`), `emits`(리터럴 `$emit` 을 선언 — 선언 안 된 리스너가 루트 요소에 새는 문제).
- **잔재 검사(09) 정정**: 오탐 3종 제거(TypeScript 제네릭·문자열의 `>>>`, 모듈 이벤트 버스, `NODE_ENV`). 새 규칙 — 그리드 옛 API, activator `{ on }`, 차트 라이브러리 옛 축 설정, `$parent.메서드()`(레이아웃 컴포넌트가 인스턴스가 되어 부모가 바뀔 수 있음). 처방은 규칙마다 한 번만, 끝에 규칙별 요약.
- **lint(16) 정정**: TypeScript 공통 부품을 JS 파서로 읽어 나던 파싱 오류 → TS 파서 연결. 동작이 같은 규칙(불필요한 이스케이프) 끔, 원래 결함인 상수 비교는 경고로.
- 8단계 개발 서버 포트 지정(`VUE3_SERVE_PORT`), 진행 표(00b)에 22단계 행, README 에 로컬 환경 파일 명령.
- 게이트: 22 단위 테스트·실브라우저 테스트(그리드 호환 부품·공통 부품 경유·차트 옵션), 실험 트리에서 22 전 분류 전환·빌드·커밋, 전환 뒤 대상 잔재 0, 두 번째 실행 무변경, 진행 표 행, lint 파싱 오류 0, 포트 배선. 문자열·주석 가림이 정규식 리터럴·템플릿 리터럴 식을 바르게 다루도록 고쳤다(20단계도 같은 함수).
- 66차까지의 변경 포함.
