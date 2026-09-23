# Vue 3 draft kit — 2026-09-24 (98th)

This directory holds one encrypted archive (`vue3-draft-kit-20260923-98.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260923-98.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- 사내 97차 실행에서 `24` 가 `The following paths are ignored by one of your .gitignore files: .env.development.local` 뒤 `24-push-branch.sh:36` 에서 종료(코드 1)해 아무것도 올라가지 않은 것 수리: 그 트리에서는 공유 env 파일이 무시 규칙에 걸려 있어 `git add` 가 거부됐고, 옛 24 는 그 실패를 다루지 않아 set -e 로 끝났다(랩 트리 .gitignore 에는 그 규칙이 없어 리허설이 못 봤다). 값 모양 검사를 통과한 그 파일 하나만 `git add -f` 로 들이고 걸린 규칙(파일:행:패턴)을 찍는다. add·commit 이 실패해도 그 파일만 빼고 push 는 계속한다
- 테스트: 실제 bare 원격으로 세 무시 출처(저장소 .gitignore 의 `.env.*.local`, info/exclude 의 `*.local`, 전역 excludesFile 의 `.env*`)에서 커밋·push, 같은 규칙에 걸린 다른 파일은 들이지 않음, 커밋 훅 거부에도 나머지 커밋 push. 옛 24 로는 새 테스트 4건이 실패함을 확인. 게이트 98 블록이 이 테스트와 README 98 절을 확인
- README 98: 97차 css-canon 은 정상(커밋 1, 빌드 통과), 이미 손으로 add -f 뒤 올렸다면 더 할 것 없음, node_modules 연결 경고는 무해
