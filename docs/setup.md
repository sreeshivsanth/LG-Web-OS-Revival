# Setup

Use Node.js 22 and npm; the lockfile pins official `@webos-tools/cli` 3.2.6.
Run `npm ci --ignore-scripts` from the repository root. Native addon build scripts
are intentionally unnecessary: every CLI invocation uses `CPU_FEATURES=0`
and `NODE_OPTIONS=--no-addons`. The app itself has no npm dependencies.

Run `npm test`, then `npm run build`. Build runs validation and tests before
packaging in a fresh temporary output directory and verifies a nonempty IPK.
The official packaging command is:

```bash
CPU_FEATURES=0 NODE_OPTIONS=--no-addons ./node_modules/.bin/ares-package apps/hello-webos --outdir dist
```

Cloud home directories may be read-only. Use the CLI's supported state override:

```bash
mkdir -p /workspace/webos-cli-state
APPDATA=/workspace/webos-cli-state npm run build
```

Do not use this empty cloud configuration for local TV deployment; reuse your
existing authenticated CLI configuration. No TV secrets are needed to build.
Cloud validation used Node 24.19.0; the provided container selects Node 22.
CLI transitive dependencies emit deprecation notices. Do not apply forced audit fixes.
