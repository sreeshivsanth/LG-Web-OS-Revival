package org.lgrevival.remote

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.os.Build
import android.graphics.Color
import android.view.*
import android.widget.*
import org.lgrevival.core.*

class MainActivity:Activity() {
 private lateinit var remote:Remote
 private lateinit var status:TextView
 private lateinit var host:EditText
 private lateinit var content:LinearLayout
 private lateinit var touchpad:TouchpadView
 private lateinit var assignment:FireTvAssignment
 private var section="Remote"
 private var activeHost=""
 private var registered=false
 private var pointerReady=false
 private var discovered=emptyList<TvInput>()
 private var inputMessage="Refresh to discover inputs from the TV."
 private var inputBusy=false
 private var uiSession=0
 private val controls=mutableListOf<Pair<Button,Command>>()
 private val prefs by lazy {getPreferences(0)}
 private fun dp(value:Int)=(value*resources.displayMetrics.density+0.5f).toInt()
 private fun column()=LinearLayout(this).apply {orientation=LinearLayout.VERTICAL}
 private fun label(parent:LinearLayout,value:String,size:Float=18f)=TextView(this).apply {text=value;textSize=size;setTextColor(Color.rgb(174,234,209));setPadding(dp(4),dp(10),dp(4),dp(10));parent.addView(this)}
 private fun button(value:String,action:()->Unit)=Button(this).apply {
  text=value;contentDescription=value;textSize=16f;gravity=Gravity.CENTER;isAllCaps=false
  minHeight=dp(56);minimumHeight=dp(56);minWidth=0;minimumWidth=0
  setPadding(dp(6),dp(12),dp(6),dp(12));setTextColor(Color.WHITE)
  setOnClickListener {if(prefs.getBoolean("haptics",true))performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);action()}
 }
 private fun action(parent:LinearLayout,value:String,run:()->Unit):Button=button(value,run).also {parent.addView(it,LinearLayout.LayoutParams(-1,-2))}
 override fun onCreate(state:Bundle?) {
  super.onCreate(state)
  val root=column().apply {setBackgroundColor(Color.rgb(9,17,16))}
  setContentView(root)
  root.setOnApplyWindowInsetsListener {v,i->
   if(Build.VERSION.SDK_INT>=30){val b=i.getInsets(WindowInsets.Type.systemBars() or WindowInsets.Type.displayCutout());v.setPadding(b.left,b.top,b.right,b.bottom)}
   else {@Suppress("DEPRECATION") v.setPadding(i.systemWindowInsetLeft,i.systemWindowInsetTop,i.systemWindowInsetRight,i.systemWindowInsetBottom)}
   i
  };root.requestApplyInsets()
  val scroll=ScrollView(this).apply {isFillViewport=true};root.addView(scroll,LinearLayout.LayoutParams(-1,-1))
  val panel=column().apply {setPadding(dp(12),dp(8),dp(12),dp(16))};scroll.addView(panel)
  label(panel,"EMERALD CITADEL",13f);label(panel,"LG Revival Remote",26f)
  status=label(panel,"Disconnected • LG 32LK610BPVA controls physically verified",15f)
  status.accessibilityLiveRegion=View.ACCESSIBILITY_LIVE_REGION_POLITE
  assignment=FireTvAssignment(object:Preferences {override fun read(key:String)=prefs.getString(key,null);override fun write(key:String,value:String){check(prefs.edit().putString(key,value).commit())}})
  remote=Remote(SecureCredentials(this),update={s,p,message->runOnUiThread {
   if(isDestroyed)return@runOnUiThread
   registered=s==State.REGISTERED;pointerReady=p
   status.text="${s.name} • $message"
   touchpad.ready=registered&&p
   controls.forEach {(b,c)->b.isEnabled=registered&&(c.button==null||p)}
   if(!registered){discovered=emptyList();inputBusy=false;if(section=="Inputs")render()}
  }})
  touchpad=TouchpadView(this,remote) {message->if(message=="click"){if(prefs.getBoolean("haptics",true))touchpad.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)}else Toast.makeText(this,message,Toast.LENGTH_SHORT).show()}
  for(names in listOf(listOf("Remote","Touchpad"),listOf("Inputs","Settings"))) {
   val row=LinearLayout(this);panel.addView(row)
   names.forEach {name->row.addView(button(name){touchpad.cancelGesture();section=name;render()},LinearLayout.LayoutParams(0,-2,1f))}
  }
  content=column();panel.addView(content)
  activeHost=prefs.getString("lastHost","192.168.1.16")!!
  render()
 }
 private fun render() {
  content.removeAllViews();controls.clear()
  when(section) {
   "Remote"->{
    label(content,"REMOTE • verified controls",17f)
    action(content,"Fire TV • switch configured input"){fireTv()}
    for(rowCommands in listOf(listOf(Command.UP),listOf(Command.LEFT,Command.OK,Command.RIGHT),listOf(Command.DOWN),listOf(Command.BACK,Command.HOME),listOf(Command.VOLUME_DOWN,Command.VOLUME_UP),listOf(Command.MUTE,Command.UNMUTE))) {
     val row=LinearLayout(this);content.addView(row)
     rowCommands.forEach {c->val b=button(c.name.replace('_',' ')){remote.send(c)};b.isEnabled=registered&&(c.button==null||pointerReady);row.addView(b,LinearLayout.LayoutParams(0,-2,1f));controls.add(b to c)}
    }
    action(content,"Connection settings / reconnect"){section="Settings";render()}
   }
   "Touchpad"->{
    label(content,"TOUCHPAD • ready for physical test",17f)
    touchpad.settings=pointerSettings();(touchpad.parent as? android.view.ViewGroup)?.removeView(touchpad)
    content.addView(touchpad,LinearLayout.LayoutParams(-1,dp(300)))
    slider("Sensitivity", "sensitivity",5)
    action(content,"Back"){remote.send(Command.BACK)};action(content,"Home"){remote.send(Command.HOME)}
   }
   "Inputs"->{
    label(content,"INPUTS • ready for physical test",17f);label(content,inputMessage,16f)
    action(content,if(inputBusy)"Refreshing…" else "Refresh input list"){refreshInputs()}.isEnabled=registered&&!inputBusy
    discovered.forEach {input->
     val alias=if(assignment.get(activeHost)==input.id)" • Fire TV Stick" else ""
     label(content,input.label+alias,20f)
     label(content,"ID: ${input.id} • ${input.type ?: "type unknown"} • ${input.connected?.let {if(it)"connected" else "not connected"} ?: "connection unknown"}",14f)
     action(content,"Switch to ${input.label}"){switchInput(input)}.isEnabled=registered
     action(content,"Assign this input to Fire TV"){
      AlertDialog.Builder(this).setMessage("Confirm the Fire TV Stick is connected to ${input.label}. No port is assumed.").setPositiveButton("Assign"){_,_->
       try{assignment.assign(activeHost,input.id,discovered);inputMessage="Fire TV assigned to ${input.label}"}catch(_:Exception){inputMessage="Assignment could not be saved"};render()
      }.setNegativeButton("Cancel",null).show()
     }
    }
    label(content,"Current input unknown. A switch acknowledgement does not prove the displayed picture. CEC is untested.",14f)
   }
   "Settings"->{
    label(content,"CONNECTION",17f)
    host=EditText(this).apply {hint="TV private IPv4 address";setTextColor(Color.WHITE);setHintTextColor(Color.LTGRAY);inputType=3;setText(activeHost);contentDescription="TV IP address"};content.addView(host,LinearLayout.LayoutParams(-1,-2))
    label(content,"Last choice: ${prefs.getString("transport","none")}. Every legacy connection requires consent.",14f)
    action(content,"Connect securely / reconnect"){connect(false)}
    action(content,"Connect legacy LAN (unencrypted)"){
     AlertDialog.Builder(this).setTitle("Trusted LAN only").setMessage("Pairing keys and commands are unencrypted on this LAN. This physically verified compatibility mode never replaces TLS silently. Continue?").setPositiveButton("Connect"){_,_->connect(true)}.setNegativeButton("Cancel",null).show()
    }
    action(content,"Disconnect"){uiSession++;touchpad.cancelGesture();remote.disconnect()}
    action(content,"Forget pairing"){
     AlertDialog.Builder(this).setMessage("Remove pairing for $activeHost? Revoke on TV separately if necessary.").setPositiveButton("Forget"){_,_->uiSession++;remote.forget(activeHost)}.setNegativeButton("Cancel",null).show()
    }
    slider("Pointer sensitivity","sensitivity",5);slider("Scroll sensitivity","scrollSensitivity",5)
    toggle("Pointer acceleration","acceleration",false);toggle("Tap to click","tap",true);toggle("Haptic feedback","haptics",true)
    label(content,"No right click or long-press action. Secure TLS failure remains under investigation; no certificate bypass is installed.",14f)
   }
  }
 }
 private fun pointerSettings()=PointerSettings(prefs.getInt("sensitivity",5),prefs.getBoolean("acceleration",false),prefs.getBoolean("tap",true),prefs.getInt("scrollSensitivity",5))
 private fun slider(title:String,key:String,default:Int) {
  val text=label(content,"$title: ${prefs.getInt(key,default)}",16f)
  content.addView(SeekBar(this).apply {max=9;progress=prefs.getInt(key,default)-1;contentDescription=title;minimumHeight=dp(48)
   setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener {
    override fun onProgressChanged(bar:SeekBar,p:Int,user:Boolean){if(user){prefs.edit().putInt(key,p+1).apply();text.text="$title: ${p+1}";touchpad.settings=pointerSettings()}}
    override fun onStartTrackingTouch(bar:SeekBar){};override fun onStopTrackingTouch(bar:SeekBar){}
   })
  },LinearLayout.LayoutParams(-1,-2))
 }
 private fun toggle(title:String,key:String,default:Boolean) {content.addView(Switch(this).apply {text=title;setTextColor(Color.WHITE);minHeight=dp(56);isChecked=prefs.getBoolean(key,default);setOnCheckedChangeListener{_,on->prefs.edit().putBoolean(key,on).apply();touchpad.settings=pointerSettings()}},LinearLayout.LayoutParams(-1,-2))}
 private fun connect(legacy:Boolean) {
  val address=host.text.toString().trim()
  if(!Protocol.privateIp(address)){status.text="Enter a valid private IPv4 LAN address";return}
  uiSession++;touchpad.cancelGesture();activeHost=address;discovered=emptyList()
  prefs.edit().putString("lastHost",address).putString("transport",if(legacy)"legacy LAN" else "secure TLS").apply()
  remote.connect(address,legacy)
 }
 private fun refreshInputs(after:(()->Unit)?=null) {
  if(!registered){status.text="Connect to TV before discovering inputs";return}
  if(inputBusy)return
  inputBusy=true;val session=uiSession
  remote.getInputs {list,error->runOnUiThread {
   if(session!=uiSession || isDestroyed)return@runOnUiThread
   inputBusy=false;discovered=list;inputMessage=error ?: if(list.isEmpty())"TV returned no external inputs" else "${list.size} inputs returned; current input unknown"
   if(section=="Inputs")render()
   if(error==null)after?.invoke()else status.text=inputMessage
  }}
 }
 private fun switchInput(input:TvInput) {
  val session=uiSession;status.text="Requesting ${input.label}…"
  remote.switchInput(input.id){error->runOnUiThread{if(session==uiSession&&!isDestroyed)status.text=error ?: "TV acknowledged switch to ${input.label}; verify picture"}}
 }
 private fun fireTv() {
  if(!registered){section="Settings";render();status.text="Connect to select the Fire TV input";return}
  section="Inputs";render()
  refreshInputs {
   val input=assignment.resolve(activeHost,discovered)
   if(input==null){inputMessage="Choose the discovered input containing your Fire TV Stick";render()}else switchInput(input)
  }
 }
 override fun onStop(){uiSession++;touchpad.cancelGesture();remote.disconnect();super.onStop()}
 override fun onDestroy(){remote.release();super.onDestroy()}
}
