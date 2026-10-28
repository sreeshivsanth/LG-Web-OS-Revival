# Local network

Phone and TV need IP-level reachability; they can use different access points.
Reserve the TV DHCP address. Manual private IPv4 entry is required and implemented.
Check guest/client isolation, separate VLANs, firewall policy and address changes.
Multicast discovery is optional and not yet implemented. Never forward TV ports
through the router. No remote commands leave the LAN. Turning off internet while
keeping local Wi-Fi must not affect operation; verify physically.
