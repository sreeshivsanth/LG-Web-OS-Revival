# Ubuntu 22.04 development container

The Containerfile provides Node 22, official LG CLI 3.2.6, OpenSSH and build utilities.
Node comes from the official Node image; tags allow security refreshes and are not
bit-for-bit image pins. Repository dependencies use package-lock.json.

Build locally without changing your existing container:

```bash
podman build -t lg-webos-dev:revival -f container/Containerfile .
podman volume create lg-webos-revival-home
podman run --rm -it --name lg-webos-revival-test --userns=keep-id   -v "$PWD:/workspace:Z"   -v lg-webos-revival-home:/home/developer   lg-webos-dev:revival
npm ci --ignore-scripts
npm run build
```

The workspace and CLI home persist; processes do not. On Fedora, `:Z` relabels
only the selected checkout for this container. Avoid mounting your entire home.
No keys are embedded or copied into the image. Do not commit private state.

## Existing key registration

Prefer the working `lg-webos-dev` environment for immediate deployment. In a new
container, securely provision the already-existing key into its persistent home
under `.ssh` yourself (outside Git), preserving restrictive permissions. Do not
print the file or retrieve/regenerate a key. Once the existing key file is present,
replace `EXISTING_KEY_FILENAME` below with its filename, not its contents:

```bash
export CPU_FEATURES=0 NODE_OPTIONS=--no-addons
ares-setup-device --add myTV --info 'host=192.168.1.16' --info 'port=9922' --info 'username=prisoner' --info 'privatekey=EXISTING_KEY_FILENAME'
ares-device --system-info --device myTV
```

If myTV already exists, use `--modify` instead of `--add`. No password is required
in scripts. Keep the registered configuration and key local.

## Safe migration

Keep `lg-webos-dev` running and intact. Inventory its workspace and configuration
locally without displaying secrets; create a secure backup outside Git. Build and
test the separately named new container, transfer only intended work and securely
provision existing authentication. Verify packaging, device query, install, launch
and remote behavior before choosing it as the primary environment. Removal or
replacement of the old container requires your explicit approval; none is automated.
