# Vue 3 초안 킷 — 23차 (에이전트 부산물이 앱 저장소로 딸려 가던 것)

```bash
sha256sum -c sha256.txt
7z x -p'<암호>' vue3-draft-kit-20260911-23.7z
source "<23차 킷 경로>/kit.sh"
```

## 22차에서 고친 것

초안에서 njh-cli(5a·상호작용)를 돌리므로 그 부산물이 초안 안에 쌓인다.
그런데 `06-publish.sh` 의 복사 제외 목록에 그것들이 빠져 있어,
**`.njh/`(텔레메트리·미니리포트·검색인덱스)가 앱 저장소에 그대로 커밋·push 됐다.**

소스가 이미 있는 같은 저장소라 유출은 아니지만, 앱 저장소에 에이전트 작업
부스러기가 섞이는 것은 위생 문제다. 세 곳을 고쳤다:

1. **복사에서 제외** — `sync-tree` 에 `--exclude .njh --exclude .tmp
   --exclude ai-repair-rejected` 추가.
2. **초안 이력에도 안 남긴다** — `05-draft`·`05a` 가 만드는 `.gitignore` 에
   `.njh/` · `.tmp/` · `ai-repair-rejected/` 를 넣는다.
3. **커밋 전에 막는다** — 새 "출하 위생 검사" 단계가 워크트리에서 이것들을
   찾으면 **게시를 중단**한다. 세기만 하고 통과시키면 검사가 아니라 장식이다.

## 이미 커밋된 저장소를 쓰고 있다면

```bash
git -C "$V3" rm -r --cached .njh .tmp ai-repair-rejected 2>/dev/null
printf '.njh/\n.tmp/\nai-repair-rejected/\n.npmrc\n' >> "$V3/.gitignore"
git -C "$V3" add .gitignore
git -C "$V3" commit -m "chore: 에이전트 작업 부스러기 추적 제외"
git -C "$V3" push
```

이력 재작성은 권하지 않는다 — 이미 push 된 공유 브랜치를 깨뜨리는 대가에 비해
얻는 것이 "부스러기 제거" 뿐이다. 텔레메트리는 비밀값을 자동 마스킹한다.

## `.npmrc` 는 as-is 에서 상속된 것이다

`06-publish` 는 복사에서 `.npmrc` 를 빼지만, `$V3` 는 as-is 저장소의 워크트리라
**출발 태그 시점 이력에 이미 들어 있다.** 킷이 만든 것이 아니다.
남겨 둘 이유가 없으면 위와 같이 추적에서 빼면 된다.

## 그 앞 판들

- 22차 — `SERVE_OK 000000` 거짓 합격 차단(`http_code` 는 세 자리만).
  7단계가 키를 환경변수에서만 찾던 것 → 실제로 물어보고 판정.
- 21차 — 포트를 추측하지 않고 서버 로그의 주소에서 읽는다(Vite 기본 5173).
- 19차 — 절대경로로 찍힌 차단 파일도 찾아낸다. 보수 회차 상한 12 → 30.
- 18차 — 게이트가 예외로 죽으면 `죽은 원인:` 을 찍는다.

## 막혔을 때 보내 주면 좋은 것

`~/.vue3-draft/` 의 `draft-facts.md` · `draft-review.md` ·
`publish-serve.log` · `route-sweep-serve.log`.
