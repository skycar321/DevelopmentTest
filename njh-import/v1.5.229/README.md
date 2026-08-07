# njh-import v1.5.229 — 노트북 킷만 (GPU 낙하 구조 봉인 완결판)

이 폴더에는 **노트북(Windows 개인 PC 로컬 LLM) 킷만** 있습니다.
- CLI 킷: **v1.5.226 폴더의 것을 그대로** 쓰세요 (런타임 동일).
- gateway / db-gateway: **v1.5.218 폴더의 것을 그대로** 쓰세요 (서버측 변경 0).

## 왜 이 버전인가

웹 딥리서치(#050) 결론: **현재 튜닝 조합(Flash Attention + KV q8_0 + num_batch 256 +
ctx 32768 + parallel 1)은 이미 16GB 내장 GPU 노트북의 최적점**입니다. 남은 문제는
튜닝값이 아니라 "여유 RAM 없이 GPU 를 쓰려다 러너가 죽는 것"이라, 이번 버전은
**막는 게이트**를 넣었습니다.

1. **여유 RAM 3단 게이트** (start-local-llm.sh)
   - 3GB 이상 → 정상(num_batch 256)
   - 2~3GB → 압박 모드(num_batch 128 자동 하향)
   - 2GB 미만 → **적재 중단** + 조치 안내 (강행: `NJH_LOCAL_ALLOW_LOW_RAM=1`)
2. **부분 적재 검출** — `size_vram > 0` 만으로는 *일부 레이어만 GPU* 인 상태도
   통과합니다. 튜닝 때 실측한 VRAM 기준선의 **90% 이상**을 요구하고, 미달이면
   1회 자동 복구 후 명시적으로 알립니다.
3. **`OLLAMA_MAX_LOADED_MODELS=1`** — 다른 모델이 같은 공유 RAM 을 무는 것을 차단.
4. 판정 버전 v4 — 기준선이 없는 구판정은 setup 이 자동으로 다시 잽니다.

또한 v1.5.228 에서 **njh 가 settings.json 의 numBatch 를 직접 읽도록** 배선해,
"setx 는 새 창부터"라는 함정 때문에 기존 창의 njh 가 다른 옵션으로 요청해
재적재(→CPU 낙하)되던 문제를 없앴습니다.

## 반입 후 할 일 (노트북)

```bash
cd /c/njh-local-llm            # 킷 압축 해제 위치
./setup-local-llm.sh           # 판정 자동 재측정(기준선 포함, 수 분)
```

마지막 줄 "ok 튜닝 완료 — GPU 상주" 확인 후 njh 사용.
사용 중 느려지면 `./start-local-llm.sh` 한 번이면 GPU 로 복귀합니다.
RAM 이 늘 부족하면 `./optimize-laptop-memory.sh --apply` 를 먼저 실행하세요.
