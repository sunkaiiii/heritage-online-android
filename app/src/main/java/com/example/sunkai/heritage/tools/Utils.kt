package com.example.sunkai.heritage.tools

import android.content.res.Configuration
import androidx.core.content.ContextCompat

object Utils {
    fun dip2px(dipValue: Int): Int {
        val scale = EHeritageApplication.instance.resources.displayMetrics.density
        return (dipValue * scale + 0.5).toInt()
    }

    fun getScreenWidth(): Int = EHeritageApplication.instance.resources.displayMetrics.widthPixels


    fun getScreenHeight(): Int = EHeritageApplication.instance.resources.displayMetrics.heightPixels


    fun getDpi(): Int = EHeritageApplication.instance.resources.displayMetrics.densityDpi


    fun getScreenMode(): Int = EHeritageApplication.instance.resources.configuration.orientation

    //是否为横屏
    fun isHorizontalScreenMode(): Boolean = getScreenMode() == Configuration.ORIENTATION_PORTRAIT

    fun getColorResource(resID: Int): Int = ContextCompat.getColor(EHeritageApplication.instance, resID)
}