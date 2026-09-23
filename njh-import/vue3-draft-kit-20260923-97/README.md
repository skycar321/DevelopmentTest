# Vue 3 draft kit — 2026-09-23 (97th)

This directory holds one encrypted archive (`vue3-draft-kit-20260923-97.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260923-97.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- 사내 95차 실행에서 `22 css-canon` 이 `Refused: parity stylesheet is not generator-owned` 로 멈춘 것 수리: 94 의 `22 css` 가 css-canon 생성기 소유 시트(src/styles/asis-parity.css) 머리에 계층 접두를 넣어 css-canon 이 거부했다(리허설은 css-canon 뒤에 css 를 다시 돌린 적이 없었다). 22 css 는 그 시트를 건너뛰고, css-canon 은 머리말 앞에 킷 접두(주석·@layer 선언)만 있으면 소유로 보고 되돌려 쓴다(손 편집은 여전히 거부). 게이트가 css-canon 뒤 `22 css` → 접두 삽입 → `22 css-canon` 을 실제로 돌려 재현·증명
- README 97: 09 `$parent` 잔재 2파일은 22 parent 의 의도된 보류(as-is 에서도 값이 undefined 였던 사슬 · 미사용 컴포넌트) — 담당자 결정 항목, 8b 의 통계 화면 한 건 실패는 as-is created() 도 같은 예외(그리드 params 없이 크기 맞춤 호출) — as-is 재측정으로 "양쪽 실패=원래", 막힌 조회 POST 의 허용 절차, PC 메모리 90% 와 SERVER_PROCESS_EXITED
