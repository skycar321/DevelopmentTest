# airflow-bundle-20260826_211433 반입 안내 (내일 3.2.1 설치 최종판)

- 소스 커밋: `6c8baba25` (main)
- SHA256: `a5354ee1e48a28265f7f00509ee42324313e267afe4c92b37cc70819e8462a1c`
- 직전판(20260826_191421) 대비 변경:
  1. **파라미터 정합성 3건 실스택 검증 수리** — ①실행 우선순위 확정(실행 conf > task 요일별(by_day) > task 파라미터 > DAG 기본값; 기본값이 by_day를 가리던 결함 수정) ②타입이 Airflow Param 스키마로 직렬화되어 ③타입 위반 conf(정수 자리에 문자열)는 실행 생성 전 거부(HTTP 500). 실제 Airflow 2.6.3 스택에서 왕복 drift 0 재측정 완료.
  2. **날짜 템플릿 시드 완전화** — 고정 토큰 11종 전부(당일·전일·전전일·3일 전·1주일 전·30일 전·당월/전월 첫날·마지막 날 등) 시드. `sql/seed_templates.sql` 재적용 필요.
  3. **숫자 지정형 안내** — 템플릿 선택에 "숫자 지정형" 그룹(N일 전/후, N개월 전, N분 전): 선택하면 `{{ 3_DAYS_AGO }}` 골격이 들어가고 숫자 부분이 선택돼 바로 교체 입력, "숫자만 바꾸면 됩니다" 힌트+`{{ 1_MONTHS_AGO:%Y%m }}` 포맷 예시.
  4. Task 파라미터 값을 비우면 "DAG 기본값 상속: <값>" 안내가 즉시 표시(재오픈 불필요).
  5. **Airflow 3.2.1 대비**: MultiCronTimetable 정식 플러그인 등록(2.6.3↔3.2.1 이중 호환 실측 완료 — 현행 2.6.3 운영에도 안전). 3.2.1 마이그레이션 도구·가이드는 `docs/migration/`(홉체인 스크립트, team UUID 사전탐지, 설치 절차, 기본기능 5종 스모크 `scripts/upgrade3/scenario_smoke.py`).

## ⚠ 반입 후 필수 조치
- DB: `batch-admin-reference/sql/airflow_control_ddl.sql` 재적용(멱등) + `sql/seed_templates.sql` 재적용(시드 11종).
- 배포: `./scripts/deploy.sh dev` 그대로.
- 3.2.1 설치 시: `docs/migration/AIRFLOW_3_UPGRADE_REHEARSAL.md`의 "내일 개발기 설치 절차" 절 순서대로(홉체인 → 검증 → scenario_smoke.py).

- 해제: `7z x airflow-bundle-20260826_211433.7z` (비밀번호) → `tar -zxvf airflow-bundle.tar.gz`
