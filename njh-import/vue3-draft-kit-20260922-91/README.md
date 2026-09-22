# Vue 3 draft kit — 2026-09-23 (91th)

This directory holds one encrypted archive (`vue3-draft-kit-20260922-91.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260922-91.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- Catch-up (`12 --kit`) treats kit-owned shared parts as whole units: a part merges only when the three-way merge is clean; when
  the operator's edit conflicts, the whole part is kept as it is and listed for replay instead of producing a hunk-mixed file
  (an upgrade rehearsal on an older tree showed that a mixed upload panel is rejected by the shared Excel planners). The contract
  check that runs before the commit still stops the round with a file:line list when a merged screen uses a member the kept part
  does not have, and the tree and baseline are restored.
- Catch-up refuses to start when the previous draft folder is damaged (screens recorded in the baseline hashes are missing) and
  prints the recovery; the check reads the hash file in its recorded format, so paths with spaces or Korean and CRLF line endings
  are not misreported. A state directory that records a base draft but has no hash file is reported as damaged.
- Step 15 refuses to run the staged spreadsheet conversion until step 22 has run on the tree; the reader profiles are measured on
  post-22 sources, so the earlier order silently held every screen for review.
- Residual spreadsheet screens: the reader profiles now accept the previous form and the search-form variant introduced by the
  shared-layer port of an earlier round; the variants are generated from source by a measurement tool, not edited by hand. On the
  lab tree (same screens, after steps 22 and 14b) the staged conversion wrote all 21 remaining screens (22 files) and the build
  passed. In house, screens whose structure differs are still held with a reason code; behaviour parity remains the in-house
  step-17 proof.
- Step-22 replay on Windows checkouts no longer reports the layout, pager and disabled-date restorations as "modified" (a CRLF
  false positive that produced operator items on the previous round).
- Generated and installed shared parts carry Korean header comments (purpose, where used, origin, change caution) with pinned
  provenance; a lint keeps them in place.
- Developer tools bundle -5 shipped separately (registry trailing-slash equivalence on reuse, retry after a failed installation,
  existing clone remotes with embedded credentials accepted and never printed, password on the command line accepted, exact-message
  remedies in both guides).
- Runbook: the continue section for this round lists the recovery for a pending or in-flight catch-up round, keeps the legacy
  spreadsheet package removal conditional on the staged conversion having written every remaining screen, and stages only the
  intended paths in the operator's build commit.
- 15b(화면 엑셀 전환)가 출하 사슬에서 처음으로 실제로 쓴다: 리더 표지가 템플릿 AST 를 통째로 핀해 줄끝·주석·공백·빈 슬롯만 달라도 14개 전부 검토 보류였던 결함을 정규화 표지로 해소, 프로파일을 두 트리 모양에서 같은 값으로 재측정(승급 리허설에 15/15b 추가, written≥1 요구).
- 반입 순서에 조회 POST 목록(08w) 조건부 선행 줄 추가(08b·17 전).
- 15b 화면별 승인: 검토된 구조와 맞지 않는 화면만 사유 코드와 함께 건너뛰고 나머지는 쓴다(요약 줄 `skipped=N`, 잔여는 상태 파일·00b 요약). 사내처럼 부분 병합된 화면 몇 개가 전체 21화면 전환을 막던 전부-아니면-전무를 해소.
- 업로드 다이얼로그 공급자 판정이 빈 outside-card 슬롯 유무(초안 세대 차이)에 흔들리지 않게 정정.
