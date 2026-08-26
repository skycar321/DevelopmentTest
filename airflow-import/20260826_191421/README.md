# airflow-bundle-20260826_191421 반입 안내 (내일 3.2.1 설치 대비판)

- 소스 커밋: `22ca333a2` (main)
- SHA256: `62dd4409e1de0152db5ec77ea7353cf87d2b2f5c7597afa77283e148096022f5`
- 직전판(20260826_154342) 대비 변경 — 리뷰 지적 파라미터 4건 반영:
  1. **task 파라미터 지정 복원** — 접힘 재편 때 숨겨졌던 param_1~5 편집이 "Task 실행 파라미터" 섹션으로 복원(원인 커밋 특정 후 수리). 값을 비우면 DAG 기본값 상속.
  2. **DAG 전체 디폴트 파라미터** — DAG 단위 `params(키/기본값/타입)` 편집 신설, Task 값이 우선.
  3. **파라미터 템플릿 목록/타입** — 전일·전월 등 만들어둔 템플릿이 파라미터 옆 "템플릿" 선택으로 뜨고, 타입(날짜/문자열/정수) 지정·표시. ⚠ DDL 추가분 있음(아래).
  4. **실행 시 파라미터 폼** — 즉시수행 승인 후 실행 대화상자에 생성 때와 동일한 키가 폼으로 뜨고 값만 수정(타입별 입력기). JSON 원문 입력은 기본 화면에서 제거.
  5. 운영 문서 동봉: `docs/PRD_DML_GUIDE.md`(운영 DML 샘플·반영 확인 절차), `docs/PARALLEL_RUN_VERIFICATION.md`+`scripts/verify_parallel_run.py`(기존 배치↔DAG 시차 병행 정합성 체크, 불일치 시 비정상 종료).

## ⚠ 반입 후 필수 조치
- **DB에 `batch-admin-reference/sql/airflow_control_ddl.sql` 재적용**(멱등) — def_yaml 6컬럼 + 이번 파라미터 템플릿 타입(parameter_type) 컬럼 포함.
- 시드 갱신: `sql/seed_templates.sql`(전일/전월 등 기본 템플릿) 적용 권장.
- 배포는 기존대로 `./scripts/deploy.sh dev`.
- 시스템컬럼 문의 답: 화면 승인 경로는 이미 **승인자 세션 ID를 sys_trtr_id에 기록**합니다(하드코딩 아님, 상세 `PRD_DML_GUIDE.md` 부록).

- 해제: `7z x airflow-bundle-20260826_191421.7z` (비밀번호) → `tar -zxvf airflow-bundle.tar.gz`
