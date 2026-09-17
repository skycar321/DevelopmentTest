# Vue 3 draft kit — 2026-09-17 (70th)

This directory holds one encrypted archive (`vue3-draft-kit-20260917-70.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260917-70.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- **새 08w 단계 — WAS 소스로 조회 전용 POST 목록 만들기**(`run/08w-was-read-endpoints.sh <WAS 리포>`, 서버를 호출하지 않는다): 컨트롤러 → 서비스(인터페이스는 구현) → 매퍼 XML(또는 어노테이션)을 따라가 select 에만 닿는 POST 경로를 `read-post-allowlist.txt` 로 만든다. insert/update/delete·프로시저·select 안의 DML·파일 쓰기·외부 시스템 호출(HTTP·웹서비스·메일)은 쓰기, 못 따라간 호출·select 에 닿지 않음·쓰기 동사로 시작하는 이름·로그인 경로는 판정 불가로 막는다. 이름이 조회여도 외부 시스템으로 조회 이력을 남기는 경로는 쓰기로 잡는다(로컬 참조 소스에서 이런 경로가 실제로 있었다).
- **8b 조회 POST**: Git Bash 가 환경변수 속 '/경로' 조각을 Windows 경로로 바꿔 조회 허용이 먹지 않던 문제 — 8b·8c 는 정규식을 파일로 넘기고, 스윕 도구는 바뀐 값을 받으면 브라우저를 띄우기 전에 원인과 고치는 법을 찍고 멈춘다. 도구가 실제로 받은 값을 한 줄로 찍는다. 08w 목록이 있으면 자동으로 쓴다(정확히 일치).
- **8b 판정 정리**: 막힌 요청의 네트워크 오류뿐인 실패는 "조회 막힘(판정 불가)", 부모 창이 데이터를 넘겨야 열리는 팝업은 "부모 데이터 필요 팝업", 막힘 없이 권한 없음 화면으로 간 것은 "이동(권한 없음)" 으로 따로 세고 담당자 배정에서 뺀다. 결과를 50줄로 자르던 것을 없애고 끝에 판정별 숫자와 진짜 실패 전부를 찍는다.
- **8b --asis**: 같은 포트·로그인 세션·허용 목록으로 as-is(Vue 2) 개발 서버를 재고, 보고서에 "to-be 에서만 안 뜸(전환 결함 후보)" 과 "as-is 도 안 뜸(원래 그런 화면)" 을 나눈다. Vue 2 마운트도 인식한다.
- 00b 에 조회 전용 POST 목록·as-is 기준 스윕 행.
- 게이트: WAS 판정 단위 테스트(합성 소스), 스윕 실브라우저 테스트 6건(MSYS 변환 거부·경로 목록·판정 정리·as-is 비교), 보고서 비교 테스트, 08w·00b·8b/8c 배선.
- 69차까지의 변경 포함.
