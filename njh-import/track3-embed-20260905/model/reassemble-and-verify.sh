#!/usr/bin/env bash
set +x
set -euo pipefail
fail() { printf 'FAIL: %s\n' "$1" >&2; exit 1; }
root="$(cd "$(dirname "$0")" && pwd)"
[[ $# -eq 1 ]] || fail 'Usage: bash reassemble-and-verify.sh NEW_OUTPUT_DIRECTORY'
destination="$1"
[[ ! -e "$destination" ]] || fail 'Output directory already exists; refusing to overwrite'
[[ -n "${NJH_RELEASE_ARCHIVE_PASSWORD:-}" ]] || fail 'Set NJH_RELEASE_ARCHIVE_PASSWORD in the environment'
case "$NJH_RELEASE_ARCHIVE_PASSWORD" in *$'\r'*|*$'\n'*) fail 'Password must not contain line breaks';; esac
if command -v 7zz >/dev/null 2>&1; then sevenzip=7zz
elif command -v 7z >/dev/null 2>&1; then sevenzip=7z
else fail 'Install 7-Zip and put 7zz or 7z on PATH'; fi
hash_file() {
  if command -v sha256sum >/dev/null 2>&1; then sha256sum "$1"
  else shasum -a 256 "$1"; fi | cut -d ' ' -f 1
}
[[ "$(wc -l < "$root/source.tsv" | tr -d '[:space:]')" == 1 ]] || fail 'Expected exactly one source record'
IFS=$'\t' read -r source_sha source_bytes source_name extra < "$root/source.tsv"
[[ "$source_sha" =~ ^[0-9a-f]{64}$ && "$source_bytes" =~ ^[1-9][0-9]*$ && "$source_name" =~ ^[A-Za-z0-9_-]+\.tar\.gz$ && -z "$extra" ]] || fail 'Invalid source metadata'
count=0
total=0
first=''
while IFS=$'\t' read -r digest bytes name extra; do
  [[ "$digest" =~ ^[0-9a-f]{64}$ && "$bytes" =~ ^[1-9][0-9]*$ && -z "$extra" ]] || fail 'Invalid part metadata'
  count=$((count + 1))
  printf -v expected '%s.7z.%03d' "${source_name%.tar.gz}" "$count"
  [[ "$name" == "$expected" ]] || fail 'Part sequence or filename mismatch'
  [[ -f "$root/$name" && ! -L "$root/$name" ]] || fail "Missing regular part: $name"
  [[ "$bytes" -le 90000000 ]] || fail "Part exceeds 90 MB: $name"
  actual_bytes="$(wc -c < "$root/$name" | tr -d '[:space:]')"
  [[ "$actual_bytes" == "$bytes" ]] || fail "Part byte count mismatch: $name"
  [[ "$(hash_file "$root/$name")" == "$digest" ]] || fail "Part SHA-256 mismatch: $name"
  [[ -n "$first" ]] || first="$name"
  total=$((total + bytes))
done < "$root/parts.tsv"
[[ "$count" -gt 0 ]] || fail 'No parts declared'
shopt -s nullglob
physical_parts=("$root/"*.7z.*)
[[ "${#physical_parts[@]}" -eq "$count" ]] || fail 'Undeclared or missing volume'
stage="$(mktemp -d "${TMPDIR:-/tmp}/njh-reassembly.XXXXXXXX")"
trap 'rm -rf -- "$stage"' EXIT
printf '%s\n' "$NJH_RELEASE_ARCHIVE_PASSWORD" | (unset NJH_RELEASE_ARCHIVE_PASSWORD; "$sevenzip" x -y "-o$stage" "$root/$first")
[[ -f "$stage/$source_name" && ! -L "$stage/$source_name" ]] || fail 'Archive does not contain the expected regular source file'
[[ "$(find "$stage" -mindepth 1 | wc -l | tr -d '[:space:]')" == 1 ]] || fail 'Archive contains unexpected entries'
[[ "$(wc -c < "$stage/$source_name" | tr -d '[:space:]')" == "$source_bytes" ]] || fail 'Reassembled source byte count mismatch'
[[ "$(hash_file "$stage/$source_name")" == "$source_sha" ]] || fail 'Reassembled source SHA-256 mismatch'
mkdir -- "$destination"
mv -- "$stage/$source_name" "$destination/$source_name"
printf 'PASS: parts=%s encrypted_bytes=%s source_bytes=%s sha256=%s output=%s\n' "$count" "$total" "$source_bytes" "$source_sha" "$destination/$source_name"
