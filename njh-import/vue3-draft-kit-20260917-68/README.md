# Vue 3 draft kit — 2026-09-17 (68th)

This directory holds one encrypted archive (`vue3-draft-kit-20260917-68.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260917-68.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- **22단계 새 분류 `parent` — 팝업의 `this.$parent.메서드()`**: 이전 레이아웃 라이브러리에서는 컨테이너·행·열 부품이 인스턴스를 만들지 않아 `$parent` 가 화면이었는데, 새 버전에서는 부품이 인스턴스라 `$parent` 가 그 부품이 된다 → 팝업 저장 뒤 목록 새로고침·닫기가 "is not a function" 으로 멈춘다(실브라우저로 재현). 호환 부품(`src/compat/vue2-parent.js`)이 라이브러리 인스턴스를 건너뛴 첫 앱 컴포넌트를 돌려주고, 거기에 메서드가 없으면 더 올라가지 않는다(원래처럼 실패). 팝업을 쓰는 화면에 그 메서드가 정의돼 있을 때만 바꾸고, `$parent.$parent…` 사슬은 줄 번호와 함께 사람 확인으로 남긴다.
- **22단계 새 분류 `style`**: 스코프 스타일의 `>>>`·`/deep/` → `:deep()`, 전환 클래스 `-enter`·`-leave` → `-enter-from`·`-leave-from`. 전환 전후 스타일 컴파일 결과가 같을 때만 쓴다.
- **20단계**: 부모가 v-model 과 같은 식의 `:value` 를 겹쳐 쓴 팝업은 사람 확인으로 남던 것을, 부모의 겹친 `:value` 를 지우고(부모 템플릿 컴파일 확인) 자식을 전환한다.
- **CRLF 경고 제거**: 킷의 모든 `git add` 에 `-c core.safecrlf=false` — 작업 트리가 LF 이고 autocrlf 일 때 파일마다 나던 경고로 출력이 덮였다(저장 내용은 같다).
- **22단계 vuetify**: 1~12 밖 격자 속성 하나로 코드모드가 파일 전체를 거부하던 것을, 그 속성만 가렸다가 되돌려 변환한다. 거부 사유를 코드·속성·줄로 남긴다(예전엔 "exit 2" 뿐).
- **16단계 lint**: 내용이 없는 .vue(원래 빈 파일)는 이름만 알리고 lint 에서 뺀다.
- **09·00b**: 처방 문구(자동 전환 명령·사슬·스토어 액션) 정정, 진행 표 22 행이 새 분류를 세고 사람 확인은 따로 센다.
- 게이트: 호환 부품 실브라우저 테스트(그대로면 실패·바꾸면 화면 메서드·더 올라가지 않음), 실험 트리에서 22 전 분류 두 번(두 번째 무변경), 잔재가 사람 확인 목록 밖에 남지 않음, 20 겹친 `:value`, CRLF 경고 없음, 스킬 거부 0, 16 빈 파일, 00b 사람 확인 행.
- 67차까지의 변경 포함.
