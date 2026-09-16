# Vue 3 draft kit — 2026-09-16 (48th)

This directory holds one encrypted archive (`vue3-draft-kit-20260916-48.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260916-48.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.1** from this channel (`v1.6.1/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- **njh 안에서 말로 시키는 병렬 보수** (njh **1.6.4** 이상): `cd "$V3" && njh` 를 열고 "5단계에서 거부된 화면들 병렬로 고쳐줘" 라고 말하면 모델이 `~/.vue3-draft/kit-path`(이제 `source kit.sh` 가 남긴다)를 읽어 `run/19-refused-screens.sh --njh-parallel` → 새 도구 `parallel_run`(서브에이전트 동시 실행, 진행은 화면에) → `--harvest`(대상 외 폐기·기능 삭제 거부·빌드 검증·파일별 커밋·원장) 를 순서대로 돌리고 집계를 보고한다. 유닛 JSON 에 `harvest`/`next`/`kit`/`v3` 메타가 실려 도구가 다음 명령을 그대로 돌려준다. 말하는 예시·중간 질문·금지 사항: `prompts/대화형/5-거부-화면-병렬.txt`. 킷의 `bundled-skills/vue3-migration/SKILL.md` 에도 같은 순서.
- 사내 실측 반영: DevAI 는 동시 2~3 이 실효 상한(동시 10 → 11건 중 10건이 40분 제한). `REFUSED_REPAIR_JOBS=3` 권장. README 상단·대화형 절 갱신.
- 47차까지의 변경(`/parallel --file` 경로·회수기·하트비트·트리 종료·건너뛰기·지침 17개) 포함.
