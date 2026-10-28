# Local deployment and acceptance

Cloud cannot establish your LAN device's readiness. These commands run locally.
On Fedora, with the repository at the preferred checkout path:

```bash
cd "$HOME/Downloads/LG Web OS Revival"
npm ci --ignore-scripts
bash scripts/deploy.sh myTV
```

Alternatively, use the existing authenticated `lg-webos-dev` container if its
`/workspace` is mounted from `~/lg-tv-dev`. Place the checkout at
`~/lg-tv-dev/LG-Web-OS-Revival` without overwriting an existing checkout, then:

```bash
podman exec -it -w /workspace/LG-Web-OS-Revival lg-webos-dev bash -lc 'npm ci --ignore-scripts && bash scripts/deploy.sh myTV'
```

Scripts also accept another registered device name as their sole argument.
`check-device.sh` queries system information, `build.sh` validates and packages,
`install.sh` installs the existing IPK, `launch.sh` launches the application,
and `deploy.sh` performs these steps in order, stopping at any failure.
An install updates this application if it already exists.

Confirm Developer Mode is enabled and that local routing reaches 192.168.1.16:9922.
Never regenerate the existing key for cloud testing.

## Physical acceptance (pending)

- Confirm system information matches model 32LK610BPVA and SDK 4.4.0.
- Confirm installation and launch succeed and the welcome screen fills the display.
- Confirm initial focus is visible; all arrows move and wrap between both buttons.
- Press OK, hold OK, use pointer clicks and reset; verify greeting count and feedback.
- Press Back; confirm platform exit/history behavior and relaunch successfully.
- Check text legibility, screen edges and responsiveness on the physical display.

Record firmware, date and outcomes before declaring the milestone complete.
