package com.example.sunkai.heritage.tools

import android.os.Handler
import android.os.Looper

/**
 * 用来实现类似activity.runonuithread的功能
 * Created by sunkai on 2018/3/12.
 */
fun runOnUiThread(runnable: Runnable) {
    Handler(Looper.getMainLooper()).post(runnable)
}



