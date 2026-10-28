package org.lgrevival.core
import okhttp3.Response
import org.json.JSONObject
import org.junit.Test
import org.junit.Assert.*
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
class InputConnectionTest {
 class Session:AutoCloseable {
  val factory=RemoteTest.Factory();val remote=Remote(RemoteTest.Memory(),{_,_,_->},factory)
  val socket:RemoteTest.Socket;val listener:okhttp3.WebSocketListener
  init {
   remote.connect("192.168.1.16",true);val call=factory.calls.poll(3,TimeUnit.SECONDS)!!;socket=call.first;listener=call.second
   listener.onOpen(socket,Response.Builder().request(socket.request()).protocol(okhttp3.Protocol.HTTP_1_1).code(101).message("OK").build())
   socket.messages.poll(3,TimeUnit.SECONDS)
   listener.onMessage(socket,"""{"type":"registered","payload":{"client-key":"fixture"}}""");socket.messages.poll(3,TimeUnit.SECONDS)
  }
  fun request()=JSONObject(socket.messages.poll(3,TimeUnit.SECONDS)!!)
  fun respond(request:JSONObject,payload:JSONObject,type:String="response") {listener.onMessage(socket,JSONObject().put("id",request.getString("id")).put("type",type).put("payload",payload).toString())}
  fun discover() {
   val results=LinkedBlockingQueue<List<TvInput>>();remote.getInputs {list,_->results.add(list)}
   val req=request();assertEquals("ssap://tv/getExternalInputList",req.getString("uri"))
   respond(req,JSONObject("""{"devices":[{"id":"from-tv","label":"HDMI 2"}]}"""));assertEquals("from-tv",results.poll(3,TimeUnit.SECONDS)!!.single().id)
  }
  override fun close(){remote.release()}
 }
 @Test fun switchUsesDiscoveredIdAndWaitsForAcknowledgement(){Session().use {s->s.discover();val results=LinkedBlockingQueue<String>();s.remote.switchInput("from-tv"){results.add(it?:"ACK")};val req=s.request();assertTrue(results.isEmpty());assertEquals("ssap://tv/switchInput",req.getString("uri"));assertEquals("from-tv",req.getJSONObject("payload").getString("inputId"));s.respond(req,JSONObject().put("returnValue",true));assertEquals("ACK",results.poll(3,TimeUnit.SECONDS))}}
 @Test fun rejectedSwitchIsNotSuccess(){Session().use {s->s.discover();val results=LinkedBlockingQueue<String>();s.remote.switchInput("from-tv"){results.add(it?:"ACK")};s.respond(s.request(),JSONObject().put("returnValue",false));assertNotEquals("ACK",results.poll(3,TimeUnit.SECONDS))}}
 @Test fun missingReturnValueIsNotSuccess(){Session().use {s->s.discover();val results=LinkedBlockingQueue<String>();s.remote.switchInput("from-tv"){results.add(it?:"ACK")};s.respond(s.request(),JSONObject());assertTrue(results.poll(3,TimeUnit.SECONDS)!!.contains("not acknowledged"))}}
 @Test fun disconnectDuringSwitchReturnsUnknown(){Session().use {s->s.discover();val results=LinkedBlockingQueue<String>();s.remote.switchInput("from-tv"){results.add(it?:"ACK")};s.request();s.remote.disconnect();assertTrue(results.poll(3,TimeUnit.SECONDS)!!.contains("Connection ended"))}}
 @Test fun inventedInputCannotBeSent(){Session().use {s->val results=LinkedBlockingQueue<String>();s.remote.switchInput("HDMI_3"){results.add(it?:"ACK")};assertTrue(results.poll(3,TimeUnit.SECONDS)!!.contains("refresh"));assertTrue(s.socket.messages.isEmpty())}}
 @Test fun pointerMotionNotReplayedAcrossReconnect(){Session().use {s->
  s.listener.onMessage(s.socket,"""{"id":"pointer","type":"response","payload":{"socketPath":"ws://192.168.1.16:3000/input"}}""")
  val(p,l)=s.factory.calls.poll(3,TimeUnit.SECONDS)!!
  l.onOpen(p,Response.Builder().request(p.request()).protocol(okhttp3.Protocol.HTTP_1_1).code(101).message("OK").build())
  s.remote.move(4,5);assertEquals(Protocol.move(4,5),p.messages.poll(3,TimeUnit.SECONDS))
  s.remote.disconnect();s.remote.move(99,99);s.remote.connect("192.168.1.16",true)
  assertNotNull(s.factory.calls.poll(3,TimeUnit.SECONDS));assertTrue(p.messages.isEmpty())
 }}
 @Test fun actualMuteAndUnmutePayloads(){Session().use {s->for(c in listOf(Command.MUTE,Command.UNMUTE)){s.remote.send(c);val req=s.request();assertEquals(c==Command.MUTE,req.getJSONObject("payload").getBoolean("mute"));s.respond(req,JSONObject().put("returnValue",true))}}}
}
