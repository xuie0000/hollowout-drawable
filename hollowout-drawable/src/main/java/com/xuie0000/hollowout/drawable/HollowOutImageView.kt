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

  private var arcStartAngle: Float
  private var arcSweepAngle: Float

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
      roundCornerStroke = getDimension(
        R.styleable.HollowOutImageView_hollow_out_round_corner_stroke, -1f
      )
      roundCornerColor = getColor(
        R.styleable.HollowOutImageView_hollow_out_round_corner_color, "#F2F2F2".toColorInt()
      )

      arcStartAngle = getFloat(R.styleable.HollowOutImageView_hollow_out_arc_startAngle, 0f)
      arcSweepAngle = getFloat(R.styleable.HollowOutImageView_hollow_out_arc_sweepAngle, 180f)

      recycle()

    }

    roundCornerPaint.run {
      strokeWidth = roundCornerStroke
      color = roundCornerColor
      style = Paint.Style.STROKE
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
      CornerShape.ROUND_CORNER -> {
        clipPath.addRoundRect(
          paddingStartEnd,
          paddingTopBottom,
          width - paddingStartEnd,
          height - paddingTopBottom,
          roundCorner,
          roundCorner,
          Path.Direction.CW
        )
        val rectF = RectF()
        // 设置左上角
        rectF.set(
          paddingStartEnd,
          paddingTopBottom,
          paddingStartEnd + roundCorner * 2,
          paddingTopBottom + roundCorner * 2
        )
        strokePath.reset()
        strokePath.arcTo(rectF, 180f, 90f, true)
        // 右上角
        rectF.set(
          width - paddingStartEnd - roundCorner * 2,
          paddingTopBottom,
          width - paddingStartEnd,
          paddingTopBottom + roundCorner * 2
        )
        strokePath.arcTo(rectF, 270f, 90f, true)
        // 右下角
        rectF.set(
          width - paddingStartEnd - roundCorner * 2,
          height - paddingTopBottom - roundCorner * 2,
          width - paddingStartEnd,
          height - paddingTopBottom
        )
        strokePath.arcTo(rectF, 0f, 90f, true)
        // 左下角
        rectF.set(
          paddingStartEnd,
          height - paddingTopBottom - roundCorner * 2,
          paddingStartEnd + roundCorner * 2,
          height - paddingTopBottom
        )
        strokePath.arcTo(rectF, 90f, 90f, true)


      }
      CornerShape.CIRCLE -> {
        val x = width / 2f
        val y = height / 2f
        val radius = if (width < height) x - paddingStartEnd else y - paddingTopBottom
        clipPath.addCircle(x, y, radius, Path.Direction.CW)
      }
      CornerShape.OVAL -> clipPath.addOval(
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
      CornerShape.ARC -> clipPath.addArc(
        paddingStartEnd,
        paddingTopBottom,
        width - paddingStartEnd,
        height - paddingTopBottom, arcStartAngle, arcSweepAngle
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