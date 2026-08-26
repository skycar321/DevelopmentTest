# v1.5.474 반입·설치 — 실제 플로우 기준 (PC에서 7z 해제 → tar.gz만 서버 반입)

## A. 반입 PC에서 (한 번)
```bash
7z x njh-gateway-v1.5.474.7z     # → njh-gateway-v1.5.474.tar.gz 나옴 (이걸 그대로 반입)
7z x njh-ocr-engine-v0.1.7z      # → njh-ocr-engine-v0.1/ "디렉터리"가 나옴 (tar.gz 아님 — 아래 한 줄로 재포장)
tar -czf njh-ocr-engine-v0.1.tar.gz -C njh-ocr-engine-v0.1 .
```
> OCR 7z 안에 tar.gz가 없는 것은 현행 패키징 형태입니다. 다음 릴리스에서 게이트웨이와 동일하게
> 7z 안에 tar.gz가 들어가도록 수정 예정 — 그때는 재포장 줄이 없어집니다.

## B. 서버 반입 (sftp → /data/llm/incoming)
| 서버 | 반입 파일 |
|---|---|
| 게이트웨이 서버 1대 | `njh-gateway-v1.5.474.tar.gz` + `njh-ocr-engine-v0.1.tar.gz` + `site.env.prefilled` |
| 모델 서버 2대 | `njh-gateway-v1.5.474.tar.gz` + `site.env.prefilled` |

## C. 모델 서버 2대 — 각각 4줄
```bash
cd /data/llm/incoming
mkdir -p ~/.njh-cli && cp site.env.prefilled ~/.njh-cli/site.env && vi ~/.njh-cli/site.env   # <REPLACE_> 4종만 채움
mkdir -p njh-gateway-kit && tar -xzf njh-gateway-v1.5.474.tar.gz -C njh-gateway-kit
bash njh-gateway-kit/bootstrap-import.sh --role model --dry-run && bash njh-gateway-kit/bootstrap-import.sh --role model
```
검증: `"$NJH_LLM_BASE/ollama/ollama-status.sh"` (기본 모델 = gemma4:12b, site.env에 적을 필요 없음)

## D. 게이트웨이 서버 1대 — 6줄
```bash
cd /data/llm/incoming
mkdir -p ~/.njh-cli && cp site.env.prefilled ~/.njh-cli/site.env && vi ~/.njh-cli/site.env   # <REPLACE_> 4종만 채움
export NJH_GATEWAY_TOKEN='<값>' NJH_EMBED_GATEWAY_TOKEN='<값>' NJH_OCR_GATEWAY_TOKEN='<값>'
mkdir -p njh-gateway-kit && tar -xzf njh-gateway-v1.5.474.tar.gz -C njh-gateway-kit
mkdir -p njh-ocr-engine && tar -xzf njh-ocr-engine-v0.1.tar.gz -C njh-ocr-engine   # bootstrap이 이 sibling 위치 자동 감지
bash njh-gateway-kit/bootstrap-import.sh --role all --dry-run && bash njh-gateway-kit/bootstrap-import.sh --role all
```
검증 2줄:
```bash
bash "$NJH_LLM_BASE/njh-gateway/e2e-smoke.sh"
bash "$NJH_LLM_BASE/njh-ocr-gateway/ocr-gateway-smoke.sh" --home "$NJH_LLM_BASE/njh-ocr-gateway"
```

## E. 개발 PC (njh-cli) — 압축 풀고 setup 한 번
```bash
7z x njh-cli-v1.5.474.7z && cd njh-cli-v1.5.474 && bash setup.sh    # Windows는 install.cmd
njh --version
```

## F. site.env 메모
- 모델 서버는 실제로 `NJH_LLM_BASE` + `NJH_MODEL_BIND_HOST` 2개만 소비 — 나머지 변수는 채워둬도 무시됨(무해).
- LLM 서빙 기본 모델은 이미 gemma4:12b (바꿀 때만 `NJH_GATEWAY_MODEL` export).
- ⚠️ 원페이지 문서의 `NJH_MATCH_PIPELINE_OLLAMA_MODEL='gemma4:12b'`는 결함 — match는 임베딩 모델 경로라 이 킷의 `nomic-embed-text-v2-moe`가 정답 (다음 릴리스에서 문서 수정).
- ⚠️ 원페이지 §2의 서버측 7z 해제 절차도 OCR 버전 디렉터리 구조와 불일치(test 라인 실패) — 이 README의 tar.gz 플로우를 쓰면 해당 없음.
