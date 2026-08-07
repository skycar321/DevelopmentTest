# njh-cli 반입킷 v1.5.226 (암호 동일) — 7B 프리셋 v2 (배터리 검증판)

**이 폴더 = 노트북 킷 + 개인 PC cli 킷** (gateway 는 ../v1.5.218 그대로 — 런타임은 v1.5.213 이후 동일).

| 대상 | 사용할 파일 |
|---|---|
| 노트북 (로컬 7B) | njh-win-laptop-v1.5.226.7z |
| 개인 PC (Codi) | njh-cli-v1.5.226.7z — 런타임은 v1.5.218 과 동일, **기본 NJH.md 다이어트판**(자기도전 축소·수정이력 기록 제거 = 매 작업 토큰·툴콜 절감)이 차이. 기존 설치 PC 는 재설치 대신 default-NJH.md 한 파일만 ~/.njh-cli/NJH.md 로 교체해도 됨(커스텀했다면 유지). |
| 게이트웨이(=DB 게이트웨이 포함 단일 킷) | ../v1.5.218/njh-gateway-v1.5.218.7z |

v1.5.225 노트북 킷을 이미 반입했다면 이 판으로 교체하세요 — 설정 시드만 다릅니다.

## v1.5.225 대비: 프리셋을 실측 A/B 로 확정

OC(4코어 CPU) 에서 같은 6개 실작업·15분 제한으로 3회 통제 실험:

| 구성 | 완주 | 중앙 소요 |
|---|---|---|
| 플래그 없음 | 3/6 | 656초 |
| 풀 프리셋(4종) | 1/6 — 재계획·재질의가 시간 초과 유발 | — |
| **라이트(v2, 이 킷)** | **4/6** | **396초 (−39.7%)** |

v2 = fuzzyEditMatch(편집 성공률) + readPathNearMiss(경로 힌트) + numKeep=auto
(프리픽스 캐시 상주 — 실측: 두 번째 턴부터 prefill 644초→1.9초).
과했던 2종(toolArgumentValidation·replanOnStuck)은 실측 근거로 뺐습니다.

## 절차 (킷 교체 후 한 번)

```bash
bash setup-local-llm.sh
```

기존 설치에 시드만 반영하려면: local-ollama-settings.json 의 tools/connection 값을
~/.njh-cli/settings.json 에 맞춰 주면 됩니다(설정 커스텀했다면 setup 이 안 덮습니다).
