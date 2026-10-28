package org.lgrevival.core
import org.json.JSONObject

data class TvInput(val id:String,val label:String,val type:String?,val connected:Boolean?)
object Inputs {
 const val LIST="tv/getExternalInputList"
 const val SWITCH="tv/switchInput"
 fun parse(payload:JSONObject):List<TvInput> {
  require(payload.optBoolean("returnValue",true))
  val devices=payload.getJSONArray("devices");require(devices.length()<=128)
  return (0 until devices.length()).map {i->
   val d=devices.getJSONObject(i);val id=d.getString("id");require(id.isNotBlank()&&id.length<=256)
   TvInput(id,d.optString("label",id).take(160),d.optString("type").ifBlank {null},if(d.has("connected")&&!d.isNull("connected"))d.getBoolean("connected") else null)
  }.distinctBy {it.id}
 }
 fun switchPayload(id:String):JSONObject {require(id.isNotBlank()&&id.length<=256);return JSONObject().put("inputId",id)}
}
interface Preferences {fun read(key:String):String?;fun write(key:String,value:String)}
class FireTvAssignment(private val preferences:Preferences) {
 fun get(tv:String)=preferences.read("fire-tv:$tv")
 fun assign(tv:String,id:String,inputs:List<TvInput>) {require(inputs.any {it.id==id});preferences.write("fire-tv:$tv",id)}
 fun resolve(tv:String,inputs:List<TvInput>)=inputs.firstOrNull {it.id==get(tv)}
}
enum class Evidence { PHYSICALLY_VERIFIED, READY_FOR_PHYSICAL_TEST, UNSUPPORTED, UNTESTED }
object Capabilities {
 val evidence=mapOf("REMOTE" to Evidence.PHYSICALLY_VERIFIED,"LEGACY_PAIRING" to Evidence.PHYSICALLY_VERIFIED,
 "POINTER_MOVE" to Evidence.READY_FOR_PHYSICAL_TEST,"POINTER_CLICK" to Evidence.READY_FOR_PHYSICAL_TEST,"POINTER_SCROLL" to Evidence.READY_FOR_PHYSICAL_TEST,
 "INPUT_LIST" to Evidence.READY_FOR_PHYSICAL_TEST,"INPUT_SWITCH" to Evidence.READY_FOR_PHYSICAL_TEST,
 "CURRENT_INPUT" to Evidence.UNTESTED,"HDMI_CEC" to Evidence.UNTESTED,"RIGHT_CLICK" to Evidence.UNSUPPORTED,"LONG_PRESS" to Evidence.UNSUPPORTED)
}
