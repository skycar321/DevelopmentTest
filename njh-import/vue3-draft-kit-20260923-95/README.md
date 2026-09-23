# Vue 3 draft kit — 2026-09-23 (95th)

This directory holds one encrypted archive (`vue3-draft-kit-20260923-95.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260923-95.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- 다른 PC 에서 to-be 로그인 403 수리(사내 2026-09-23: 같은 소스가 한 PC 는 되고 다른 PC 는 안 됨, as-is 는 둘 다 됨): as-is axios 0.19 는 withCredentials 면 다른 포트 WAS(localhost:8080 → 8085)에도 XSRF-TOKEN 쿠키를 X-XSRF-TOKEN 헤더로 붙였는데 to-be axios 1.20 은 1.6.2 부터 같은 오리진이 아니면 withXSRFToken: true 를 명시해야 붙인다 — CSRF 를 강제하는 WAS(응답 쿠키에 XSRF-TOKEN 이 있는 PC)만 403. 22 에 axios 분류 추가: withCredentials = true 옆에 withXSRFToken = true(표지 KIT95_AXIOS_XSRF, 멱등, axios < 1.6.2 면 건너뜀, 손으로 이미 넣은 트리는 중복 안 함). 게이트에 CSRF 강제 가짜 WAS 를 다른 포트에 띄운 실브라우저 증명(as-is 0.19 → 200 / to-be 1.20 → 403 / 수리 뒤 200)
