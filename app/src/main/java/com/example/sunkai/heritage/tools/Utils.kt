package com.example.sunkai.heritage.tools

import android.content.res.Configuration

object Utils {
    fun dip2px(dipValue:Int):Int{
        val scale=GlobalContext.instance.resources.displayMetrics.density;
        return (dipValue*scale+0.5).toInt()
    }

    fun getScreenWidth():Int{
        return GlobalContext.instance.resources.displayMetrics.widthPixels
    }

    fun getScreenHeight():Int{
        return GlobalContext.instance.resources.displayMetrics.heightPixels
    }

    fun getDpi():Int{
        return GlobalContext.instance.resources.displayMetrics.densityDpi
    }

    fun getScreenMode():Int{
        return GlobalContext.instance.resources.configuration.orientation
    }

    //是否为横屏
    fun isHorizontalScreenMode():Boolean{
        return getScreenMode() == Configuration.ORIENTATION_PORTRAIT
    }
}