# Physical acceptance history

Target: LG 32LK610BPVA, webOS SDK 4.4.0, firmware 05.50.70.
Results below are owner reports, not cloud device tests.

## Preserved results

| Test | Result |
|---|---|
| Hello webOS: all six original acceptance checks | PASS |
| Android legacy LAN pairing, prompt approved | PASS |
| Up / Down / Left / Right | PASS each |
| OK / Back / Home | PASS each |
| Volume Up / Volume Down | PASS each |
| Mute / Unmute | PASS each |
| Secure TLS connection | FAIL / UNDER INVESTIGATION |
| Original Android remote labels | FAIL: clipped on physical phone |

The label defect has a code fix and automated layout tests. Physical retest remains
UNTESTED; the original failure is retained. Pairing persistence, foreground/restart,
Fedora-off and WAN-off operation were not included in the reported PASS list.

## New touchpad checks — all UNTESTED

1. Open Touchpad; verify disconnected message, connect, then pointer-ready state.
2. Slow horizontal, vertical and diagonal movement; precision over small targets.
3. Fast movement, low/high sensitivity, acceleration off/on; check long-duration use.
4. Short tap selects; drag, long touch, cancelled gesture and multitouch never click.
5. Two fingers scroll up/down; lift either finger and confirm no accidental click.
6. Disconnect during gesture, reconnect: no movement is replayed.
7. Background/foreground, process restart, Wi-Fi off/on, TV offline/online and AP roaming.
8. Test with Fedora powered off and WAN disconnected while local Wi-Fi stays active.

## New input checks — all UNTESTED

1. Refresh external inputs. Record actual IDs privately without credentials.
2. Identify HDMI 1 and HDMI 2; HDMI 3 only if physically present/returned.
3. Switch each returned HDMI input and verify picture after acknowledgement.
4. Return to Live TV only if it is returned as a usable input; otherwise use Home/TV remote.
5. Assign the actual Fire TV input; test Fire TV quick action from Remote.
6. Restart app; confirm assignment persists. Reconnect, refresh and switch again.
7. Missing/removed assignment opens Inputs and asks for reassignment, never guesses.
8. Current-input highlighting is NOT IMPLEMENTED; do not record a false PASS.
9. Reject input permission / disconnect during switch; verify clear failure/unknown result.

## UI retest — UNTESTED

Small phone, larger font/display size, portrait/landscape, gesture/three-button system
navigation: labels centered and fully readable, controls >=48dp, system bars clear,
scrolling works outside touchpad. Check TalkBack and haptics disabled in system settings.

## Fire TV SIMPLINK / HDMI-CEC — all UNTESTED

Test independently: Fire TV wakes LG; automatic input switch; LG remote navigates
Fire TV; LG power-off effect on Stick; volume routing; SIMPLINK enabled/disabled.
Record settings and restore them. Distinguish IR from CEC. No CEC assumption is part
of LG network control. Direct Fire TV control is deferred.
