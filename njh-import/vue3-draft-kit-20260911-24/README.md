# Vue 3 초안 킷 — 24차 (화면 담당자용 Vue 2 잔재 검사기)

```bash
sha256sum -c sha256.txt
7z x -p'<암호>' vue3-draft-kit-20260911-24.7z
source "<24차 킷 경로>/kit.sh"
```

## 새로 들어간 것 — 내 화면에 Vue 2 문법이 뭐가 남았나

```bash
bash "$KIT/run/09-vue2-check.sh" src/views/foo/Bar.vue   # 내 화면 하나
bash "$KIT/run/09-vue2-check.sh" src/views/foo           # 내가 맡은 묶음
bash "$KIT/run/09-vue2-check.sh"                         # 초안 전체
```

**작업 전**에 돌리면 무엇을 고쳐야 하는지, **작업 후**에 돌리면 다 고쳤는지 알 수 있다.
잔재가 없으면 종료코드 0 이라 검증 게이트로도 쓸 수 있다.

출력은 줄·열·원문·처방을 함께 준다.

```
src/views/Pub/login.vue
  14:36   [높음] process.env
          console.info("VUE_APP_MODE --> " + process.env.VUE_APP_MODE);
          → 브라우저에 process 가 없다. import.meta.env 로 바꾸거나 define 으로 주입한다.
```

심각도는 셋이다. **높음** = 런타임에 깨진다 · **중간** = 동작하지만 바꾸는 편이 낫다 ·
**참고** = 눈으로 확인할 것. 기본은 높음·중간이 있으면 실패, `--strict` 면 참고도 실패.

검사 규칙 26종: Vue 전역 API · `new Vue` · `$listeners` · `$children` · `.native` ·
`.sync` · `slot-scope` · `$set`/`$delete` · filters · 필터 파이프 · `beforeDestroy` ·
vuex · `$store.*` · vuex 매퍼 · **`process.env`** · composition-api · 디렉티브 훅 ·
Vue 2 모달 패키지 · `$scopedSlots` · `$parent` · 이벤트 버스 · `render(h)` ·
`functional` · 전환 클래스 · `>>>`/`/deep/`

오탐을 줄이는 장치도 넣었다.
- **주석은 검사하지 않는다.** TODO 주석에 옛 코드를 남긴 파일이 영원히 빨간불이 되면 안 된다.
- **pinia 의 `mapState` 는 잡지 않는다.** vuex 에서 import 했을 때만 잔재로 센다.
- `v-slot=` 과 `{{ a || b }}` 를 옛 문법으로 오인하지 않는다.

## 23차에서 고친 것

**(1) `.env*` 와 `.gitignore` 가 삭제되던 문제.**
드라이버는 `.env*` 를 민감 파일로 보고 초안에 넣지 않는데, 게시 단계의 `--delete` 동기화가
"초안에 없으면 지운다" 로 작동해 워크트리에서 지워 버렸다. 초안이 만든 짧은 `.gitignore` 가
as-is 21줄짜리를 덮어쓴 것도 같은 원인이다.
→ 이 파일들을 **동기화 대상에서 제외**하고, 이미 지워진 저장소는 **출발 태그에서 되살린다**.

**(2) 빌드는 통과하는데 화면이 빈 두 가지.**
Vite 는 `VITE_` 로 시작하는 변수만 노출하고, 브라우저에는 `process` 가 없다.
그래서 `VUE_APP_MODE` 가 `undefined` 가 되어 라우터 가드가 운영 분기로 빠지고
**로그인 경로가 막혔으며**, 소스에 남은 `process.env` 는 ReferenceError 를 냈다.
→ 게시 단계가 `vite.config.mjs` 에 `envPrefix` 와 `process.env` 치환을 **자동으로 넣는다**.
담당자가 손댄 설정은 덮어쓰지 않고 넣을 두 줄만 알려 준다.

**(3) 로그인 라우트를 못 알아보던 문제.**
라우트 스윕이 경로 이름에 `login|sign|auth` 가 있는지로만 판별해서, 경로가 난수 문자열이면
"가드 때문에 못 열림" 을 "화면이 깨짐" 으로 잘못 셌다. → **컴포넌트 파일 경로도 함께 본다.**

## 그 앞 판들

- 23차 — 에이전트 부산물(`.njh/`)이 앱 저장소로 딸려 가던 것. 커밋 전 차단 게이트 신설.
- 22차 — `SERVE_OK 000000` 거짓 합격 차단. 7단계 키 판정을 실제 연결 확인으로.
- 21차 — 포트를 추측하지 않고 서버 로그의 주소에서 읽는다(Vite 기본 5173).
- 19차 — 절대경로로 찍힌 차단 파일도 찾아낸다. 보수 회차 상한 12 → 30.
- 18차 — 게이트가 예외로 죽으면 `죽은 원인:` 을 찍는다.

## 막혔을 때 보내 주면 좋은 것

`~/.vue3-draft/` 의 `draft-facts.md` · `draft-review.md` · `route-sweep.md`,
그리고 `bash "$KIT/run/09-vue2-check.sh" <내 화면> --json` 출력.
