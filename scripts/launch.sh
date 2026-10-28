#!/usr/bin/env bash
source "$(dirname -- "${BASH_SOURCE[0]}")/common.sh"
device_arg "$@"; need ares-launch
ares-launch --device "$DEVICE" com.example.hellowebos
