# Vue 3 draft kit — 2026-09-24 (115th)

This directory holds one encrypted archive (`vue3-draft-kit-20260924-115.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260924-115.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- 115차는 초안 단계·공통 부품 수리라 사내 트리에는 `12 --kit`(같은 as-is 로 새 킷 초안을 다시 만들어 달라진 화면·킷 부품만 3방향 병합)으로 들어온다. 12 는 병합 뒤 20·22 전체를 다시 돌리므로 114차 분류도 함께 적용된다
- 초안 격리 화면 해소: 부품이 자기 prop 에 v-model 을 건 화면(Vue 2 는 prop 을 제자리에서 바꿨고 Vue 3 컴파일러는 거부)을 초안이 통째로 격리하던 것 — 격리 직전 단계 generic-prop-model-mirror 가 그 prop 을 지역 사본(data 에서 prop 값으로 시작, prop 이 바뀌면 watch 로 덮음)으로 옮기고 템플릿 참조(v-for·슬롯 지역 이름은 가림, 객체 줄임 표기는 키 보존)와 this.<prop> 를 사본으로. this 별칭·같은 이름 감시자·이름 충돌·화살표 data 는 그대로 격리. 랩 초안 격리 1 → 0, 그 대화상자가 as-is 와 같게 열림(0.01%)
- 12 --kit: 병합으로 격리가 풀린 화면의 as-is 보존 파일(<화면>.vue.quarantined)을 같은 커밋에서 지운다 — 요약·커밋 메시지에 "격리 해제 N"
- 엑셀 업로드 대화상자 한 줄(40px) 큼: as-is 는 파일 입력이 머리 상자 안 칸에 있어 입력 → drm → 건수가 한 줄이었는데 업로드 패널이 입력·읽기 버튼을 요약 앞에 그렸다. 패널이 실행 때 요약 안 빈 일반 요소가 정확히 하나면 그 칸에 되돌리고, 채택기는 그 칸이 원래 자리임을 증명할 때만 그 모양을 쓴다(아니면 머리 상자를 원문 그대로). 화면 템플릿은 바뀌지 않아 14b·15b 프로필 그대로(114차 초안과 그 화면 바이트 동일). 랩 3.62% → 0.09%
- 12 --kit: 충돌한 킷 부품도 그 파일 이력이 전부 킷 커밋(초안·22 분류·catch-up)이면 새 판을 받는다 — 옛 킷 트리 재현(승급 리허설)에서 업로드 패널이 22 aggrid 의 import 경로 변경 때문에 "운영자 수정" 으로 판정돼 통째 유지됐다(패널 수정이 옛 트리에 안 닿는 원인). 운영자 커밋이 하나라도 있으면 예전처럼 유지. 22 common 과 같은 판정을 한 곳에서
- 22 aggrid: 옛 킷 판 호환 부품이 든 트리에서 첫 실행 사람 확인 3건(Custom grid provider requires review) — 같은 실행 끝에 새 판으로 바꿀 호환 부품을 검사는 옛 판으로 읽었다(두 번째 실행에서야 풀림). 이번 실행이 쓸 새 판으로 검사
- 초안 테스트의 낡은 단언(단계 추가 뒤 갱신 안 된 순서 단언) 때문에 그 뒤의 격리 단언이 그동안 한 번도 돌지 않았다 — 현재 순서로 고침
- 테스트: prop v-model 5 · 초안 격리 1 · 12 --kit 격리 해제 1 · 업로드 패널 7(표 패널 경유 포함) — 전부 revert-red 확인. 게이트 115 블록(신선 사슬 초안 격리 0·사본 변환 화면·#summary 모양 유지·신선 사슬 22 사람 확인 기준선·테스트), 승급 리허설에 사람 확인 기준선
