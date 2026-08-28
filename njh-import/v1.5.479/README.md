# njh-import v1.5.479 — Nexus 라이브러리 조사 킷

사내 Nexus 에 Vue3 전환에 필요한 패키지가 실제로 있는지 조사하는 스크립트입니다.
전체 njh-cli 배포본이 아니라 **조사 스크립트만** 담은 경량 킷입니다.

## 담긴 것

| 파일 | 용도 |
|---|---|
| `nexus-조사/nexus-라이브러리-조사.ps1` | PowerShell 진입점 (Windows) |
| `nexus-조사/nexus-라이브러리-조사.sh` | Git Bash / macOS / Linux 진입점 |
| `nexus-조사/nexus-라이브러리-조사.md` | 사용법 · 결과 판정 · 문제 해결 |

두 진입점은 **같은 조사 로직**을 공유하므로 어느 쪽으로 돌려도 같은 형식의 결과가 나옵니다.
`node` 와 `curl` 이 필요합니다(둘 다 개발 PC 에 이미 있습니다).

## 실행

```powershell
powershell -ExecutionPolicy Bypass -File .\nexus-라이브러리-조사.ps1 `
  -NexusUrl "<Nexus 주소>" -ProjectPath "<프로젝트 경로>"
```

```bash
sh ./nexus-라이브러리-조사.sh --nexus-url "<Nexus 주소>" --project-path "<프로젝트 경로>"
```

결과는 `nexus-라이브러리-조사-결과.txt` 로 저장됩니다. **이 파일 하나만 전달**하시면 됩니다.
토큰·비밀번호는 기록되지 않습니다.

## 조회가 안 될 때 (TLS 인증서)

`curl` 이 `000` 을 뱉거나 REST 조회가 실패하면 대개 **서버가 아니라 PC 의 인증서 신뢰** 문제입니다.
Git Bash 의 curl 은 자체 CA 번들을 쓰므로, 사내 루트 CA 가 Windows 저장소에만 있으면
npm·Maven 은 되는데 이 도구만 실패할 수 있습니다.

```powershell
# 1) 원인 확인 (1회만 — 이 상태로 두지 마십시오)
... -InsecureDiagnostic

# 2) 영구 해법 — 사내 루트 CA 지정
... -CaBundle corp-ca.pem
```

CA 추출과 고정 방법은 동봉된 `.md` 에 있습니다.

## 이 도구가 하지 않는 것

- 지정하신 주소가 응답하지 않으면 **다른 주소로 갈아타지 않고 중단**합니다.
- 공용 레지스트리(`registry.npmjs.org` 등)는 Nexus 가 아니므로 조사 대상에서 제외합니다.
- 조회에 실패하면 패키지 판정·차단 목록·hosted 분류를 **하나도 만들지 않습니다.**
  도달하지 못한 상태에서 만든 목록은 사실이 아니기 때문입니다.

## 무결성

`SHA256SUMS.txt` 참조. 7z 암호는 **별도 채널**로 전달되며 이 저장소에는 없습니다.

```powershell
Get-FileHash .\njh-nexus-survey-v1.5.479.7z -Algorithm SHA256
```
