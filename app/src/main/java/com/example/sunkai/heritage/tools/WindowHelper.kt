package com.example.sunkai.heritage.tools

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi

//设置全屏沉浸窗口
object WindowHelper {
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun setWindowFullScreen(activity: Activity) {
        val decorView = activity.window.decorView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            var option = (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                option = option or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                option = option or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.window.navigationBarColor = Color.TRANSPARENT
            }
            decorView.systemUiVisibility = option
        }
    }
}