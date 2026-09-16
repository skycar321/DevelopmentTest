# Vue 3 draft kit — 2026-09-16 (56th)

This directory holds one encrypted archive (`vue3-draft-kit-20260916-56.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260916-56.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.1** from this channel (`v1.6.1/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- **19d 오판 수정**: 이전 19d 는 "대기 파일 없음" 을 모델 답변에 `NO_PENDING` 글자가 있는지로 판정해, 모델이 그 단어를 인용하자 대기 파일이 수십 건인데 "없다, 08a 로" 라고 안내하고 회수도 건너뛰었다(사내). 이제 대상 파일은 킷 도우미가 정하고 프롬프트를 stdin 으로 넘긴다. 도우미는 첫 줄에 "대상 파일 · 대기 N건 · 진행 중인 병렬 실행 없음" 을 못 박아, 상태 폴더의 옛 병렬 로그를 보고 "실행 중" 이라 손을 떼는 오판을 막는다.
- **`/goal` 대용**: TUI 명령 `/vue3-goal [N]` — 대기 파일을 한 건씩 고치고 회수하며 N건(기본 5)까지 묻지 않고 계속. 셸 `bash "$KIT/run/19d-devai-run.sh" --loop N` — 스크립트가 반복을 쥐고(한 건 = DevAI 한 번 + 회수 한 번) 끝에 요약 표. REPAIRED 가 못 된 파일은 그 반복에서 건너뛴다(도우미 `goal-start`·`next-unit --goal`·`harvest --goal`, 테스트 8/8).
- **njh-cli 용 스킬을 DevAI 에서 그대로**: 킷 `bundled-skills/vue3-migration`(Vue 2→3 변환 지식, 참조 문서 700여 개)을 복사 없이 `.opencode/opencode.json` 의 `skills.paths` 로 연결하고, 킷·상태 폴더만 `external_directory` 허용. DevAI 운영 스킬은 이름을 `vue3-devai` 로 바꿔 겹치지 않게 했고, 옛 킷이 설치한 같은 이름 폴더는 설치기가 치운다. 랩 엔진에서 두 스킬 모두 잡히는 것 확인.
- **설치 직후 실제로 보이는지 확인**: 00d 가 DevAI 가 있으면 `debug skill`·`agent list` 로 스킬 두 개·서브에이전트를 확인해 OK/★ 로 찍는다. TUI 에서 스킬은 `/` 목록이 아니라 `/skills` 안에 있다(랩 캡처 확인). 반드시 Vue 3 작업 트리 폴더에서 `devai` 를 연다.
- 55차까지의 변경 포함.
