package com.example.sunkai.heritage.views

import android.view.MotionEvent
import android.view.View

interface CollaborativeView {
    var dispatchTouchEventHandler : ((View, MotionEvent) -> Unit)? get set
}