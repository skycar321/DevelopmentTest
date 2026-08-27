# NJH-CLI 반입 패키지

버전: 1.5.474
릴리스 노트: `릴리스노트-v1.5.474.md`

이 디렉터리 전체가 배송 단위입니다. 아카이브 하나만 이동하거나 설치하지 마세요.

1. 승인된 별도 보안 채널에서 `SHA256SUMS.txt` 바이트의 SHA-256 신뢰 앵커를 받아 먼저 비교합니다.
2. `shasum -a 256 -c SHA256SUMS.txt`로 매니페스트 자체를 제외한 full channel manifest의 모든 파일을 검증합니다.
3. `sha256.txt`는 정확히 세 역할 archive subset입니다. OCR·Ollama archive와 channel sibling 검증에는 사용하지 않습니다.
4. 승인된 별도 `release.env`에서 암호를 읽어 필요한 role archive를 추출합니다. 현재 v1.5.473 published profile은 정규 파일 19개와 `.7z` archive 5개입니다.
5. 서버 all-role 설치는 Gateway package의 one-page card에서 `bootstrap-import.sh --role all --dry-run` 후 `bootstrap-import.sh --role all`을 실행합니다.
6. `BOOTSTRAP PASS role=all`, `https://<approved-host>:<gateway-port>/monitor`, `http://<approved-host>:<ocr-port>/ocr`를 보관하고, 출력되면 `http://<approved-host>:<match-port>/console`도 보관합니다.
7. 추출한 CLI의 `docs/사용자/통합-설치가이드.html`과 `docs/사용자/06-post-import-first-30-minutes.md`를 따릅니다.

이 파일은 수신자 인수의 시작점이며, 작업 정확성 또는 모델 성능의 통과 선언이 아닙니다.
## 반입 후 모델 파일 무결성 확인 (필수)

`ollama list`는 manifest만 읽으므로, **목록에 보여도 실제 파일이 잘려 있을 수 있습니다.**
반입 중 잘린 blob은 모델을 올리는 순간에야 드러나며, 아래와 같은 오류로 나타납니다.

```
Error: tensor "token_embd.weight" offset+size (841592224) exceeds file size (222822400)
```

ollama는 blob 파일 이름을 그 파일의 SHA-256으로 짓습니다. 따라서 체크섬 파일이 없어도
파일명과 실제 해시를 비교하면 잘린 파일을 전부 찾을 수 있습니다. **모델 반입 직후 한 번,
그리고 모델을 올리기 전에 반드시 실행하세요.**

```bash
MODELS="${OLLAMA_MODELS:-$HOME/.ollama/models}"
for f in "$MODELS"/blobs/sha256-*; do
  want=$(basename "$f" | sed 's/^sha256-//')
  got=$(sha256sum "$f" | cut -d' ' -f1)
  if [ "$want" != "$got" ]; then
    echo "손상: $(basename "$f")  크기=$(wc -c < "$f")"
  fi
done
echo "검사 완료"
```

- 모델 저장소 크기에 따라 **수 분이 걸립니다.** `검사 완료`만 나오면 정상입니다.
- `sha256sum`이 없는 환경에서는 `shasum -a 256`으로 바꿔 실행하세요.
- Windows 노트북은 Git Bash에서 실행합니다.

**손상이 나온 경우**: 해당 모델을 제거하고 다시 반입합니다. `import-model-store.sh`는 기존
파일을 덮어쓰지 않고 병합하므로, 손상된 blob을 먼저 지워야 새로 복사됩니다.

```bash
ollama rm <모델이름>
rm "$MODELS"/blobs/sha256-<손상된해시>
bash import-model-store.sh <모델폴더>
ollama run <모델이름> "안녕"
```
