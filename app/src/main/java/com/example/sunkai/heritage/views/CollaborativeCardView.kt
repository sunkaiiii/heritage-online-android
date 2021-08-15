package com.example.sunkai.heritage.views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.google.android.material.card.MaterialCardView

class CollaborativeCardView@JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
):MaterialCardView(context,attrs,defStyleAttr),CollaborativeView {
    private var dispatchTouchEventHandler: ((View, MotionEvent) -> Unit)? = null
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val event = ev?:return super.dispatchTouchEvent(ev)
        dispatchTouchEventHandler?.let {
            it(this,event)
        }
        return super.dispatchTouchEvent(event)
    }

    override fun setDispatchTouchEventHandler(eventHandler: (View, MotionEvent) -> Unit) {
        this.dispatchTouchEventHandler = eventHandler
    }
}