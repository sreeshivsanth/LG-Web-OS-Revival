package org.lgrevival.core

import kotlin.math.*
enum class GestureState { IDLE, POSSIBLE_TAP, DRAGGING, SCROLLING, CANCELLED }
data class PointerSettings(val sensitivity:Int=5,val acceleration:Boolean=false,val tap:Boolean=true,val scrollSensitivity:Int=5)
/** Coordinates are density-independent; no absolute TV coordinate mapping. */
class GestureInterpreter(var settings:PointerSettings=PointerSettings(), private val move:(Double,Double)->Unit, private val scroll:(Double)->Unit, private val click:()->Unit) {
 var state=GestureState.IDLE; private set
 private var x=0.0;private var y=0.0;private var startX=0.0;private var startY=0.0;private var started=0L
 fun down(x:Double,y:Double,time:Long) {this.x=x;this.y=y;startX=x;startY=y;started=time;state=GestureState.POSSIBLE_TAP}
 fun beginScroll(x:Double,y:Double) {this.x=x;this.y=y;state=GestureState.SCROLLING}
 fun motion(nx:Double,ny:Double) {
  if(state==GestureState.IDLE || state==GestureState.CANCELLED) return
  val dx=nx-x;val dy=ny-y
  if(state==GestureState.SCROLLING) {if(abs(dy)>=0.5) {scroll(dy*settings.scrollSensitivity.coerceIn(1,10)/25.0);x=nx;y=ny};return}
  if(state==GestureState.POSSIBLE_TAP) {
   if(hypot(nx-startX,ny-startY)<8.0) return
   state=GestureState.DRAGGING
  }
  if(hypot(dx,dy)<0.5) return
  x=nx;y=ny
  val acceleration=if(settings.acceleration && hypot(dx,dy)>8) 1.5 else 1.0
  val scale=settings.sensitivity.coerceIn(1,10)/5.0*acceleration
  move((dx*scale).coerceIn(-80.0,80.0),(dy*scale).coerceIn(-80.0,80.0))
 }
 fun up(time:Long) {if(state==GestureState.POSSIBLE_TAP && time-started in 0..250 && settings.tap) click();state=GestureState.IDLE}
 fun cancel() {state=GestureState.CANCELLED}
}
data class MotionFrame(val dx:Int,val dy:Int,val scroll:Int)
/** 30 Hz max; retains fractions and untransmitted deltas. Bounded backlog prevents long tails. */
class MotionBuffer {
 private var x=0.0;private var y=0.0;private var scroll=0.0;private var last=Long.MIN_VALUE
 fun move(dx:Double,dy:Double) {if(dx.isFinite()&&dy.isFinite()){x=(x+dx).coerceIn(-640.0,640.0);y=(y+dy).coerceIn(-640.0,640.0)}}
 fun scroll(delta:Double) {if(delta.isFinite())scroll=(scroll+delta).coerceIn(-40.0,40.0)}
 fun pending()=abs(x)>=1 || abs(y)>=1 || abs(scroll)>=1
 fun drain(now:Long):MotionFrame? {
  if(!pending() || (last!=Long.MIN_VALUE && now-last<34))return null
  last=now;val dx=x.toInt().coerceIn(-80,80);val dy=y.toInt().coerceIn(-80,80);val ds=scroll.toInt().coerceIn(-10,10)
  x-=dx;y-=dy;scroll-=ds;return MotionFrame(dx,dy,ds)
 }
 fun clear() {x=0.0;y=0.0;scroll=0.0;last=Long.MIN_VALUE}
}
