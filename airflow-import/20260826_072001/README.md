# Airflow 제어 반입판 — 2026-08-26 (**오류표시 현지화 전수 + 화면용어 통일 + def_yaml 승인기록**)

- 번들: `airflow-bundle-20260826_072001.7z`
- sha256: `아래 SHA256SUMS 파일 참조`
- source commit: `d26b8a96c`
- 직전 판 `20260825_181718` 이후의 야간 개선 41커밋을 모두 포함합니다.

---

## 이번 판의 핵심 3가지

### 1. 오류 표시 현지화 전수 정비
영문 서버 문구(예: `Service Unavailable`)가 화면 "기술 상세"에 그대로 노출되던
경로 **11곳**(알림·목록 변경·자동 변환·선후행 추천·공유 재시도, unknown 상태 라벨 2곳 포함)을
전수 수리했습니다. 오류 코드(`RUN_TASK_STORE_FAILURE` 형태)와 한국어 문구·추적 ID 는
그대로 보존됩니다 — 운영 문의 근거가 사라지지 않습니다.

### 2. 화면 용어 통일
화면·메뉴·안내 전반의 표기를 정본으로 통일했습니다(대표: `Airflow 제어`, `DAG 만들기`,
`실행주기`, `실행 요청`, `승인/반려`, `다시 시도`, `새로고침` 등 26패밀리).
재발 방지 계약(retired 표기 재유입 시 검사 실패)이 함께 들어갔습니다.

### 3. 배치어드민 승인 → def_yaml 기록(DB 정본 강화)
어제 판의 "테이블이 정본" 구조를 승인 흐름까지 연결 — **배치어드민에서 카탈로그/실행주기
변경을 승인하면 `__definition__` 행의 def_yaml 이 함께 갱신**됩니다(파일/ops-DAG 경로는
폴백 유지, 운영기 403 정책 불변). 실 PostgreSQL 통합테스트로 검증했습니다.

### 부수 수정
- 피드백 배너가 알림 패널을 덮어 클릭을 막던 레이어링 결함 수리(z-order 토큰화)
- 검증 게이트 다수의 하니스 결함 수리(브라우저 실측 확증 포함)

---

## 반입 방법 (⚠ 이번 판부터 배포 명령에 후보 ID 필수)

    7z x airflow-bundle-20260826_072001.7z        # 비밀번호 입력
    tar -zxvf airflow-bundle.tar.gz               # → airflow-bundle/

    # 배포 — AIRFLOW_DEPLOY_CANDIDATE_ID 가 없으면 시작 전에 중단됩니다(서버 무변경).
    # 값은 번들 타임스탬프 권장(후보별 rollback record 이름이 됩니다).
    AIRFLOW_DEPLOY_CANDIDATE_ID=20260826_072001 BASE_DIR=/data/lowcode/aa \
    CONTAINER_RUNTIME=podman NEED_SUDO=true ./scripts/deploy.sh dev

배치어드민은 batch-admin-reference 델타를 기존 절차대로 반영하십시오.
(js/css/html/mapper/sql 변경 포함 — **Java 만 교체하면 미반영됩니다**)
