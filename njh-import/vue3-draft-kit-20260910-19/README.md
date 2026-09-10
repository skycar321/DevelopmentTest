# Vue 3 초안 킷 — 19차 (절대경로로 찍힌 차단 파일도 찾아낸다)

```bash
sha256sum -c sha256.txt
7z x -p'<암호>' vue3-draft-kit-20260910-19.7z
source "<19차 킷 경로>/kit.sh"
bash "$KIT/run/05a-ai-repair.sh"
```

## 18차에서 고친 것 — 5a 가 9회차에서 멈추던 원인

빌드 로그에 차단 파일이 **분명히 적혀 있는데도** "차단 파일 특정 실패" 로 멈췄다.

```
Rolldown failed to resolve import "vuex" from "D:/…/src/App.vue"
   → 차단 파일 특정 실패
```

- Windows 의 `path.relative` 는 **백슬래시로** 돌려준다(`src\App.vue`). 경로 구분자를
  상대화 **앞에서만** 통일하고 뒤에서 안 해서, 그 다음 검사가 전부 빗나갔다.
- 코드프레임 형태(`╭─[ src/main.js:1:8 ]`)는 이미 상대경로라 이 경로를 타지 않는다.
  그래서 8회차까지는 멀쩡했고 9회차에서만 터졌다.
- 회귀 테스트 4건 추가(`tools/build-blocker.test.mjs`). Windows·POSIX 표기를 모두 검사한다.

## 보수 회차 상한을 30 으로 올렸다

12 는 추측이었다. 사내 실측에서 `import Vue from 'vue'` 한 종류만으로 8회차를 썼다
(파일 11개, 스텁 되돌림 재시도 포함). 더 필요하면 그대로 올려서 쓴다:

```bash
AI_REPAIR_MAX=60 bash run/05a-ai-repair.sh
```

이미 쌓인 커밋은 유지되고, 빌드가 막힌 지점부터 다시 간다.

## 그 앞 판들

- 18차 — 게이트가 예외로 죽으면 그 예외의 이름·메시지·스택을 `죽은 원인:` 으로 찍는다.
  빌드 로그가 아예 없으면 "빌드에 도달하기 전에 멈췄다" 고 정확히 말한다.
- 17차 — 초안 폴더가 사용 중이면 옮기다 실패한 채 진행하지 않고 즉시 멈춘다.
- 16차 — `05a` 가 초안 git 저장소에 보수 1건 = 커밋 1건으로 쌓는다.
- 15차 — 낡은 초안 자동 정리(`.kit-tip` 도장 + 세 거부 사유 자동 재생성).
- 14차 — 초안 생성 직후 git 기준선 커밋.

## 막혔을 때 보내 주면 좋은 것

`~/.vue3-draft/` 의 `repair-build.log` · `ai-repair.md`,
그리고 `git -C "$DRAFT" log --oneline` 과 `git -C "$DRAFT" diff --stat`.
