# LG Revival Remote — touchpad and inputs

Native Kotlin, minSdk 26, SDK 35, JDK 21, Gradle 8.9, AGP 8.7.3. Existing baseline
preserved. This is a debug/test build, not a store release. Android connects directly
to the TV; no Fedora/Podman/cloud relay. No analytics or mandatory internet.

```bash
cd mobile/android
./gradlew :core:test :app:testDebugUnitTest :app:assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

APK: app/build/outputs/apk/debug/app-debug.apk. Test source includes protocol,
mocked connections, gestures, input parsing/acknowledgements, Fire TV preference
persistence and small-screen Robolectric layout checks. Physical testing remains
necessary; see physical-test-matrix.md.

## Screens

Remote preserves physically verified D-pad, OK, Back, Home and audio controls.
Buttons now use density-independent minimum sizes and content-wrapping height,
centered multiline labels, system bar/cutout insets and a vertical scroll container.
Settings provides explicit secure/legacy connection, disconnect, forget and touchpad
preferences. Every legacy reconnect requires consent; secure failures never fall back.
App backgrounding closes both sockets. Returning requires explicit reconnect, avoiding
storms and surprise pairing prompts. Connection generation guards discard stale callbacks.

Touchpad sends relative dp deltas only. Gesture states: IDLE, POSSIBLE_TAP,
DRAGGING, SCROLLING, CANCELLED. Tap <=250ms and motion <8dp; >=8dp becomes movement.
After drag threshold, jitter accumulates until >=0.5dp. Sensitivity 1–10 (default 5),
optional 1.5x acceleration above 8dp per sample (default off), per-sample clamp 80.
Movement coalesces to at most 30 frames/sec (34ms), retaining fractional deltas.
Per-frame move clamp 80, backlog bounded to 640 to prevent delayed tails; saturation
intentionally drops excess. Two-finger centroid vertical scroll has independent 1–10
sensitivity, clamp 10 per frame/backlog 40. Third finger, pointer removal, cancel,
disconnect and screen change cancel gestures. No mouse-button-held drag, right click
or long-press semantics are invented. Tap and remote haptics respect system settings.

Inputs refreshes the device list and shows returned label/id/type/connected state.
No assumed HDMI count or IDs. Select Switch to request that exact ID. Positive
returnValue acknowledgement is required; current displayed input stays unknown.
Fire TV assignment confirms a discovered input and persists it per selected TV IP.
Fire TV action refreshes first; missing association or missing ID opens assignment.
Current-input highlighting and direct Fire TV remote control are not implemented.

## Limitations

IP-scoped credentials/aliases remain the existing foundation's identity model. Use
DHCP reservation; stable device identity and secure credential migration after address
change are future work. No automatic certificate enrollment. TLS diagnosis is redacted
and class-based; handshake errors do not prove a particular certificate cause.
New input permissions may require a fresh TV-approved pairing; do not discard working
credentials automatically. No CEC, Apps or arbitrary privileged service integration.

## Cloud validation of this extension

38 core tests passed, covering verified control encodings, touchpad math/framing,
cancellation/coalescing, malformed input data, persisted assignment, exact switch IDs,
positive/negative/missing acknowledgements, connection loss and stale-motion discard.
Two Robolectric tests passed at 320dp/xhdpi: label bounds and >=48dp touch targets,
plus disconnected Fire TV/settings/input navigation. The debug APK built successfully.
Hello webOS: four regression tests passed and source remains unchanged. These checks
do not replace actual screen, font-size, insets, touchpad or HDMI physical acceptance.
