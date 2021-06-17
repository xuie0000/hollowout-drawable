package com.xuie0000.hollowout.drawable.util

import android.content.res.Resources
import android.util.DisplayMetrics
import android.util.TypedValue

val Float.dp
  get() = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this,
    Resources.getSystem().displayMetrics
  )

val Int.dp get() = this.toFloat().dp

val Float.dp2px
  get() = this * (Resources.getSystem().displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)

val Int.dp2Px get() = this.toFloat().dp2px