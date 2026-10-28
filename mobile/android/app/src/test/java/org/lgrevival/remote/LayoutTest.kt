package org.lgrevival.remote
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
@RunWith(RobolectricTestRunner::class)
@Config(sdk=[28],qualifiers="w320dp-h640dp-xhdpi")
class LayoutTest {
 private fun buttons(v:View):List<Button> = (if(v is Button)listOf(v) else emptyList()) + (if(v is ViewGroup)(0 until v.childCount).flatMap {buttons(v.getChildAt(it))} else emptyList())
 private fun layout(activity:MainActivity):View {
  val root=activity.findViewById<View>(android.R.id.content)
  val density=activity.resources.displayMetrics.density
  root.measure(View.MeasureSpec.makeMeasureSpec((320*density).toInt(),View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec((640*density).toInt(),View.MeasureSpec.EXACTLY));root.layout(0,0,root.measuredWidth,root.measuredHeight);return root
 }
 @Test fun remoteLabelsHaveRoomAndDpTouchTargets() {
  val c=Robolectric.buildActivity(MainActivity::class.java).setup();try {
   val a=c.get();val root=layout(a);val density=a.resources.displayMetrics.density
   val controls=buttons(root);assertTrue(controls.any {it.text=="VOLUME DOWN"})
   controls.forEach {b->assertTrue("touch target: ${b.text}",b.height>=48*density);assertTrue("vertical clipping: ${b.text}",b.layout.height<=b.height-b.compoundPaddingTop-b.compoundPaddingBottom);for(i in 0 until b.layout.lineCount)assertTrue("horizontal clipping: ${b.text}",b.layout.getLineWidth(i)<=b.width-b.compoundPaddingLeft-b.compoundPaddingRight+1)}
  }finally {c.pause().stop().destroy()}
 }
 @Test fun sectionsAndFireShortcutAccessibleWhenDisconnected() {
  val c=Robolectric.buildActivity(MainActivity::class.java).setup();try {
   val a=c.get();var root=layout(a);buttons(root).first {it.text.toString().startsWith("Fire TV")}.performClick();root=layout(a);assertTrue(buttons(root).any {it.text=="Connect securely / reconnect"});buttons(root).first {it.text=="Inputs"}.performClick();root=layout(a);assertFalse(buttons(root).first {it.text=="Refresh input list"}.isEnabled)
  }finally {c.pause().stop().destroy()}
 }
}
