# Vue 3 draft kit — 2026-09-26 (121th)

This directory holds one encrypted archive (`vue3-draft-kit-20260926-121.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260926-121.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- 121차는 사내 120차 결과 사진 9장 판독과 최근 3주 요청·지적 전수 대조(101건)에서 나온 수리다. 초안 규칙은 120차와 같다(12 --kit 다시 안 돌림)
- 1차 완료 판정이 네 조건이 됐다: ① 잔재 0(담당자 결정으로 보류한 항목은 따로) ② 화면 렌더 생존 ③ 동작 동등성 ④ 옛 엑셀 라이브러리(xlsx 계열) import·선언 0. 예전 진행 표는 ①②③ 만 보아 xlsx 가 남아도 완료로 읽힐 수 있었다. 00b 끝과 23 사진 끝에 같은 네 줄
- 00b 엑셀 축 행: 미리보기 결과가 그 뒤 바뀐 트리를 말하지 않으면 날짜와 함께 다시 돌리라고 짚는다(사내 사진의 "자동 0" 은 오래전 결과였다)
- 17 두 화면 "to-be 에서만 예외 — Network Error": 두 쪽 모두 같은 조회 요청이 응답 없이 끊겼는데, as-is 운영 모드는 처리기에 잡힌 요청 오류를 예외로 남기지 않아 to-be 에서만 예외로 보였다(랩에서 조회 연결을 끊어 같은 모양 재현). 요청 실패가 전부 같은 모양이고 글자가 전송 실패 문구뿐일 때만 뺀다. 조작은 성공에서 제외되고 화면은 "두 쪽 같은 조회 실패(WAS·조회 조건 확인)" 로 남는다
- 17 응답 없이 끊긴 조회는 화면을 다시 열어 한 번 더 누른다(두 쪽 같은 규칙, HTTP 오류·막은 요청·이동 중 취소 제외)
- 17 탭 행 수 차이(to-be 21 · as-is 0): 누른 뒤 1.5초만 기다려 느린 조회가 끝나기 전에 셌다 — 대기 중 조회가 끝날 때까지 최대 15초
- 17 한쪽 조작에만 잡힌 격자 폭 경고: 두 판 모두 화면 열기 때 내는 늦은 경고가 첫 조작 창으로 넘어갔다 — 누르기 전에 콘솔도 0.6초 조용할 때까지 최대 3초 더
- 17 120차의 누르기 전 대기 창에서 난 예외·경고가 어디에도 기록되지 않던 결함: 화면 기록의 늦은 관측으로 남기고 to-be 에서만 난 늦은 예외는 차이로 센다
- 08b·17 닫힌 대화상자 화면이 정적으로 숨긴 공통 로딩바 때문에 "전환이 깨뜨린 것" 으로 보이던 것: 정적으로 숨긴 형제는 허용
- 적대 검토(opus) 10건 반영: 대기 창의 요청 실패 기록, to-be 에서만 실패한 화면 열기·대기 중 조회는 차이, 요청 실패 정체에 오류 코드·CORS 거부, 늦은 오류 개수 비교·잘림은 증명 못 함, 두 쪽 콘솔 대기 길이 차이를 흡수(늦은 창+첫 조작 창), 한쪽만 다시 누른 조작 따로 표시, 숨긴 형제는 로딩바 모양만
- 판정 ① 은 담당자가 09 --hold 로 남긴 보류만 뺀다(파일·줄·규칙·그 줄 코드가 같을 때만 — 코드가 바뀌면 풀림). ② 는 as-is 는 뜨는데 to-be 는 안 뜬 모든 화면이 0 이어야, ③ 은 가장 최근 17 결과가 VERIFIED 이고 트리보다 새로워야, ④ 는 사용처 스캐너·모든 의존성 필드·설치 목록
- 15 자동 전환의 엑셀 도우미가 행 데이터의 새 키를 호출자 머리글 배열에 더하지 않던 결함(SheetJS 는 넘긴 배열을 늘린다): 그 배열 길이로 범위를 자르는 화면의 내려받기 열이 달라졌다 — SheetJS 와 같게 고치고 대조 테스트, 설치본은 옛 판으로 등록해 15 --write 가 새 판으로 올린다
- 사내 순서: 00b → 12 --transplant-receipts → 15 → 15 --write → 08a → 17(두 쪽 다시) → 09 → 23 → 00b → 24
- 테스트: 121 테스트 14(17 비교 5·실브라우저 3·판정 5·닫힌 대화상자 1), revert-red 11 확인
