package com.example.sunkai.heritage.tools

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v7.graphics.Palette
import androidx.graphics.drawable.toBitmap
import com.example.sunkai.heritage.Data.GlobalContext
import com.example.sunkai.heritage.R

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
    return Palette.from(this).generate().getDominantColor(ContextCompat.getColor(GlobalContext.instance, R.color.colorPrimaryDark))
}

fun Bitmap.generateDarkColor():Int{
    return Palette.from(this).generate().getDarkVibrantColor(ContextCompat.getColor(GlobalContext.instance,R.color.colorPrimary))
}

fun Drawable.generateTextColor():Int{
    val bitmap=this.toBitmap()
    return bitmap.generateTextColor()
}

fun Bitmap.generateTextColor():Int{
    return Palette.from(this).generate().dominantSwatch?.titleTextColor?:ContextCompat.getColor(GlobalContext.instance,R.color.colorPrimary)
}