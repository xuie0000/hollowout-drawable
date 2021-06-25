package com.xuie0000.hollowout.drawable

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.toColorInt
import androidx.core.graphics.withSave
import com.xuie0000.hollowout.drawable.util.blur
import com.xuie0000.hollowout.drawable.util.dp

internal open class HollowOut(
  private val context: Context,
  attrs: AttributeSet?,
  private val view: AppCompatImageView
) {

  /**
   * 遮罩默认颜色
   */
  private var coverColor: Int
  private val defaultCoverColor = "#99000000".toColorInt()

  /**
   * 间距
   */
  private var outPadding: Float
  private var outPaddingStart: Float
  private var outPaddingTop: Float
  private var outPaddingEnd: Float
  private var outPaddingBottom: Float

  private var cornerShape: CornerShape

  /**
   * 圆角矩形的角度
   */
  private var roundCorner: Float
  private var roundCornerStroke: Float
  private var roundCornerColor: Int

  private val bounds = RectF()
  private val clipPath = Path()
  private val strokePath = Path()

  private var blurBitmap: Bitmap? = null

  private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
  private val roundCornerPaint = Paint(Paint.ANTI_ALIAS_FLAG)

  private val drawRoundCornerStroke get() = cornerShape == CornerShape.ROUND_CORNER && roundCornerStroke > 0

  init {
    context.obtainStyledAttributes(attrs, R.styleable.HollowOutImageView).apply {

      coverColor = getColor(
        R.styleable.HollowOutImageView_hollow_out_color, -1
      )
      outPadding = getDimension(
        R.styleable.HollowOutImageView_hollow_out_padding, 40.dp
      )
      outPaddingStart = getDimension(
        R.styleable.HollowOutImageView_hollow_out_paddingStart, -1f
      )
      outPaddingTop = getDimension(
        R.styleable.HollowOutImageView_hollow_out_paddingTop, -1f
      )
      outPaddingEnd = getDimension(
        R.styleable.HollowOutImageView_hollow_out_paddingEnd, -1f
      )
      outPaddingBottom = getDimension(
        R.styleable.HollowOutImageView_hollow_out_paddingBottom, -1f
      )

      cornerShape = CornerShape.values()[getInt(
        R.styleable.HollowOutImageView_hollow_out_shape,
        CornerShape.ROUND_CORNER.ordinal
      )]
      roundCorner = getDimension(
        R.styleable.HollowOutImageView_hollow_out_roundCorner, 10.dp
      )
      roundCornerStroke = getDimension(
        R.styleable.HollowOutImageView_hollow_out_roundCornerStroke, -1f
      )
      roundCornerColor = getColor(
        R.styleable.HollowOutImageView_hollow_out_roundCornerColor, "#F2F2F2".toColorInt()
      )

      recycle()

    }

    roundCornerPaint.run {
      strokeWidth = roundCornerStroke
      color = roundCornerColor
      style = Paint.Style.STROKE
    }
  }

  fun layout() {
    if (blurBitmap == null) {
      blurBitmap = when {
        coverColor != -1 -> createColorBitmap(view.width, view.height, coverColor)
        // filter color resources, eg:`android:background="@color/teal_200"`
        view.drawable != null && view.drawable.intrinsicWidth > 0 -> context.blur(view.drawable.toBitmap())
        view.background != null && view.background.intrinsicWidth > 0 -> context.blur(view.background.toBitmap())
        else -> null
      }
    }
  }

  fun sizeChanged() {
    val width = view.width
    val height = view.height
    //设置离屏缓冲的范围
    bounds.set(0f, 0f, width.toFloat(), height.toFloat())
    //设置Clip Path的矩形区域
    val paddingStart = if (outPaddingStart > 0) outPaddingStart else outPadding
    val paddingTop = if (outPaddingTop > 0) outPaddingTop else outPadding
    val paddingEnd = if (outPaddingEnd > 0) outPaddingEnd else outPadding
    val paddingBottom = if (outPaddingBottom > 0) outPaddingBottom else outPadding
    when (cornerShape) {
      CornerShape.ROUND_CORNER -> {
        clipPath.addRoundRect(
          paddingStart,
          paddingTop,
          width - paddingEnd,
          height - paddingBottom,
          roundCorner,
          roundCorner,
          Path.Direction.CW
        )
        val rectF = RectF()
        // 设置左上角
        rectF.set(
          paddingStart,
          paddingTop,
          paddingEnd + roundCorner * 2,
          paddingBottom + roundCorner * 2
        )
        strokePath.reset()
        strokePath.arcTo(rectF, 180f, 90f, true)
        // 右上角
        rectF.set(
          width - paddingStart - roundCorner * 2,
          paddingTop,
          width - paddingEnd,
          paddingBottom + roundCorner * 2
        )
        strokePath.arcTo(rectF, 270f, 90f, true)
        // 右下角
        rectF.set(
          width - paddingStart - roundCorner * 2,
          height - paddingTop - roundCorner * 2,
          width - paddingEnd,
          height - paddingBottom
        )
        strokePath.arcTo(rectF, 0f, 90f, true)
        // 左下角
        rectF.set(
          paddingStart,
          height - paddingTop - roundCorner * 2,
          paddingEnd + roundCorner * 2,
          height - paddingBottom
        )
        strokePath.arcTo(rectF, 90f, 90f, true)
      }
      CornerShape.CIRCLE -> {
        val x = width / 2f
        val y = height / 2f
        val radius = if (width < height) x - paddingStart else y - paddingTop
        clipPath.addCircle(x, y, radius, Path.Direction.CW)
      }
      CornerShape.OVAL -> clipPath.addOval(
        paddingStart,
        paddingTop,
        width - paddingEnd,
        height - paddingBottom, Path.Direction.CW
      )
      CornerShape.RECTANGLE -> clipPath.addRect(
        paddingStart,
        paddingTop,
        width - paddingEnd,
        height - paddingBottom,
        Path.Direction.CW
      )
    }
  }

  fun draw(canvas: Canvas) {
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
      // CornerShape.ROUND_CORNER
      if (drawRoundCornerStroke) {
        canvas.drawPath(strokePath, roundCornerPaint)
      }
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