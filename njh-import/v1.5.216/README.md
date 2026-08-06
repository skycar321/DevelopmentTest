# njh-cli 반입킷 v1.5.216 (암호 동일) — 노트북 OOM 봉인판

**노트북은 이 판을 쓰세요.** v1.5.215 대비:

1. **모델 이중적재 OOM 봉인** — 종료가 부모만 죽이고 모델(6.5GB)을 쥔 러너
   자식을 남겨, 재기동 시 이중 적재로 램이 터지던 것(현장 발생) —
   종료를 프로세스 트리째로 + `start-local-llm.sh` 가 기동 전 고아 러너 자동 정리.
2. `start-local-llm.sh` 가 GPU 판정을 setx 로도 보강 등록(자가치유).

## 노트북 절차 (복구 포함)
```bash
taskkill //F //T //IM ollama.exe   # 잔존 프로세스 트리째 정리(1회)
bash start-local-llm.sh            # GPU 기동 + 모델 상주
/c/njh-ollama/ollama.exe ps        # PROCESSOR 열이 "100% GPU" 면 확정
```
