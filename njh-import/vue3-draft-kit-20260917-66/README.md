# Vue 3 draft kit — 2026-09-17 (66th)

This directory holds one encrypted archive (`vue3-draft-kit-20260917-66.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260917-66.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- **8a·8b 구동기 경로 수정**: 킷 경로에 한글이 있으면 동봉 브라우저 구동기를 찾지 못했다(도구가 자기 위치를 URL 경로 문자열로 계산해 퍼센트 인코딩·드라이브 중복). 파일 경로 변환으로 고쳤고, 8a·8b 는 개발 서버를 띄우기 전에 `--preflight` 로 구동기·브라우저를 먼저 확인한다. Windows 에서 서버를 끌 때 그 포트에서 듣고 있는 프로세스만 트리째 정리한다.
- **로그인 세션 보존**: 브라우저 세션 파일은 쿠키·localStorage 만 담아 sessionStorage 에 로그인 상태를 두는 앱은 저장한 세션으로도 로그인 화면으로 돌아갔다. 8a 가 sessionStorage 와 로그인 중 호출한 API 오리진을 함께 저장하고, 8b·8c 가 화면 스크립트보다 먼저 복원한다.
- **8b 안전장치 조정**: 세션 저장 때 실제로 부른 API 오리진의 GET/HEAD 는 허용한다. POST 는 계속 차단하되, 조회 전용임을 확인한 경로만 `ROUTE_SWEEP_READ_POST` 로 열 수 있다(막힌 POST 상위 경로를 보고서에 표시). 가드가 다른 화면으로 보낸 경우는 "뜸" 이 아니라 "다른 화면으로 이동" 으로 따로 센다. 화면 주소 호스트 기본값은 localhost(`ROUTE_SWEEP_HOST`).
- **API 주소 안내**: 8a·8b 가 개발 서버가 쓸 모드 값과 출처 파일을 보여 준다. 로컬 WAS 로 붙이는 방법(`.env.development.local`, 커밋 금지)을 README 에 적었다.
- **잔재 검사 보강(09)**: 목표 Vuetify 버전이 내보내지 않는 Vuetify 2 전용 컴포넌트 18종과 `<transition>`/`<keep-alive>` 바로 안의 `<router-view>` 를 잡는다.
- **기타**: 20단계 전환은 원본 줄바꿈(CRLF)을 보존한다. 일부 보조 도구가 Windows 경로에서 직접 실행 판정에 실패해 아무 출력 없이 끝나던 것을 고쳤다.
- 게이트: 한글 경로 사전 점검, 실브라우저로 sessionStorage 복원·API 오리진 허용·조회 POST 허용·쓰기 POST 차단·이동 판정, 포트 리스너 파서, 모드 출처, 8단계 배선, 새 잔재 규칙을 확인한다.
- 65차까지의 변경 포함.
