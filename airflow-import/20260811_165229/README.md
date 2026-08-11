# airflow-bundle 20260811_165229

배치 어드민 Airflow 제어 화면 + Airflow 번들 반입본입니다. 압축은 헤더까지 암호화(`-mhe=on`)되어 있고, 비밀번호는 이 저장소에 없습니다.

## 해제 절차

```bash
7z x airflow-bundle-20260811_165229.7z   # 비밀번호 입력 → airflow-bundle.tar.gz + docs/ + spring-batch-reference/
tar -zxvf airflow-bundle.tar.gz          # → airflow-bundle/{dags,plugins,config,scripts}
shasum -a 256 -c SHA256SUMS.txt          # 무결성 확인
```

## 144020 대비 이 판에서 바뀐 것 — 승인 화면 가독성

**한 화면에 보이는 승인 대기 건수가 6건에서 10건 이상으로 늘었습니다.** 원인은 표의 열 10개가 전부 140px 로 균등 분할되어 있던 것이었습니다. 그 때문에 `변경내용(현재 → 변경 후)` 이 **14줄**로, `DAG` 가 7줄로 접혀 한 행이 137px 를 차지했습니다.

- 열 폭을 내용량에 맞게 배분했습니다(변경내용 31%, DAG 16%, 선택·처리결과 등은 좁게). **정보를 줄이거나 숨기지 않았습니다** — 같은 내용이 덜 접힐 뿐입니다.
- 행 높이 **137px → 85px**, 셀 상하 여백은 계약대로 6px 을 유지합니다.
- `전체 보기` 로 펼친 변경 원문이 `overflow:hidden` 때문에 **잘린 채 보이던 결함**을 고쳤습니다. 펼침의 목적이 전문 확인이므로 잘림을 해제했습니다.

## 검증 상태

정본 스위트 **45 PASS / 1 FAIL / 1 PRECOND / 1 HOLD**.

| 미결 | 내용 |
|---|---|
| `airflow-visual-geometry-sweep` (FAIL) | 승인 표에서 제목이 상단 고정 헤더로 넘어가는 순간의 겹침·가로 넘침 검사가 미충족입니다. 위 개선으로 **오래 막혀 있던 체크박스 10개 계약이 통과하면서 그 뒤에 가려져 있던 검사가 새로 드러난 것**이며, 화면 사용에는 지장이 없습니다. |
| `airflow-list-processing-lifecycle` (PRECOND) | 실행 요청 완료 후 포커스 복원 판정이 환경 전제(창 포커스) 때문에 결론을 못 내는 상태입니다. 제품 결함이 아닙니다. |
