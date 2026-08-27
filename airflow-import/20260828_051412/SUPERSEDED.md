# 이 세트는 사용하지 마십시오 — 20260828_060427 로 대체됨

배포 스크립트는 정상이지만 **Kafka mTLS 가이드가 한 판 뒤처져 있습니다.**

체크리스트 13행이 "Broker URI 필드가 아니라 `broker_url` **파라미터**를 바꾸라" 고 지시하는데,
이 세트의 가이드에는 **그 파라미터를 어디서 편집하는지가 없습니다**(`Pipeline Parameters` 언급 0건).
대체 세트에는 편집 경로가 4곳에 들어가 있습니다.

구분법: 아카이브 안 `docs/KAFKA_SSL_STREAMSETS_GUIDE.html` 에서
`Pipeline Parameters` 를 찾아 0건이면 이 세트(구판), 4건이면 대체 세트입니다.
