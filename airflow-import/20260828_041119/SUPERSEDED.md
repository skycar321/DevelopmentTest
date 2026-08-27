# 이 세트는 사용하지 마십시오 — 20260828_051412 로 대체됨

배포 후 로그 스캔이 **양 타깃 모두 scheduler 컨테이너만** 읽습니다.
Airflow 3.2.1 은 DAG 파싱을 독립 `dag-processor` 가 하므로, 배포 치명으로 분류되는
오류(직렬화·import·plugin-load)가 기록되는 곳을 한 번도 보지 않습니다.
실측: 직렬화 실패가 `dag-processor` 로그에만 있을 때 이 세트의 deploy 는 **rc=0(성공)** 을 냅니다.

2.6.3 에는 영향이 없습니다(파서가 scheduler 이므로 같은 컨테이너입니다).
대체 세트는 `20260828_051412` 이며, `airflow-bundle/scripts/deploy.sh` 에
`_SLOG_TARGETS` 가 있는지로 구분할 수 있습니다.
