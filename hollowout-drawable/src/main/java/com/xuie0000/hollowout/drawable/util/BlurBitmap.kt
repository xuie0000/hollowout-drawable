/*
 * Copyright (c) 2021 Jie Xu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xuie0000.hollowout.drawable.util

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import kotlin.math.roundToInt

/**
 * http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2016/0816/6543.html
 * https://developer.android.com/guide/topics/renderscript/compute.html
 */

/**
 * 图片缩放比例
 */
private const val BITMAP_SCALE = 0.4f

/**
 * 最大模糊度(在0.0到25.0之间)
 */
private const val BLUR_RADIUS = 25f

/**
 * 模糊图片的具体方法
 *
 * @param image   需要模糊的图片
 * @return 模糊处理后的图片
 */
fun Context.blur(image: Bitmap): Bitmap {
  // 计算图片缩小后的长宽
  val width = (image.width * BITMAP_SCALE).roundToInt()
  val height = (image.height * BITMAP_SCALE).roundToInt()

  // 将缩小后的图片做为预渲染的图片。
  val inputBitmap = Bitmap.createScaledBitmap(image, width, height, false)
  // 创建一张渲染后的输出图片。
  val outputBitmap = Bitmap.createBitmap(inputBitmap)

  // 创建RenderScript内核对象
  val rs = RenderScript.create(this)
  // 创建一个模糊效果的RenderScript的工具对象
  val blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))

  // 由于RenderScript并没有使用VM来分配内存,所以需要使用Allocation类来创建和分配内存空间。
  // 创建Allocation对象的时候其实内存是空的,需要使用copyTo()将数据填充进去。
  val tmpIn = Allocation.createFromBitmap(rs, inputBitmap)
  val tmpOut = Allocation.createFromBitmap(rs, outputBitmap)

  // 设置渲染的模糊程度, 25f是最大模糊度
  blurScript.setRadius(BLUR_RADIUS)
  // 设置blurScript对象的输入内存
  blurScript.setInput(tmpIn)
  // 将输出数据保存到输出内存中
  blurScript.forEach(tmpOut)

  // 将数据填充到Allocation中
  tmpOut.copyTo(outputBitmap)

  return outputBitmap
}
