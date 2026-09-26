# Vue 3 draft kit — 2026-09-26 (127th)

This directory holds one encrypted archive (`vue3-draft-kit-20260926-127.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260926-127.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- 127차는 새 명령이 없다. 126차를 끝까지 돌린 트리는 22 vuetify 한 줄과 08a → 17 → 23 → 00b → 24 만. 126차를 아직 안 돌렸으면 127차 폴더로 126차 순서를 그대로
- 로그인 화면 두 가지(랩 CSS 픽셀 비교에서 남은 차이를 끝까지 추적): 탭 창의 모델 초기값이 비어 있으면 Vuetify 4 는 첫 항목을 화면이 붙은 뒤에야 골라, 그 안 화면이 마운트 때 준 입력칸 포커스가 숨은 상태에서 무시됐다 — 항목 값이 없는 탭 창만 초기값이 비었을 때 첫 항목을 고르게. Vuetify 2 의 입력칸 뒤 아이콘은 밑줄 안, Vuetify 4 는 밖(안은 append-inner-icon) — as-is 원본에 같은 속성이 있을 때만 옮긴다(두 번째 실행·새로 쓴 코드는 그대로, 선택 목록 계열은 사람 확인). 랩 0.38% → 0.30%
- 17 행 미증명 화면마다 이유: 조건 대화상자(조회를 눌러도 요청 없이 안내 대화상자 — 조회 조건을 채워야 하는 화면, 조건 기본값을 채우는 권한이 있는 계정이면 일부 풀린다)·응답 0행·응답 행이 있는데 두 쪽 격자 0행·행 수를 못 잼·요청 없음. 17 범위 미완료 안내에 한 줄, 보고서에 절. 범위 판정 기준은 그대로. 랩: 권한 없는 목 계정 13(조건 대화상자 11 · 응답 0행 1 · 행 수 못 잼 1) → 버튼 권한을 다 가진 목 계정 11(조건 대화상자 9) — 권한으로 풀리는 것은 조건 기본값을 채우는 화면뿐
- 민감한 이름의 필드가 있는 응답도 행 개수는 남긴다(값·지문은 버린다) — 행 미증명 이유가 '행 수 못 잼' 으로 떨어지지 않게
- 17 판정 실브라우저 테스트가 게이트 밖이라 121차 대기 창 변경 뒤 조용히 실패하던 것 — 테스트 전제를 새 대기 창에 맞추고 게이트에 넣었다
- 21b --apply 가 멈추면 스택 추적 대신 원인(package.json 과 lock 어긋남 / 오프라인 설치 실패)·먼저 할 일·되돌린 사실·종료 코드 2, JSON 에 같은 분류
- 랩에서 125·126차 사내 순서를 끝까지(목 WAS 합성 행): 엑셀 변환(30화면) 뒤에도 17 짝지은 조작 71 모두 같음·차이 0, CSS 픽셀 비교 최대 0.38% — 엑셀 걷어내기가 조회 동작과 모양을 깨지 않았다. 127차 22 vuetify 뒤: 짝 73 모두 같음·차이 0, CSS 51화면 평균 0.02%·최대 0.30%
- 사내 순서(126차 끝낸 트리): 00b → 22 vuetify → 08a → 17 → 23 → 00b → 24
- 테스트: 127 게이트(행 미증명 이유 분류·17 배선·탭 창 첫 항목·입력칸 아이콘·22 분류 배선·21b 멈춤 요약 7 + 17 판정 실브라우저·민감 응답 7), 조각마다 되돌리면 빨강
