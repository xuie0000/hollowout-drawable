# android hollowout drawable

镂空遮罩图层库是为了适配网易云Video的封面时看到有一层图层，且转角还画了线，没有找到好的方案就自己写成了库

<img src="hollowout-drawable.png" width="256"/><img src="hollowout-drawable2.png" width="256"/><img src="https://user-images.githubusercontent.com/8099426/122522727-da158c00-d048-11eb-81e6-00ce67ec0b75.gif" width="256"/>

> Notes: library use 'androidx.palette:palette-ktx', if don't want to， you may give up

# Usage

repositories add `mavenCentral()`

```groovy
implementation "com.xuie0000:hollowout.drawable:1.0.1"
```

support AppCompatImageView&**ShapeableImageView** hollow out drawable

```xml
<com.xuie0000.hollowout.drawable.HollowOutImageView
    android:id="@+id/image"
    android:layout_width="match_parent"
    android:layout_height="160dp"
    android:scaleType="centerCrop"
    app:hollow_out_padding="18dp"
    app:hollow_out_shape="round_corner"
    app:layout_constraintTop_toTopOf="parent" />
```

```xml
<com.xuie0000.hollowout.drawable.HollowOutShapeableImageView
    android:id="@+id/image"
    android:layout_width="match_parent"
    android:layout_height="160dp"
    android:scaleType="centerCrop"
    app:hollow_out_padding="18dp"
    app:hollow_out_shape="round_corner"
    app:layout_constraintTop_toTopOf="parent" />
```

## 属性介绍

Attribute | Type | Default | Description
---|---|---|---
hollow_out_padding | dimension | 40dp | padding to view side
hollow_out_padding_horizontal | dimension | | horizontal padding to view side
hollow_out_padding_vertical | dimension | | vertical padding to view side
hollow_out_shape | enum | round_corner | shapes, include round_corner,circle,oval,rectangle,arc
hollow_out_color | color | null | if the color exist, will **replace blur image**
hollow_out_round_corner | dimension | 10dp | round corner radius
hollow_out_round_corner_stroke | dimension | | round corner stroke width
hollow_out_round_corner_color | color | 10dp | round corner stroke color
hollow_out_arc_startAngle | float | 0f | arc start angle
hollow_out_arc_sweepAngle | float | 180f | arc sweep angle

# References

https://zhuanlan.zhihu.com/p/329825945