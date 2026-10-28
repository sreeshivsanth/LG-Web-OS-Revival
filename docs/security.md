# Security and threat model

No SSH key reuse, telemetry, account, cloud relay or internet control. Keys are
encrypted with AES-GCM using a non-exportable Android Keystore key, with the TV IP
as authenticated data. Android backups are disabled. Storage failures close the
connection rather than falling back to plaintext preferences. Screens/logs never
include raw TV messages or credentials. Forget and TV-side revocation are separate.

Threats: another LAN member can impersonate a TV or observe plaintext legacy
traffic; use a trusted LAN, not guest/shared Wi-Fi. A stolen unlocked phone can
control paired TVs; use device lock and revoke pairing. Keystore protection does
not defend a fully compromised running phone. Debug builds are not release builds.

TLS checks are never disabled. Legacy ws requires explicit per-connect consent.
Android manifest permits cleartext because addresses are dynamic, but the only
network adapter enforces private IP and same-TV pointer URL/port constraints.
Future network features must retain these checks. No public endpoint accepts keys.
No blind trust of returned socketPath. Certificate pin enrollment is not implemented.

Audit of existing pinned CLI: 6 npm findings (1 critical, 4 high, 1 moderate) in
@webos-tools/cli, body-parser, brace-expansion, js-yaml, proxy-addr and qs. These are
build-time transitive tools, not bundled into Hello webOS. No forced fixes applied;
npm suggests even a CLI downgrade, which is not a validated remediation. Avoid
running CLI web servers on untrusted interfaces or feeding it untrusted packages.
Review upstream patched CLI compatibility in a separate toolchain change. Android
OkHttp/Gradle dependencies require their own ongoing advisory review; no claim of
complete exploitability assessment or zero vulnerabilities.
