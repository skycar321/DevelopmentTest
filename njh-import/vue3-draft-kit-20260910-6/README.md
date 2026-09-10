# Vue 3 draft kit — 2026-09-10 rev 6 (encrypted, lab tip 72d84cf5)

This directory publishes one archive, `vue3-draft-kit-20260910-6.7z`. It replaces rev 5.

- Format: 7z, AES-256, **encrypted headers** (`-mhe=on`). Without the password neither the file list nor the contents can be read.
- Password: the same archive password as the client kits in this channel. It is delivered separately, never through this repository.
- Integrity: compare against `sha256.txt` before extracting.

```bash
shasum -a 256 -c sha256.txt          # or: certutil -hashfile vue3-draft-kit-20260910-6.7z SHA256
7z x -p vue3-draft-kit-20260910-6.7z # enter the password when prompted
```

## What changed in rev 6

Each numbered step is now a single script under `run/`, so the operator runs one command per step instead of pasting a block. The scripts fail fast, assert they are in the intended worktree and branch, and were exercised end to end against a real Vue 2 source tree (152 single-file components) before publication: dependency install, one-command draft, publish commit and gap report all completed, and the produced lock file is present in the commit.

Defects fixed since rev 2:

- The publish step deleted the dependency lock file that the install step had just produced, because the draft tree never contains one. Every developer other than the lock owner would have been unable to install. The copy now excludes the lock and asserts it survived.
- The one-command draft has hard preconditions on the source manifest (runtime versions and the exact three build script commands). They were undocumented, so a deviation stopped the run mid-way with an unexplained refusal. A new check reports them before anything is created.
- The registry address was derived from the legacy project configuration. The dependencies for the new stack come from a different repository, so the derivation was wrong; the address is now entered once and passed on the command line, never written into a file that would be committed.
- A follow-up kit's parts update skipped plugins, styles, shared utilities and configuration, and could overwrite locally corrected parts without warning. It now covers every generated path and prints the diff before committing.
- The catch-up procedure judged "has a developer edited this screen" against a commit tag; replacing an untouched screen moved it away from that tag, so from the second round every screen read as edited. It now compares against a recorded hash of the generated draft, refreshed each round. It also lists operational changes outside the screen directory, which were previously dropped silently.
- The kit carried two descriptions of the same procedure that had drifted apart. There is now one, and the offline HTML is generated from it rather than maintained by hand.
- The library decision sheet still listed packages that the manifest no longer ships. It was regenerated from the current manifest.

Contents (high level): the migration skill bundle at the lab integration tip named in `delivery-line.json`, the step scripts, the library decision documents, the dual-runtime procedure, the draft gap-report tool, and reference analysis lists.















source env.sh
grep -B1 -A3 "error during build" "$DRAFT/analysis/draft-project/build.stderr" | head -40
node -e 'const d=JSON.parse(require("fs").readFileSync(process.argv[1],"utf8"));const l=d.screens||[];const by={};for(const s of l)for(const r of(s.refusals||[])){const k=String(r.reason||r.code).slice(0,60);by[k]=(by[k]||0)+1}console.log(Object.entries(by).sort((a,b)=>b[1]-a[1]).slice(0,8).map(([k,v])=>v+"  "+k).join("\n"));console.log("skipped:",l.filter(s=>s.status==="skipped").map(s=>s.file).join(", "))' "$DRAFT/analysis/draft-project/result.json"










source env.sh
node -e '
const d=JSON.parse(require("fs").readFileSync(process.argv[1],"utf8"));
for(const s of (d.screens||[])) for(const r of (s.refusals||[]))
  if(/generated-sfc-invalid|unsupported-sfc-shape|SFC_PARSE_ERROR/.test(String(r.reason||r.code)))
    console.log(String(r.reason||r.code).slice(0,90), "  <-", s.file);
' "$DRAFT/analysis/draft-project/result.json"

그리고 CSS 쪽도 사내에 같은 문제가 몇 개나 있는지 미리 알면 좋습니다. 3차 수정이 그걸 다 덮는지 판단할 수 있습니다.

node -e '
const fs=require("fs"),path=require("path"),root=process.argv[1];
const walk=(d,a=[])=>{for(const e of fs.readdirSync(d,{withFileTypes:true})){const p=path.join(d,e.name);
 if(e.isDirectory()){if(!["node_modules",".git","dist"].includes(e.name))walk(p,a)}else if(/\.(vue|css)$/.test(e.name))a.push(p)}return a};
let n=0;
for(const f of walk(path.join(root,"src")).concat(fs.existsSync(path.join(root,"public"))?walk(path.join(root,"public")):[])){
 const s=fs.readFileSync(f,"utf8");
 const css=f.endsWith(".css")?s:(s.match(/<style[^>]*>([\s\S]*?)<\/style>/g)||[]).join("\n");
 const o=(css.match(/\/\*/g)||[]).length,c=(css.match(/\*\//g)||[]).length;
 if(o!==c){n++;console.log((c-o>0?"짝없는 */ "+(c-o):"안닫힌 /* "+(o-c)), path.relative(root,f))}}
console.log("불균형 파일", n);
' "$ASIS"





