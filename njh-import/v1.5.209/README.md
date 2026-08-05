# njh-cli 반입킷 v1.5.209

암호 걸린 7z 두 개입니다. **암호는 별도 채널로 전달**됩니다(저장소에 넣지 않습니다).

| 파일 | 누가 가져가나 | 내용 |
|---|---|---|
| `njh-gateway-v1.5.209.7z` | **서버 담당(AP01/02/03)** | 게이트웨이+백엔드 설치킷. 안에 서버용 `njh-gateway-v1.5.209.tar.gz`와 통합 러너북(`설치가이드-통합.html`) |
| `njh-cli-v1.5.209.7z` | **개인 PC 사용자** | njh-cli 본체 + 사용자 문서 + 번들 스킬 + 진단 |

## v1.5.208 → v1.5.209 델타: 킷 역할 분리 확정

**njh-cli 7z에서 게이트 서버 설치물(`gateway/db-gateway/` 킷·`njh-gateway.js`)을 제거했습니다.**
이전 버전을 풀면 클라이언트 패키지 안에 게이트 설치 킷이 함께 나와 "어느 쪽으로 설치하나"
혼동이 있었습니다(현장 보고). 이제:

- **서버 설치 = gateway 7z 하나로만.** `tar -xzf njh-gateway-v1.5.209.tar.gz -C /data/llm/njh-gateway-kit`
  → `./install-all.sh model`(AP01/02) / `both`(AP03). DB는 `~/.bashrc`의 `sqms_jdbc_*`에서 자동 조립.
- **개인 PC = cli 7z 하나로만.** 게이트 관련 파일은 문서(README) 하나만 남습니다.
- 동봉 가이드(import-guide, 폐쇄망 운영 가이드)의 레이아웃 자동감지도 이에 맞게 갱신:
  구판(≤v1.5.208)이 이미 풀려 있는 서버는 기존 경로 그대로 인식하고, 신판은 게이트 킷 경로를 물어봅니다.
- 게이트 업그레이드 검증 명령 경로 교정: 킷 루트에서 `node ops/verify-upgrade.mjs`.

## 설치

서버: gateway 7z를 풀어 나온 `설치가이드-통합.html`을 브라우저로 열고 0단계부터 순서대로.
15Gi 노드 메모리 부족 시: `NJH_OLLAMA_KV_CACHE_TYPE=q8_0 NJH_OLLAMA_FLASH_ATTENTION=1 ./install-all.sh model`.
기존 설치 위에 그대로 풀면 됩니다(설정·모델 보존). 상세 델타 이력: `njh-import/v1.5.208/README.md`.
