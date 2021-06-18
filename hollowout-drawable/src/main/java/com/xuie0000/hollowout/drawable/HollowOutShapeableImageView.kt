package com.xuie0000.hollowout.drawable

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import com.google.android.material.imageview.ShapeableImageView
import java.lang.reflect.Field

class HollowOutShapeableImageView @JvmOverloads constructor(
  context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ShapeableImageView(context, attrs, defStyleAttr) {

  private var maskPath: Path? = null
  private val clearPaint = Paint().apply {
    isAntiAlias = true
    color = Color.WHITE
    xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
  }

  init {
    maskPath = get(this, "maskPath")
  }

  private val hollowOut = HollowOut(context, attrs, this)

  @SuppressLint("DrawAllocation")
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
    maskPath?.let {
      canvas.drawPath(it, clearPaint)
    }
  }

  operator fun get(instance: Any, variableName: String): Path? {
    val targetClass: Class<*> = instance.javaClass.superclass
    val superInst: ShapeableImageView = targetClass.cast(instance) as ShapeableImageView
    val field: Field
    return try {
      field = targetClass.getDeclaredField(variableName)
      //修改访问限制
      field.isAccessible = true
      // superInst 为 null 可以获取静态成员
      // 非 null 访问实例成员
      field.get(superInst) as Path
    } catch (e: Exception) {
      e.printStackTrace()
      null
    }
  }
}