#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
export CPU_FEATURES=0
export NODE_OPTIONS=--no-addons
export PATH="$ROOT/node_modules/.bin:$PATH"
need() { command -v "$1" >/dev/null || { echo "Missing tool: $1. Run npm ci in $ROOT." >&2; exit 1; }; }
device_arg() {
  if (( $# > 1 )); then echo "Usage: $0 [registered-device-name]" >&2; exit 2; fi
  DEVICE="${1-myTV}"
  [[ "$DEVICE" =~ ^[a-zA-Z0-9][a-zA-Z0-9_-]*$ ]] || { echo 'Invalid registered device name' >&2; exit 2; }
}
