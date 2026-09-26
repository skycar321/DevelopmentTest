# Vue 3 draft kit — 2026-09-27 (128th)

This directory holds one encrypted archive (`vue3-draft-kit-20260927-128.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260927-128.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- 128차는 새로 돌릴 단계가 없다 — 18단계(DevAI 로 검토 화면 처리)를 쓰는 사람에게 필요한 판. 127차를 안 돌렸으면 128차 폴더로 127차 순서를 그대로
- 18단계 가이드 보강: 랩에서 강한 모델이 사내 약모델 역할로 18단계를 가이드대로만 따라 해 채점(엑셀 검토 소견 31건 — 충분 7·부족 13·모호 11). 업로드 계열 13편에 킷 엑셀 런타임 절차(다른 가이드와 바이트까지 같은 공용 블록)를 넣고, 타이머 안의 리더 등록·숫자 상태를 돌려주는 업로드·외부 암호화 연쇄·런타임 셀 문서 모양의 먼저 확인과 멈춤 조건을 더했다(멈춤 조건은 줄이지 않았다)
- 사내 사진 상위 검토 코드 4종 가이드를 합성 예제로 검출기 동작과 대조해 고쳤다 — 템플릿 참조 가이드의 틀린 문장(123차 검출기 변경과 어긋남), 호출자 없음 가이드의 틀린 전제, 묶음 머리글·숫자 행은 값 영역 증명 없이는 멈춤
- 22 vuetify 규칙(127·126차)을 시작 태그 토큰 단위로: 속성값·주석 안 글자를 고치던 것, 안/밖 아이콘 겹침, 이벤트 수식어·값 바인딩 수식어·작은따옴표 v-model 을 놓치던 것(적대 검토 9건 재현). 사내·랩 모양 트리 333파일에서 127차와 결과 같음
- 17 행 미증명 이유: 응답을 요청과 (메서드·경로·순번)으로 짝짓는다 · 21b 적용 오류 코드는 세 가지뿐, 결과 파일을 못 써도 한국어 요약·종료 코드 2
- 게이트 밖 킷 테스트 742개 전수 점검: 낡은 전제 26개 수리(제품 변경 없음), 실제 결함 수리 — 참조 문서 호출 경로(킷에 없는 스크립트는 BLOCKED 로 멈춤), 초안 게이트 오프라인 오류, 카탈로그 문서 재생성, 명세 옵션(주지 않으면 결과 동일). 엑셀 버튼 채택·날짜/알림 스타일 규칙 정밀화는 승급 리허설이 사내 모양 화면의 날짜 선택기 채택 후퇴를 잡아 127 판 유지(다음 차수)
- 사내 순서(127차 끝낸 트리): 00b → 18(계획만, 모델 호출 없음) → 00b. DevAI 로 18 을 돌리면 새 가이드로 처리된다
- 테스트: 128·129·130 게이트(결함 수리 회귀 9 · 가이드 계약·합성 예제 77 · 낡은 테스트 12 파일 69 · 결함 회귀 51+1 · 가로채기 브라우저 3 · 환경 복구 22 파일 103), 조각마다 되돌리면 빨강. 127 게이트 21b 확인을 새 정규화 형태로
