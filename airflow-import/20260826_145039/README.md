# airflow-bundle-20260826_145039 반입 안내 (리뷰용 최신판)

- 소스 커밋: `c3bb4efdd` (main)
- SHA256: `2471725ec9a24f9020f7ea61f5be780fa808521241829f962adc6e5d049998ba`
- 이전 반입판(20260826_135915) 대비 변경:
  1. **필터 결함 3건 수리** — 드롭다운 안 타이핑 검색 미동작 수정, 툴팁 있는 컬럼명(배치이름·실행주기) 흰 글씨 → 다른 헤더와 동일 색, 헤더 셀을 컬럼명(툴팁·밑줄)/정렬 화살표/필터 버튼 3개 독립 영역으로 분리(hover 상호 간섭 제거).
  2. **작업로그 오류 점검가이드 신설** — DAG 무관 공통: 로그 화면에서 오류 시그니처 자동 매칭 → 오류영역 배경 하이라이트 + "재수행 가능 여부/어느 서버·서비스·파일을 점검" 한글 가이드 카드(규칙 20종: 36 에이전트, SDC 풀, SSH, DB, 권한, 파스 오류 등). 미지 오류는 재수행 판단 기준 포함 일반 체크리스트 폴백.
  3. **DAG 만들기 문구 삭제** — "운영자가 배치를 찾고…" 설명 단락 제거.
  4. **def_yaml SQL 오류 근본수리** — `batch-admin-reference/sql/airflow_control_ddl.sql` 에 마커행 6컬럼 멱등 ALTER(ADD COLUMN IF NOT EXISTS: sched_json, base_catalog_hash, def_yaml, use_yn, sched_version, approved_request_id) 추가.
  5. **주석 한글화** — mapper XML·application.yml·css 영어 주석 한글화.

## ⚠ 반입 후 필수 조치
- **DB에 `airflow_control_ddl.sql` 재적용** (멱등이라 재실행 안전) — 적용해야 "column def_yaml does not exist" 오류가 해소됩니다.
- 배포 절차 변동 없음: `./scripts/deploy.sh dev` 그대로.

- 해제: `7z x airflow-bundle-20260826_145039.7z` (비밀번호) → `tar -zxvf airflow-bundle.tar.gz`
