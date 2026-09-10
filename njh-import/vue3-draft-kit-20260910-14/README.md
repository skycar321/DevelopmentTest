# Vue 3 초안 킷 — 14차

**초안($DRAFT)이 git 으로 추적된다.** 5단계가 생성 직후를 커밋해 기준선으로 남긴다.

```bash
sha256sum -c sha256.txt
7z x -p'<암호>' vue3-draft-kit-20260910-14.7z
source "<14차 킷 경로>/kit.sh"      # 설정·상태는 ~/.vue3-draft, 옮길 것 없음
bash "$KIT/run/00b-resume.sh"
```

## 13차에서 달라진 것

초안은 드라이버 산출물이라 지금까지 git 저장소가 아니었다. 그런데 **보수 작업이 초안 안에서**
일어난다(5a·상호작용·손편집). 기준선이 없으면 무엇이 어떻게 바뀌었는지 볼 수 없고,
**AI 가 기능을 지웠는지 확인할 수단이 사용자에게 없다.** VS Code 도 프로젝트로 인식하지 못했다.

- `run/05-draft.sh` 가 초안 생성 직후 `git init` + 기준선 커밋을 남긴다(`node_modules/`·`dist/` 제외).
- 이후 변경은 그대로 보인다:
  ```bash
  git -C "$DRAFT" status              # 바뀐 파일
  git -C "$DRAFT" diff                # 무엇이 어떻게
  git -C "$DRAFT" checkout -- <파일>  # 되돌리기
  ```
- 이 저장소는 초안 안에만 있다. 6단계는 `--exclude .git` 로 복사하므로 `$V3` 로 따라가지 않는다.
- `run/00b-resume.sh` 가 "초안 보수: N개 파일 변경됨 / 생성 시점 그대로" 를 함께 보여 준다.

## 이미 13차로 초안을 만들어 둔 경우

킷을 새로 받기 전에도 지금 바로 기준선을 만들 수 있다(아직 손대지 않았다면):

```bash
cd "$DRAFT"
printf 'node_modules/\ndist/\n' > .gitignore
git init -q && git add -A && git commit -q -m "draft baseline"
```

## 그 앞 판들

- 13차 — `05a`·`08c`·`08-review` 가 DevAI 키 위치를 추측하지 않고 **연결이 실제로 대답하는지** 확인한다
  (키는 환경변수여도 되고 `~/.njh-cli/settings.json` 의 `apiKey` 여도 된다).
- 11차 — Windows 경로 형태(`D:/…`·`/d/…`·`file:///D:/…`)를 못 읽어 차단 파일을 못 찾던 문제,
  `DRAFT_REFUSED` 사유가 화면에 안 나오던 문제 수정.
- 10차 — 설정·진행 상태를 킷 밖 `~/.vue3-draft/` 로 분리. 킷은 갈아 끼우기만 하면 된다.
- 그 이전 — 초안 빌드 통과(배선 12종 + 프로파일 계약 테스트), 공통 부품 전량 착지,
  게시 3관문(build·serve·부품 착지), 화면 렌더 실측 3단계(`08a`~`08c`).

## 막혔을 때 보내 주면 좋은 것

`~/.vue3-draft/` 의 `repair-build.log` · `ai-repair.md` · `draft-facts.md` ·
`route-sweep.md` · `화면-담당자-배정.md`, 그리고 `git -C "$DRAFT" diff` 출력.
로그인 세션은 보내지 않는다.










1. 잃지 않게 커밋만 해 두십시오. 최종 기준선으로 삼자는 게 아니라 보험입니다.

cd "$DRAFT"
printf 'node_modules/\ndist/\n' > .gitignore
git init -q && git add -A && git commit -q -m "checkpoint: AI 수동 보수 (재생성 시 버릴 것)"

2. 거부 사유 한 줄 — 이게 없으면 재실행이 도박입니다.

node -e 'const d=JSON.parse(require("fs").readFileSync(process.argv[1],"utf8"));console.log(d.status,"|",d.failure,"|",JSON.stringify(d.plan||null).slice(0,300))' "$STATE/draft-stdout.json"
