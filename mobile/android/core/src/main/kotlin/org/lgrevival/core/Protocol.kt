package org.lgrevival.core

import org.json.JSONArray
import org.json.JSONObject
import java.net.URI

enum class State { DISCONNECTED, CONNECTING, PAIRING, REGISTERED, ERROR }
enum class Command(val button: String? = null, val endpoint: String? = null) {
 UP("UP"), DOWN("DOWN"), LEFT("LEFT"), RIGHT("RIGHT"), OK("ENTER"), BACK("BACK"), HOME("HOME"),
 VOLUME_UP(endpoint="audio/volumeUp"), VOLUME_DOWN(endpoint="audio/volumeDown"), MUTE(endpoint="audio/setMute"), UNMUTE(endpoint="audio/setMute")
}
interface Credentials { fun get(host: String): String?; fun put(host: String, key: String); fun forget(host: String) }
object Protocol {
 fun privateIp(host: String): Boolean {
  val parts = host.split('.')
  if (parts.size != 4 || parts.any { it.isEmpty() || it.length > 3 || (it.length > 1 && it[0] == '0') || it.any { c -> c !in '0'..'9' } }) return false
  val p = parts.map { it.toInt() }; if (p.any { it !in 0..255 }) return false
  return p[0] == 10 || (p[0] == 172 && p[1] in 16..31) || (p[0] == 192 && p[1] == 168)
 }
 fun pointerAllowed(value: String, host: String, insecure: Boolean): Boolean = try {
  val u = URI(value)
  u.host == host && u.userInfo == null && u.fragment == null &&
    ((u.scheme == "wss" && u.port == 3001) || (insecure && u.scheme == "ws" && u.port == 3000))
 } catch (_: Exception) { false }
 fun registration(key: String?): String {
  val permissions = JSONArray(listOf("CONTROL_AUDIO", "CONTROL_INPUT_JOYSTICK", "CONTROL_MOUSE_AND_KEYBOARD"))
  val payload = JSONObject().put("pairingType", "PROMPT").put("forcePairing", false)
    .put("manifest", JSONObject().put("manifestVersion", 1).put("permissions", permissions))
  if (key != null) payload.put("client-key", key)
  return JSONObject().put("id", "register").put("type", "register").put("payload", payload).toString()
 }
 fun request(id: String, endpoint: String, payload: JSONObject = JSONObject()): String = JSONObject()
  .put("id", id).put("type", "request").put("uri", "ssap://$endpoint").put("payload", payload).toString()
 fun button(command: Command): String { require(command.button != null); return "type:button\nname:${command.button}\n\n" }
}
