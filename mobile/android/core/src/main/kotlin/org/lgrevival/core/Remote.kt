package org.lgrevival.core

import okhttp3.*
import org.json.JSONObject
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/** All state transitions serialized; no credentials or raw TV responses sent to UI/logs. */
class Remote(private val credentials: Credentials, private val update: (State, Boolean, String) -> Unit, private val client: WebSocket.Factory = OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).readTimeout(0, TimeUnit.SECONDS).build()) {
 private val worker = Executors.newSingleThreadScheduledExecutor()
 private var socket: WebSocket? = null
 private var pointer: WebSocket? = null
 private var state = State.DISCONNECTED
 private var generation = 0
 private var sequence = 0
 private var pointerReady = false
 private val pending = mutableMapOf<String, Command>()
 private val denied = mutableSetOf<Command>()
 private fun dispatch(action: () -> Unit) { try { worker.execute(action) } catch (_: java.util.concurrent.RejectedExecutionException) { /* late callback after Activity destruction */ } }
 private fun emit(message: String) = update(state, pointerReady, message)
 fun connect(host: String, insecure: Boolean) = dispatch {
  closeSockets(); generation++; val session = generation
  if (!Protocol.privateIp(host)) { state = State.ERROR; emit("Enter a private IPv4 LAN address."); return@dispatch }
  state = State.CONNECTING; emit("Connecting directly to $host")
  val key = try { credentials.get(host) } catch (_: Exception) { state=State.ERROR; emit("Secure credential storage failed. Forget and pair again."); return@dispatch }
  val url = if (insecure) "ws://$host:3000" else "wss://$host:3001"
  socket = client.newWebSocket(Request.Builder().url(url).build(), object: WebSocketListener() {
   override fun onOpen(ws: WebSocket, response: Response) { dispatch { if(session == generation) { state=State.PAIRING; emit("Approve the pairing prompt on your TV."); ws.send(Protocol.registration(key)) } } }
   override fun onMessage(ws: WebSocket, text: String) { dispatch {
    if(session != generation) return@dispatch
    try {
     require(text.length <= 65536)
     val msg = JSONObject(text); val type=msg.optString("type"); val payload=msg.optJSONObject("payload") ?: JSONObject()
     if(type == "registered" && state == State.PAIRING) {
      val issued=payload.optString("client-key"); require(issued.isNotBlank() && issued.length <= 4096)
      credentials.put(host, issued); state=State.REGISTERED; emit("Paired. Controls are experimental until physically verified.")
      ws.send(Protocol.request("pointer", "com.webos.service.networkinput/getPointerInputSocket"))
     } else if(msg.optString("id") == "pointer" && state == State.REGISTERED) {
      val path=payload.optString("socketPath")
      if(type == "error" || !Protocol.pointerAllowed(path,host,insecure)) { emit("Navigation unavailable: pointer endpoint rejected or unsafe."); return@dispatch }
      pointer=client.newWebSocket(Request.Builder().url(path).build(),object: WebSocketListener() {
       override fun onOpen(webSocket: WebSocket, response: Response) { dispatch { if(session==generation) { pointerReady=true; emit("Navigation channel ready; verify each action on TV.") } } }
       override fun onFailure(webSocket: WebSocket,t:Throwable,response:Response?) { dispatch { if(session==generation) {pointerReady=false;emit("Navigation channel failed.")} } }
       override fun onClosed(webSocket: WebSocket,code:Int,reason:String) { dispatch { if(session==generation) {pointerReady=false;emit("Navigation channel closed.")} } }
      })
     } else if(type == "error" && state == State.PAIRING) { fail("Pairing denied. Approve on TV or forget and re-pair.") }
     else {
      val cmd=pending.remove(msg.optString("id"))
      if(cmd != null) {
       if(type == "error" || payload.optBoolean("returnValue",true)==false) {denied.add(cmd);emit("${cmd.name} rejected; disabled for this connection.")}
       else if(type == "response") emit("TV acknowledged ${cmd.name}; confirm physical result.")
       else emit("Unexpected command response; result unknown.")
      }
     }
    } catch (_: Exception) { fail("Invalid response or credential storage failure. Connection closed.") }
   } }
   override fun onFailure(ws:WebSocket,t:Throwable,response:Response?) { dispatch { if(session==generation) fail("Connection failed. Check LAN routing and certificate trust. TLS is never bypassed.") } }
   override fun onClosed(ws:WebSocket,code:Int,reason:String) { dispatch { if(session==generation) fail("TV disconnected. Reconnect when ready.") } }
  })
  worker.schedule({if(session==generation && state != State.REGISTERED) fail("Connection/pairing timed out after 45 seconds.")},45,TimeUnit.SECONDS)
 }
 fun send(command: Command) = dispatch {
  if(state != State.REGISTERED || command in denied) {emit("Command unavailable; connect or check capability rejection.");return@dispatch}
  if(command.button != null) {
   if(!pointerReady || pointer?.send(Protocol.button(command)) != true) emit("Navigation unavailable.") else emit("${command.name} sent; input protocol has no action acknowledgement.")
  } else {
   val id="command-${++sequence}"; val payload=JSONObject()
   if(command == Command.MUTE || command == Command.UNMUTE) payload.put("mute",command == Command.MUTE)
   pending[id]=command
   if(socket?.send(Protocol.request(id,command.endpoint!!,payload)) != true) {pending.remove(id);emit("Send failed.")}
   worker.schedule({if(pending.remove(id)!=null) emit("Command timed out; result unknown.")},8,TimeUnit.SECONDS)
  }
 }
 private fun closeSockets() {socket?.cancel();pointer?.cancel();socket=null;pointer=null;pointerReady=false;pending.clear();denied.clear()}
 private fun fail(message:String) {generation++;closeSockets();state=State.ERROR;emit(message)}
 fun disconnect() = dispatch {generation++;closeSockets();state=State.DISCONNECTED;emit("Disconnected")}
 fun forget(host:String) = dispatch {generation++;closeSockets();state=State.DISCONNECTED;try {credentials.forget(host);emit("Pairing forgotten locally; revoke it on the TV too if needed.")} catch (_:Exception) {emit("Could not erase credential; check device storage.")}}
 fun release() { disconnect(); dispatch {(client as? OkHttpClient)?.let { it.dispatcher.executorService.shutdown();it.connectionPool.evictAll() };worker.shutdownNow()} }
}
