# LG Web OS Revival

Lightweight Hello webOS application for the LG 32LK610BPVA, webOS TV 4.4.
**Status: Ready for local deployment. Physical TV acceptance remains pending.**

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
The advanced launcher is deferred until physical testing and explicit approval.
