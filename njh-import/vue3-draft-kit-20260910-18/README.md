# Vue 3 초안 킷 — 18차 (게이트가 죽은 원인을 이제 말해 준다)

```bash
sha256sum -c sha256.txt
7z x -p'<암호>' vue3-draft-kit-20260910-18.7z
source "<18차 킷 경로>/kit.sh"
bash "$KIT/run/05-draft.sh"
```

## 17차에서 고친 것

17차는 `DRAFT_BUILD_GATE_FAILED` 만 찍고 **왜 죽었는지는 한 줄도 안 남겼다**.
빌드 로그조차 없어서 차단 파일도 못 찾고, 화면에 남는 건 코드 하나뿐이었다.

- `*_GATE_FAILED` · `*_STAGE_FAILED` 는 "빌드 실패" 가 아니라 **게이트가 예외로 죽었다** 는 뜻이다.
  이제 그 예외의 이름·메시지·스택(5줄)을 `죽은 원인:` 줄로 찍는다.
- 빌드 로그가 아예 없으면 "차단 파일을 못 찾았다" 대신
  **"빌드에 도달하기 전에 멈췄다"** 고 정확히 말한다.
- 영수증(`result.json` · 표준출력 JSON)에도 `failureDetail` 로 남으므로 나중에 다시 읽을 수 있다.

## 진단 빌드가 실패하면

`05-draft.sh` 가 멈추는 것은 **정상 동작**이다(6단계는 빌드가 통과해야 게시한다).
그대로 다음을 돌린다:

```bash
bash run/05a-ai-repair.sh          # 회차 상한 12
AI_REPAIR_MAX=40 bash run/05a-ai-repair.sh   # 고칠 파일이 12개를 넘을 때
```

05a 는 초안에서 `npm run build` 를 직접 돌리므로 5단계가 차단 파일을 못 찾았어도 영향이 없다.
보수 1건 = 커밋 1건으로 쌓이고, 대상 외 변경과 기능 삭제는 자동으로 되돌린다.

## 그 앞 판들

- 17차 — 초안 폴더가 사용 중이면 옮기다 실패한 채 진행하지 않고 즉시 멈춘다.
- 16차 — `05a` 가 초안 git 저장소에 보수 1건 = 커밋 1건으로 쌓는다.
- 15차 — 낡은 초안 자동 정리(`.kit-tip` 도장 + 세 거부 사유 자동 재생성).
- 14차 — 초안 생성 직후 git 기준선 커밋.
- 13차 — njh 연결을 키 위치로 추측하지 않고 실제로 물어본다.

## 막혔을 때 보내 주면 좋은 것

`~/.vue3-draft/` 의 `repair-build.log` · `ai-repair.md` · `draft-facts.md`,
그리고 `git -C "$DRAFT" log --oneline` 과 `git -C "$DRAFT" diff --stat`.







AI_REPAIR_MAX=30 bash run/05a-ai-repair.sh

영수증은 제가 경로를 잘못 알려 드렸습니다

생성됨: undefined / 포매터: undefined 는 그 파일에 제가 말한 필드가 없다는 뜻입니다 — 스테이지 영수증의 껍데기 모양이 제가 가정한 것과 다릅니다. 실제 모양을 보는 명령으로 바꿉니다:

node -e 'const d=JSON.parse(require("fs").readFileSync(process.argv[1],"utf8"));
console.log("키:",Object.keys(d).join(", "));
console.log(JSON.stringify(d).slice(0,1200));' \
  "$DRAFT/.tmp/draft-project/stages/generic-native-bootstrap/receipt.json"

★ 인사이트 ─────────────────────────────────────
거부: [] 와 사유: (없음) 이 빈 배열로 나온 것은 의미가 있습니다. 코드모드가 "이 파일들은 내가 못 하겠다"고 거부한 게 아니라, 아예 후보로 잡지도 않았다는 뜻일 가능성이 큽니다. 거부는 기록이 남지만, 후보에서 빠진 건 아무 기록도 안 남습니다 — 침묵이 가장 나쁜 실패 모드입니다. 위 명령의 출력이 그걸 갈라 줍니다.
─────────────────────────────────────────────────

급하지 않습니다. 05a 가 끝난 뒤에 찍어 주셔도 됩니다. 지금은 05a 를 끝까지 두시는 게 우선입니다.

끝나면 이것도 같이 보내 주시면 다음 킷 코드모드를 정확히 만들 수 있습니다:

git -C "$DRAFT" log --oneline
git -C "$DRAFT" show --stat HEAD
