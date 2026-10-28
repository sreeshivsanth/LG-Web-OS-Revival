#!/usr/bin/env bash
source "$(dirname -- "${BASH_SOURCE[0]}")/common.sh"
device_arg "$@"; need ares-install
ipk="$ROOT/dist/com.example.hellowebos_1.0.0_all.ipk"
[[ -s "$ipk" ]] || { echo 'No IPK. Run scripts/build.sh first.' >&2; exit 1; }
ares-install --device "$DEVICE" "$ipk"
