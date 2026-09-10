# Vue 3 초안 킷 — 20차 (합격 기준 2/3 에서 말없이 끝나던 것)

```bash
sha256sum -c sha256.txt
7z x -p'<암호>' vue3-draft-kit-20260911-20.7z
source "<20차 킷 경로>/kit.sh"
bash "$KIT/run/06-publish.sh"
```

## 19차에서 고친 것

`06-publish.sh` 가 `== 합격 기준 2/3 — npm run serve 기동 응답` 만 찍고
**성공도 실패도 남기지 않은 채** 프롬프트로 돌아왔다.

원인은 `set -euo pipefail` 이다. 개발 서버가 먼저 죽어 있으면 정리 단계의
`kill "$SERVE_PID"` 가 0 이 아닌 값을 반환하고, `set -e` 가 그 자리에서 스크립트를
끝낸다 — **바로 다음 줄의 "기동 응답이 없다" 안내에 도달하지 못한다.**
실패를 알려 주려고 쓴 줄이, 그 실패 때문에 실행되지 못한 것이다.

- 서버 정리를 `stop_serve` 헬퍼 하나로 모았다(`run/_lib.sh`). 모든 단계가
  실패해도 스크립트를 멈추지 않고, 자식 PID 하나만 정확히 겨냥한다.
- `pkill -P` 를 전부 없앴다(5곳). MSYS 에서 프로세스 그룹을 훑는 위험한 형태다.
- 포트가 아직 열려 있으면 그 사실을 **알려만 준다**(다음 실행 전에 창을 닫으라고).

적용 파일: `06-publish.sh` · `08-review.sh` · `08a-login-session.sh` ·
`08b-route-sweep.sh` · `08c-screen-repair.sh`

## 기동이 실제로 안 될 때 볼 것

```bash
tail -30 "$STATE/publish-serve.log"
```

## 그 앞 판들

- 19차 — 절대경로로 찍힌 차단 파일도 찾아낸다(Windows `path.relative` 백슬래시).
  보수 회차 상한 12 → 30.
- 18차 — 게이트가 예외로 죽으면 `죽은 원인:` 으로 이름·메시지·스택을 찍는다.
- 17차 — 초안 폴더가 사용 중이면 즉시 멈춘다.
- 16차 — `05a` 가 보수 1건 = 커밋 1건으로 쌓는다.

## 막혔을 때 보내 주면 좋은 것

`~/.vue3-draft/` 의 `publish-serve.log` · `repair-build.log` · `ai-repair.md`,
그리고 `git -C "$DRAFT" log --oneline`.
