package com.example.sunkai.heritage.tools

import android.app.Activity
import android.graphics.Color

//设置全屏沉浸窗口
object WindowHelper {
    //TODO 修复全屏的问题
    fun setWindowFullScreen(activity: Activity) {
        activity.window.navigationBarColor = Color.TRANSPARENT
        activity.window.statusBarColor = Color.TRANSPARENT
    }
}