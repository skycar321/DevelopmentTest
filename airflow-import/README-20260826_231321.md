# airflow-bundle-20260826_231321 반입 안내 (설치 당일 최종판 — 이 판을 사용)

- 소스 커밋: `9ca9257a8` (main)
- SHA256: `d35d728d59c75b40305f4e4a8bde8a444d31640011dee5cd879f64947b67c954`
- 직전판(211433) 대비 — 어제 밤 육안 검수 5건 반영:
  1. 고급 "단일 실행주기(옛 형식)" 입력 제거 (옛 형식 카탈로그는 로드 시 규칙으로 자동 정규화)
  2. 상단 메뉴 드롭다운 오버레이 — 본문 안 밀림
  3. 파라미터 유형·기준월을 파라미터 섹션 "파라미터 기준" 그룹으로 이동
  4. 파라미터 1행 기본 + "＋ 파라미터 추가" (값 있는 행은 항상 표시)
  5. task 표시명 정책 숨김(기본 id, 설정된 카탈로그만 표시)
  + YAML 기능 미노출분 1차 조회 노출(DAG 상세/요약), 개발 샌드박스 고정 테스트 계정 문서화
- 211433의 내용(파라미터 정합성 실스택 종결·시드 11종·숫자 지정형 안내·3.2.1 대비 일체) 전부 포함.

## ⚠ 반입 후 필수 조치 (211433과 동일)
- DB: `batch-admin-reference/sql/airflow_control_ddl.sql` + `sql/seed_templates.sql` 재적용(멱등)
- 배포: `./scripts/deploy.sh dev` 그대로
- 3.2.1: `docs/migration/AIRFLOW_3_UPGRADE_REHEARSAL.md` "내일 개발기 설치 절차" → `scenario_smoke.py` 5종 확인

- 해제: `7z x airflow-bundle-20260826_231321.7z` (비밀번호) → `tar -zxvf airflow-bundle.tar.gz`
