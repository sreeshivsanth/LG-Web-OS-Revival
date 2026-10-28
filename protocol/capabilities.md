# Capability matrix

Evidence levels: official LG documented, historical LG-origin SDK, community
documented, experimentally implemented, physically verified, unsupported.
Cloud implementation/build tests do not establish physical capability.

| Feature | Evidence | Physical state |
|---|---|---|
| Hello webOS | Owner report, original six tests | PASS |
| Legacy pairing | Historical SDK + owner report | PASS |
| D-pad / OK / Back / Home | Community framing + owner report | PASS |
| Volume +/- / mute / unmute | Community endpoints + owner report | PASS |
| Secure transport | Standard TLS checks | FAIL / under investigation |
| Relative pointer / click / vertical scroll | Community framing, implemented and unit tested | READY FOR PHYSICAL TEST |
| INPUT_LIST / INPUT_SWITCH | Community SSAP, implemented and unit tested | READY FOR PHYSICAL TEST |
| Fire TV assignment / shortcut | Local association + INPUT_SWITCH | READY FOR PHYSICAL TEST |
| CURRENT_INPUT | Foreground app is not reliably equivalent to physical input | UNTESTED, not implemented |
| HDMI_CEC | Separate inter-device behavior | UNTESTED, not implemented |
| Right click / meaningful long press | No supported semantics established | UNSUPPORTED in this client |
| Apps / channels / playback / power / wake | Deferred | UNTESTED |

Availability at runtime additionally requires registration and, for navigation and
touchpad, an open pointer socket. Inputs use exact IDs discovered in the current
connection; reconnect clears discovery. Errors/timeouts are never treated as success.
No ordinary TV-app Luna permissions are inferred from remote SSAP evidence.
