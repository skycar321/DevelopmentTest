#!/bin/bash
# Node 포터블 재조립: 파트 합치기 → SHA 검증 → 해제
set -e
cat node-v22.23.1-linux-x64.tar.xz.part-* > node-v22.23.1-linux-x64.tar.xz
echo "재조립 완료. SHA256 검증:"
shasum -a 256 -c SHA256SUMS --ignore-missing 2>/dev/null || sha256sum -c SHA256SUMS --ignore-missing
tar xf node-v22.23.1-linux-x64.tar.xz
echo "완료: ./node-v22.23.1-linux-x64/bin/node"
./node-v22.23.1-linux-x64/bin/node --version
