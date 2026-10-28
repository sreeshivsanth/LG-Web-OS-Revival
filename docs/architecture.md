# LG Revival architecture

Phase 1 foundation: native Android -> TV directly over trusted LAN. No laptop,
cloud, account, telemetry or gateway participates. Fedora will be a separate client
in a later phase, not a server required by Android. Hello webOS is preserved.
User reports all six Hello physical acceptance checks PASS (2026-10-28).

Android has a pure Kotlin protocol/network core and a small native Activity UI.
The Emerald Citadel palette is original: charcoal and emerald, no licensed art.
Network state is serialized on one worker, events delivered to the UI thread.
Leaving the foreground closes sockets; reconnect is explicit and reuses encrypted
credentials. No background service, discovery loop or automatic pairing loop.

Multiple pairing credentials are indexed by manually selected IP; last address is
remembered. A named saved-TV picker, model querying and automatic discovery remain
future UX work. No fabricated model or connection capability claims.

Phase gates: Android physical test -> launcher MVP -> advanced protocol features
-> independent Fedora client -> streaming integration. The latest master mission
supersedes the previous launcher-first order and branch name.

The existing Amazon Fire TV Stick is a first-class HDMI device. See fire-tv.md.
LG network control, input switching, CEC and optional direct Fire TV control are
separate capabilities and trust boundaries. Core LG controls never depend on CEC.


Owner subsequently verified legacy pairing and all eleven basic controls. The current
milestone extends Android with Touchpad (GestureInterpreter/MotionBuffer), Inputs,
FireTvAssignment and redacted Diagnostics. The existing Remote transport and encrypted
store remain. Remote sockets serialize state on one worker; gesture coalescing is
foreground-only. TV launcher and independent Fedora client remain deferred.
