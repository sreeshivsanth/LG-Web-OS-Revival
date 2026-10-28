# Protocol evidence and limits — 2026-10-28

Sources inspected:
- LG-origin Connect SDK Android registration implementation:
  https://github.com/ConnectSDK/Connect-SDK-Android-Core/blob/master/src/com/connectsdk/service/webos/WebOSTVServiceSocketClient.java
  It constructs manifestVersion/permissions and registers without a copied signed
  test identity. This is historical SDK evidence, not a current webOS 4.4 guarantee.
- Community implementation:
  https://github.com/bendavid/aiopylgtv/blob/master/aiopylgtv/webos_client.py
  https://github.com/bendavid/aiopylgtv/blob/master/aiopylgtv/endpoints.py
  Confirms register/registered, client-key, ssap request framing, pointer socket
  and type:button newline framing. Its test signature and broad privileged
  permission list are deliberately not copied.

Official LG webOS TV pages could not be retrieved: environment proxy returned
403 for webostv.developer.lge.com. References requiring review:
- https://webostv.developer.lge.com/develop/references/application-manager
- https://webostv.developer.lge.com/develop/references/appinfo-json
- https://webostv.developer.lge.com/develop/guides/acg-guide
- https://webostv.developer.lge.com/develop/guides/back-button

Do not equate network SSAP endpoints with public in-app Luna API authorization.
App enumeration, launching another app, external URL opening, system settings
and default Home registration remain unconfirmed for ordinary developer apps on
this exact target. No such integration is implemented. No default Home claim.
Hello requiredACG is unchanged pending official guidance and target validation;
CLI's empty-array recommendation alone does not prove legacy compatibility.

Implemented experimental network subset: unsigned PROMPT registration, client-key
reuse, pointer socket acquisition, UP/DOWN/LEFT/RIGHT/ENTER/BACK/HOME, audio
volumeUp/volumeDown/setMute. Request IDs correlate acknowledgements; pointer input
has no action acknowledgement. Minimal permissions may be refused on this TV;
report refusal instead of adding a test signature or privilege workaround.

wss:3001 uses standard certificate/hostname checks. Self-signed certificates will
fail; no trust-all manager or implicit fallback. Explicit user consent enables
ws:3000 only for RFC1918 IPv4. This exposes keys/commands to the trusted LAN and is
not encrypted. Scoped certificate enrollment/pinning is future work, not claimed.
Pointer URLs must match the selected IP and allowed scheme/port; redirects are
not followed. Pairing timeout 45s; command timeout 8s; connection timeout 10s.

Subscription/unsubscribe framing exists in SDK evidence but is not implemented.
Discovery, app APIs, text entry, playback, channels, power and Wake-on-LAN
remain later investigations. No public internet or device scans.


## Touchpad and input extension

The same community webos_client.py source confirms:
- move: type:move, dx, dy, down:0, newline-delimited with blank terminator.
- click: type:click with blank terminator; no right-button field demonstrated.
- scroll: type:scroll, dx:0, dy, blank terminator.
- tv/getExternalInputList returns devices with IDs; tv/switchInput takes inputId.
No proprietary signing identity added. Registration now additionally requests
READ_INPUT_DEVICE_LIST and CONTROL_INPUT_TV. Existing approved keys are reused;
permission denial is reported, not worked around. Basic controls have owner-reported
PASS results; pointer motion/click/scroll and inputs remain ready for physical test.

Current input is not inferred from the last requested switch. Community get_input
maps foreground application; that is insufficient evidence for reliable HDMI state.
No CURRENT_INPUT integration. Right click/long press not supported in this client.

TLS diagnostics distinguish TCP, timeout, identity, certificate cause, handshake and
generic WebSocket failures without printing exception messages or TV credentials.
Some TLS causes overlap; no TLS-version assertion without diagnostic evidence.
No fingerprint capture, trust-on-first-use or per-TV pin enrollment is implemented.
