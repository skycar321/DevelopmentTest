#!/bin/bash
# 서버 어딘가에 특정 파이썬 패키지가 깔려 있는지 전수 조사한다. 읽기 전용 — 아무것도 바꾸지 않는다.
#
# 사용: bash find_package.sh litellm            # 취약 버전 판정까지 하려면 ↓
#       bash find_package.sh litellm 1.83.7     # 두 번째 인자 = 안전한 최소 버전
#
# 왜 이렇게까지 하나 — 보안 스캐너는 "이 서버에 X 가 있다" 만 알려 주고 어느 파이썬인지는
# 안 알려 준다. 흔히 쓰는 조사 스크립트들이 놓치는 함정이 넷 있고, 실제로 다 밟는다:
#
#   ① 인터프리터 목록을 손으로 나열한다 (python3, /usr/bin/python3, /usr/local/bin/python3 …).
#      인프라가 소스빌드로 올린 /usr/python/bin/python3.12 같은 건 목록에 없어 통째로 빠진다.
#      → 이 스크립트는 **돌고 있는 프로세스에서 인터프리터를 역추적**하고 디스크도 훑는다.
#   ② `find / -xdev` 는 다른 파일시스템을 건너뛴다. /data 나 /opt 가 별도 마운트면 안 본다.
#      → 마운트 지점을 먼저 열거해 **각각** 훑는다.
#   ③ `pip list` 만 본다. pip 이 없는 환경(슬림 이미지·소스빌드 파이썬)에서는 빈손으로 끝난다.
#      → pip 이 없으면 그 인터프리터의 **site-packages 를 직접** 뒤진다.
#   ④ 버전을 안 본다. 있다/없다만 알면 조치를 못 정한다.
#      → dist-info 이름에서 버전을 꺼내 안전버전과 비교해 판정까지 낸다.
set -uo pipefail
PKG="${1:?사용법: bash find_package.sh <패키지명> [안전한최소버전]}"
SAFE="${2:-}"
PKG_LC="$(printf '%s' "$PKG" | tr '[:upper:]' '[:lower:]')"
# PyPI 이름 정규화: dist-info 는 '-' 와 '_' 를 오간다(litellm / lite_llm).
PKG_RE="$(printf '%s' "$PKG_LC" | sed 's/[-_]/[-_]/g')"

hr() { echo; echo "════════ $* ════════"; }
HITS_FILE="$(mktemp)"; trap 'rm -f "$HITS_FILE"' EXIT INT TERM

# ── 버전 비교(PEP440 단순형). rc=0 이면 $1 < $2
ver_lt() { [ "$1" = "$2" ] && return 1; [ "$(printf '%s\n%s\n' "$1" "$2" | sort -V | head -1)" = "$1" ]; }

hr "[0] 이 서버의 파이썬 인터프리터 전수 발굴"
PYS="$(mktemp)"
# (a) PATH 에 있는 것
for n in python python2 python3 python3.6 python3.7 python3.8 python3.9 python3.10 python3.11 python3.12 python3.13; do
  command -v "$n" >/dev/null 2>&1 && command -v "$n" >> "$PYS"
done
# (b) ★ 돌고 있는 프로세스에서 역추적 — 손으로 나열한 목록이 놓치는 커스텀 경로가 여기서 잡힌다.
if [ -d /proc ]; then
  for p in /proc/[0-9]*; do
    exe="$(readlink -f "$p/exe" 2>/dev/null)" || continue
    case "$exe" in */python*) echo "$exe" >> "$PYS" ;; esac
  done
fi
ps -eo args= 2>/dev/null | tr ' ' '\n' | grep -E '^/.*/python[0-9.]*$' >> "$PYS" 2>/dev/null || true
# (c) 흔한 커스텀 설치 접두사를 디스크에서 훑는다(/usr/python 같은 소스빌드 위치 포함)
for pre in /usr /usr/local /opt /app /srv /data /home /root /usr/python; do
  [ -d "$pre" ] || continue
  find "$pre" -maxdepth 5 -type f -perm -u+x -regex '.*/bin/python3\.[0-9]+' 2>/dev/null >> "$PYS"
done
sort -u "$PYS" -o "$PYS"
echo "  발견한 인터프리터 $(wc -l < "$PYS" | tr -d ' ')개:"
sed 's/^/    /' "$PYS"

hr "[1] 인터프리터별 확인 (pip 있으면 pip, 없으면 site-packages 직접)"
while read -r PY; do
  [ -x "$PY" ] || continue
  VER="$("$PY" -V 2>&1 | head -1)"
  OUT="$("$PY" -m pip show "$PKG" 2>/dev/null | sed -n 's/^\(Version\|Location\|Required-by\): /      \1: /p')"
  if [ -n "$OUT" ]; then
    echo "  ★ $PY  ($VER)"; echo "$OUT"
    "$PY" -m pip show "$PKG" 2>/dev/null | sed -n 's/^Version: //p' | sed "s|^|$PY |" >> "$HITS_FILE"
    continue
  fi
  # pip 이 없거나 pip 이 못 보는 경우 → 이 인터프리터가 실제로 import 하는 경로를 직접 뒤진다.
  SP="$("$PY" -c 'import site,sys;print("\n".join(site.getsitepackages()+[site.getusersitepackages()]))' 2>/dev/null)"
  FOUND=""
  while read -r D; do
    [ -d "$D" ] || continue
    for M in "$D"/${PKG_LC}-*.dist-info "$D"/${PKG_LC}-*.egg-info; do
      [ -e "$M" ] || continue
      V="$(basename "$M" | sed -E "s/^.*-([0-9][^-]*)\.(dist|egg)-info$/\1/")"
      echo "  ★ $PY  ($VER)  → $M"; echo "      Version: $V"
      echo "$PY $V" >> "$HITS_FILE"; FOUND=1
    done
  done <<< "$SP"
  [ -z "$FOUND" ] && echo "    $PY  ($VER) → 없음"
