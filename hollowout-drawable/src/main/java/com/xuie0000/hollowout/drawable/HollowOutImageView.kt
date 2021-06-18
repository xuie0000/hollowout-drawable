package com.xuie0000.hollowout.drawable

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

/**
 * 参考：https://zhuanlan.zhihu.com/p/329825945
 */
class HollowOutImageView @JvmOverloads constructor(
  context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

  private val hollowOut = HollowOut(context, attrs, this)

  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    super.onLayout(changed, left, top, right, bottom)
    hollowOut.layout()
  }

  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    super.onSizeChanged(w, h, oldw, oldh)
    hollowOut.sizeChanged()
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    hollowOut.draw(canvas)
  }

}