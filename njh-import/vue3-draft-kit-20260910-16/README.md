# Vue 3 초안 킷 — 16차 (초안 커밋이 쌓인다)

```bash
sha256sum -c sha256.txt
7z x -p'<암호>' vue3-draft-kit-20260910-16.7z
source "<16차 킷 경로>/kit.sh"
bash "$KIT/run/05-draft.sh"      # 낡은 초안은 자동으로 옆으로 치우고 새로 만든다
```

## 처음부터 제대로 돌리는 순서

```
05-draft.sh    초안 생성 → 생성 직후 git 기준선 커밋
               (옛 킷으로 만든 초안이 있으면 $DRAFT-stale-<시각> 으로 옮기고 새로 만든다)
07-njh-check.sh  njh-cli 연결 (05a 가 필요하면 먼저)
05a-ai-repair.sh 남은 빌드 차단을 하나씩 → **보수 1건 = 커밋 1건**
06-publish.sh    $V3_BRANCH 에 게시 (build · serve · 부품 착지 3관문)
08 ~ 08c         실측 · 화면 렌더 측정 · 화면 보수
```

## 15차에서 달라진 것 — 보수마다 커밋이 남는다

`05a-ai-repair.sh` 가 초안의 git 저장소를 직접 쓴다. 스냅샷 복사 대신 git 을 쓰므로
**무엇을 언제 왜 고쳤는지가 커밋 이력으로 남는다.**

- 보수 1건 = 커밋 1건: `fix(draft): <파일> 빌드 차단 해소 (njh-cli, +N/-M)`
- 대상 외 파일이 바뀌면 `git checkout` 으로 되돌린다. 새로 생긴 파일은 지우지 않고
  `ai-repair-rejected/` 로 보관한다.
- **기능을 지워 빌드를 통과시킨 수정은 되돌린다**(원본 40% 이상 삭제 + 추가가 그 절반 미만, 또는 5줄 미만).
- 시작할 때 초안에 손댄 내용이 있으면 "보수 전 체크포인트" 로 먼저 커밋해 잃지 않는다.
- 끝나면 쌓인 커밋 수와 확인 명령을 찍는다.

```bash
git -C "$DRAFT" log --oneline          # 무엇을 언제 고쳤는지
git -C "$DRAFT" diff <커밋>^ <커밋>     # 그 보수의 실제 변경
git -C "$DRAFT" checkout -- <파일>      # 마음에 안 들면 되돌리기
```

## 15차 — 낡은 초안 때문에 거부되던 문제

사내 실행이 `DRAFT_REFUSED / DRAFT_REGISTRY_CHANGED` 로 막혀 있었다. 옛 킷으로 만든 초안이
남아 있었고 새 킷은 스테이지 구성이 달라 드라이버가 거부한 것이다. 그런데 `05-draft.sh` 가
그 초안을 치우는 조건을 **영수증의 `skillTip` 에 의존**했고, 그 필드가 없어 조용히 실패했다.

- 이제 5단계가 **직접 도장(`.kit-tip`)** 을 남긴다.
- 그래도 위 세 거부(`REGISTRY_CHANGED`·`SOURCE_CHANGED`·`CONSUMER_DIVERGED`)가 나면
  옛 초안을 `$DRAFT-stale-<시각>` 으로 옮기고 **한 번만 자동 재생성**한다.
- 기준선에 `.gitattributes (* -text)` 를 넣어 Windows 줄바꿈 변환이 기준을 흔들지 않게 했다.

이 문제 때문에 화면 스테이지가 돌지 못했고, **킷이 자동으로 고칠 수 있는 결함들**
(닫히지 않은 `</tr>`, 0바이트 빈 SFC, 짝 없는 CSS 주석 종료자)이 손작업으로 넘어가 있었다.
16차로 다시 돌리면 그것들은 코드모드가 처리한다.

## 손으로 고쳐 둔 초안이 있다면

지우지 않는다 — `$DRAFT-stale-<시각>` 에 그대로 남는다. 새 초안이 자동으로 처리하지 못한
것만 골라 옮기면 된다.

```bash
cp "$DRAFT-stale-"*/src/views/<그 화면>.vue "$DRAFT/src/views/<그 화면>.vue"
```

## 막혔을 때 보내 주면 좋은 것

`~/.vue3-draft/` 의 `repair-build.log` · `ai-repair.md` · `draft-facts.md`,
그리고 `git -C "$DRAFT" log --oneline` 과 `git -C "$DRAFT" diff --stat`.
