# njh-import v1.5.227 — 노트북 킷만 (GPU 낙하 최종 봉인)

이 폴더에는 **노트북(Windows 개인 PC 로컬 LLM) 킷만** 있습니다.
- CLI 킷: **v1.5.226 폴더의 것을 그대로** 쓰세요 (런타임 동일).
- gateway / db-gateway: **v1.5.218 폴더의 것을 그대로** 쓰세요 (서버측 변경 0).

## 이번 버전 내용 (njh-win-laptop-v1.5.227.7z)

현장 실증(2026-08-07) 봉인: 튜닝(q8_0+32768)이 GPU 통과했는데 **RAM 96% 시점에
njh 접속하자 GPU→CPU 재낙하**. 원인 = 내장 GPU 는 공유 메모리라 여유 RAM 이 없으면
prefill 순간 버퍼 할당이 실패해 러너가 죽고 CPU 로 재배치됨.

1. **tune-gpu-ctx.sh**: GPU 프로브·채택·njh 요청 전부 **num_batch=256 통일**(순간
   버퍼 절반). 그래도 안 되면 128 최후 단. 판정 버전 TUNE_V=3 — **구판정은 setup 이
   자동 재측정**.
2. **start-local-llm.sh**: 여유 RAM<2GB 경고 + optimize-laptop-memory.sh --apply
   안내. 낙하가 이미 일어났어도 start 재실행으로 GPU 복귀.

## 반입 후 할 일 (노트북)

```bash
cd /c/njh-local-llm            # 킷 압축 해제 위치
./setup-local-llm.sh           # 구판정 자동 재측정 포함 (수 분)
```

마지막 줄 "ok 튜닝 완료 — GPU 상주 (…, num_batch=256)" 확인 후 njh 사용.
사용 중 느려지면(=GPU 낙하 의심) `./start-local-llm.sh` 한 번이면 복귀합니다.
RAM 이 늘 96%+ 라면 `./optimize-laptop-memory.sh --apply` 를 권장합니다.
