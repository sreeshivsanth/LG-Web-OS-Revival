#!/usr/bin/env bash
source "$(dirname -- "${BASH_SOURCE[0]}")/common.sh"
device_arg "$@"; need ares-device
ares-device --system-info --device "$DEVICE"
