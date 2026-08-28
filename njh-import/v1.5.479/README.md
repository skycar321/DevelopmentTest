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

## 실행이 안 될 때

**증상**: `powershell -ExecutionPolicy Bypass -File .\nexus-라이브러리-조사.ps1` 실행 시
`식 또는 문에서 예기치 않은 ')' 토큰입니다` / `UnexpectedToken` 파서 에러가 쏟아지고,
에러 본문의 한글이 `"?끊땋??"` 처럼 깨져 보임.

**원인**: Windows PowerShell 5.1 은 BOM 없는 `.ps1` 을 시스템 ANSI 코드페이지(한국어 Windows = CP949)
로 읽습니다. 한글 문자열 리터럴이 깨지면서 따옴표 짝이 어긋나 파서가 무너집니다.

**해결**: 이 아카이브의 스크립트는 **UTF-8 BOM** 으로 저장되어 있어 5.1 에서도 정상 동작합니다.
이전에 받으신 파일에서 위 에러가 났다면 **이 버전으로 다시 받아** 실행해 주세요.

대안으로 `pwsh`(PowerShell 7) 가 설치돼 있다면 인코딩과 무관하게 동작합니다:

```
pwsh -File .\nexus-라이브러리-조사.ps1 -NexusUrl "<주소>" -ProjectPath "<프로젝트경로>"
```
