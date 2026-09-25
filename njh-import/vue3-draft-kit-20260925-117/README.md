# Vue 3 draft kit — 2026-09-25 (117th)

This directory holds one encrypted archive (`vue3-draft-kit-20260925-117.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260925-117.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- 117차는 초안 생성기가 Windows 에서 20분 뒤 멈춘 것의 수리다 — 변환 규칙·화면 결과는 116차와 같다
- 원인: 초안 트랜잭션은 단계 결과를 파일마다 임시 파일 → 이름 바꾸기 → 임시 폴더 지우기로 옮긴다(한 회차 수만 번). Windows 는 방금 쓴 파일·폴더를 백신·색인기·다른 프로세스가 잠깐 열고 있으면 이 호출이 EPERM·EBUSY 로 실패한다. 사내 116차 12 --kit 은 단계 폴더 33개 중 마지막만 없고 failure.json 이 없었다 = 변환이 아니라 게시 쪽 fs 호출. 생성기는 이 오류를 DRAFT_REGISTERED_STAGE_FAILED 한 단어로 바꾸며 문구·단계·경로를 버렸다
- 다시 시도: Windows 에서 초안 파일 교체(이름 바꾸기·지우기·쓰기·임시 폴더)가 일시 잠금 코드면 짧게 쉬고 다시(최대 약 13초). 임시 폴더 정리는 끝내 안 되면 남기고 계속(.tmp 는 입력 아님). 잠금 해제 실패가 원래 오류를 덮지 않게. 재시도 횟수는 결과·stdout 에 남는다
- 같은 폴더에서 이어하기: 그래도 일시 잠금으로 멈추면 12·05 가 같은 폴더에서 생성기를 다시 부른다 — 끝난 단계는 영수증으로 건너뛰고 멈춘 단계부터(최대 2번). 결정론 오류는 다시 부르지 않는다. 실측(실제 33단계 생성기, 31번 단계에 EPERM 주입): 멈춘 모양이 사내와 같고, 이어하기가 앞 단계를 다시 하지 않고 끝나며, 한 번에 만든 초안과 src 바이트가 같다
- 원인 기록: 단계 밖에서 던진 오류에 단계·위치(준비/게시)를 붙이고, 드라이버가 failureDetail(단계·코드·호출·경로·스택 6줄)과 재시도 집계를 stdout 에 싣는다. 12 는 거부 줄 아래 "- 원인:" 으로 찍는다. 05 의 "죽은 원인" 출력은 생성기가 값을 내보내지 않아 지금까지 한 번도 나온 적이 없었다(빌드 게이트 예외 원인도 이제 실린다)
- 진행 표시가 생성기의 임시 쓰기 폴더(.tmp)를 훑지 않는다
- 테스트: 재시도 4 · 트랜잭션 2(게시 rename EPERM 두 번 회복·단계 밖 오류 단계 표시와 같은 폴더 이어하기) · 거부 원인 3(경로 줄임·stdout 요약·CLI 밖 거부) · 12 끝-대-끝 2(일시 잠금 이어하기 1회로 회차 완료·최대 2번 뒤 원인 출력·결정론 오류는 다시 부르지 않음) — 새 테스트 revert-red 확인, 관련 테스트 18파일 116차와 실패 목록 동일. 게이트 117 블록
