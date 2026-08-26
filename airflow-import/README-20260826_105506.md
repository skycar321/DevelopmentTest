# Airflow 제어 반입판 — 2026-08-26 2차 (**배포 무입력화: 그냥 ./deploy.sh dev 로 됩니다**)

- 번들: `airflow-bundle-20260826_105506.7z`
- source commit: `08d20b493`
- 직전 판 `20260826_072001` + **배포 후보 ID 자동 유도** 1건.

## 이번 판의 변경 — 오전 반입 시 겪으신 중단 해소

072001 판은 `AIRFLOW_DEPLOY_CANDIDATE_ID` env 없이는 배포가 시작 전에 중단됐습니다.
이번 판부터 **env 없이도 번들 정보(패키징 시각)로 후보 ID 를 자동 유도**해
롤백 레코드를 만들고 진행합니다. env 는 원할 때만 오버라이드로 쓰면 됩니다.

    7z x airflow-bundle-20260826_105506.7z        # 비밀번호 입력
    tar -zxvf airflow-bundle.tar.gz               # → airflow-bundle/
    BASE_DIR=/data/lowcode/aa CONTAINER_RUNTIME=podman NEED_SUDO=true ./scripts/deploy.sh dev

(dev --clean 사용 시: 카탈로그 YAML·DML 정의는 보존/재생성되지만, dags/ 에 손으로
넣어둔 .py 는 보존 대상이 아닙니다 — 자세한 규칙은 072001 README 참조)
