# njh-cli 반입킷 v1.5.225 (암호 동일) — 7B 사용성 통합판 (223+224+225)

**노트북 킷만 있습니다** (cli/gateway 는 ../v1.5.218 그대로 — 런타임 동일).
v1.5.222 이후 노트북 개선 3개 버전을 하나로 통합했습니다:

## ① GPU 판정을 실부하 기준으로 (v1.5.223)

튜닝이 "빈 적재 GPU ok" 를 믿었다가 njh 첫 실호출(대형 prefill)에 CPU 로 낙하하던
갭 봉인 — 이제 각 컨텍스트 후보를 **적재 + ~12k 토큰 실부하 prefill 완주 + 그 후
GPU 유지** 3중 통과해야 채택합니다. verify [6-2b]: njh 왕복 직후 GPU 유지 재확인.

## ② 7B 사용성 프리셋 + 슬림 NJH.md (v1.5.224)

- 설정 시드에 검증된 opt-in 묶음(현장 A/B −20.1% 근거): fuzzyEditMatch(편집실패
  1위 해소), toolArgumentValidation(실행 전 스키마 검증), readPathNearMiss,
  **numKeep=auto**(프리픽스 KV 상주 — OC 실측: 캐시 히트 시 prefill 644초→1.9초,
  **2턴째부터 사실상 즉답**), replanOnStuck(반복 멈춤 시 종료 대신 재계획).
- NJH.md 7B 슬림판: 자기도전 루프 등 작은 모델에서 반복(STUCK)을 유발하는 절 제거.
  사용자 수정본은 절대 덮지 않습니다.

## ③ 행동 규칙 다이어트 (v1.5.225)

기본 NJH.md 에서 수정이력 자동 기록(수정마다 파일쓰기 유발) 제거, 자기도전을
대형 변경 1사이클로 축소 — 매 턴 낭비 툴콜·출력 토큰 절감.

## 절차 (킷 교체 후 한 번)

```bash
bash setup-local-llm.sh     # 튜닝(실부하 기준)·프리셋·슬림 규칙까지 전부 자동
```

이후 재부팅엔 `bash start-local-llm.sh` 하나. 메모리 빠듯하면
`bash optimize-laptop-memory.sh` (진단) / `--apply` / `--apply-kv`.
