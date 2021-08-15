package com.example.sunkai.heritage.views

import android.view.MotionEvent
import android.view.View

interface CollaborativeView {
    fun setDispatchTouchEventHandler(eventHandler:(View, MotionEvent)->Unit)
}