done < "$PYS"

hr "[2] 파일시스템 전수 — 마운트별로(다른 파일시스템 건너뛰기 함정 회피)"
MPS="$(awk '$3!~/^(proc|sysfs|devtmpfs|devpts|tmpfs|cgroup|cgroup2|overlay|squashfs|fuse.*|nsfs|mqueue|debugfs|tracefs|securityfs|pstore|bpf|configfs|autofs)$/{print $2}' /proc/mounts 2>/dev/null | sort -u)"
[ -z "$MPS" ] && MPS="/"
echo "  훑을 마운트: $(echo "$MPS" | tr '\n' ' ')"
for M in $MPS; do
  find "$M" -xdev -maxdepth 9 -type d \
       \( -iname "${PKG_LC}-*.dist-info" -o -iname "${PKG_LC}-*.egg-info" \) 2>/dev/null | while read -r D; do
    V="$(basename "$D" | sed -E "s/^.*-([0-9][^-]*)\.(dist|egg)-info$/\1/")"
    INST="$(cat "$D/INSTALLER" 2>/dev/null | tr -d '\n')"
    WHEN="$(stat -c '%y %U' "$D" 2>/dev/null || stat -f '%Sm %Su' "$D" 2>/dev/null)"
    echo "  ★ $D"
    echo "      Version: $V   설치도구: ${INST:-불명}   생성: $WHEN"
    [ -f "$D/direct_url.json" ] && echo "      출처: $(cat "$D/direct_url.json")"
    echo "FS $V" >> "$HITS_FILE"
  done
done

hr "[3] 돌고 있나 · 포트 · 서비스 · 컨테이너"
ps -ef 2>/dev/null | grep -i "[${PKG_LC:0:1}]${PKG_LC:1}" | grep -v find_package | sed 's/^/  ★ proc: /' || echo "    실행중 프로세스 없음"
{ command -v ss >/dev/null && ss -lntp 2>/dev/null || netstat -lntp 2>/dev/null; } | grep -iE ":4000|$PKG_LC" | sed 's/^/  ★ port: /' || true
systemctl list-units --all --no-pager 2>/dev/null | grep -i "$PKG_LC" | sed 's/^/  ★ svc: /' || true
for RT in docker podman "sudo podman"; do
  $RT ps -a --format '{{.Image}} {{.Names}} {{.Status}}' >/dev/null 2>&1 || continue
  $RT ps -a --format '{{.Image}} {{.Names}} {{.Status}}' 2>/dev/null | grep -i "$PKG_LC" | sed "s|^|  ★ $RT ctr: |"
  $RT images 2>/dev/null | grep -i "$PKG_LC" | sed "s|^|  ★ $RT img: |"
  # ★ 이미지 이름에 안 들어가도 컨테이너 '안' 에 깔려 있을 수 있다 — Airflow 가 대표적이다.
  $RT ps -a --format '{{.Names}}' 2>/dev/null | while read -r C; do
    [ -z "$C" ] && continue
    O="$($RT exec "$C" sh -lc "python -m pip show $PKG 2>/dev/null | sed -n 's/^Version: //p'" 2>/dev/null | tr -d '\r')"
    [ -n "$O" ] && { echo "  ★ 컨테이너 $C → $PKG $O"; echo "CTR:$C $O" >> "$HITS_FILE"; }
  done
done

hr "[4] 누가 언제 깔았나"
grep -rns "$PKG_LC" /root/.bash_history /home/*/.bash_history 2>/dev/null | tail -8 | sed 's/^/  /' || echo "    히스토리 흔적 없음"
ls -la /var/log/*pip* 2>/dev/null | sed 's/^/  /' || true

hr "판정"
if [ ! -s "$HITS_FILE" ]; then
  echo "  이 서버에서 $PKG 를 못 찾았다."
  echo "  그래도 스캐너가 잡는다면 리포트의 **대상 경로**를 보라 — 컨테이너 이미지 레이어이거나"
  echo "  이 스크립트가 안 훑은 마운트일 수 있다. 경로를 알면 그 디렉토리를 직접 ls 하면 끝난다."
else
  echo "  발견:"
  sort -u "$HITS_FILE" | sed 's/^/    /'
  if [ -n "$SAFE" ]; then
    echo
    while read -r WHERE V; do
      [ -z "${V:-}" ] && continue
      if ver_lt "$V" "$SAFE"; then echo "    ⚠ $WHERE : $V  <  $SAFE  → 조치 필요"
      else echo "    ✅ $WHERE : $V  >=  $SAFE  → 안전"; fi
    done < <(sort -u "$HITS_FILE")
  fi
  echo
  echo "  다음 단계:"
  echo "    · 어느 도구가 끌고 왔나 →  <그 python> -m pip show $PKG   의 Required-by"
  echo "    · 그 도구를 쓴다        →  <그 python> -m pip install -U '$PKG>=${SAFE:-최신}'"
  echo "    · 뭔지 모르겠다/안 쓴다 →  제거가 낫다. 안 쓰는 패키지는 다음 CVE 때 또 걸린다."
  echo "    ⚠ Airflow 가 쓰는 인터프리터라면 업그레이드 전 의존성 충돌을 먼저 본다:"
  echo "        <그 python> -m pip install --dry-run -U '$PKG>=${SAFE:-}' 2>&1 | tail -20"
fi
