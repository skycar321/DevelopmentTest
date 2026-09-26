# Vue 3 draft kit — 2026-09-26 (123th)

This directory holds one encrypted archive (`vue3-draft-kit-20260926-123.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260926-123.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- 123차는 사내 122차 사진(엑셀 축 자동 0·15b 중단) 판독에서 나온 ④ 옛 엑셀 라이브러리(xlsx) 걷어내기 수리와, 업로드 .xlsx 전용 결정(사용자 2026-09-26)의 적용이다. 초안 규칙은 122차와 같다(12 --kit 다시 안 돌림)
- 15b 가 고정 내보내기 화면 하나의 import 모양·스크립트 호출 거부로 트랜잭션 전체를 멈추던 것 — 그 화면만 건너뛰고 나머지는 쓴다(공유 계획 거부는 그대로 전체 중단). 새 가이드 8편
- 검출기가 이름 있는 슬롯·v-for 안 버튼의 처리기 이름을 그 스코프가 묶는지 보지 않고 전부 가려 "호출자 없음" 으로 보던 것 — 실제로 묶는 이름만 가린다(못 뽑는 모양은 예전처럼 전부 가림)
- 묶음 내보내기 자동 변환이 꺼져 있던 것 — 그리드 공통 부품 3개가 주석만 다른 옛 판이라 바이트 핀이 안 맞았다. 22 common 이 실행 코드가 킷 판과 같을 때만 킷 판 바이트로 바꾼다(코드가 다르면 두고 참고). 15 미리보기가 꺼진 이유를 한 줄로 찍는다. 복제본: 핀 8/8, 자동 19 → 21, 쓰기 뒤 빌드 통과
- 업로드는 .xlsx 만: 공통 부품·14b·15b 리더 7종·업로드 패턴 정책이 .xls 를 한국어 한 문장으로 거절한다. 122차 판(영어 안내)은 안내만 올린다. as-is 와 다른 의도된 동작 차이
- 18 가이드 NO_OBSERVED_CALLER §4-B: 두 판 모두 닿는 곳이 없는 메서드는 지우지 않고 호출 이름만 1:1
- 사내 순서: 00b → 22 aggrid common → 14b → 15 → 15 --write → 08a → 17 → 09 → 23 → 00b → 24
- 테스트: 123 테스트 10(15b 화면 단위 거부·.xlsx 전용 안내 7·22 common 그리드 부품 핀) + 검출기 스코프 사례, revert-red 확인
