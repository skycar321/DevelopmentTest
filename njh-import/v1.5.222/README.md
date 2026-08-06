# njh-cli 반입킷 v1.5.222 (암호 동일) — 메모리 진단·최적화판

**노트북 킷만 있습니다** (cli/gateway 는 ../v1.5.218 그대로 — 런타임 동일).

## v1.5.221 대비: optimize-laptop-memory.sh 신설 (7번째 진입점)

모델 상주 노트북의 "딱히 킨 것도 없는데 RAM 99%" 상황용:

```bash
bash optimize-laptop-memory.sh              # 진단만 — 상위 프로세스 12·서비스 상태 리포트
bash optimize-laptop-memory.sh --apply      # SysMain·검색 인덱서 중지 (관리자, --revert 원복)
bash optimize-laptop-memory.sh --apply-kv   # KV 캐시 양자화(q8_0) + 재기동 → ~0.9GB 확보
```

- 기본 실행은 **무변경 진단**. 보안(Defender)은 건드리지 않음.
- start/tune 에 OLLAMA_NUM_PARALLEL=1 고정(병렬 KV 중복 할당 봉인) 포함.

## 권장 순서 (v1.5.221 에서 이미 튜닝 완료된 노트북)

```bash
bash optimize-laptop-memory.sh          # 진단 출력 확인 후
bash optimize-laptop-memory.sh --apply  # 필요 시
```
