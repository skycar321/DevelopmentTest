# Vue 3 draft kit — 2026-09-24 (112th)

This directory holds one encrypted archive (`vue3-draft-kit-20260924-112.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260924-112.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- 111차는 배송 전 리허설(90 블록 네이티브 버튼 복원 시험 4건)에서 멈춰 업로드하지 않았다 — 새 달력 부품의 V2 리셋 `:where(.app-date-picker-v2) button { border-style: none }` 을 앱 버튼 테두리의 경쟁 소유자로 봤다(검출기는 조상을 보지 않는 보수 판정). 112차는 테두리 없애기를 부품 클래스(.v-btn·머리 값 버튼)로 옮기고 111 내용을 함께 싣는다
- 112차 화면 비교를 누른 상태(포커스)·체크한 상태까지 넓힘: 입력칸이 많은 화면에서 입력칸을 차례로 눌러 as-is 와 비교 — 체크박스 1.43% → 0.00%, 라디오 1.05% → 0.06%, 선택칸 0.14% → 0.02%, 날짜 칸 5.76% → 1.01%(남은 것은 떠 있는 라벨 scale 과 글꼴 크기 래스터), 친 글자 가장 어두운 화소 62 → 33(as-is 33), 정적 68개 화면 14개 개선·나빠진 화면 0
- css-canon: V2 흐림은 색의 알파뿐이다 — V2 입력 글자 색(.87)을 옮기면 V4 .v-field__input 강조 불투명도(.87)가 한 번 더 곱해졌다 → 1(밝은 테마만). 색 클래스가 붙은 입력칸 아이콘(V2 validationState 아이콘)도 1(자리는 설치본의 V4 규칙에서). 옮긴 V2 라벨 색은 :where(:not(.v-field--focused *, .v-field--error *)) 로 포커스·오류 칸을 비킨다(V2 는 그 상태에 primary--text·error--text 가 덮었다). V2 떠 있는 라벨 자리·줄 높이 → V4 underlined 떠 있는 라벨, V2 밑줄 자리(슬롯 아래 1px) → V4 underlined·filled 윤곽 가상 요소 top 1px
- 22 theme: V2 입력 부품은 color 를 안 주면 포커스·선택 때 primary(validatable·selectable computedColor — as-is 설치본에서 읽음). 킷 defaults 한 줄에 입력칸 계열 color(+ V4 VInput 에 있으면 iconColor), 체크박스·라디오·스위치 color. 표지 KIT112_V2_INPUT_COLOR, 손으로 둔 항목은 그대로, 설치본에서 못 읽으면 사람 확인(조용히 넘기지 않는다). V2 mdi 아이콘 별칭이 V4 와 다른 키만 V2 값으로(clear mdi-close — 입력칸 지우기 × 가 동그라미 × 였다, warning·error) — createVuetify 바로 아래 icons.aliases, 표지 KIT112_V2_ICON_ALIASES, as-is 가 mdi 가 아니거나 설정에 icons 가 있으면 두지 않는다
- 22 css: 앱이 V2 색 도우미(.X--text·.X)에 준 규칙에 V4 짝(.text-X·.bg-X) 선택자를 더한다(옛 선택자 유지, 테마 색 이름만, scss 그대로, 멱등) — 앱의 primary 색 덮기가 V4 포커스·선택 색 클래스에도 걸린다
- 111 내용(날짜 선택 달력): 22 common AppDatePicker 새 판 — Vuetify 2 v-date-picker 와 같은 DOM·클래스·치수(:where 로 V2 명시도·적재 순서), 메뉴 자리는 V2 menuable 계산(뒤집지 않고 창 안으로·칸 왼쪽 + 40), 이전 판 kit99 픽스처·공급자 묶음. 22 css·09 잔재는 킷 V2 DOM 부품의 클래스 무리를 살아 있다고 보고, 옛 22 css 가 V4 이름으로 바꾼 달력 규칙은 as-is 원본에 V2 이름이 있을 때만 되돌림. css-canon 탭 제외 가드 명시도 0(:where(:not(.v-tab)))
- 테스트: css-canon 59(입력 불투명도·라벨 상태 가드·떠 있는 라벨·밑줄), vuetify2-theme 19(입력 부품 기본 색·실행 증명·아이콘 별칭), vuetify2-css 13(색 도우미 짝 단위·끝-대-끝), 실브라우저 R5/R6(달력) — 전부 revert-red 확인. 게이트 112 블록(자산의 맨 button 테두리 규칙 없음·킷 표지·리허설 트리 parity 다섯 규칙·content.css 짝·설치본 없으면 사람 확인·픽스처 뒤 22 theme 커밋·사람 확인 0·두 표지·멱등·테스트)
