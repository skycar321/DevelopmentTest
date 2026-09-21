# Vue 3 draft kit — 2026-09-20 (80th)

This directory holds one encrypted archive (`vue3-draft-kit-20260920-80.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260920-80.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- Restore measured native-button borders through structural ownership checks, preserving unrelated styles and refusing tampered blocks.
- Strengthen Excel upload error-display, generated dependencies, continuation, dataflow and review-screen coverage checks.
- Report unsupported source blocks and inferred usage as unverified instead of clean.
- Require compatible viewport metadata for route-sweep comparisons; retain warning and dialog observations.
- Register executable tests with the test runner and cover CRLF category output, idempotence and tamper refusal.
- Continue from kit 79 using the kit 80 instructions inside the encrypted archive. Regenerate both sweep reports; old reports are not proof of equivalence.
- Remaining Excel and behavior work is not claimed complete. Use the generated five-page report for operator feedback.
- Keep photo-report reopening instructions within the display-width contract even when the state directory has a long Unicode path.







# ① 출발 태그가 정상인가 (오진 여부 판별)
git -C "$V3" rev-parse -q --verify "$BASE_TAG^{commit}" >/dev/null && echo "태그 있음" || echo "태그 없음"
git -C "$V3" merge-base --is-ancestor "$BASE_TAG" HEAD && echo "조상 맞음" || echo "조상 아님 ← 오진 원인"

# ② 이력에서 .env* 를 건드린 커밋과 파일 이름만
git -C "$V3" log --oneline --name-only --diff-filter=ACMDR "$BASE_TAG..HEAD" -- '.env*' | head -20

- "조상 아님/태그 없음" → 실제 env 변경이 아니라 기준선 문제입니다. 아래 (A)
- 커밋·파일이 나온다 → 진짜 env 이력 변경입니다. 아래 (B)

(A) 기준선 문제 — env 는 건드리지 않은 경우

git -C "$V3" fetch --tags origin        # 태그를 못 받아온 경우
echo "$BASE_TAG"                        # 지금 기준선이 무엇인지 확인
git -C "$V3" tag --list | tail -10      # 실제 출발 태그 확인
