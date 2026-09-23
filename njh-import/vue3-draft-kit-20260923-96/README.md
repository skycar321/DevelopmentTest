# Vue 3 draft kit — 2026-09-23 (96th)

This directory holds one encrypted archive (`vue3-draft-kit-20260923-96.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260923-96.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- `.env.development.local` 을 팀 공용 파일로(사용자 결정 2026-09-23): 다른 PC 에 로그인 WAS 주소 파일이 없어 요청이 vite 서버 자신으로 가 404 였다. 24 가 미추적 `.env.development.local` 의 값 모양을 검사(키는 NODE_ENV·VUE_APP_*·VITE_*·BRMS_APP_* 만, 비밀 모양 키 이름·사용자정보 주소·긴 무작위 값 거부, 값은 출력하지 않음)한 뒤 `chore(env)` 로 커밋해 같이 올린다. push-config-guard 는 그대로(값을 보지 않음) — 검사를 통과한 blob identity 만 승인 목록으로 넘겨 그 버전만 통과. 손으로 먼저 커밋한 파일도 모양이 맞으면 통과, 비밀 모양 버전이 이력에 있으면 차단. 다른 PC 는 자기 파일을 지우고 pull
