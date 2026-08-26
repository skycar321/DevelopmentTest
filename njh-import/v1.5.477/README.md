# NJH-CLI 반입 패키지

버전: 1.5.477
릴리스 노트: `릴리스노트-v1.5.477.md`

이 디렉터리 전체가 배송 단위입니다. 아카이브 하나만 이동하거나 설치하지 마세요.

1. 승인된 별도 보안 채널에서 `SHA256SUMS.txt` 바이트의 SHA-256 신뢰 앵커를 받아 먼저 비교합니다.
2. `shasum -a 256 -c SHA256SUMS.txt`로 매니페스트 자체를 제외한 full channel manifest의 모든 파일을 검증합니다.
3. `sha256.txt`는 정확히 세 역할 archive subset입니다. OCR·Ollama archive와 channel sibling 검증에는 사용하지 않습니다.
4. 승인된 별도 `release.env`에서 암호를 읽어 필요한 role archive를 추출합니다. 현재 v1.5.473 published profile은 정규 파일 19개와 `.7z` archive 5개입니다.
5. 서버 all-role 설치는 Gateway package의 one-page card에서 `bootstrap-import.sh --role all --dry-run` 후 `bootstrap-import.sh --role all`을 실행합니다.
6. `BOOTSTRAP PASS role=all`, `https://<approved-host>:<gateway-port>/monitor`, `http://<approved-host>:<ocr-port>/ocr`를 보관하고, 출력되면 `http://<approved-host>:<match-port>/console`도 보관합니다.
7. 추출한 CLI의 `docs/사용자/통합-설치가이드.html`과 `docs/사용자/06-post-import-first-30-minutes.md`를 따릅니다.

이 파일은 수신자 인수의 시작점이며, 작업 정확성 또는 모델 성능의 통과 선언이 아닙니다.
