package org.lgrevival.core
import org.junit.Test
import org.junit.Assert.*
import org.json.JSONObject
class ProtocolTest {
 @Test fun privateAddressesOnly() {listOf("192.168.1.16","10.0.0.2","172.16.0.1").forEach {assertTrue(Protocol.privateIp(it))};listOf("8.8.8.8","127.0.0.1","localhost","192.168.1.999","192.168.01.1","192.168.1.1/path").forEach {assertFalse(Protocol.privateIp(it))}}
 @Test fun registrationHasNoCopiedSignature() {val msg=JSONObject(Protocol.registration(null));assertEquals("register",msg.getString("type"));val p=msg.getJSONObject("payload");assertFalse(p.has("client-key"));assertFalse(p.getJSONObject("manifest").has("signatures"));assertEquals("PROMPT",p.getString("pairingType"))}
 @Test fun savedCredentialIsUsed() {assertEquals("fixture-only",JSONObject(Protocol.registration("fixture-only")).getJSONObject("payload").getString("client-key"))}
 @Test fun pointerCannotRedirectOutsidePairedTv() {assertTrue(Protocol.pointerAllowed("ws://192.168.1.16:3000/input","192.168.1.16",true));assertFalse(Protocol.pointerAllowed("ws://192.168.1.16:3000/input","192.168.1.16",false));assertFalse(Protocol.pointerAllowed("ws://192.168.1.17:3000/input","192.168.1.16",true));assertFalse(Protocol.pointerAllowed("ws://user@192.168.1.16:3000/input","192.168.1.16",true))}
 @Test fun buttonFraming() {assertEquals("type:button\nname:ENTER\n\n",Protocol.button(Command.OK))}
 @Test fun requestFraming() {val msg=JSONObject(Protocol.request("1","audio/setMute",JSONObject().put("mute",true)));assertEquals("ssap://audio/setMute",msg.getString("uri"));assertTrue(msg.getJSONObject("payload").getBoolean("mute"))}
 @Test(expected=IllegalArgumentException::class) fun audioIsNotPointerCommand() {Protocol.button(Command.MUTE)}
}
