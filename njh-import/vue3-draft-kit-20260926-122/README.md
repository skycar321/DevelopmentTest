# Vue 3 draft kit — 2026-09-26 (122th)

This directory holds one encrypted archive (`vue3-draft-kit-20260926-122.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260926-122.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- 122차는 1차 완료 판정 ④ 옛 엑셀 라이브러리(xlsx) 걷어내기(최우선)의 남은 화면을 막던 킷 안쪽 결함 수리다. 초안 규칙은 121차와 같다(12 --kit 다시 안 돌림)
- 18 소견별 드라이버가 15 와 다른 검출기(스킬 안 옛 사본)로 소견을 뽑아 묶음 내보내기 소견을 한 건도 못 보고 이미 풀린 소견을 계속 냈다 — 이제 15 와 같은 검출기·정책(공용 함수 하나). 첫 줄에 검출기 경로가 찍힌다. 복제본 계획: 소견 423 → 고유 133, 가이드 없음 0
- 18 가이드 38 → 240편(검출기 전 소견 코드·15b 거부 코드). 새 202편은 UNVERIFIED 이고 적대 검토 결함 18건을 재현 뒤 반영했다 — 저장 파일명이 .xlsx 가 아니면·픽셀 열 너비·겹친 병합·반복 콜백 안 저장·날짜/셀 키 객체 행·.xls 올리기는 멈추고 사유를 남긴다. 기본 변환 문서는 "킷 런타임이 있으면 호출 이름만 1:1" 이 본 변환
- 검출기가 킷 초안 vite 설정(함수 모양·객체 별칭·vue 자산 주소 옵션)을 거부해 엑셀 화면 25개에 별칭 소견을 냈다(가이드는 설정을 다시 쓰라고 해 환경 변수 치환을 깰 뻔했다) — 그 모양만 좁게 증명, 분기·다른 문장·async·함수 안 이름 가림은 여전히 거부(검증 12종 + 긍정 1)
- 엑셀 런타임의 plain·1904 표시가 Vue 반응형 프록시를 지나면 사라져 data() 필드에 담은 워크북에 스타일이 새로 생기던 결함 — 객체 자신의 숨은 기호 속성으로. 121 판은 옛 판으로 등록해 15 --write 가 올린다
- 15 앞에 22 aggrid: 열 순서 옵션이 남은 화면은 변환 뒤 규칙 검사에 걸려 15 가 못 바꾼다(이미 적용이면 바꿀 파일 0). 복제본 4화면 더 자동 변환(xlsx 남은 파일 33 → 29)
- 사내 순서: 00b → 22 aggrid → 12 --transplant-receipts → 15 → 15 --write → 18 계획만(MAX=0) → 18 시험 5건 → 08a → 17 → 09 → 23 → 00b → 24
- 테스트: 122 테스트 9(드라이버 검출기 3·가이드 2·공용 검출기 2·반응형 표시 2), 검출기 별칭 검증, revert-red 전부 확인
