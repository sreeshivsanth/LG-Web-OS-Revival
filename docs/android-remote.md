# Android foundation

Native Kotlin, minSdk 26 (Android 8), compile/target SDK 35, AGP 8.7.3, Kotlin
2.0.21, Gradle 8.9, JDK 21. SDK 35 is a conservative reproducible baseline, not a
claim to the newest store target in 2026. Upgrade review is required before store
distribution. No Play Store publication or release signing is configured.

Open mobile/android in Android Studio. Install Android SDK Platform 35 and its
build tools through SDK Manager. Set the local SDK path via Android Studio
(local.properties is ignored). From that directory:

```bash
./gradlew :core:test :app:assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

No SDK was preinstalled in cloud; official SDK tools were installed with published
checksums verified. Core tests cover protocol validation, pairing/key reuse,
reconnection and malformed responses using mocked sockets. Full timeout/offline
coverage, Keystore instrumentation and accessibility device tests remain to expand.
Android 8 is the chosen minimum to keep platform security and networking behavior
predictable; older Android versions are not supported by this initial client.

Enter TV address, choose secure connect. If its certificate is rejected, see
protocol-research.md. Explicit legacy LAN mode is available with a consent dialog;
it is unencrypted. Pair on TV. Controls become available only after registration,
and navigation additionally requires the pointer channel. Use reconnect after
returning to the foreground. No persistent background connection.

## Cloud validation — 2026-10-28

`./gradlew :core:test :app:assembleDebug` passed using JDK 21, SDK Platform 35
and AGP-selected Build Tools 34. Nine core tests passed (seven protocol validation,
two mocked connection tests). APK: app/build/outputs/apk/debug/app-debug.apk.
Hello webOS's four tests also passed; its source and manifest remain unchanged.
No Android emulator, phone or TV execution has been performed. The full runtime
and accessibility behavior still needs physical validation.
