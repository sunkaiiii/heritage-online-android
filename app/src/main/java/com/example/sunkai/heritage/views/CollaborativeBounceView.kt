package com.example.sunkai.heritage.views

import android.view.MotionEvent

interface CollaborativeBounceView : CollaborativeView {
    var onBounceListener: ((BounceType) -> Unit)?
    var onMoveListener: ((Int, Float) -> Unit)?
    var interceptTouchEventBlocker:((event:MotionEvent)->Boolean)?
    var interceptMoveEventBlocker:((event:MotionEvent,moveOrientation:MoveOrientation)->Boolean)?
    var currentState: BounceType
    var minBoundry: Int
    var maxBoundry: Int
    var initialActionDownTranslationY: Float
    var horizentalInitialPosition: Float
    var viewTranslationDistance: Float
    var autoBounce: Boolean
    val intercepter: CollaborativeBounceIntercepter
        get() = CollaborativeBounceIntercepterImpl(
            this
        )


    fun setOnBounceAction(listener: (BounceType) -> Unit) {
        this.onBounceListener = listener
    }


    fun setBounceBoundry(minBoundry: Int, maxBoundry: Int) {
        this.minBoundry = minBoundry
        this.maxBoundry = maxBoundry
        viewTranslationDistance = maxBoundry.toFloat()
    }

    fun setOnMoveAction(listener: (distance: Int, offsetPercentage: Float) -> Unit) {
        this.onMoveListener = listener
    }

    fun setTouchEventBlocker(blocker:(event:MotionEvent)->Boolean){
        this.interceptTouchEventBlocker = blocker
    }

    fun setMoveEventBlocker(blocker: (event:MotionEvent,moveOrientation:MoveOrientation) -> Boolean){
        this.interceptMoveEventBlocker = blocker
    }

    enum class BounceType {
        Expand,
        Hide
    }

    enum class MoveOrientation{
        Down,
        Up
    }


}