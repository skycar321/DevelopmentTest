# airflow-bundle-20260826_154342 반입 안내 (엑셀-패리티 필터판)

- 소스 커밋: `18328c2c5` (main)
- SHA256: `24e77690fe4a2d13148c17a295a9ead8a5fa5c4f54e16421a4ff8e0fc4ea48fb`
- 직전판(20260826_145039) 대비 변경:
  1. **필터 엑셀 방식 전환** — 체크/검색은 드롭다운 안에서만 바뀌고 **확인을 눌러야 목록 반영**(취소·Esc·바깥 클릭=변경 버림). 하단 확인/취소 버튼 신설(닫기 문제 해결), "모두 선택" 3-상태 표시, 상단 "<컬럼명>에서 필터 해제" 항목, 필터 적용 컬럼 깔때기 상태 표시.
  2. **CodeQL HIGH 보안 수리** — `AirflowControlService.java` 스케줄 다중크론 분할의 다항 백트래킹(ReDoS) 제거(공백 5만자 실측 973ms→0.3ms, 동작 등가).
- 여전히 포함(145039 반입 안 했다면 함께 적용됨): 작업로그 오류 점검가이드, DAG 만들기 문구 삭제, def_yaml DDL, 주석 한글화, 필터 검색/흰글씨/3영역 수리.

## ⚠ 반입 후 필수 조치 (145039를 건너뛴 경우)
- DB에 `batch-admin-reference/sql/airflow_control_ddl.sql` 재적용(멱등) — def_yaml 오류 해소.
- 배포는 기존대로 `./scripts/deploy.sh dev`.

- 해제: `7z x airflow-bundle-20260826_154342.7z` (비밀번호) → `tar -zxvf airflow-bundle.tar.gz`
