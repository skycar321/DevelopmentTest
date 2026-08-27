# airflow 반입판 20260827_092846 — 2.6.3 / 3.2.1 양버전 (정정판)

같은 소스(main `5b8f52a3c`)에서 대상 Airflow 버전별로 두 벌을 만들었다.
운영은 9월까지 2.6.3, 개발기는 3.2.1이므로 **환경에 맞는 것만** 반입한다.

> 이 판은 **20260827_082231 을 대체한다.** 그 판의 3.2.1 번들은 내부 README 가 자기를
> 2.6.3 으로 잘못 안내했다. 기계적 배포 가드는 정상이었지만 사람이 읽는 쪽이 틀렸다.

| 대상 Airflow | 파일 | SHA-256 |
|---|---|---|
| 2.6.3 | `airflow-bundle-airflow-2.6.3-20260827_092846.7z` | `634fd5e47ccfbefd0eb97c521ac820b8af005e4a03008d72861cac40ab44356a` |
| 3.2.1 | `airflow-bundle-airflow-3.2.1-20260827_092851.7z` | `95dcc87a396c77c2732a2953358617154a4d402b458da9c790d549009f78eb9c` |

번들 안에 대상 버전이 각인돼 있고(`airflow_target_profile.json`), **배포 시 컨테이너의 실제
Airflow 버전과 다르면 배포가 거부된다.** 각 번들의 `airflow-bundle/README.md` 도 자기 대상
버전을 표시한다.

## 1. 배치어드민(웹 화면) 반영

7z 를 전체 해제한 뒤 `batch-admin-reference/src/**`, `mapper/`, `static/`, `templates/`, `sql/`
을 사내 `ui-batchadmin-main` 원본 경로에 덮어쓰고 jar 를 재빌드해 배포한다.

> ⚠ **Java 만 교체하면 이번 판 변경의 상당수가 반영되지 않는다.** css/js/html/mapper.xml 이
> 함께 바뀌었다.

## 2. Airflow 서버 반영

같은 7z 안의 `airflow-bundle-airflow-<버전>.tar.gz` 를 Airflow 서버에서 해제한 뒤
`./scripts/deploy.sh dev` (운영은 `prd`).

## 3. 반입 후 필수 조치 (두 버전 공통)

1. `batch-admin-reference/sql/airflow_control_ddl.sql` 재적용 — 멱등 ALTER
2. `sql/seed_templates.sql` 재적용 — **두 종류를 심는다. 둘 다 확인할 것**
   - 흐름 템플릿 **15종**
   - 파라미터 템플릿 **11종**: `{{ TODAY }}`(당일) · `{{ YESTERDAY }}`(전일) ·
     `{{ 2_DAYS_AGO }}`(전전일) · `{{ 3_DAYS_AGO }}` · `{{ 7_DAYS_AGO }}`(1주일 전) ·
     `{{ 30_DAYS_AGO }}` · `{{ MONTH_FIRST }}`(당월 첫날) · `{{ MONTH_LAST }}`(당월 마지막 날) ·
     `{{ 1_MONTHS_AGO:%Y%m }}`(전월) · `{{ PREV_MONTH_FIRST }}`(전월 첫날) ·
     `{{ PREV_MONTH_LAST }}`(전월 마지막 날)
     ※ `{{ 1_DAYS_AGO }}` 는 별도 항목이 아니라 `{{ YESTERDAY }}` 와 같은 값이다.
   - 15종만 보고 넘어가면 "전일/전월이 파라미터 목록에 안 뜬다"는 지적이 그대로 재발한다

개발기 3.2.1 설치 절차는 번들 안 `docs/migration/DEV_INSTALL_321_CHECKLIST.md` 참조.

## 4. 이번 판에 들어간 것

- 실행 요청: 만들 때 정한 키 그대로 표시, 값만 수정(원문 JSON 은 고급 입력으로 분리)
- 파라미터 유형(날짜/문자열/정수), 고정 템플릿 11종 + 숫자 지정형 안내
- 로그 화면: **로그를 못 가져올 때도** "어디를 어떻게 점검하라" 가이드가 뜬다
  (서버·서비스 / 접근권한 / 데이터·선행작업 분류와 재수행 안전 여부 포함)
- DAG 상세: 원본 진입점·source_root·alias·job/module 해시·tmp_tables·트랜잭션 묶음·
  strict traceback 정책을 읽기 전용 근거로 노출(값을 못 얻으면 "확인 불가"로 표시)
- 승인 화면: 변경 내용(현재 → 변경 후) 열, 멀티 실행주기 표기 대칭 정리
- DAG 목록: 선택 열을 제외한 10개 데이터 헤더별 필터 + 타이핑 검색, 1024px 폭 넘침 수정
- 화면 안내(둘러보기)가 접힌 요소를 가리키던 문제 수정

## 5. 실측 근거

- Maven 통합테스트 **69 실행 / 실패 0 / 오류 0**
- pytest 축 **467 통과**
- 실브라우저: 등록·실행·로그확인·스케줄조정·활성비활성 **5기능 전부 실측**
- 2.6.3 · 3.2.1 **양쪽 런타임에서 같은 5기능 오류 0** (같은 소스 68 DAG, 신규 등록 후 69)
- 포함 계약 rc=0, 무암호 목록 시도 rc=2(잠금 정상)
