# njh-cli 반입킷 v1.5.215 (암호 동일)

| 파일 | 누가 |
|---|---|
| `njh-win-laptop-v1.5.215.7z` | **노트북 — 이 판 권장** |
| `njh-cli-v1.5.215.7z` | 개인 PC |
| `njh-gateway-v1.5.215.7z` | 서버 — v1.5.213+ 설치했으면 교체 불필요 |

## v1.5.214 → v1.5.215 델타

**`start-local-llm.sh` 신규 동봉** — 재부팅 후 이것 하나만 실행하면:
저장된 GPU 판정 적용 → serve 기동 → 모델 메모리 선적재(24h 상주) → 상주 확인.
실행 직후부터 njh 첫 질문이 빠릅니다.

```bash
bash start-local-llm.sh
```

(v1.5.214의 GPU 영속 setx·TUI 마감 4건 포함. vulkan 오버레이는 기존 그대로.)
