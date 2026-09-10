# Vue 3 초안 킷 — 12차

11차 + **05a·08c 가 DevAI 키를 먼저 확인한다.**

```bash
sha256sum -c sha256.txt
7z x -p'<암호>' vue3-draft-kit-20260910-12.7z
source "<12차 킷 경로>/kit.sh"      # 설정·상태는 ~/.vue3-draft, 옮길 것 없음
bash "$KIT/run/00b-resume.sh"
```

## 11차에서 고친 것

- `run/05a-ai-repair.sh` 와 `run/08c-screen-repair.sh` 가 **DevAI 키(`NJH_OPENAI_CHAT_KEY`)를
  먼저 확인하고, 없으면 회차를 태우기 전에 멈춘다.** 이전에는 키 없이 돌면 njh 호출이 매번
  실패해 "수정 없음" 만 12줄 쌓였다.
- **5a 는 번호가 6단계 앞이지만 7단계(njh-cli 연결)에 의존한다.** 빌드가 막혔다면 7단계를
  먼저 끝내고 5a 로 돌아온다. 런북에 명시했다.

## 순서 (빌드가 막혔을 때)

```
05-draft.sh          초안 생성 → DRAFT_REFUSED / DRAFT_BUILD_FAILED 면
07-njh-check.sh      njh-cli 연결 (키를 환경변수에 넣는다)  ← 앞당겨서 한다
05a-ai-repair.sh     차단 파일을 하나씩 보수 → 빌드 통과할 때까지
06-publish.sh        커밋 (build · serve · 부품 착지 3관문)
08 ~ 08c             실측 · 화면 렌더 측정 · 화면 보수
```

## 그 앞 판들

10차부터 설정·진행 상태는 킷 밖 **`~/.vue3-draft/`** 에 있다. 킷은 갈아 끼우기만 하면 되고
`kit.sh` 를 source 하면 같은 설정으로 이어진다. 11차는 Windows 경로 형태를 못 읽어
차단 파일을 못 찾던 문제와, `DRAFT_REFUSED` 사유가 화면에 안 나오던 문제를 고쳤다.

## 막혔을 때 보내 주면 좋은 것

`~/.vue3-draft/` 의 `repair-build.log` · `ai-repair.md` · `draft-facts.md` ·
`route-sweep.md` · `화면-담당자-배정.md`. 로그인 세션은 보내지 않는다.
