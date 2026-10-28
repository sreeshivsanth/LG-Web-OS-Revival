#!/usr/bin/env bash
source "$(dirname -- "${BASH_SOURCE[0]}")/common.sh"
device_arg "$@"
bash "$ROOT/scripts/check-device.sh" "$DEVICE"
bash "$ROOT/scripts/build.sh"
bash "$ROOT/scripts/install.sh" "$DEVICE"
bash "$ROOT/scripts/launch.sh" "$DEVICE"
echo 'Launch command succeeded. Confirm picture, focus, OK and Back on the physical TV.'
