# Vue 3 draft kit — 2026-09-19 (75th)

This directory holds one encrypted archive (`vue3-draft-kit-20260919-75.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260919-75.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- **조사 결과를 사진으로 내보내는 새 단계 `run/23-report.sh`.** 사내에서는 보고서 파일을 밖으로 못 꺼내고 화면 사진만 나온다. 그런데 사진에 담기던 건 요약 숫자뿐이라(`DYNAMIC_DISPATCH_REVIEW 108` 같은 줄) 그 숫자만으로는 "규칙을 넓히면 풀리는가" 를 정할 수 없었고, 한 가지 확인에 한 차수씩 썼다. 이 단계는 `~/.vue3-draft` 의 보고서들에서 **판단에 필요한 것만 골라** 절마다 한 화면씩 찍는다 — 한 줄은 **140칸 이내**(한글은 두 칸으로 센다), 한 절은 45줄 이내, 긴 경로는 파일 이름만. 핵심은 **[1/5] 엑셀 축이 자동 전환을 거부한 사유마다 대표 소스 한 줄**(파일:줄 + 코드)을 붙이는 것이다 — 15단계가 그 자리를 기록하도록 함께 고쳤다(전에는 `reviewByCode` 건수만 남았다). 아직 안 돌린 단계는 그 절에 "아직 안 돌렸다: <명령>" 한 줄만 찍고 **계속 간다**. 같은 내용이 `~/.vue3-draft/보고-<날짜>.txt` 에도 남아 사진이 흐리면 `less -S` 로 다시 띄워 찍으면 된다.
- **22단계 `css` — 구조가 바뀐 선택자도 목표가 하나면 바꾼다.** 73·74차는 DOM 이 바뀐 것을 전부 "사람 확인" 으로만 남겼고 `public/css/content.css` 에 12건이 그대로 남아 있었다. 이 선택자들은 **지금 어느 요소에도 안 맞는다** — 스타일이 통째로 빠진 상태다. 그래서 자동 전환의 위험은 "틀린 스타일이 붙는다" 가 아니라 "덜 맞는 자리에 붙는다" 이고, 아무것도 안 붙는 지금보다 as-is 에 가깝다. 바꾸는 것: 같은 자리 이름(`.v-input__slot`→`.v-field`, `.v-tabs-bar`→`.v-slide-group`), 깊이가 달라진 것은 자식 결합자(`>`)를 자손으로 풀고(`A > B` 는 `A B` 의 부분집합이라 맞던 자리는 계속 맞는다), 앞·뒤 버튼이 갈라진 페이지 번호는 선택자를 둘로 늘리고, 입력칸 전환이 일어난 선택자 안의 `<fieldset>` 은 `.v-field__outline` 으로. 목표 이름은 **설치된 Vuetify 에 실제로 있을 때만** 쓴다. 한 요소 선택자에 입력 루트와 `.v-field` 가 섞이는 것처럼 두 층으로 쪼개야 하는 것은 그대로 사람 확인이다(사내 12건 중 11건 자동·1건 확인).
- **22단계 `aggrid` — 폐기된 그리드 setter 를 `setGridOption` 으로.** 새 판의 폐기 래퍼는 **그대로 `setGridOption` 을 부르고 경고만 한 줄 더 찍는다**(설치본 `dist` 로 확인: `setPinnedBottomRowData(rows){ this.deprecatedUpdateGridOption("pinnedBottomRowData", rows) }`). 동작은 같고 콘솔만 더러워져 진짜 오류가 경고에 묻힌다. 대응표 85종은 손으로 적지 않고 **설치본에서 뽑았고**(`tools/ag-grid-setters.json`) 테스트가 설치본으로 다시 맞춰 본다 — 라이브러리 판이 올라가 대응이 달라지면 거기서 먼저 터진다. 수신자가 `.api` 이고 최상위 인자가 정확히 하나일 때만 바꾼다(사내 as-is 320곳이 전부 이 한 모양이고 `.api` 아닌 수신자는 0곳이다). 인자를 `!value` 로 뒤집어 넘기는 `setExcludeHiddenColumnsFromQuickFilter` 는 표에서 뺐다.
- **그리드 호환 부품 두 가지.** ① 낱개 열 메서드(`setColumnVisible`·`setColumnPinned`·`autoSizeColumn`·`moveColumn`)를 복수형으로 위임한다 — 경고만 사라지고 동작은 같다. ② **이미 파괴된 그리드에 온 쓰기는 건너뛰되 한 번은 알린다.** 화면이 사라진 뒤 도착한 응답이 `setRowData()` 를 부르면 라이브러리가 매번 경고를 냈다. 조용히 삼키면 "왜 목록이 안 그려지지" 를 못 쫓으므로 처음 한 번만 무엇을 건너뛰었는지 찍는다. 읽기(`get…`)는 그대로 넘긴다 — 값으로 판단하는 쪽이 `undefined` 를 받으면 더 크게 깨진다.
- **속성값 끝의 세미콜론.** `width="50px;"` 는 Vue 3 가 스타일 값으로 정규화하며 `Unexpected semicolon at the end of 'width' style value` 경고를 낸다. `;` 는 선언 구분자지 값의 일부가 아니라 떼도 뜻이 같다. 정적 값의 크기 속성에만 적용한다(값 안에 `;` 가 더 있으면 사람이 본다).
- **Vue 3 브랜치를 원격에 올리는 새 단계 `run/24-push-branch.sh`.** 20·22·15·18·8c 가 만든 커밋이 로컬에만 쌓이는데 올리는 단계가 없었다(6단계가 초안 때 한 번 미는 게 전부 — 사내 실측으로 22단계 커밋 6개가 원격에 없었다). 브랜치 확인 → 안전줄 브랜치(`backup-<날짜>`) → 실리지 말아야 할 것 검사(`.njh/`·`node_modules/`·`.env*`·`.npmrc` 의 인증값) → 올리기. 거부되면(non-fast-forward) **강제 푸시를 권하지 않고 사실만 찍는다**: 추적 ref 가 없으면(한 브랜치만 따라오는 클론) 만드는 법, 원격에만 있는 커밋의 **작성자·날짜·제목**, 공통 조상 유무 → 공통 조상이 있으면 `pull --rebase`, 없으면 옛 이력을 이름 바꿔 보존. `-X ours`·`-X theirs` 는 한쪽 변경을 말없이 지우므로 쓰지 않는다. 거부는 "원격이 앞섰다" 가 아니라 "갈라졌다" 는 뜻이고, 남의 작업이냐 옛 초안 이력이냐에 따라 처리가 정반대라 확인 전에는 아무것도 하면 안 된다.
- **1차 lint 설정은 이 복제본에서만 무시한다.** 16단계가 매번 킷에서 덮어쓰는 파일이라 커밋하면 킷 판이 바뀔 때마다 작업 트리가 더러워지고 차수마다 뜻 없는 diff 가 남는다(12단계 따라잡기가 `add -A` 를 안 쓰는 이유도 이것이다). `.gitignore`(as-is 소유)는 건드리지 않고 워크트리의 공용 제외 목록에만 한 줄 적는다.
- **Node 내장 모듈 externalize 위험을 따로 센다.** 사내 콘솔에 `Module "fs" has been externalized … Cannot access "fs.writeFileSync"`(0918)에 이어 `Module "stream" … Cannot access "stream.Readable"`(0919) 이 나왔다. 둘 다 **접근하는 순간 터진다** — Vue CLI 는 빈 객체로 때워 줘서 조용했다. 앱이 직접 import 하는 일은 거의 없고 **라이브러리가 끌고 온다**(xlsx 계열 → `fs`·`stream`·`crypto`·`path`). 23단계 [5/5] 가 **어느 화면이 어떤 모듈을 끌고 오는지** 세어 찍는다 — 엑셀 축이 못 바꾼 화면과 겹치므로 그대로 18단계 보수 우선순위가 된다.
- **차이가 곧 결함은 아니다 — 23단계가 갈라 찍는다.** 17단계 결과에서 ① **네트워크 계열 예외**는 `※재현 필요(시점일 수 있다)` ② **행 수만 다른 것**은 `※행 수만 다름 — 데이터 시점일 수 있다` 로 표시한다. 사내 눈확인에서 17 이 잡은 Network Error 한 건은 화면이 정상 렌더됐고("조회 결과가 없습니다"), 행 수 차이 한 건은 기본 조회기간이 **오늘~오늘**이라 데이터 시점 차이로 확정됐다. 행 **내용 지문**이 다른 것은 표시 형식 문제라 성격이 달라 그대로 둔다.
- **확인한 것과 못 한 것을 갈라 둔다.** 공통 그리드 부품이 화면의 `columnTypes`(천 단위 구분 formatter)를 빠뜨리는지 — **빠뜨리지 않는다**(옵션 병합이 `Object.assign` 전개라 키를 고르지 않고, 그리드는 그 값을 그리드 옵션에서 읽는다). 테스트로 고정했다. 다만 해당 화면 그리드에 숫자 칸이 없어 **표시 차이의 실제 원인은 아직 미확인**이다 — 다음 차수로 넘긴다. 탭 `href`→`value` 는 탭 5개·탭마다 다른 그리드·`@click` 핸들러가 함께 있는 실제 모양으로 다시 검증했다(값 맞물림 유지, `href` 잔존 0).
- 진행 표(00b)에 "원격 반영"(안 올라간 커밋 수)·"사진용 보고" 행. 74차까지의 변경 포함.
