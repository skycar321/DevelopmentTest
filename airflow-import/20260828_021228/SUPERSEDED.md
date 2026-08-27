# 이 세트는 사용하지 마십시오 — 20260828_041119 로 대체됨

이 세트는 배포 판정 수정이 들어갔다고 설명했지만 **실제로는 들어가지 않았습니다.**
`scripts/classify_scheduler_error_lines.py` 와 그 계약은 동봉됐으나, 그것을 호출하는
`scripts/deploy.sh` 변경이 누락되어 `deploy.sh` 는 여전히 scheduler 로그에 ERROR 가 보이면
설치 성공을 실패로 뒤집습니다.

전체 셸 스위트가 이 누락을 잡아냈고, 배선을 복구한 세트가 `20260828_041119` 입니다.
아카이브를 풀어 `airflow-bundle/scripts/deploy.sh` 에 `_SLOG_CLASSIFIER` 가 있는지로
어느 쪽인지 구분할 수 있습니다.
