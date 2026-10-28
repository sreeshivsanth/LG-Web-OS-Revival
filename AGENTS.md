# Development guidance
Use this existing checkout; cloud tasks are already isolated. Do not create worktrees unless requested.
Target webOS TV 4.4 using ES5 JavaScript, local assets and lightweight CSS. No firmware changes or rooting.
Run npm test and npm run build before reporting packaging readiness.
Set CPU_FEATURES=0 and NODE_OPTIONS=--no-addons for every webOS CLI command.
Never read, copy into Git, or print credentials. TV deployment is local and must not be claimed without evidence.
Hello webOS physical acceptance and launcher approval were provided by the owner. Follow the master phase gates: Android foundation first, then physical Android acceptance before advanced control integrations. Never claim CEC or Fire TV control is verified without hardware results.
