# Compatibility and validation

Target: LG 32LK610BPVA, SDK 4.4.0, firmware 05.50.70, ARMv7.
Application JavaScript uses ES5 syntax, DOM event listeners and textContent.
CSS uses basic media queries, gradients and focus outlines; no frameworks,
CDNs, animations, fetch, modules or newer browser APIs are required.
Keyboard codes: arrows 37–40, OK 13, Back 461. Back is not prevented and the
manifest leaves webOS default Back handling enabled. There is no app history.
The platform owns exit behavior; confirm on hardware.

Tests execute the real application script in a controlled DOM fixture: focus
and wrapping, greeting/reset/pointer interaction, held OK suppression, Back
pass-through and focus recovery. They do not substitute for browser rendering
or real remote testing. PNG dimensions and required manifest fields are checked;
the official CLI performs packaging validation too.

CLI 3.2.6 warns about missing requiredACG. This app calls no Luna APIs and targets
older webOS 4.4; no modern ACG declaration is added merely to suppress a warning.
Store submission and newer-platform eligibility are outside this milestone.

Cloud package generation and four automated tests passed. Physical installation,
launch, visual rendering and remote behavior have not been verified.
Container image build is not yet verified. Node 22 is the local target; cloud
checks used the available Node 24 runtime, supported by the CLI's >=20 engine range.

Official references:
- https://webostv.developer.lge.com/develop/references/appinfo-json
- https://webostv.developer.lge.com/develop/guides/back-button
- https://webostv.developer.lge.com/develop/tools/cli-introduction

Launcher milestone remains gated on user approval. Investigate official app
launch permissions before implementation; do not assume system launcher replacement.
