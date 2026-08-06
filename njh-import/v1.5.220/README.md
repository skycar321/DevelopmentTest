# njh-cli 반입킷 v1.5.220 (암호 동일) — GPU 판정 = 실사용 상태 일치판

**노트북 킷만 있습니다** (cli 는 ../v1.5.218, gateway 는 ../v1.5.218 그대로 — 런타임 동일).

v1.5.219 대비 변경 2건:

1. **GPU 판정을 운영 numCtx 적재 상태로** — 이전 판은 작은 기본 컨텍스트로 적재된
   상태를 보고 "ok GPU" 라고 했는데, njh 가 32768 컨텍스트로 첫 요청을 보내는 순간
   모델이 재적재되며 CPU 로 떨어지는 케이스를 놓쳤습니다(현장 실증 — GPU 메모리
   그래프가 한동안 높다가 뚝 떨어지던 그것). 이제 start 의 warm-up 과 verify [6-1b]
   가 settings.json 의 운영 numCtx 로 적재한 뒤 판정합니다:
   - "ok GPU 적재 확인" 이 나오면 → njh 를 써도 GPU 가 유지됩니다.
   - CPU 로 나오면 → 32k 컨텍스트가 GPU 메모리에 안 들어가는 것이며, 자동 복구
     실패 시 serve 로그의 GPU 라인이 자동 출력됩니다.
2. **serve 기동 로그** — start 로 띄운 serve 도 이제
   `~/.njh-cli/logs/ollama-serve.log`(+.err.log) 에 로그가 남아 vulkan 적재
   실패/폴백 원인을 볼 수 있습니다(이전엔 로그가 아예 없어 grep 이 비었음).

## 절차 (동일)

```bash
bash start-local-llm.sh     # 운영 컨텍스트로 적재 + GPU 판정까지 자동
```

CPU 로 판정되는 경우 numCtx 축소(16384) 또는 KV 캐시 양자화 테스트 결과에 따라
다음 판에서 기본값이 확정됩니다.
