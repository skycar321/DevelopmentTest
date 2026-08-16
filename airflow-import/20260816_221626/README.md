# Airflow 제어 반입판 (batch-admin + airflow-bundle)

- 번들: `airflow-bundle-20260816_221626.7z` (1154289 bytes)
- sha256: `fcbee31b0178dd179972f3345aa10eea23c6f5cdd4e115b2bc5bac10a379ed5b`
- **직전 판 `20260816_210353` 에 deploy 수정 1건만 더한 판입니다.** 그 판을 이미 받으셨다면 이 판으로 교체하세요.

## 이 판에서 추가된 것 — 자동 생성 DAG 가 목록에 안 보이던 문제 차단

사내 dev 에서 실제로 겪은 증상입니다. `deploy.sh` 실행 후 **DAG 수백 개가 목록에 안 보이고**
파이썬 배치 등 85개 정도만 보였습니다.

**원인**: `[pre0]` 로더 스캐폴딩은 배포 초반에 돌고, streamsets/spring 자동 변환 카탈로그는
그보다 **뒤인** `[4b/5]` 와 sync ops DAG 에서 생깁니다. 그래서 그 카탈로그들이 이번 배포에서
정적 로더를 못 받고 전부 캐치올 파일(`zz_runtime_catalogs.py`) 하나로 몰렸고,
그 파일이 **DAG 247개를 지고 파싱에 30.817초**가 걸렸는데
`core.dagbag_import_timeout` 이 **30.0초**라 파일이 통째로 타임아웃했습니다.
결과적으로 등록은 446개인데 **직렬화된 것은 79개뿐**이라 목록에 안 뜬 것입니다.
(타임아웃은 import 에러 목록에 남지 않아 `[4] import 에러 없음` 으로 보입니다.)

**조치**: 자동 변환이 끝난 뒤 로더 스캐폴딩을 **한 번 더** 돌리는 단계 `[4c/5]` 를 넣었습니다.
캐치올은 정적 로더가 가져가지 않은 것만 맡으므로 자동으로 비워지고, 파싱 부하가 파일 단위로
쪼개져 단일 파일 타임아웃이 사라집니다. **두 번째 배포를 기다릴 필요가 없습니다.**
배포 로그에 추가된 로더 수가 `(before → after)` 로 찍힙니다.

### 확인 방법
```bash
./scripts/deploy.sh dev          # [4c/5] 에서 "정적 로더 N개 추가" 출력 확인
./scripts/diagnose.sh            # [5] zz_runtime_catalogs 개수가 크게 줄고 [6] CHECKED 가 늘어야 정상
```
그래도 남으면 컨테이너 env 로 `AIRFLOW__CORE__DAGBAG_IMPORT_TIMEOUT=120` 상향 후 `./deploy.sh up dev`.

### 참고 — 자동 변환이 "적용" 되려면
`[12]` 에 `applied_ids=0 / mode=approval / staged_ids=472` 로 나오면 변환은 됐고 **적용만 대기** 중입니다.
최초 대량 반입은 무인 적용이 편합니다:
```bash
podman exec --user airflow aa_airflow-webserver_1 \
  airflow dags trigger cm_ops_sync_streamsets_autogen -c '{"mode":"auto"}'
```
이후 crontab 변경분 현행화는 원래대로 배치어드민 **자동 변환 검토** 화면에서 승인하시면 됩니다.

## 그 외 내용은 직전 판과 동일

용어 통일(`실행 요청`·`스케줄 변경`), 상세 모달 맞춤 버튼 CSS, 빌더 실행주기 중복 라벨 정리,
목록 `multi-cron:` 접두어 제거, `작업 흐름도` 용어 통일, 그리고 **멀티크론 배치의 스케줄을
화면에서 바꿀 수 없던 버그** 수정이 모두 포함돼 있습니다. 반입 절차와 검증 결과는
`20260816_210353/README.md` 와 동일합니다.
