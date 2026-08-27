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
## 메모리 안전장치: swap 설정 (선택 사항, root 권한 필요)

LLM 모델 적재는 순간적으로 수 GB를 요구합니다. **swap이 0인 호스트에서는 메모리가
초과되는 순간 커널 OOM Killer가 발동하고, 물리 코어가 적은 서버에서는 그 전에 이미
SSH조차 응답하지 않게 됩니다.** 클라우드 리눅스 이미지는 기본적으로 swap을 만들지
않으므로, 온프렘에서 이관된 서버는 swap이 사라져 있는 경우가 많습니다.

### 먼저 현재 상태 확인

```bash
free -h                      # Swap 행이 모두 0이면 미설정
cat /proc/sys/vm/swappiness  # 0이면 swap을 만들어도 커널이 쓰지 않습니다
swapon --show                # 아무것도 안 나오면 활성 swap 없음
```

### 방법 A — swapfile (어느 배포판에서나 동일, 권장)

```bash
sudo fallocate -l 8G /var/swapfile        # fallocate 미지원 시 아래 dd 사용
# sudo dd if=/dev/zero of=/var/swapfile bs=1M count=8192 status=progress
sudo chmod 600 /var/swapfile
sudo mkswap /var/swapfile
sudo swapon /var/swapfile
```

재기동 후에도 유지하려면 `/etc/fstab`에 다음 한 줄을 **추가**합니다.

```
/var/swapfile  none  swap  sw  0 0
```

### 방법 B — Azure 리소스 디스크 (waagent)

리소스 디스크(`/mnt`)가 마운트된 Azure VM에서 쓰는 표준 방식입니다.
`/etc/waagent.conf`에서 두 값을 변경합니다.

```
ResourceDisk.Format=y
ResourceDisk.EnableSwap=y
ResourceDisk.SwapSizeMB=8192
```

```bash
sudo systemctl restart waagent    # 배포판에 따라 walinuxagent
```

> ⚠️ 리소스 디스크의 swap은 VM 할당 해제(deallocate) 시 사라졌다가 부팅 때 다시
> 만들어집니다. 그 동작을 원치 않으면 방법 A를 쓰세요.

### swappiness — swap을 만들었다면 반드시 함께

`vm.swappiness`는 커널이 메모리를 회수할 때 **프로세스 메모리를 swap으로 내보낼지,
파일 캐시를 버릴지** 정하는 0~100 비율입니다. 시간이나 용량 단위가 아닙니다.

| 값 | 동작 |
| --- | --- |
| `0` | swap을 사실상 쓰지 않고 **OOM Killer를 먼저 발동**합니다 |
| `10` | 실제 메모리 압박이 왔을 때만 swap을 사용합니다 (권장) |
| `60` | 배포판 기본값. 압박이 없어도 선제적으로 swap을 씁니다 |

**`vm.swappiness=0`이 설정된 호스트에서는 swap을 추가해도 효과가 없습니다.** 반드시
같이 변경하세요.

```bash
sudo tee /etc/sysctl.d/99-njh-swappiness.conf <<'CONF'
vm.swappiness = 10
CONF
sudo sysctl --system
```

기존 `/etc/sysctl.conf`에 `vm.swappiness = 0`이 있으면 그 값이 나중에 적용될 수 있으니,
해당 줄을 직접 수정하거나 제거해야 합니다.

### 적용 확인

```bash
free -h                      # Swap 총량이 표시되어야 합니다
sysctl vm.swappiness         # vm.swappiness = 10
swapon --show                # 장치/파일, 크기, 사용량
```

### 반드시 알아두실 것 — swap은 용량이 아닙니다

swap은 **모델을 담는 공간이 아닙니다.** 추론은 토큰마다 가중치 전체를 읽으므로,
가중치가 swap으로 밀려나면 매 토큰마다 디스크를 다시 읽어 사실상 멈춥니다.
swap의 역할은 두 가지뿐입니다.

- 일시적 초과분을 흡수해 OOM Killer가 무차별로 프로세스를 죽이는 것을 막습니다.
- 유휴 데몬의 콜드 페이지를 밀어내 **서버 접속이 유지**되게 합니다.

따라서 **모델이 가용 메모리에 들어가도록 구성하는 것이 우선**이고(작은 모델 선택,
`OLLAMA_CONTEXT_LENGTH` 축소, `OLLAMA_KV_CACHE_TYPE=q8_0`), swap은 그 위에 얹는
보험입니다. 조직 정책상 swap을 두지 않는 환경이라면 **VM 메모리 증설 또는 더 작은
모델**로 해결하는 것이 정석이며, 이 경우 위 설정은 건너뛰어도 됩니다.
