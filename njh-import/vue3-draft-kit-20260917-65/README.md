# Vue 3 draft kit — 2026-09-17 (65th)

This directory holds one encrypted archive (`vue3-draft-kit-20260917-65.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260917-65.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- **최종 to-be 는 정확한 버전만**: 라이브러리 결정 맵은 37개 모두 목표 버전(targetVersion)으로 정해 두었는데, 매니페스트에는 캐럿(`^`) 10개가 남아 있었다. 65차 매니페스트는 목표 버전으로 고정했다(범위 지정자 0). 진행 표(00b)가 작업 트리에 남은 범위 지정자를 짚고, 버전 표기만 달라도 `11-commons-update.sh --deps-only` 를 안내한다. 기존 선언의 하한보다 높은 목표 버전(3개)은 설치 단계가 저장소 수신을 확인한다.
- **8단계 실측 표 정정**: 머리글 시각이 UTC 로 찍히던 것을 이 PC 의 현지 시각 + UTC 오프셋으로. 공통 부품 표를 "이름 참조 / 부품 실사용 / 이름·보조 함수만 / 실사용 중 옛 패턴 공존 / 미채택 후보" 로 나눴다 — 이전의 "채택" 은 부품이나 보조 함수 이름이 한 번만 나와도 셌기 때문에 실제 교체 수가 아니었다. 8단계 검토 지시도 "이름·보조 함수만" 화면을 교체 대상으로 본다.
- **새 21단계 — 라이브러리 사용처 스캔**(`run/21-library-usage.sh`, 읽기 전용): import·require·import()·@import 와 import 없이 쓰는 전역·템플릿 흔적(추정)을 파일:줄로 모으고, 결정 맵과 대조해 "제거·대체로 정했는데 아직 쓰이는 것 / 선언 없이 쓰는 것 / 선언만 남은 것 / 범위 지정자" 를 짚는다. `--asis` 로 as-is 스냅샷, `--review` 로 DevAI(또는 njh-cli)가 사용처를 열어 "필수 교체 / 선택 / 범위 밖" 으로 판정(코드 수정 없음). 도구 테스트 5건.
- 게이트: 매니페스트 범위 지정자 0, 11 --deps-only 뒤 00b "없음", 캐럿 주입 시 00b 경고, 실측 시각 형식·부품 실사용 표, 21 to-be·as-is·DevAI 판정과 작업 트리 무변경을 확인한다.
- 문서: `docs/` 에 마이그레이션 전체 계획·현황 HTML(오프라인 단일 파일)을 넣었다.
- 64차까지의 변경 포함.
