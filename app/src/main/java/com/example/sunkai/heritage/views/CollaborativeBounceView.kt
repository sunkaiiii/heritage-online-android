package com.example.sunkai.heritage.views

import android.view.MotionEvent
import androidx.lifecycle.ViewModel
import com.example.sunkai.heritage.entity.CollaborativeViewModel
import com.example.sunkai.heritage.entity.CollaborativeViewModelImpl

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
    var collaborativeViewModel:CollaborativeViewModel?
    var intercepter: CollaborativeBounceIntercepter?

    fun setOnBounceAction(listener: (BounceType) -> Unit) {
        this.onBounceListener = listener
    }


    fun setBounceBoundry(minBoundry: Int, maxBoundry: Int,collaborativeViewModel: CollaborativeViewModel) {
        this.minBoundry = minBoundry
        this.maxBoundry = maxBoundry
        collaborativeViewModel.viewTranslationDistance.value = collaborativeViewModel.viewTranslationDistance.value ?: maxBoundry.toFloat()
        this.collaborativeViewModel = collaborativeViewModel
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