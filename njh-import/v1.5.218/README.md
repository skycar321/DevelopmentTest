# njh-cli 반입킷 v1.5.218 (암호 동일) — GPU 미적용 자가진단판

**노트북 "판정은 vulkan 인데 ollama ps = 100% CPU" 증상을 겪었다면 이 판을 쓰세요.**
v1.5.217 대비 변경 3건:

1. **vulkan 판정 ≠ 실적용 갭 봉인** — 판정 파일만 믿지 않습니다.
   - `setup-local-llm.sh`: 저장 판정이 vulkan 이어도 운영 ollama 폴더의
     `lib/ollama/vulkan/ggml-vulkan.dll` 실존을 재확인 — 없으면 큰 경고와 함께
     이번 세션 CPU 강등 + 복구법 안내(오버레이 복구 시 자동 vulkan 복귀).
   - `verify-local-llm.sh` [6-1b] 신설: 판정 vulkan 이면 **모델이 실제 GPU 에
     올라갔는지(size_vram>0)까지 검사** — CPU 적재면 명시 실패로 표시하고
     원인(오버레이 누락 / env 미상속)과 진단 명령을 출력합니다.
     이제 작업관리자 눈대중이 아니라 verify 가 판정합니다.
   - `start-local-llm.sh`: 상주 확인 때 "GPU 적재 — VRAM N MB" 또는
     "판정 vulkan 인데 CPU 적재!" 를 명시 출력.
2. **로컬 LLM 자동 기동 감사수정 5건** — 커스텀 포트(11434 외) 자동 기동 보존
   (OLLAMA_HOST 유도), njh 2개 동시 기동 잠금(이중 적재·오살 방지), 남이 방금
   띄운 콜드스타트 serve 3초 유예, 상태 URL 안전화, Windows 실행경로 절대화.
3. 게이트웨이 첫-바이트 페일오버 행동 테스트 영구화(운영 동작 변화 없음).

## GPU 미적용 노트북 복구 절차

```bash
# 1) 원인 확인 — 운영 ollama 에 vulkan 오버레이가 있는가?
ls /c/njh-ollama/lib/ollama/vulkan/          # ggml-vulkan.dll 있어야 함
# 없으면: vulkan 패키지(njh-vulkan-igpu-ollama-*.7z)를 INSTALL-VULKAN.md 대로
#         C:\njh-ollama\lib\ollama\vulkan\ 에 풀어 넣기
# 2) 트리째 종료 후 재기동
taskkill //F //T //IM ollama.exe
bash start-local-llm.sh                      # "GPU 적재 — VRAM N MB" 확인
/c/njh-ollama/ollama.exe ps                  # PROCESSOR "100% GPU" 확인
```
