# Track3 임베딩 킷 (nomic-v2-moe 모델 + 임베딩/매치 게이트웨이 서비스) — 분할본, 2026-09-05 final

- `model/` : `njh-embed-model-nomic-v2-moe.7z.001…` 분할본 + `parts-manifest.json` + `reassemble-and-verify.sh`
- `service/`: `njh-track3-service.7z.001…` 분할본 + 동일 매니페스트
- 사내 반입 후: 각 디렉터리에서 `bash reassemble-and-verify.sh` → 원본 tar.gz 복원 + sha256 검증. 암호는 별도 채널.
- 검증: 이 디렉터리의 `SHA256SUMS.txt` 로 `shasum -a 256 -c SHA256SUMS.txt`
