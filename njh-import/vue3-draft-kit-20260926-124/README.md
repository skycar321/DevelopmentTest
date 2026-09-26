# Vue 3 draft kit — 2026-09-26 (124th)

This directory holds one encrypted archive (`vue3-draft-kit-20260926-124.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260926-124.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- 124차는 조회 폼 슬롯 이름을 읽을 수 있게 하는 것이다 — 화면 변화 없음, 적용은 선택. 123차 할 일을 안 했으면 123차 절부터
- 초안이 만든 행-열 슬롯 이름(#cell-1-4) 대신 칸 안 첫 입력의 v-model 끝 이름(#cell-orgId), 그 앞 글자 칸은 #cell-orgId-label. 칸 id·입력 id/name 이 있으면 그것이 먼저, 못 정하는 칸은 행-열 그대로. 서술자 const·주석 표 이름도 같이
- 22 search-slots(선택 실행, 기본 목록 밖): 이미 옮긴 트리의 두 모양(한 줄 서술자·const 서술자)을 이름만 바꾼다. 킷이 지은 이름이 아니거나 증명 못 한 파일은 통째로 두고 사람 확인. 옛 조회 폼 부품 트리는 :shell 서술자로만
- 선택하지 않은 트리의 12 --kit 초안은 이전 이름으로 만든다(병합이 이름 바꾸기를 들여오지 않는다) — 복제본 72화면이 이전 킷 결과와 바이트까지 같다. 적용하면 12 --kit 도 새 이름, git revert 로 되돌리면 다시 이전 이름
- 15b 고정 화면 판정은 조회 폼 바로 아래 슬롯 이름을 순번으로 비교한다 — 이름만 바뀐 화면은 같은 화면(규칙 없이 이름만 바꾸면 계획 21 → 10 으로 떨어지던 것을 막는다)
- 사내 순서: 00b → 22 --list search-slots(보기) → (선택) 22 search-slots → 00b → 24
- 테스트: 124 테스트 32(이름 계약·이름 바꾸기·계약 판정·15b 표지·22 옵트인), 되돌리면 빨강 확인
