# Vue 3 draft kit — 2026-09-25 (119th)

This directory holds one encrypted archive (`vue3-draft-kit-20260925-119.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260925-119.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- 119차는 사내 117차 결과 사진 22장에서 찾은, 1차 완료 판정(① 잔재 0 · ② 화면 렌더 생존 · ③ 동작 동등성)을 막던 킷 결함 수리다. 화면 결과는 117·118차와 같다(아래 겹침 부품 자리·그리드 null 옵션 두 가지만 화면이 바뀐다)
- ③ 17 as-is "SERVER_PROCESS_EXITED"(113·117차)의 실체: 08b 가 Git Bash 의 $!(MSYS 프로세스 번호)를 node 생존 확인(process.kill)에 그대로 넘겨, 살아 있는 as-is 개발 서버를 첫 확인에서 죽었다고 판정했다(사진에 10초 진행 줄이 한 줄도 없던 것과 일치 — to-be 는 이 경로를 안 써서 멀쩡). /proc/<번호>/winpid 로 Windows 번호를 넘기고, EPERM 은 살아 있음·한 번의 "없다" 로 단정하지 않음. 기동이 실패하면 서버 로그의 오류 줄을 화면에 바로 찍는다. 랩에서 17 을 as-is 기동부터 비교까지 끝까지 돌려 확인(게이트는 이 전체 경로를 돌린 적이 없었다)
- ① 킷이 만든 공통 CSS 가 킷 잔재 검사에 걸리던 것: css-canon 이 V2 다이얼로그 바깥 상자(.v-dialog__content)를 V4 오버레이 루트 .v-dialog 로 옮긴 줄을 09 가 [높음] 잔재로 잡았다 — 어느 단계도 풀 수 없는 잔재. V4 에만 있는 조합 .v-overlay.v-dialog 로 쓰고 검사도 그 조합을 V4 루트로 본다. 랩: 09 잔재 8 → 7(남은 것은 22 가 사유를 단 사람 확인과 랩 전용 픽스처)
- 22 aggrid 사람 확인 1: 지울 옛 그리드 옵션이 이미 없는데도 파일 안의 this 전달로 멈췄다 — 지울 것이 없으면 바로 무변경, 킷 호환 함수 vue2Parent(this)·vue2Ancestor(this, n) 는 안전한 사용
- 설치본 통째 복사 낭비: Git Bash 가 정션 명령의 스위치를 드라이브 경로로 바꿔 정션이 한 번도 안 만들어졌고, 폴백 ln -s 는 Git Bash 에서 폴더 통째 복사였다 — 12·22 가 돌 때마다 to-be node_modules 전체를 킷 폴더에 복사했다 지웠다. 스위치를 이스케이프하고 네이티브 링크만 시도(복사 안 함). as-is 워크트리 연결도 같다
- 초안 단계별 소요 시간 기록 — 12·05 가 상위 5 를 한 줄로(사내 초안 1060초 vs 랩 158초의 느린 단계를 다음 사진으로 특정)
- 새로 고침·주소로 바로 연 화면에서 대화상자·머리글 툴팁이 화면 안에 남던 것(22 theme): 104차 attach ".v-application" 은 Vuetify 4 가 처음 그릴 때 한 번만 찾는다 — 앱이 처음 그려지는 동안은 루트가 문서에 붙기 전이라 경고 뒤 제자리(랩 78화면 중 68, 메뉴로 옮겨 간 화면만 정상). 늘 있는 자리(compat/vuetify2-overlay-host.js)를 attach 로 주고 앱 루트가 생기면 그 안 맨 끝으로 옮긴다. 실브라우저: 옛 판 경고+제자리 / 새 판 경고 0·루트 맨 끝·대화상자 창 가운데
- 그리드 호환 부품: 화면 초기화가 예외로 멈춰 gridOptions 가 null 인 채 마운트되면 as-is(ag-grid-vue 25)는 그리드를 만들지 않았다(빈 상자) — 공통 그리드 부품이 null 을 복제해 빈 격자·Loading 을 그렸다(랩 17 "행 내용 다름" 3건의 실체). 25 처럼 만들지 않는다(22 aggrid 가 호환 부품을 새 판으로)
- 17 동작 동등성 판정: as-is 개발 서버는 .env.development 의 NODE_ENV=production 으로 Vue 2 운영 빌드라 Vue 경고를 내지 않는다 → to-be Vue 개발 경고는 비교에서 빼고 보고서에 따로. 화면을 여는 동안의 콘솔 오류·경고를 전부 기록해 to-be 가 더 낸 것을 본다. 두 쪽이 같은 모양으로 막힌 화면(같은 권한 이동·같은 팝업·같은 닫힌 대화상자·둘 다 안 뜸·같은 버튼 진단)은 "비교 불가(양쪽 같은 모양)" 로 옮긴다(면제, 검증 아님 — to-be 가 더 낸 것이 있으면 면제 안 함). 00b 는 차이 0 이어도 범위가 덜 찼으면 ★ 범위 미완료. 적대 검토(opus) 반영
- 로그인 화면 큰 제목 줄 간격·부제 자간이 as-is 와 달랐던 것(22 common): as-is 앱 껍데기는 로그인 화면 이름을 라우터와 대소문자가 다른 글자로 비교해 로그인 화면도 껍데기 안에서 그렸는데, 초안이 대소문자를 고쳐 껍데기 밖으로 나가 .v-application 규칙(문단 여백·제목 자간)이 빠졌다. null 메뉴 복원이 있는 트리에서만 as-is 글자로 되돌린다 — 랩 로그인 화면 as-is 와 0px
- 17 측정 보정: 닫힌 서랍 안 버튼(V4 는 화면 밖+inert, V2 는 visibility:hidden)을 숨김으로 · AG Grid 25/31 경고 이름표(ag-grid:/AG Grid:) 차이는 같은 진단 · 두 쪽 모두 같은 곳으로 이동한 화면도 면제(화면 열기 차이 없을 때) · VERIFIED 인데 범위가 덜 찬 결과도 검증 파일을 남기고 "범위 미완료" 로 안내(전에는 "비교 조건이 맞지 않다" 로 잘못 찍고 00b 가 "없음")
- 로그인 세션 만료: 앱 로그인 쿠키는 1시간 — 랩 17 에서 세션을 만든 지 1시간이 지나 to-be 75/78 이 토큰 만료로 튕겼다. 17 은 만료된 세션이면 재기 전에 멈추고 20분 안이면 알린다. 스윕은 앱이 늘린 쿠키 수명(값 그대로)을 세션 파일에 이어 써서 to-be 뒤 as-is 스윕이 만료로 시작하지 않는다
- 사내 순서: 22 에 theme·common 추가, 17 바로 앞에 08a(로그인 세션 1시간), 17 은 두 쪽 다시 잰다(--reuse-tobe 없이 — to-be 가 바뀌었고 기록 도구도 바뀌었다)
- 랩 17 끝-대-끝(as-is 기동부터 비교까지): 짝지은 조작 52·같음 52·차이 0(처음 표시 다름 3 → 0), 비교 못 한 화면 52 → 15, 비교 불가(양쪽 같은 모양) 31. 남은 15 = 불러올 때 알림 대화상자가 열린 화면 7·두 쪽이 같은 예외로 실패한 조회 4·첫 이동 실패 뒤 주소 처리(Vue Router 3/4) 2·AG Grid 31 열 정의 검증 경고 1·as-is 만 실패 1 — 다음 차수 대상
- 테스트: 생존 확인 2·링크 흉내 1·로그 원인 줄 1·단계 시간 2·다이얼로그 루트 2·그리드 옵션 2 추가, 오래 죽어 있던 08b 재사용 통합 테스트 대역 보충 — revert-red 확인. 게이트 119 블록(신선 사슬 시트 V4 조합·09 시트 잔재 없음·단계 시간 기록·플러그인 늘 있는 자리·17 판정 테스트 9·겹침 부품 실브라우저 3)
