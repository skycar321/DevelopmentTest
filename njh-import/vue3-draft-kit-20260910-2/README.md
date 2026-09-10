# Vue 3 draft kit — 2026-09-10 rev 2 (encrypted, lab tip 72d84cf5)

This directory publishes one archive, `vue3-draft-kit-20260910-2.7z`. It supersedes `vue3-draft-kit-20260910`.

- Format: 7z, AES-256, **encrypted headers** (`-mhe=on`). Without the password neither the file list nor the contents can be read.
- Password: the same archive password as the `njh-cli` kits in this channel. It is delivered separately, never through this repository.
- Integrity: compare against `sha256.txt` before extracting.

```bash
shasum -a 256 -c sha256.txt          # or: certutil -hashfile vue3-draft-kit-20260910-2.7z SHA256
7z x -p vue3-draft-kit-20260910-2.7z # enter the password when prompted
```

What changed since the previous revision:

- The runbook no longer creates a draft commit tag. The "has a developer edited this screen" test now compares each screen against a recorded hash of the generated draft. The tag version answered correctly only for the first catch-up round: replacing an untouched screen with a newly generated one moves it away from the tagged commit, so from the second round every screen read as hand-edited. The hash record is refreshed at the end of each round, so it stays correct.
- The runbook now targets the current client kit version published in this channel.

After extraction, start with the Korean README inside the extracted folder. It explains the import order:
install the matching client kit, regenerate the library decision sheet, prepare the A/B trees, run the one-command draft, compare, and report gaps.

Contents (high level): the migration skill bundle at the lab integration tip named in `delivery-line.json`, the library decision documents,
the A/B dual-runtime procedure, the draft gap-report tool, and reference analysis lists.







cd /c/work/ui-web          # as-is 실제 경로로 바꾸세요
git fetch origin --prune && git rev-parse --short origin/dev
node -p "const p=require('./package.json');JSON.stringify({name:p.name,vue:p.dependencies.vue,vuetify:p.dependencies.vuetify,scripts:p.scripts,extra:['optionalDependencies','peerDependencies','overrides','resolutions'].filter(k=>p[k])},null,1)"
node -v; npm -v
sed -n 's/^registry=//p' .npmrc
ls node_modules | wc -l
git status --short | wc -l





git remote -v
git config --get credential.helper
git config --get-regexp '^url\.' ; echo "(빈 출력이면 url 치환 없음)"
ls ~/.ssh/*.pub 2>/dev/null || echo "SSH 키 없음"
cat .npmrc 2>/dev/null; echo "--- 위가 프로젝트 .npmrc"
cat ~/.npmrc 2>/dev/null; echo "--- 위가 사용자 .npmrc"
npm config get registry
git log --oneline -1 dev
git rev-parse --abbrev-ref HEAD









echo "=== 1) 설치된 파이썬 패키지"
for py in python3 python /usr/bin/python3 /usr/local/bin/python3; do
  command -v $py >/dev/null 2>&1 && { echo "-- $py ($($py -V 2>&1))"; $py -m pip list 2>/dev/null | grep -i litellm; }
done
command -v pipx >/dev/null && { echo "-- pipx"; pipx list 2>/dev/null | grep -i litellm; }
command -v conda >/dev/null && { echo "-- conda"; conda list 2>/dev/null | grep -i litellm; }

echo "=== 2) 파일시스템 전수(가상환경 포함)"
find / -xdev \( -name "litellm" -o -name "litellm-*.dist-info" \) -maxdepth 9 2>/dev/null | head -20

echo "=== 3) 돌고 있는지 · 포트"
ps aux | grep -i "[l]itellm"
ss -lntp 2>/dev/null | grep -E ":4000|litellm"

echo "=== 4) 서비스 · 컨테이너"
systemctl list-units --all 2>/dev/null | grep -i litellm
command -v docker >/dev/null && { docker ps -a --format '{{.Image}} {{.Names}} {{.Status}}' | grep -i litellm; docker images | grep -i litellm; }

echo "=== 5) 누가 언제 깔았나"
find / -xdev -name "litellm-*.dist-info" -maxdepth 9 2>/dev/null | while read -r d; do
  stat -c '%y %U %n' "$d"; cat "$d/INSTALLER" 2>/dev/null; done
grep -rn "litellm" ~/.bash_history /root/.bash_history 2>/dev/null | tail -5
