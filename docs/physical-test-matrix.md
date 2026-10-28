# Physical acceptance

User reported Hello webOS: six acceptance checks PASS on LG 32LK610BPVA, SDK 4.4.0,
firmware 05.50.70 (2026-10-28). No new physical tests executed in cloud.

All Android rows below are UNTESTED. Record PASS/FAIL/PARTIAL/UNSUPPORTED after
execution, including phone/Android version and observed error (never client key).

1. Install debug APK on phone; put phone and TV on same trusted LAN.
2. Shut down/disconnect Fedora. Keep it off throughout remote-control tests.
3. Enter TV IP; test secure connection and record certificate outcome.
4. If needed and acceptable, explicitly consent to legacy unencrypted LAN mode.
5. Request pairing; approve on TV. Reject once too and verify honest error feedback.
6. Disconnect/reconnect and confirm saved pairing is reused.
7. Test Up/Down/Left/Right separately and record effects.
8. Test OK, Back and Home separately.
9. Test volume up/down, mute and unmute; record rejected/unsupported actions.
10. Playback is disabled/not implemented; do not mark it PASS.
11. Restart Android app and verify explicit reconnect with saved credential.
12. Remove internet access but retain LAN Wi-Fi; repeat basic controls.
13. Background app, turn Wi-Fi off/on, reconnect; check timeouts and no stale commands.
14. Forget TV, confirm consent prompt returns on a fresh pairing attempt.

Launcher, advanced controls and Fedora gates remain UNTESTED/deferred. Wake-on-LAN
requires separate MAC, network standby, wired/wireless and Quick Start+ testing.
