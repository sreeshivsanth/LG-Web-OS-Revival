package org.lgrevival.core
import org.json.JSONObject
import org.junit.Test
import org.junit.Assert.*
class InputsTest {
 @Test fun preservesActualIdsAndUnknownTypes(){val list=Inputs.parse(JSONObject("""{"devices":[{"id":"vendor-port-A","label":"HDMI 2","type":"HDMI","connected":true},{"id":"future-id","type":"future"}]}"""));assertEquals("vendor-port-A",list[0].id);assertEquals(true,list[0].connected);assertEquals("future",list[1].type);assertNull(list[1].connected)}
 @Test fun emptyInputsValid(){assertTrue(Inputs.parse(JSONObject("""{"devices":[]} """)).isEmpty())}
 @Test(expected=Exception::class) fun malformedInputsRejected(){Inputs.parse(JSONObject("""{"devices":[{"label":"Missing ID"}]}"""))}
 @Test fun exactSwitchId(){assertEquals("opaque-123",Inputs.switchPayload("opaque-123").getString("inputId"))}
 @Test fun assignmentPersistsAndMissingInputDoesNotResolve(){val values=mutableMapOf<String,String>();val prefs=object:Preferences{override fun read(key:String)=values[key];override fun write(key:String,value:String){values[key]=value}};val input=TvInput("actual-id","HDMI",null,null);FireTvAssignment(prefs).assign("tv-A",input.id,listOf(input));assertEquals(input,FireTvAssignment(prefs).resolve("tv-A",listOf(input)));assertNull(FireTvAssignment(prefs).resolve("tv-A",emptyList()));assertNull(FireTvAssignment(prefs).get("tv-B"))}
 @Test(expected=IllegalArgumentException::class) fun cannotAssignInventedInput(){val prefs=object:Preferences{override fun read(key:String):String?=null;override fun write(key:String,value:String){}};FireTvAssignment(prefs).assign("tv","HDMI_1",emptyList())}
}
