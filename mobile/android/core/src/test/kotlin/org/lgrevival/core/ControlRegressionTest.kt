package org.lgrevival.core
import org.junit.Test
import org.junit.Assert.*
import javax.net.ssl.*
class ControlRegressionTest {
 @Test fun allVerifiedNavigationEncodings(){val expected=mapOf(Command.UP to "UP",Command.DOWN to "DOWN",Command.LEFT to "LEFT",Command.RIGHT to "RIGHT",Command.OK to "ENTER",Command.BACK to "BACK",Command.HOME to "HOME");expected.forEach {(c,name)->assertEquals("type:button\nname:$name\n\n",Protocol.button(c))}}
 @Test fun verifiedAudioEndpoints(){assertEquals("audio/volumeUp",Command.VOLUME_UP.endpoint);assertEquals("audio/volumeDown",Command.VOLUME_DOWN.endpoint);assertEquals("audio/setMute",Command.MUTE.endpoint);assertEquals(Command.MUTE.endpoint,Command.UNMUTE.endpoint)}
 @Test fun diagnosticsAreSpecificAndRedacted(){assertTrue(Diagnostics.connection(SSLPeerUnverifiedException("secret")).contains("identity"));assertTrue(Diagnostics.connection(java.net.ConnectException("secret")).contains("TCP"));assertTrue(Diagnostics.connection(SSLHandshakeException("secret")).contains("handshake"));assertFalse(Diagnostics.connection(Exception("secret")).contains("secret"))}
 @Test fun capabilityHistoryPreserved(){assertEquals(Evidence.PHYSICALLY_VERIFIED,Capabilities.evidence["REMOTE"]);assertEquals(Evidence.READY_FOR_PHYSICAL_TEST,Capabilities.evidence["INPUT_SWITCH"]);assertEquals(Evidence.UNTESTED,Capabilities.evidence["HDMI_CEC"])}
}
