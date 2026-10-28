#!/usr/bin/env bash
source "$(dirname -- "${BASH_SOURCE[0]}")/common.sh"
(( $# == 0 )) || { echo "Usage: $0" >&2; exit 2; }
need node; need npm; need ares-package
cd "$ROOT"
npm test
mkdir -p dist
# Package to a fresh directory so stale artifacts cannot imply success.
tmp="$(mktemp -d "$ROOT/dist/build.XXXXXX")"
trap 'rm -rf -- "$tmp"' EXIT
ares-package "$ROOT/apps/hello-webos" --outdir "$tmp"
ipk="$tmp/com.example.hellowebos_1.0.0_all.ipk"
[[ -s "$ipk" ]] || { echo 'Expected IPK was not generated' >&2; exit 1; }
mv -- "$ipk" "$ROOT/dist/"
echo "Package ready: $ROOT/dist/com.example.hellowebos_1.0.0_all.ipk"
