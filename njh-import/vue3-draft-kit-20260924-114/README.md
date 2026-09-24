# Vue 3 draft kit — 2026-09-24 (114th)

This directory holds one encrypted archive (`vue3-draft-kit-20260924-114.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260924-114.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- 114차는 사내 113차 실행(97차 트리에 113차를 한 번에 적용)에서 사내 트리에서만 나온 "사람 확인" 8건 중 킷 결함 4가지의 수리다 — 넷 다 "킷이 예전에 만든 것·앞 분류가 방금 바꾼 것을 뒤 분류가 못 알아본" 같은 부류(랩은 분류를 하나씩 쌓아 온 macOS 트리라 못 봤다)
- 22 chart: 호환 부품(툴팁 기본값) 소유 판정을 바이트 비교 → 코드 비교로 — 트리의 부품은 80~90차 킷 판이었고 92차 주석 규약이 머리 주석만 바꿨다(코드 본문 동일, 출하 킷에서 확인). 옛 판을 손 수정으로 봐 파이·도넛·폴라·레이더·버블 래퍼 5개가 86·105·113차 차트 보정을 한 번도 받지 못했다. 주석·공백·줄끝·따옴표만 다르면 킷 소유, 부품은 킷 새 판으로, 코드를 고친 판은 여전히 손작업. 탭 호환 부품도 같은 판정
- 22 chart: 반응형 차트 래퍼의 생성기 출력 대조를 LF 로 — Windows CRLF 체크아웃에서 현재 판·이전 판 모두 소유 증명 실패로 거부됐다(91차 다른 복원 규칙과 같은 결함, 이 경로만 빠졌다). 쓰기는 원래 줄끝 그대로
- 22 layout-restore: 전체 화면 로딩 덮개 측정 전제(앱 CSS z-index 10)를 먼저 도는 22 z-scale 이 V4 척도로 옮겨 두어 거부되던 것 — 킷이 옮긴 값은 표지의 as-is 값으로 읽는다(다른 값·다른 표지는 여전히 거부)
- 22 css-restore: 비활성 날짜 칸 측정 전제(앱 바깥선 윤곽 색)를 먼저 도는 22 css 가 V4 윤곽 조각으로 나눠 두어 모든 트리에서 거부되던 것 — 원래 선택자와 나눈 조각 둘 다 알아본다(두 번·다른 색·섞인 묶음은 거부)
- 113차 17 에서 as-is 개발 서버가 컴파일 중 스스로 끝난 것(SERVER_PROCESS_EXITED)은 로그 확인 대기 — 원인을 고친 뒤 `17 --reuse-tobe` 로 as-is 만 다시 잰다
- 테스트: 관련 279개 통과(게이트 90 블록 8개 포함), 새 테스트 전부 revert-red 확인. 게이트 114 블록(신선 사슬 CRLF 트리에서 세 분류 사람 확인 0·덮개·비활성 날짜 칸 복원, 80~90차 킷 판 부품 픽스처 → 래퍼 올림·부품 새 판·멱등)
