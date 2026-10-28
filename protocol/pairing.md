# Pairing

Select private TV IP -> connect -> unsigned permission manifest -> approve prompt
on TV -> receive registered/client-key -> encrypt using Android Keystore -> reuse
on explicit reconnect. This is independent of Developer Mode SSH.

Forget removes the selected local encrypted key. Revoke the paired remote on the
TV if the phone is lost. Keys are scoped to IP; use a DHCP reservation. Never export
keys into fixtures. No copied LG test signature, vendor identity or signing keys.
