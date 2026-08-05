# njh-cli 반입킷 v1.5.206

암호 걸린 7z 두 개입니다. **암호는 별도 채널로 전달**됩니다(저장소에 넣지 않습니다).

| 파일 | 크기 | 용도 |
|---|---|---|
| `njh-cli-v1.5.206.7z` | 20.4 MB | njh-cli 본체 + 사용자 문서 + 번들 스킬 + 진단 |
| `njh-gateway-v1.5.206.7z` | 1.8 MB | 게이트웨이 설치킷 |

## v1.5.205 → v1.5.206 델타 (현장 요청 2건)

### 1. `/mode` 명령 신설 — Shift+Tab 없이 권한 모드 전환
mintty 등에서 Shift+Tab 키가 전달되지 않아 권한 모드를 바꿀 방법이 없던 문제의 우회로입니다.

```
/mode                     현재 모드 + 선택지 표시
/mode acceptEdits         파일 수정 자동 허용, 셸은 확인
/mode bypassPermissions   모든 도구 자동 승인 (묻지 않음)  — 별칭: yolo, bypass
/mode ask                 위험 작업마다 확인 (기본)
```

Shift+Tab 순환과 같은 전이 경로를 타므로 동작이 갈리지 않습니다.

### 2. `scripts/njh-toolcall-doctor.sh` — "모델이 왜 도구를 안 부르는가" 사진-판정 진단
파일 반출이 불가한 폐쇄망에서 **화면 요약 한 장**으로 원격 진단이 되도록 만든 도구입니다.

```
bash scripts/njh-toolcall-doctor.sh        # 최신 런타임 로그 자동 선택
```

마지막 판정 박스만 사진 찍어 공유하면 됩니다:

| VERDICT | 뜻 | 다음 행동 |
|---|---|---|
| `CONTRACT-MISSING` | 도구 계약이 요청에 안 실림 | njh-cli 결함 — 사진 보고 |
| `MODEL-SILENT` | 계약은 나갔는데 모델이 발화 안 함 | `/mode acceptEdits` 후 재시도, 여전하면 백엔드 문제 |
| `TOOLS-WORKING` | 발화·실행 정상 | 완주 실패는 예산·검증 단계 — 실패 화면과 함께 보고 |

프롬프트·코드 원문은 출력하지 않습니다(집계·판정만) — 사진 반출에 안전합니다.

## v1.5.205 에서 이어지는 것

워크플로 데드락(`workflow.auto` OFF), 게이트 워밍 HTTP 400(`keepAlive "-1"`), 모델 상주 단계
자동화, 롤백 unbound 변수, GPU 강제 해제(numGpu) — 상세는 `njh-import/v1.5.205/README.md`.

## 설치

v1.5.205 와 동일: 모델 노드는 `./install-all.sh model`, 겸용 노드는 `./install-all.sh both`.
기존 설치 위에 그대로 풀면 됩니다(설정·모델 보존).
