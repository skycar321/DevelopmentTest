# njh-cli 반입킷 v1.5.221 (암호 동일) — GPU 컨텍스트 자동 튜닝판

**노트북 킷만 있습니다** (cli 는 ../v1.5.218, gateway 는 ../v1.5.218 그대로 — 런타임 동일).

## v1.5.220 대비: tune-gpu-ctx.sh 신설 — "A/B 테스트도 셸이 알아서"

njh 의 운영 numCtx(32768) KV 캐시가 내장 GPU 에 안 들어가 CPU 로 떨어지던 문제
(GPU 메모리가 올랐다 뚝 떨어지던 증상)의 **해법 탐색 자체를 자동화**했습니다:

1. 운영 numCtx → 24576 → 16384 사다리로 **실제 적재하며 GPU 유지 실측**
2. 전부 CPU 면 **KV 캐시 양자화(q8_0, 컨텍스트 손해 없음)** 켜고 재도전
3. 최적 조합을 판정 파일에 저장 + **settings.json 의 local.numCtx 자동 동기**(백업 생성)
4. 최적 상태로 재기동·상주까지 완료
5. 16384 도 CPU 면 "이 노트북은 CPU 운용이 정답" 정직 보고

`setup-local-llm.sh` 가 vulkan 채택 시 **자동 실행**합니다. 이후 재부팅엔
`start-local-llm.sh` 가 저장된 조합(양자화 env 포함) 그대로 띄웁니다.

## 노트북 절차

```bash
# 킷 교체 후 (자동 튜닝까지 수 분):
bash setup-local-llm.sh
# 또는 튜닝만 다시:
bash tune-gpu-ctx.sh
# 마지막 줄 "ok 튜닝 완료 — GPU 상주 (VRAM N MB, num_ctx=...)" 확인
```
