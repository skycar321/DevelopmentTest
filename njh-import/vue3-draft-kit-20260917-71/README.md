# Vue 3 draft kit — 2026-09-17 (71th)

This directory holds one encrypted archive (`vue3-draft-kit-20260917-71.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260917-71.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- **탭 값 전환(22단계 vuetify)**: Vuetify 2 탭은 `<v-tab href="#tab1">` 의 href 에서 탭 값을 만들었지만 목표 Vuetify 의 탭은 `value` 로만 값을 받는다. 그대로면 탭 값이 인덱스가 되어 탭 안 내용(그리드)이 보이지 않고 탭을 눌러도 바뀌지 않는다(실브라우저 테스트로 확인). 정적 `href="#값"` → `value="값"`, `:href="'#접두' + 식"` → `:value="'접두' + 식"`. 그 밖의 동적 href 는 사람 확인. 잔재 검사(09)에 새 규칙, 진행 표(00b)에 "탭 href".
- **as-is 비교 무효 판정(8b 보고서)**: VUE_APP_MODE·포트가 다르거나, as-is 가 한 화면도 뜨지 않았거나, 막힌 요청으로 대부분 이동했거나, 가드 조회 경로에 접두어만 붙었거나, 허용 조건이 다르면 비교를 무효로 찍고 담당자 명단에서 아무것도 빼지 않는다(조건이 다른 비교가 진짜 실패를 명단에서 지운 일을 막는다).
- **8b `--asis` 사전 확인**: as-is 폴더에 to-be 와 같은 `.env.development.local` 이 없으면 복사 명령을 보여 주고 멈춘다. VUE_APP_MODE 가 다르면 멈춘다. 포트를 to-be 와 같게 고정하고, 포트가 차 있으면 띄우기 전에 멈추며, 다른 포트로 떴으면 멈춘다. 기동 대기 기본 900초, 실패해도 서버 프로세스 트리를 끈다(Windows). 8a 도 포트가 차 있으면 멈춘다.
- **첫 실패 요청(8b)**: 실패·조회 막힘 줄과 보고서에 첫 실패 요청을 붙인다 — 스윕이 막음 / HTTP 상태 / CORS·연결 실패 / 요청 없음(주소 형식). 서버·CORS 로 실패한 조회가 있으면 "조회 막힘" 이 아니라 실패로 센다.
- **Vue 2 훅 예외를 같은 기준으로**: Vue 2 는 수명 주기 훅 예외를 콘솔에만 남기고 Vue 3 개발 모드는 던진다. as-is 스윕에서 그 콘솔 오류를 예외로 세, as-is 에도 있던 결함이 전환 결함 후보로 보이지 않게 했다.
- **라이브러리 결정 정정**: `html2pdf.js` 유지 → **0.14.0 상향**(0.14.0 미만 XSS, 함께 설치되는 jspdf 의 치명 취약점을 jspdf 4.2.1 로 해소). to-be 매니페스트·결정 맵·결정서를 바꿨다. PDF 저장 결과는 화면에서 확인한다.
- 게이트: 탭 실브라우저 테스트, 실험 트리 22 뒤 탭 href 0·70차 모양에서 22 재실행(탭 파일만 커밋)·00b, 스윕 실브라우저 테스트(첫 실패 요청·Vue 2 훅 예외·같은 포트 as-is 비교), 보고서 단위 테스트(비교 무효), 8b `--asis` 사전 확인(환경 파일·VUE_APP_MODE·포트), 포트 확인 함수, html2pdf.js 결정·매니페스트·결정 준수.
- 70차까지의 변경 포함.
