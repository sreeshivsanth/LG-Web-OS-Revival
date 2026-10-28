# LG Web OS Revival

Lightweight Hello webOS application for the LG 32LK610BPVA, webOS TV 4.4.
**Hello webOS: physically verified by the owner; all six acceptance checks passed.**

Dark, TV-sized interface with two focusable buttons, wrapping arrow navigation,
OK activation, pointer support, live greeting feedback and platform-owned Back behavior.
No runtime dependencies, network requests or Luna APIs.

```bash
npm ci --ignore-scripts
npm test
npm run build
bash scripts/deploy.sh myTV
```

Deployment requires your already registered TV on your local network. Never put credentials in this repository.
Generated package: `dist/com.example.hellowebos_1.0.0_all.ipk` (ignored by Git).

See [setup](docs/setup.md), [deployment](docs/deployment.md),
[compatibility](docs/compatibility.md) and [container setup](container/README.md).
The launcher is approved; the latest master mission prioritizes Android direct control first.

## Android direct-control foundation

See [architecture](docs/architecture.md), [Android build](docs/android-remote.md),
[protocol evidence](docs/protocol-research.md), [capabilities](protocol/capabilities.md),
[security](docs/security.md) and [physical tests](docs/physical-test-matrix.md).
Android connects directly to TV without Fedora or cloud. Basic legacy pairing/navigation/audio are now physically verified by the owner.
Touchpad and HDMI/Fire TV switching are ready for physical test. Launcher and Fedora implementation follow the master phase gates.
