# Vue 3 draft kit — 2026-09-17 (64th)

This directory holds one encrypted archive (`vue3-draft-kit-20260917-64.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260917-64.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.1** from this channel (`v1.6.1/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- **새 20단계 — 컴포넌트 v-model 계약 전환**: Vue 3 에서 부모의 `v-model="x"` 는 `modelValue`/`update:modelValue` 로 컴파일되는데, 게시된 초안의 팝업·입력 컴포넌트 다수가 Vue 2 방식(`props.value`·`$emit('input')`)이라 **부모 v-model 로 여는 팝업이 열리지 않는다**. 빌드·서버 기동·8b 렌더 스윕(화면 마운트만 본다)·기존 잔재 검사 어느 것도 이것을 잡지 못했다. 실험 트리에서 57개 컴포넌트·부모 v-model 89곳을 찾았고, Vue 3.5.39 로 같은 부모 v-model 을 렌더해 Vue 2 방식은 닫힌 채·전환 뒤에는 열림을 확인했다(테스트에 포함).
- `bash run/20-vmodel-contract.sh --list` 로 대상을 보고, `bash run/20-vmodel-contract.sh` 로 전환 → 빌드 확인 → 커밋 한 개. 모델을 부르지 않는 결정론 변환이다. 자식만 바꾸고(부모 v-model 은 Vue 3 에서 맞는 모양), 템플릿은 전환 전후 컴파일로 인스턴스 참조가 1:1 로 대응할 때만, 스크립트는 파싱될 때만 바꾼다. 부모가 `:value`/`@input` 로 직접 묶는 컴포넌트 등 확신할 수 없는 파일은 "사람 확인" 으로 남긴다. 실험 트리: 전환 56 · 사람 확인 1, 빌드 통과, 재실행 시 전환 0.
- **잔재 검사(09)에 `vmodel-contract`(높음) 규칙** — ① 잔재 0 판정에 포함된다. 진행 표(00b)가 남은 수를 짚고 20단계를 안내한다.
- **8단계 실측의 `.npmrc` 표기**: 주소 한 줄 커밋은 04단계 설계라 "0이어야" 가 아니었다. 이제 그 안의 인증 줄 수를 센다.
- 검토 보고서 대조 메모(README): ag-grid 31 의 `api.setRowData` 는 제거가 아니라 사용 중단 경고만 난다(31.3.4 코드 확인). `<template>` 위의 import 줄은 원본에도 있던, 컴파일러가 무시하는 글자다.
- 63차까지의 변경 포함.
