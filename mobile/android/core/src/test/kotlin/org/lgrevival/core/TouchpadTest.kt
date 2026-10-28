package org.lgrevival.core
import org.junit.Test
import org.junit.Assert.*
class TouchpadTest {
 private class Pad(settings:PointerSettings=PointerSettings()) {
  val moves=mutableListOf<Pair<Double,Double>>();val scrolls=mutableListOf<Double>();var clicks=0
  val gesture=GestureInterpreter(settings,{x,y->moves.add(x to y)},{scrolls.add(it)},{clicks++})
 }
 @Test fun shortTapClicksButLongPressDoesNot(){val p=Pad();p.gesture.down(10.0,10.0,0);p.gesture.up(200);assertEquals(1,p.clicks);p.gesture.down(10.0,10.0,500);p.gesture.up(800);assertEquals(1,p.clicks)}
 @Test fun relativeMotionAndDragNeverClick(){val p=Pad();p.gesture.down(100.0,100.0,0);p.gesture.motion(110.0,105.0);p.gesture.motion(115.0,103.0);p.gesture.up(100);assertEquals(listOf(10.0 to 5.0,5.0 to -2.0),p.moves);assertEquals(0,p.clicks)}
 @Test fun tapThresholdAndDeadZone(){val p=Pad();p.gesture.down(0.0,0.0,0);p.gesture.motion(7.0,0.0);assertTrue(p.moves.isEmpty());p.gesture.motion(9.0,0.0);p.gesture.motion(9.1,0.1);assertEquals(1,p.moves.size);assertEquals(GestureState.DRAGGING,p.gesture.state)}
 @Test fun sensitivityAndAcceleration(){val low=Pad(PointerSettings(1));val high=Pad(PointerSettings(10,true));for(p in listOf(low,high)){p.gesture.down(0.0,0.0,0);p.gesture.motion(10.0,0.0)};assertEquals(2.0,low.moves[0].first,0.001);assertEquals(30.0,high.moves[0].first,0.001)}
 @Test fun hugeMotionClamped(){val p=Pad();p.gesture.down(0.0,0.0,0);p.gesture.motion(10000.0,-10000.0);assertEquals(80.0 to -80.0,p.moves.single())}
 @Test fun twoFingerScrollNeverClicks(){val p=Pad();p.gesture.down(0.0,0.0,0);p.gesture.beginScroll(0.0,20.0);p.gesture.motion(0.0,30.0);p.gesture.motion(0.0,10.0);p.gesture.up(100);assertEquals(listOf(2.0,-4.0),p.scrolls);assertTrue(p.moves.isEmpty());assertEquals(0,p.clicks)}
 @Test fun cancelDiscardsTapAndFurtherMotion(){val p=Pad();p.gesture.down(0.0,0.0,0);p.gesture.cancel();p.gesture.motion(40.0,40.0);p.gesture.up(10);assertEquals(0,p.clicks);assertTrue(p.moves.isEmpty())}
 @Test fun tapSettingDisablesClick(){val p=Pad(PointerSettings(tap=false));p.gesture.down(0.0,0.0,0);p.gesture.up(10);assertEquals(0,p.clicks)}
 @Test fun coalescingAndRateLimitPreserveNet(){val b=MotionBuffer();b.move(50.5,10.0);b.move(50.5,-5.0);assertEquals(MotionFrame(80,5,0),b.drain(0));assertNull(b.drain(10));assertEquals(MotionFrame(21,0,0),b.drain(34));assertFalse(b.pending())}
 @Test fun fractionalScrollAccumulates(){val b=MotionBuffer();b.scroll(0.4);assertNull(b.drain(0));b.scroll(0.7);assertEquals(MotionFrame(0,0,1),b.drain(1))}
 @Test fun disconnectAndReconnectHaveNoStaleFrames(){val b=MotionBuffer();b.move(40.0,50.0);b.scroll(3.0);b.clear();assertNull(b.drain(50));b.move(2.0,3.0);assertEquals(MotionFrame(2,3,0),b.drain(51))}
 @Test fun protocolFrames(){assertEquals("type:move\ndx:2\ndy:-3\ndown:0\n\n",Protocol.move(2,-3));assertEquals("type:click\n\n",Protocol.click());assertEquals("type:scroll\ndx:0\ndy:-2\n\n",Protocol.scroll(-2))}
}
