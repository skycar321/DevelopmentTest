# njh-cli 반입킷 v1.5.217 (암호 동일) — 로컬 LLM 자동 기동판

v1.5.216 대비 변경 1건:

1. **로컬 LLM 자동 기동** — njh-cli 에 들어가거나 `/connection` 을 로컬로
   전환했을 때 로컬 LLM(127.0.0.1)이 죽어 있으면 **알아서 기동하고 응답까지
   기다립니다**(최대 45초). 이제 `start-local-llm.sh` 를 따로 돌리지 않아도
   njh 만 실행하면 됩니다.
   - 저장된 GPU 판정(`~/.njh-cli/ollama-accel.env`)대로 Vulkan/CPU 모드로 뜹니다.
   - 클라우드 접속 차단(`OLLAMA_NO_CLOUD=1`)이 항상 강제됩니다.
   - 기동 전에 고아 러너를 트리째 정리합니다(v1.5.216 OOM 봉인과 동일 정책 —
     이중 적재 재발 방지).
   - 원격 주소(게이트웨이·사내 서버)는 절대 자동 기동하지 않습니다.
   - 끄려면: 환경변수 `NJH_NO_LOCAL_AUTOSTART=1`.

## 노트북 절차

```bash
# 그냥 njh 실행 → 로컬 LLM 이 꺼져 있으면 자동으로 뜹니다.
# 수동 기동/재부팅 직후 워밍이 필요하면 기존 셸도 그대로 사용 가능:
bash start-local-llm.sh            # GPU 기동 + 모델 상주(warm-up)
/c/njh-ollama/ollama.exe ps        # PROCESSOR 열이 "100% GPU" 면 확정
```

주의: 자동 기동은 서버만 띄웁니다(모델 warm-up 은 첫 질문 때 자동 적재 —
첫 응답만 수십 초 느릴 수 있음). 미리 상주시키려면 `start-local-llm.sh` 사용.
