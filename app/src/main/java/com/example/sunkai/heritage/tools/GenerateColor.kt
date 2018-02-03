package com.example.sunkai.heritage.tools

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v7.graphics.Palette
import com.example.sunkai.heritage.Data.GlobalContext
import com.example.sunkai.heritage.R

/**
 * 判断图片颜色
 * Created by sunkai on 2018/1/18.
 */

fun generateColor(drawable: Drawable?):Int{
    if(drawable is BitmapDrawable){
        val bitmap=drawable.bitmap
        return generateColor(bitmap)
    }
    return ContextCompat.getColor(GlobalContext.instance, R.color.colorPrimaryDark)
}

fun generateColor(bitmap: Bitmap):Int{
    return Palette.from(bitmap).generate().getDominantColor(ContextCompat.getColor(GlobalContext.instance, R.color.colorPrimaryDark))
}

fun generateDarkColor(drawable: Drawable):Int{
    return if(drawable is BitmapDrawable){
        generateDarkColor(drawable.bitmap)
    }else{
        ContextCompat.getColor(GlobalContext.instance, R.color.colorPrimaryDark)
    }

}

fun generateDarkColor(bitmap: Bitmap):Int{
    return Palette.from(bitmap).generate().getDarkVibrantColor(ContextCompat.getColor(GlobalContext.instance,R.color.colorPrimary))
}

fun generateTextColor(drawable: Drawable):Int?{

    return if(drawable is BitmapDrawable) generateTextColor(drawable.bitmap) else null
}

fun generateTextColor(bitmap: Bitmap):Int?{
    return Palette.from(bitmap).generate().dominantSwatch?.titleTextColor
}