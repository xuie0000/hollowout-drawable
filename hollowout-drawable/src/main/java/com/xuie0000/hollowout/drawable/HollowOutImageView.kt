package com.xuie0000.hollowout.drawable

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.toColorInt
import androidx.core.graphics.withSave
import com.xuie0000.hollowout.drawable.util.blur
import com.xuie0000.hollowout.drawable.util.dp


/**
 * 参考：https://zhuanlan.zhihu.com/p/329825945
 */
class HollowOutImageView @JvmOverloads constructor(
  context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

  private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

  /**
   * 遮罩默认颜色
   */
  private var coverColor: Int
  private val defaultCoverColor = "#99000000".toColorInt()

  /**
   * 间距
   */
  private var outPadding: Float
  private var outPaddingHorizontal: Float
  private var outPaddingVertical: Float
  private var cornerShape: CornerShape

  /**
   * 圆角矩形的角度
   */
  private var roundCorner: Float

  private val bounds = RectF()
  private val porterDuffXfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OUT)
  private val clipPath = Path()

  private var blurBitmap: Bitmap? = null

  init {
    context.obtainStyledAttributes(attrs, R.styleable.HollowOutImageView).apply {

      coverColor = getColor(
        R.styleable.HollowOutImageView_hollow_out_color, -1
      )
      outPadding = getDimension(
        R.styleable.HollowOutImageView_hollow_out_padding, 40.dp
      )
      outPaddingHorizontal = getDimension(
        R.styleable.HollowOutImageView_hollow_out_padding_horizontal, -1f
      )
      outPaddingVertical = getDimension(
        R.styleable.HollowOutImageView_hollow_out_padding_vertical, -1f
      )

      cornerShape = CornerShape.values()[getInt(
        R.styleable.HollowOutImageView_hollow_out_shape,
        CornerShape.ROUND_CORNER.ordinal
      )]
      roundCorner = getDimension(
        R.styleable.HollowOutImageView_hollow_out_round_corner, 10.dp
      )

      recycle()

    }
  }

  @SuppressLint("DrawAllocation")
  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    super.onLayout(changed, left, top, right, bottom)

    if (blurBitmap == null) {
      blurBitmap = when {
        coverColor != -1 -> createColorBitmap(width, height, coverColor)
        drawable != null -> context.blur(drawable.toBitmap())
        background != null -> context.blur(background.toBitmap())
        else -> null
      }
    }
  }


  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    super.onSizeChanged(w, h, oldw, oldh)
    //设置离屏缓冲的范围
    bounds.set(0f, 0f, width.toFloat(), height.toFloat())
    //设置Clip Path的矩形区域
    val paddingStartEnd = if (outPaddingHorizontal > 0) outPaddingHorizontal else outPadding
    val paddingTopBottom = if (outPaddingVertical > 0) outPaddingVertical else outPadding
    when (cornerShape) {
      CornerShape.ROUND_CORNER -> clipPath.addRoundRect(
        paddingStartEnd,
        paddingTopBottom,
        width - paddingStartEnd,
        height - paddingTopBottom,
        roundCorner,
        roundCorner,
        Path.Direction.CW
      )
      CornerShape.CIRCLE -> clipPath.addOval(
        paddingStartEnd,
        paddingTopBottom,
        width - paddingStartEnd,
        height - paddingTopBottom, Path.Direction.CW
      )
      CornerShape.RECTANGLE -> clipPath.addRect(
        paddingStartEnd,
        paddingTopBottom,
        width - paddingStartEnd,
        height - paddingTopBottom,
        Path.Direction.CW
      )
    }

  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    //Canvas的离屏缓冲
    val count = canvas.saveLayer(bounds, paint)
    //KTX的扩展函数相当于对Canvas的 save 和 restore 操作
    canvas.withSave {
      //画遮罩的颜色
      blurBitmap?.let {
        canvas.drawBitmap(it, null, bounds, null)
      } ?: canvas.drawColor(defaultCoverColor)
      //按Path来裁切
      canvas.clipPath(clipPath)
      //画镂空的范围
      canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.SRC)
    }
    //把离屏缓冲的内容,绘制到View上去
    canvas.restoreToCount(count)
  }

  private fun createColorBitmap(width: Int, height: Int, color: Int): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint()
    paint.color = color
    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
    return bitmap
  }
}