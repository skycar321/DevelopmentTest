# Vue 3 draft kit — 2026-09-26 (125th)

This directory holds one encrypted archive (`vue3-draft-kit-20260926-125.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260926-125.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- 125차는 ④ 옛 엑셀 라이브러리(xlsx) 걷어내기의 마지막 단계를 순서에 넣은 것이다. 123차 할 일을 아직 안 했으면 125차 순서가 그것을 포함한다
- 사내 모양 복제본에서 끝까지 잰 결과: 사내 순서(22 aggrid common → 14b → 15 → 15 --write, 15b 포함) 뒤 화면·공통 코드의 xlsx 사용 0, package.json 선언 3개만 남고 21b 가 제거 가능으로 판정한다(시작 48파일 → 14b 뒤 46 → 15·15b 뒤 0)
- 공개 폴더의 SheetJS 사본: as-is 의 정적 페이지가 <script> 로 싣는 SheetJS 0.15.0 사본(패키지보다 옛 판, 같은 취약점 계열)이 있었다. 그 페이지의 살아 있는 코드는 쓰지 않는다. 예전 ④ 판정은 src·package.json 만 봐 선언을 지우면 완료로 잘못 읽었다 — 이제 ④ 와 21b 관문이 세고, 21b --apply 가 쓰이지 않음이 증명될 때만 사본과 그 script 줄을 걷어낸다(설치 실패 때 되돌림)
- 21b 가 판정·적용 결과를 사진 한 장 분량의 요약으로 찍는다(JSON 은 파일)
- 21b 의 오프라인 설치가 Windows 에서 npm 을 shell 없이 불러 실패하던 것(사용 0 이 된 성공 경로에서만) — Windows 에서 shell 을 켠다, 킷이 싣는 npm 류 호출 전부를 검사하는 테스트
- 15 미리보기·23 사진이 검토로 남은 화면마다 대표 사유·줄·세부 문구를 한 줄로 찍는다(템플릿 참조는 사유별 건수) — 사진 한 장으로 남은 원인을 가른다. 1차 완료 판정 ③ 은 범위 미완료 이유에 행 미증명 수를 보인다
- 사내 순서: 00b → 22 aggrid common → 14b → 15 → 15 --write → (검토 화면이 남으면 18) → 21 → 21b --excel-gate → 21b --apply → 빌드·커밋 → 21b --excel-gate → 08a → 17 → 09(+ 담당자 보류) → 23 → 00b → 24
- 테스트: 125 게이트 41(사본 찾기·증명 7가지 반례·걷어내기·21b 관문/적용/되돌리기·④③ 판정·요약·Windows npm 호출·사진용 검토 줄), 조각마다 되돌리면 빨강
