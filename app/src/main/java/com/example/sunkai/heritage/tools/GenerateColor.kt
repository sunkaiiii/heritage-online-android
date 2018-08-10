package com.example.sunkai.heritage.tools

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette

/**
 * 判断图片颜色
 * Created by sunkai on 2018/1/18.
 */

fun Drawable.generateColor():Int{
    val bitmap=this.toBitmap()
    return bitmap.generateColor()
}

fun Drawable.generateDarkColor():Int{
    val bitmap=this.toBitmap()
    return bitmap.generateDarkColor()
}

fun Bitmap.generateColor():Int{
    return Palette.from(this).generate().getDominantColor(getThemeColor())
}

fun Bitmap.generateDarkColor():Int{
    return Palette.from(this).generate().getDarkVibrantColor(getThemeColor())
}

fun Drawable.generateTextColor():Int{
    val bitmap=this.toBitmap()
    return bitmap.generateTextColor()
}

fun Bitmap.generateTextColor():Int{
    return Palette.from(this).generate().dominantSwatch?.titleTextColor?: getThemeColor()
}