# njh-import v1.5.479 — Nexus 라이브러리 조사 킷

사내 Nexus 저장소에 어떤 라이브러리가 실제로 제공되는지 조사하는 스크립트입니다.
전체 njh-cli 배포본이 아니라 **조사 스크립트만** 담은 경량 킷입니다.

## 담긴 것

| 경로 | 설명 |
|---|---|
| `nexus-조사/nexus-라이브러리-조사.ps1` | 조사 스크립트 (PowerShell) |
| `nexus-조사/nexus-라이브러리-조사.md` | 결과 판정 읽는 법 (hosted/proxy 구분 등) |

## 사용법

1. 아래 SHA-256 을 대조해 파일이 온전한지 먼저 확인합니다.
2. 7z 을 해제합니다. **암호는 별도 채널로 전달**되며 이 저장소에는 없습니다.
3. `nexus-라이브러리-조사.md` 를 먼저 읽고 나서 `.ps1` 을 실행합니다.
4. 산출된 결과 txt 를 회신해 주시면 반입 요청 목록으로 환산합니다.

## 무결성

`SHA256SUMS.txt` 참조. 다운로드 후 대조:

```powershell
Get-FileHash .\njh-nexus-survey-v1.5.479.7z -Algorithm SHA256
```

```bash
shasum -a 256 njh-nexus-survey-v1.5.479.7z
```
