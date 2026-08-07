# njh-import v1.5.230 — 노트북 킷 + ollama 런타임(선택)

- CLI 킷: **v1.5.226 폴더의 것을 그대로** 쓰세요 (런타임 동일).
- gateway / db-gateway: **v1.5.218 폴더의 것을 그대로** 쓰세요 (서버측 변경 0).

## 파일

| 파일 | 크기 | 용도 |
|---|---|---|
| `njh-win-laptop-v1.5.230.7z` | 1.7MB | 노트북 킷 (필수) |
| `njh-ollama-runtime-v0.32.6-slim.7z` | 19MB | ollama 런타임 교체용 (**선택 — 기본은 적용하지 마세요**) |
| `SHA256SUMS.txt` | — | 무결성 검증값 |

## 1. 노트북 킷 (필수)

```bash
cd /c/njh-local-llm            # 킷 압축 해제 위치
./setup-local-llm.sh           # 판정 자동 재측정(기준선 포함, 수 분)
```

v1.5.229 에서 들어간 GPU 낙하 봉인이 그대로 있습니다: 여유 RAM 3단 게이트
(3GB↑ 정상 / 2~3GB num_batch 128 / 2GB↓ 적재 중단), 부분 적재 검출(기준선 90%),
`OLLAMA_MAX_LOADED_MODELS=1`.

## 2. ollama 런타임 (선택 — 권장하지 않음)

⚠️ **실익이 확인되지 않았습니다.** 업그레이드 근거였던 "새 버전에 내장 GPU
메모리 보정 추가"는 소스 대조로 **반박**됐습니다:

- v0.32.6 릴리스 노트에 iGPU·Vulkan·메모리 보정 언급 없음
- v0.32.1→v0.32.6 의 `server/sched.go` 변경은 이미지 생성 제거가 전부
- 해당 로직(`availableMemoryForPlacement`/`systemLimited`)은 **v0.32.1 에 이미 존재**
- Vulkan 런타임도 v0.32.1 공식 빌드가 이미 동봉 중

남은 기대는 llama.cpp 엔진 갱신뿐입니다. **지금 GPU 가 잘 돌고 있다면 그대로 두세요.**

적용해 보고 싶다면:

```bash
# 1) 전용 폴더에 풀기 (예: C:\njh-local-llm\ollama-new)
# 2) 먼저 무엇을 할지만 확인
bash upgrade-ollama.sh --dry-run
# 3) 적용 (원본 통째 백업 → 교체 → GPU 판정 재측정)
bash upgrade-ollama.sh
# 4) 전후 비교 → 개선 없으면 되돌리기
bash measure-laptop.sh
bash upgrade-ollama.sh --rollback
```

`upgrade-ollama.sh` 는 vulkan 이 없는 패키지를 거부하고(GPU 상실 방지), 교체 전
현재 런타임을 통째로 백업합니다. 모델 파일(`%USERPROFILE%\.ollama`)은 건드리지
않습니다.

원본: ollama v0.32.6 공식 Windows 빌드(1.39GB)에서 **NVIDIA CUDA 라이브러리 제거**
(이 노트북엔 NVIDIA GPU 없음) 후 재압축 = 19MB. 공식 sha256 대조 완료.
