# Vue 3 draft kit — 2026-09-16 (43th)

This directory holds one encrypted archive (`vue3-draft-kit-20260916-43.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260916-43.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.1** from this channel (`v1.6.1/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- **19 단계 재실행이 이미 보수된 파일을 건너뛴다**: 42차 사내에서 거부 47건에 상한 20으로 돌리면 다음 실행이 같은 20개를 또 돌렸다. 이제 이전 실행 원장(`<state>/parallel/*/ledger.jsonl`)에서 `REPAIRED` 된 파일은 건너뛰고 "이전 실행에서 보수됨 N개 건너뜀" 으로 표시한다. 다시 돌리려면 `REFUSED_REPAIR_REDO=1`. TIMEOUT·REJECTED 는 자동으로 다시 대상이 된다.
- **거부 지침 2건 보강** (`prompts/refusal-guides.ko.json`, 16개): 유틸 `.js` 가 `import Vue from 'vue'` 를 쓰는 `native-module-foreign-vue-use` 전용 지침(디렉티브 훅 개명 bind→beforeMount 등, 등록은 main.js 대신 보고), 엑셀 채택 거부 지침에 "내려받기 뒤 이어지는 문장(다운로드 이력 기록)은 await 뒤 같은 순서로 유지"와 `getColumnDefs` 대체 방식 추가.
- **05 단계 판정 표기 정정**: "★ 인프라 단계 거부(이것이 main.js 를 Vue 2 로 남긴다)" 가 `native-` 로 시작하는 사유를 전부 인프라로 찍어 파일별 거부(그리드 이벤트·유틸)를 main.js 문제로 오해시켰다(42차 사내). 이제 핵심(main.js·router·store) 거부만 ★ 로, 나머지는 "파일별 거부 N건(빌드에 영향 없음, 19단계 대상)" 으로 집계하고, 초안 `src/main.js` 에 `createApp` 이 있는지 **파일을 직접 검사**해 한 줄로 찍는다.
- **06 단계 끝 안내 문구**를 현재 순서(07 → 19 --list → 19 → 08a → 08b → 15 → 16 → 18 → 09 → 17)로 교체(옛 "A/B 대조 7단계·10-gap-report" 제거).
- 42차 사내 실측(참고): 05 `DRAFT_DIAGNOSTIC_BUILT` 166화면(converted 123·partial 41·skipped 1·quarantined 1), 06 BUILD_OK·SERVE_OK·부품 착지 23/23, 07 연결 OK, 19 대상 47건.
- **README·00b 안내 갱신**: 상단 "지금 바로 시작하기" 의 19 줄에 `REFUSED_REPAIR_MAX=<거부 수> REFUSED_REPAIR_JOBS=2`, 18 줄에 `PHASE1_JOBS=2`, 전체 순서 줄에 19 추가, 07 의 버전 안내(1.6.1 이상, 채널 최신 1.6.2). `00b-resume.sh` 의 19 행도 같은 명령을 찍는다.
