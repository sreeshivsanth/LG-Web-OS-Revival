# Existing Fire TV Stick: first-class external device

The owner already has a Fire TV Stick connected to this LG TV. Do not recommend
buying another device or treat it as a hypothetical future accessory.

LG mode uses independent TV network pairing. Fire TV shortcut will select the
configured LG HDMI input. Future Fire TV mode will be a separate optional adapter
with its own authorization, never Developer Mode SSH or LG client-key reuse.

Configuration plan: query the community LG network tv/getExternalInputList after
basic Android physical acceptance, let the user identify the Fire TV input by its
label and physical verification, and persist that returned input ID locally.
Do not assume HDMI1 or silently choose an input. Input switching uses the separate
experimental tv/switchInput capability only after permission/device validation.
The prominent launcher tile should read “Fire TV · HDMI [configured label]” and
say “Select input first” until configured. It switches input, not a Fire TV app.
No implementation currently claims to perform this action.

Modern streaming services should prefer the existing Fire TV when native LG
support is absent/obsolete. Availability of each Fire TV app, account/region and
subscription still requires checking on the Stick. Never sideload APKs onto webOS.

## Separate physical gates (all UNTESTED)

Record LG firmware, Fire TV model/Fire OS version and actual HDMI port. Inspect
SIMPLINK settings on LG and HDMI-CEC Device Control on Fire TV without assuming
labels or behavior are identical across firmware. Preserve existing settings.

| Capability | Test | Status |
|---|---|---|
| LG network input switch | From another input, request configured HDMI; verify picture | UNTESTED |
| Automatic CEC input switch | Press Fire TV Home while on another input | UNTESTED |
| Power synchronization | Test each power direction separately; record standby settings | UNTESTED |
| Fire TV wakes LG | With LG in standby, wake Fire TV; distinguish CEC from remote IR | UNTESTED |
| LG remote navigates Fire TV | On Fire TV HDMI, test arrows/OK/Back individually | UNTESTED |
| Volume through LG | Confirm LG volume changes; distinguish remote IR from CEC/network | UNTESTED |

Repeat relevant tests with CEC disabled if needed to establish the actual mechanism,
then restore the user's settings. Failure of CEC must not disable LG network
D-pad/audio controls. No assumption of app-level Fire TV navigation or power-on.

Official references for later current documentation review:
https://www.lg.com/us/support (SIMPLINK for this model/region)
https://www.amazon.com/gp/help/customer/display.html (Fire TV HDMI-CEC support)
These landing pages are research pointers, not evidence of verified interoperability.
Direct Fire TV remote integration is deferred; do not enable unauthenticated ADB
or expose device debug/control ports as a shortcut.
