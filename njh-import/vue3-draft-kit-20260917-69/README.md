# Vue 3 draft kit — 2026-09-17 (69th)

This directory holds one encrypted archive (`vue3-draft-kit-20260917-69.7z`, 7z AES-256 with
encrypted headers) and its checksum. The password is the same as the njh-cli kits and is
delivered separately.

## Verify and extract

```bash
sha256sum -c sha256.txt        # or: shasum -a 256 -c sha256.txt
7z x -p<password> vue3-draft-kit-20260917-69.7z
```

Then read `vue3-carry-20260909-v2/README-사내-반입.md` — its first section "지금 바로 시작하기" is the
command order for a fresh start.

## Requires

- The njh-cli replacement kit **v1.6.10** or later from this channel (`v1.6.10/`).

## Rehearsal

The full chain (00-env → 01 → 02 → 03 → 04 → 05 → 06, then a fresh start with 0c and the chain again)
was run on the lab tree before packaging; result **PASS**.

## What changed

- **개발 서버 504 해결 — 22단계 새 분류 `vite`**: 화면을 옮겨 다니면 개발 서버가 처음 보는 라이브러리를 발견해 의존성을 다시 묶고 버전 해시를 바꾼다. 페이지가 새로고침되지 않으면 옛 해시 요청이 504 (Outdated Optimize Dep)가 되어 화면이 "Failed to fetch dynamically imported module" 로 안 떴다. `src` 가 import 하는 설치된 라이브러리를 `vite.config` 의 `optimizeDeps.include`(표시된 블록)에 넣어 서버 시작 때 전부 묶는다. 설치되지 않은 라이브러리를 import 하는 옛 파일은 목록에서 빼고 보고서에 남긴다. 실브라우저 테스트로 넣기 전(다시 묶기·옛 해시 504)과 넣은 뒤(다시 묶기 없음·200·지연 화면 모두 뜸)를 확인했다.
- **그리드 페이지 크기 선택기**: 그리드 라이브러리 새 버전은 페이지 나누기를 쓰면 "Page Size:" 선택기를 기본으로 붙인다(옛 버전에는 없었다). 호환 부품이 화면이 직접 정하지 않았을 때만 선택기를 끈다. 이미 들어간 호환 부품은 22 `aggrid` 가 킷 판과 다르면 갱신한다(줄바꿈만 다른 것은 같은 판).
- **8b 스윕**: 504 (Outdated Optimize Dep) 증거로 실패한 화면은 한 번 더 열고, 그래도 504 면 "실패" 가 아니라 "개발 서버 의존성" 으로 따로 세어 담당자 배정에서 뺀다. 컴파일 오류(500)는 그대로 실패.
- 진행 표(00b)의 22 행이 미리 묶기 없음·호환 부품 옛 판을 짚는다. README 에 69차 절과 순서.
- 게이트: 미리 묶기 실브라우저 테스트, 선택기 실브라우저 테스트 4건, 8b 504 재시도 테스트, 68차 모양 트리에서 22 재실행이 호환 부품 갱신·vite 만 바꾸는지와 CRLF 체크아웃 멱등.
- 68차까지의 변경 포함.
