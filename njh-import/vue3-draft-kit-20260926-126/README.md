# Vue 3 draft kit — 2026-09-26 (126th)

This directory holds one encrypted archive (`vue3-draft-kit-20260926-126.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260926-126.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- 126차는 새 명령이 없다(125차 순서 그대로 + 22 vuetify css-canon 한 줄). 125차를 아직 안 돌렸으면 126차 폴더로 125 순서를 하면 된다
- CSS 픽셀 비교(보고서, 관문 아님): 08b·17 이 화면을 열 때마다 판정 직후 뷰포트 사진을 상태 폴더에 남기고, 두 쪽 사진이 있으면 17 이 비교 앞에서 "CSS 픽셀 비교: 화면 N · 평균 · 1% 넘는 화면" 과 차이 큰 5화면을 찍는다. 기준은 랩 계산과 같다(랩 68화면을 화면별로 같은 숫자로 재현). 판정·영수증은 사진과 무관하고(사진 없이 잰 결과와 같음), 스윕 시간 +0.3%. 사진 파일에는 업무 데이터가 담기므로 밖으로 옮기지 않는다
- 로그인·찾기 화면 모양 수리(랩 비교에서 1% 넘은 두 화면 2.45%·1.99% → 0.38%·0.01%): V4 fill-height 컨테이너의 세로 가운데 정렬, 탭 막대 높이(옮긴 고정값이 height prop 을 누름 → 변수), 탭 배경(V4 가 모르는 background-color → bg-color, V2 팔레트 이름은 설치본 팔레트 hex), 카드 액션 안 버튼의 기본 변형(V4 는 글자 버튼 → variant="elevated")
- 17 성공 제외의 이유를 가른다: 두 쪽이 같은 예외로 멈춘 조작은 "as-is 결함이 그대로 옮겨졌다" 절로 따로 싣고, 17·00b·1차 완료 판정 ③ 줄에 같은 예외 수와 행 미증명 수를 보인다(범위 판정 기준은 그대로). 랩: 성공 제외 9 가 전부 두 쪽 같은 예외였다
- 스윕이 화면 순서에 흔들리지 않게: 08b·17 은 화면을 한 브라우저 문맥에서 차례로 연다 — 앞 화면(SSO 진입 화면)이 로그인 쿠키를 지우면 뒤 화면 전부가 "토큰 만료" 로 튕겼다(랩 to-be 75/78). 화면마다 사라진 세션 쿠키만 되살린다(값은 세션 파일 그대로). 쿠키를 지운 원인은 킷 토큰 계약(문자열 아닌 토큰이면 쿠키 삭제 — as-is 는 "undefined" 쿠키로 가드를 통과했다)이며 의도된 강화다
- 랩 행 모드(목 WAS 가 조회마다 합성 행) 전체 측정: 짝지은 조작 71 모두 같음·차이 0, CSS 픽셀 비교 51화면 평균 0.02%·1% 넘는 화면 0
- Windows: 18 자기 검사·검출기 검증 스크립트의 폴더 링크를 정션으로(권한 없는 PC 의 EPERM, 92차와 같은 부류) + 킷이 싣는 스크립트 전체를 검사하는 테스트
- 사내 순서: 00b → 22 aggrid common → 22 vuetify css-canon → 14b → 15 → 15 --write → (검토 화면이 남으면 18) → 21 → 21b --excel-gate → 21b --apply → 빌드·커밋 → 21b --excel-gate → 08a → 17 → 09(+ 담당자 보류) → 23 → 00b → 24
- 테스트: 126 게이트(같은 예외 분류·③ 문구·Windows 폴더 링크·탭 배경·카드 액션 버튼·css-canon 구조 대응·비활성 버튼·세션 쿠키 되살리기 9 + CSS 픽셀 비교 19), 조각마다 되돌리면 빨강
