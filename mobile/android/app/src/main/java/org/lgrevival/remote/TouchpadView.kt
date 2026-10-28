package org.lgrevival.remote

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.MotionEvent
import android.view.Gravity
import android.widget.TextView
import org.lgrevival.core.*

class TouchpadView(context:Context,private val remote:Remote,private val report:(String)->Unit):TextView(context) {
 private val handler=Handler(Looper.getMainLooper())
 private val buffer=MotionBuffer()
 var ready=false
  set(value) {if(field!=value)cancelGesture();field=value;text=if(value) "Swipe to move\nTap to select\nTwo fingers to scroll" else "Touchpad disconnected\nConnect or reconnect in Settings"}
 private val gesture=GestureInterpreter(move={x,y->buffer.move(x,y);schedule()},scroll={d->buffer.scroll(d);schedule()},click={performClick()})
 var settings:PointerSettings
  get()=gesture.settings
  set(value) {gesture.settings=value}
 private var scheduled=false
 private val frame=object:Runnable {override fun run() {
  scheduled=false
  if(!ready){cancelGesture();return}
  buffer.drain(SystemClock.uptimeMillis())?.let {if(it.dx!=0||it.dy!=0)remote.move(it.dx,it.dy);if(it.scroll!=0)remote.scroll(it.scroll)}
  if(buffer.pending())schedule()
 }}
 init {gravity=Gravity.CENTER;textSize=21f;setTextColor(Color.rgb(166,243,208));setBackgroundColor(Color.rgb(25,48,42));isClickable=true;isFocusable=true;contentDescription="TV touchpad. Swipe to move, tap to select, two fingers to scroll. Use Remote tab for accessible directional controls.";ready=false}
 private fun schedule() {if(!scheduled){scheduled=true;handler.postDelayed(frame,34)}}
 fun cancelGesture() {gesture.cancel();buffer.clear();handler.removeCallbacks(frame);scheduled=false;parent?.requestDisallowInterceptTouchEvent(false)}
 override fun performClick():Boolean {super.performClick();if(ready){remote.click();report("click")}else report("Touchpad is disconnected");return true}
 override fun onTouchEvent(e:MotionEvent):Boolean {
  if(!ready){if(e.actionMasked==MotionEvent.ACTION_DOWN)report("Touchpad is disconnected; reconnect in Settings");return true}
  val density=resources.displayMetrics.density
  fun centerX()=(0 until e.pointerCount).sumOf {e.getX(it).toDouble()}/e.pointerCount/density
  fun centerY()=(0 until e.pointerCount).sumOf {e.getY(it).toDouble()}/e.pointerCount/density
  when(e.actionMasked) {
   MotionEvent.ACTION_DOWN->{buffer.clear();gesture.down(e.x/density.toDouble(),e.y/density.toDouble(),e.eventTime);parent?.requestDisallowInterceptTouchEvent(true)}
   MotionEvent.ACTION_POINTER_DOWN->{buffer.clear();if(e.pointerCount==2)gesture.beginScroll(centerX(),centerY())else cancelGesture()}
   MotionEvent.ACTION_MOVE->gesture.motion(centerX(),centerY())
   MotionEvent.ACTION_POINTER_UP->cancelGesture()
   MotionEvent.ACTION_UP->{gesture.motion(centerX(),centerY());gesture.up(e.eventTime);parent?.requestDisallowInterceptTouchEvent(false)}
   MotionEvent.ACTION_CANCEL->cancelGesture()
  }
  return true
 }
 override fun onDetachedFromWindow() {cancelGesture();super.onDetachedFromWindow()}
}
