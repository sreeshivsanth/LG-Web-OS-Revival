# Capability matrix

| Capability | Evidence | Implementation | Exact TV status |
|---|---|---|---|
| Hello webOS | User physical report | Existing app preserved | PASS, six checks |
| Pairing and client-key reuse | Historical LG-origin SDK + community | Experimental | UNTESTED |
| D-pad, OK, Back, Home | Community pointer framing | Experimental, gated on pointer socket | UNTESTED |
| Volume, mute/unmute | Community SSAP endpoints | Experimental, rejects/timeouts reported | UNTESTED |
| TLS | Standard verified transport | Self-signed TV certificates rejected | UNTESTED |
| Plaintext LAN | Community ws:3000 | Explicit consent, private IP only | UNTESTED |
| App enumeration/launch, inputs | Community endpoints; in-app rights distinct | Deferred | UNTESTED |
| Channels, playback, text, pointer motion | Community evidence, incomplete audit | Deferred | UNTESTED |
| Power-off | Separate from wake | Deferred | UNTESTED |
| Wake-on-LAN | Needs MAC, standby/Quick Start+, transport investigation | Disabled | UNTESTED |
| System settings / default Home | No verified ordinary app authorization | Not implemented | UNKNOWN |
| Discovery | Manual IP always available | Multicast deferred | UNTESTED |

An available socket is not proof that every key has the intended physical effect.
Unsupported responses disable further dispatch of that command for the session.
Controls are labeled experimental; no remote capability is marked physically verified.
