# Vue 3 draft kit — 2026-09-18 (74th)

This directory holds one encrypted archive (`vue3-draft-kit-20260918-74.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260918-74.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- **22단계가 시작조차 못 하던 것을 고쳤다.** 73차는 킷 폴더에 설치본(`node_modules`)을 이어 두고, **못 이으면 멈췄다**. 사내 Git Bash 가 `MSYSTEM=MSYS` 로 떠 있었고 그 환경에서는 Windows 경로를 얻는 `pwd -W` 가 POSIX 경로(`/d/…`)를 그대로 돌려준다. 73차는 그 결과를 검사하지 않고 뒤에 `\node_modules` 를 붙여 정션 만들기 명령에 넘겼으므로 정션도 심링크도 만들어지지 않았고, 화면에 찍힌 안내문도 `-Path '/d/…\node_modules'` 라는 깨진 값이었다(파일 잠김 때문이 아니다 — 그때 node 프로세스는 하나도 없었다).
- **이제 그 연결은 "있으면 좋은 것" 이다.** 스킬 스크립트는 라이브러리를 이름으로 가져오고 Node 는 그 이름을 **가져오는 파일 자리에서 위로** 찾기 때문에(현재 폴더·`NODE_PATH` 는 보지 않는다) 연결이 필요했다. 74차는 스킬을 실행기(`tools/skill-run.mjs`)로 띄워 **이름 해석이 실패하면 대상 프로젝트 자리에서 한 번 더 해석**한다 — 경로를 손으로 짓지 않고 Node 기본 해석기에 부모 자리만 바꿔 넘기므로 `exports`·`import/require` 조건이 그대로 지켜진다. 05·11·12·18·22 가 모두 이 실행기를 쓰고, **어느 단계도 연결 실패로 멈추지 않는다.** 연결을 아예 시도하지 않게 하려면 `KIT_LINK_DISABLE=1`.
- **Windows 경로 변환은 한 곳에서만 한다.** `run/_win-path.sh` 의 `win_path`(명령 인자용 `D:\x`)·`win_path_m`(설정 값용 `D:/x`) 가 변환 도구 → `pwd -W` → 손수 변환을 차례로 쓰고 **결과가 드라이브 문자로 시작하는지 반드시 검사한다.** 못 바꾸면 `MSYSTEM` 과 원래 값을 찍고 Windows 전용 처리를 **건너뛴다** — 깨진 값을 명령으로 넘기지 않는다. 슬래시를 역슬래시로 바꾸는 치환을 이 파일 밖에서 하는 것은 게이트가 막는다.
- **남은 복사본을 못 지워도 계속 간다.** 73차의 `ln -s` 가 링크가 아니라 복사본을 남겼으면 지우고 다시 잇되, 무언가 붙잡고 있어 못 지우면 **붙잡은 프로세스를 찾는 명령과 지우는 명령(올바른 Windows 경로로)을 찍고 연결 없이 계속한다.** 손으로 지울 필요는 없다.
- **MSYS·MINGW 둘 다 받는지 훑었다.** Windows 분기가 `MINGW*` 만 받던 자리가 없는지(사내가 쓴 것은 `MSYS*`), 프로세스 종료·포트 조회 명령이 두 환경에서 같은지 확인했다 — 단계 스크립트는 모두 두 모양을 함께 받고, 게이트가 이 규칙을 계속 검사한다.
- **단계 동작은 바꾸지 않았다.** 73차에서 하던 자리에서 그대로 이어서 하면 된다(README 의 "73차를 적용한 트리에서 74차로" 절).
- 게이트: **리허설 전체를 연결 없는 조건으로** 돌려 05·초안·22 가 끝까지 가는 것과 스킬 코드모드가 파일마다 도는 것을 확인하고, 실행기를 안 거치면 실제로 실패하는 것·거치면 도는 것을 함께 잰다. 연결이 **되는** 경우도 22 재실행으로 따로 본다(한 분류만 다시 커밋). 경로 변환·연결·실행기 단위 테스트 27건.
- **남은 제약**: 실제 Windows 실기 확인은 아직 못 했다(그쪽 장비가 꺼져 있다). 사내에서 22 를 돌린 화면 출력(또는 `~/.vue3-draft/logs/22-vue3-api-compat.log`)을 보내 주면 바로 확인한다.
- 73차까지의 변경 포함.
