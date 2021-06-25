# android hollowout drawable

镂空遮罩图层库是为了适配网易云Video的封面时看到有一层图层，且转角还画了线，没有找到好的方案就自己写成了库

<p align="center">
<img src="hollowout-drawable.png" width="32%"/>
<img src="hollowout-drawable2.png" width="32%"/>
<img src="https://user-images.githubusercontent.com/8099426/122522727-da158c00-d048-11eb-81e6-00ce67ec0b75.gif" width="32%"/>
</p>

> Notes: library use 'androidx.palette:palette-ktx', if don't want to， you may give up

## Including in your project
[![Maven Central](https://img.shields.io/maven-central/v/com.xuie0000/hollowout.drawable.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.xuie0000%22%20AND%20a:%22hollowout.drawable%22)

repositories add `mavenCentral()`

```groovy
implementation "com.xuie0000:hollowout.drawable:1.0.3"
```

# Usage

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
    app:layout_constraintTop_toTopOf="parent"
    app:shapeAppearanceOverlay="@style/roundedCornerStyle" />
```

if you load net-image, need **disable hardware** function

```kotlin
binding.image.load(url) {
  allowHardware(false)
}
```

## 属性介绍

Attribute | Type | Default | Description
---|---|---|---
hollow_out_padding | dimension | 40dp | padding to view side
hollow_out_paddingStart | dimension | | start/left padding to view side
hollow_out_paddingTop | dimension | | top padding to view side
hollow_out_paddingEnd | dimension | | end/right padding to view side
hollow_out_paddingBottom | dimension | | bottom padding to view side
hollow_out_shape | enum | round_corner | shapes, include round_corner,circle,oval,rectangle
hollow_out_color | color | null | if the color exist, will **replace blur image**
hollow_out_roundCorner | dimension | 10dp | round corner radius
hollow_out_roundCornerStroke | dimension | | round corner stroke width
hollow_out_roundCornerColor | color | 10dp | round corner stroke color

# References

https://zhuanlan.zhihu.com/p/329825945