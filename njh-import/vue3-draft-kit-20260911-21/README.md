# Vue 3 초안 킷 — 21차 (서버는 떠 있었는데 못 찾던 것)

```bash
sha256sum -c sha256.txt
7z x -p'<암호>' vue3-draft-kit-20260911-21.7z
source "<21차 킷 경로>/kit.sh"
bash "$KIT/run/06-publish.sh"
```

## 19·20차에서 고친 것 — 결함 두 개가 겹쳐 있었다

`06-publish.sh` 가 `== 합격 기준 2/3 — npm run serve 기동 응답` 만 찍고
**성공도 실패도 남기지 않은 채** 프롬프트로 돌아왔다. 서버는 처음부터 멀쩡했다.

**(1) 포트를 추측했다.** 스크립트는 `package.json` 의 `serve` 에서 `--port <숫자>` 를
찾는데, 이 프로젝트는 `vite --host 127.0.0.1` 이라 `--port` 가 없다. 그래서 폴백
**8081** 을 60초 내내 두드렸다. 서버는 **5173**(Vite 기본 포트)에 떠 있었다.

**(2) 실패 안내가 실패 때문에 실행되지 못했다.** `set -euo pipefail` 아래에서
정리 단계의 `kill "$SERVE_PID"` 가 0 이 아닌 값을 반환하면 `set -e` 가 그 자리에서
스크립트를 끝낸다 — 바로 다음 줄의 "기동 응답이 없다" 에 도달하지 못한다.

고친 내용:

- **포트는 서버가 로그에 찍은 주소에서 읽는다**(`wait_for_serve`). 추측하지 않는다.
  ANSI 색이 숫자 사이에 끼어 있어도(`:\e[1m5173\e[0m`) 정확히 읽는다.
  로그에서 못 읽으면 5173·8081·8080·3000 을 **확인**한다(추측이 아니라 최후 확인).
- **서버 정리는 `stop_serve` 하나로 모았다.** 모든 단계에 실패 허용이 붙어 있어
  `set -e` 가 안내를 삼키지 않는다. `pkill -P` 도 전부 없앴다(MSYS 위험 형태).
- 적용: `06-publish` · `08-review` · `08a-login-session` · `08b-route-sweep` ·
  `08c-screen-repair` (5곳 전부, 잔존 0)

## 그 앞 판들

- 19차 — 절대경로로 찍힌 차단 파일도 찾아낸다(Windows `path.relative` 백슬래시).
  보수 회차 상한 12 → 30.
- 18차 — 게이트가 예외로 죽으면 `죽은 원인:` 으로 이름·메시지·스택을 찍는다.
- 17차 — 초안 폴더가 사용 중이면 즉시 멈춘다.
- 16차 — `05a` 가 보수 1건 = 커밋 1건으로 쌓는다.

## 막혔을 때 보내 주면 좋은 것

`~/.vue3-draft/` 의 `publish-serve.log` · `route-sweep-serve.log` ·
`repair-build.log` · `ai-repair.md`, 그리고 `git -C "$DRAFT" log --oneline`.
