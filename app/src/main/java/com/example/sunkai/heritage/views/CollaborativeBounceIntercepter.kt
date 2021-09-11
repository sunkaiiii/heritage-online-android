package com.example.sunkai.heritage.views

import android.view.MotionEvent

interface CollaborativeBounceIntercepter {
    fun dispatchTouchEvent(event: MotionEvent)
}