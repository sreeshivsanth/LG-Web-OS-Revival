package org.lgrevival.core
import okhttp3.*
import okio.ByteString
import org.junit.Test
import org.junit.Assert.*
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
class RemoteTest {
 class Memory:Credentials {var key:String?=null;override fun get(host:String)=key;override fun put(host:String,key:String){this.key=key};override fun forget(host:String){key=null}}
 class Socket(private val req:Request):WebSocket {
  val messages=LinkedBlockingQueue<String>()
  override fun request()=req
  override fun queueSize()=0L
  override fun send(text:String):Boolean {messages.add(text);return true}
  override fun send(bytes:ByteString)=false
  override fun close(code:Int,reason:String?)=true
  override fun cancel() {}
 }
 class Factory:WebSocket.Factory {
  val calls=LinkedBlockingQueue<Pair<Socket,WebSocketListener>>()
  override fun newWebSocket(request:Request,listener:WebSocketListener):WebSocket {val s=Socket(request);calls.add(s to listener);return s}
 }
 @Test fun pairingPersistenceDisconnectAndReconnect() {
  val f=Factory();val creds=Memory();val events=LinkedBlockingQueue<State>()
  val r=Remote(creds,{s,_,_->events.add(s)},f)
  try {
   r.connect("192.168.1.16",true)
   val (s,l)=f.calls.poll(3,TimeUnit.SECONDS)!!
   l.onOpen(s,Response.Builder().request(s.request()).protocol(okhttp3.Protocol.HTTP_1_1).code(101).message("Switching").build())
   assertTrue(s.messages.poll(3,TimeUnit.SECONDS)!!.contains("register"))
   l.onMessage(s,"""{"type":"registered","payload":{"client-key":"synthetic-test-only"}}""")
   assertTrue(s.messages.poll(3,TimeUnit.SECONDS)!!.contains("getPointerInputSocket"))
   assertEquals("synthetic-test-only",creds.key)
   r.disconnect();r.connect("192.168.1.16",true)
   val (s2,l2)=f.calls.poll(3,TimeUnit.SECONDS)!!
   l2.onOpen(s2,Response.Builder().request(s2.request()).protocol(okhttp3.Protocol.HTTP_1_1).code(101).message("Switching").build())
   assertTrue(s2.messages.poll(3,TimeUnit.SECONDS)!!.contains("synthetic-test-only"))
  } finally {r.release()}
 }
 @Test fun malformedResponseFailsWithoutEchoingPayload() {
  val f=Factory();val states=LinkedBlockingQueue<Pair<State,String>>()
  val r=Remote(Memory(),{s,_,m->states.add(s to m)},f)
  try {r.connect("192.168.1.16",true);val(s,l)=f.calls.poll(3,TimeUnit.SECONDS)!!;l.onMessage(s,"secret-invalid");var e=states.poll(3,TimeUnit.SECONDS)!!;if(e.first==State.CONNECTING)e=states.poll(3,TimeUnit.SECONDS)!!;assertEquals(State.ERROR,e.first);assertFalse(e.second.contains("secret-invalid"))}finally{r.release()}
 }
}
