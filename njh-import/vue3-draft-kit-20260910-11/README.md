# Vue 3 초안 킷 — 11차 (현장 실행 결함 수정)

암호 7z(AES-256, 헤더 암호화). 암호는 njh-cli 킷과 같고 별도 채널로 전달된다.

```bash
sha256sum -c sha256.txt
7z x -p'<암호>' vue3-draft-kit-20260910-11.7z
```

## 10차를 쓰던 중이라면 — 그대로 이어서 한다

설정·진행 상태는 `~/.vue3-draft/` 에 있으므로 **옮길 것이 없다.** 새 킷을 풀고:

```bash
source "<11차 킷 경로>/kit.sh"
bash "$KIT/run/00b-resume.sh"
```

## 10차 실행에서 나온 결함 3건을 고쳤다

**1. 빌드가 막혔는데 차단 파일을 못 찾았다** (`★ 차단 파일 특정 실패`)
Windows 에서 오류 경로가 `D:/…`·`/d/…`·`file:///D:/…` 로 제각각인데 그 형태를 못 읽었다.
`[plugin vite:vue] <경로>:<줄>:<칸>` 형태와 Windows 절대경로를 읽도록 고쳤다.
→ **`run/05a-ai-repair.sh` 가 이제 차단 파일을 찾아 njh-cli 로 하나씩 보수한다.**

**2. 초안이 거부됐는데 이유가 화면에 안 나왔다** (`상태: DRAFT_REFUSED` 뒤에 아무 설명 없음)
드라이버는 거부 사유를 `failure` 에 담는데 스크립트가 그 항목을 안 찍고 있었다. 이제
거부 사유·계획 거부·차단 파일과 그 앞뒤 로그를 함께 보여 준다.

**3. `00b-resume.sh` 가 `vue ? · vuetify ?` 로 나왔다**
`node -p "require('<경로>')"` 는 Git Bash 에서 경로가 문자열 안에 있어 변환되지 않는다.
경로를 인자로 넘기도록 고쳤다.

**4. 부모 화면이 함께 바뀌어야 하는 경우를 보고한다**
prop 에 `v-model` 을 쓰던 화면을 Vue 3 형태로 고치면 부모가 `v-model:<prop>` 으로 받아야 한다.
njh-cli 는 대상 파일만 고치고, 부모가 할 일을 `ai-repair.md` 에 남긴다(부모 파일은 건드리지 않는다).

## 막혔을 때 보내 주면 좋은 것

`~/.vue3-draft/` 의 `repair-build.log` · `ai-repair.md` · `draft-facts.md` ·
`route-sweep.md` · `화면-담당자-배정.md`. 로그인 세션(`.route-session.json`)은 보내지 않는다.

## 킷 구조 (10차부터)

설정·진행 상태는 킷 밖 **`~/.vue3-draft/`**. 킷은 갈아 끼우는 물건이라 `kit.sh` 만 source 하면
같은 설정·같은 진행 상태로 이어진다. `docs/뷰3-초안-사내-런북.md`(같은 내용의 `.html`)가 절차 정본이다.
