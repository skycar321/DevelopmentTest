# Airflow 제어 반입판 — 2026-08-26 4차 (**칸 폭·DB반영 배지·주석 한글화**)

- 번들: `airflow-bundle-20260826_121742.7z` / source commit: `a675dae36`
- 직전 판 `20260826_112055` 위에 아래를 얹은 판입니다.

## 오전 2차 제보 반영
- 승인/반려 잘림: 원인 = '실행' 접두 라벨(.af-tag min-width 44px) — 축소형 접두어로 교체
- 실행 방식 열 축소(146/110px) ↔ 승인/반려 열 확장(152/164px)
- 실행주기 `DB` 배지 → **`DB반영`** + 열 헤더 툴팁 설명 추가
  (의미: 실행주기 변경 승인이 DB 정본에 반영된 행 — 목록 반영 뒤 Airflow 적용)

## 소스 주석 한글화(요청 반영)
출하 소스 전반(프런트 js/css/html·Java·python/shell/sql) 영어 주석을 한국어로 정리,
장황 주석 축약. 고유명사·API·설정키·라이선스 보존, **동작 코드 무변경**(언어별
파서/토크나이저로 파일별 불변성 증명 완료).

## 반입: 무입력 그대로
    7z x airflow-bundle-20260826_121742.7z && tar -zxvf airflow-bundle.tar.gz
    BASE_DIR=/data/lowcode/aa CONTAINER_RUNTIME=podman NEED_SUDO=true ./scripts/deploy.sh dev
배치어드민 델타는 java+js+css+html 전부 교체.
