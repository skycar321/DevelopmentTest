# Vue 3 draft kit — 2026-09-17 (63th)

This directory holds one encrypted archive (`vue3-draft-kit-20260917-63.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260917-63.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.1** from this channel (`v1.6.1/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- **진행 표(`00b-resume.sh`) 결함 수정**: 엑셀 축 잔재 건수가 늘 "0?건" 으로 찍혔다(검사 결과에 없는 키를 셌고 grep 종료코드가 끼어들었다). 이제 잔재 총수를 읽는다. 거부 화면은 전체 건수 대신 19단계와 같은 기준(원장·보수 커밋)으로 남은 건수를 보인다. DevAI 가 있으면 DevAI 버전·실행기·에이전트 행을 보이고, njh-cli 가 없어도 7단계를 요구하지 않는다.
- **의존성만 맞추는 `11-commons-update.sh --deps-only`**: 진행 표가 "킷이 의존성을 바꿨다" 고 했는데 차이가 16단계 lint·타입 검사 도구(devDependencies)뿐인 경우, 새 초안·부품 브랜치까지 만드는 11단계 전체 대신 package.json 만 킷 매니페스트에 맞추고 `npm install --registry`·빌드 확인·커밋한다(부품·화면은 건드리지 않음). 진행 표가 이 경우를 구분해 안내한다.
- **8단계 검토를 DevAI 로**: 실측(빌드·기동·집계)은 모델 없이, 검토 보고서만 DevAI 비대화형(주 에이전트 지정, 편집 권한 자동 승인 없음)으로 만든다. njh 로 하려면 `VUE3_AI_WORKER=njh`.
- 게이트: 진행 표 행(잔재 숫자·남은 거부·DevAI), 개발 도구 의존성 하나를 뺀 트리에서 `--deps-only` 안내→설치·빌드·커밋, 8단계 실측·DevAI 검토 보고서.
- 62차까지의 변경 포함.
