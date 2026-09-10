# Vue 3 초안 킷 — 13차

12차의 **DevAI 키 검사가 잘못 막던 것**을 고쳤다.

```bash
sha256sum -c sha256.txt
7z x -p'<암호>' vue3-draft-kit-20260910-13.7z
source "<13차 킷 경로>/kit.sh"      # 설정·상태는 ~/.vue3-draft, 옮길 것 없음
bash "$KIT/run/00b-resume.sh"
```

## 12차에서 고친 것

12차의 `05a`·`08c` 는 환경변수 `NJH_OPENAI_CHAT_KEY` 가 없으면 멈췄다. 그런데 키는
**`~/.njh-cli/settings.json` 의 `apiKey`** 에 두어도 정상 동작한다 — 그 경우 연결이 멀쩡한데도
막혔다. **키 위치를 추측하지 않고, njh-cli 가 실제로 대답하는지 한 번 물어보도록** 바꿨다.

- `run/05a-ai-repair.sh` · `run/08c-screen-repair.sh` — 시작 시 연결을 한 번 확인한다.
  답하면 진행, 안 하면 7단계를 안내하고 멈춘다(회차를 태우지 않는다).
- `run/08-review.sh` — 같은 이유로 njh 검토를 건너뛰던 것을 고쳤다.
- `run/00b-resume.sh` — "DevAI 키 ★ 없음" 이 결함처럼 보이던 표시를 정정했다.
  (읽기 전용 단계라 모델을 부르지 않는다. 실제 확인은 7단계가 한다.)
- 런북 — 키는 환경변수를 권하되 개인 단말의 `~/.njh-cli/settings.json` 의 `apiKey` 도 허용.
  **리포에 커밋되는 `.njh/settings.json` 에는 넣지 않는다**(이 규칙은 그대로).

## 빌드가 막혔을 때 순서

```
05-draft.sh          초안 생성
07-njh-check.sh      njh-cli 연결          ← 5a 는 여기에 의존한다. 앞당겨서 한다
05a-ai-repair.sh     차단 파일을 하나씩 보수 (기능 삭제는 스크립트가 되돌린다)
06-publish.sh        커밋 (build · serve · 부품 착지 3관문)
08 ~ 08c             실측 · 화면 렌더 측정 · 화면 보수
```

상호작용으로 직접 고쳐도 된다: `source kit.sh` 뒤 `cd "$DRAFT" && njh`.
그 경우 스크립트 가드레일이 없으므로 **기능을 지워 빌드만 통과시키지 않았는지 직접 확인**한다.

## 막혔을 때 보내 주면 좋은 것

`~/.vue3-draft/` 의 `repair-build.log` · `ai-repair.md` · `draft-facts.md` ·
`route-sweep.md` · `화면-담당자-배정.md`. 로그인 세션은 보내지 않는다.
