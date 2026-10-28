package org.lgrevival.remote
import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.graphics.Color
import android.view.View
import android.widget.*
import org.lgrevival.core.*
class MainActivity:Activity() {
 private lateinit var remote:Remote
 private lateinit var status:TextView
 private lateinit var host:EditText
 private val controls=mutableListOf<Pair<Button,Command>>()
 override fun onCreate(state:Bundle?) {
  super.onCreate(state)
  val panel=LinearLayout(this).apply {orientation=LinearLayout.VERTICAL;setPadding(24,40,24,24);setBackgroundColor(Color.rgb(9,17,16))}
  setContentView(ScrollView(this).apply {addView(panel)})
  fun label(text:String,size:Float=18f)=TextView(this).apply {this.text=text;textSize=size;setTextColor(Color.rgb(170,241,207));setPadding(8,16,8,16);panel.addView(this)}
  label("EMERALD CITADEL",14f);label("LG Revival Remote",28f)
  label("Direct LAN • no laptop or cloud required")
  host=EditText(this).apply {hint="TV private IPv4 address";setTextColor(Color.WHITE);setHintTextColor(Color.LTGRAY);inputType=3;setText(getPreferences(0).getString("lastHost","192.168.1.16"));contentDescription="TV IP address";panel.addView(this)}
  status=label("Disconnected • model and commands unverified on this connection")
  status.accessibilityLiveRegion=View.ACCESSIBILITY_LIVE_REGION_POLITE
  remote=Remote(SecureCredentials(this), update = {s,p,message->runOnUiThread {status.text="${s.name}: $message";controls.forEach {(b,c)->b.isEnabled=s==State.REGISTERED && (c.button==null || p)}}})
  fun action(text:String,click:()->Unit) {panel.addView(Button(this).apply {this.text=text;minHeight=56;setOnClickListener {click()}})}
  action("Connect / reconnect securely") {connect(false)}
  action("Legacy LAN connection (unencrypted)") {
   AlertDialog.Builder(this).setTitle("Unencrypted local protocol")
    .setMessage("Only use trusted private Wi-Fi. Pairing keys and commands can be observed on this LAN. This does not bypass TLS. Continue with ws port 3000?")
    .setPositiveButton("Connect") {_,_->connect(true)}.setNegativeButton("Cancel",null).show()
  }
  action("Forget selected TV") {AlertDialog.Builder(this).setMessage("Forget the saved credential for this address?").setPositiveButton("Forget") {_,_->remote.forget(host.text.toString().trim())}.setNegativeButton("Cancel",null).show()}
  label("Experimental controls • verify on TV",16f)
  for(row in listOf(listOf(Command.UP),listOf(Command.LEFT,Command.OK,Command.RIGHT),listOf(Command.DOWN),listOf(Command.BACK,Command.HOME),listOf(Command.VOLUME_DOWN,Command.VOLUME_UP),listOf(Command.MUTE,Command.UNMUTE))) {
   val line=LinearLayout(this);panel.addView(line)
   for(cmd in row) {val b=Button(this).apply {text=cmd.name.replace('_',' ');contentDescription=text;minHeight=64;isEnabled=false;setOnClickListener {remote.send(cmd)}};line.addView(b,LinearLayout.LayoutParams(0,72,1f));controls.add(b to cmd)}
  }
  label("Playback, channels, apps, inputs and power are not enabled in this foundation. No device capabilities are claimed verified.",16f)
 }
 private fun connect(insecure:Boolean) {val value=host.text.toString().trim();if(Protocol.privateIp(value)) getPreferences(0).edit().putString("lastHost",value).apply();remote.connect(value,insecure)}
 override fun onStop() {super.onStop();remote.disconnect()}
 override fun onDestroy() {remote.release();super.onDestroy()}
}
