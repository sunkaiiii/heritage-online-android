package com.example.sunkai.heritage.tools

import android.content.res.Configuration
import androidx.compose.ui.unit.Dp
import androidx.core.content.ContextCompat

object Utils {
    fun dip2px(dipValue: Int): Int {
        val scale = EHeritageApplication.instance.resources.displayMetrics.density
        return (dipValue * scale + 0.5).toInt()
    }

    fun px2dip(pxValue:Int):Int{
        val scale = EHeritageApplication.instance.resources.displayMetrics.density
        return (pxValue/scale).toInt()
    }

    @JvmName("dip2px1")
    fun Int.dip2px(): Int {
        return dip2px(this)
    }

    @JvmName("px2dip1")
    fun Int.px2dip():Int{
        return px2dip(this)
    }

    fun Float.dip2px(): Int {
        return Utils.dip2px(this.toInt())
    }

    fun getScreenWidth(): Int = EHeritageApplication.instance.resources.displayMetrics.widthPixels


    fun getScreenHeight(): Int = EHeritageApplication.instance.resources.displayMetrics.heightPixels


    fun getDpi(): Int = EHeritageApplication.instance.resources.displayMetrics.densityDpi


    fun getScreenMode(): Int = EHeritageApplication.instance.resources.configuration.orientation

    //是否为横屏
    fun isHorizontalScreenMode(): Boolean = getScreenMode() == Configuration.ORIENTATION_PORTRAIT

    fun getColorResource(resID: Int): Int =
        ContextCompat.getColor(EHeritageApplication.instance, resID)
}

fun getString(id: Int) =
    EHeritageApplication.instance.getString(id)