package com.example.sunkai.heritage.views

import android.view.MotionEvent

class CollaborativeBounceIntercepterImpl(val view: CollaborativeBounceView) :
    CollaborativeBounceIntercepter {
    override fun dispatchTouchEvent(event: MotionEvent) {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                view.initialActionDownTranslationY = view.viewTranslationDistance
                view.horizentalInitialPosition = event.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                val moveOrientation = if(event.rawY - view.horizentalInitialPosition > 0)CollaborativeBounceView.MoveOrientation.Down else CollaborativeBounceView.MoveOrientation.Up
                if(view.interceptMoveEventBlocker==null || view.interceptMoveEventBlocker?.let { it(event,moveOrientation) } == false){
                    moveView(event.rawY)
                }

            }
            MotionEvent.ACTION_UP -> {
                if (view.autoBounce) {
                    onBounce()
                }
            }
        }
    }

    private fun onBounce() {
        val moveDistance =
            if (view.currentState == CollaborativeBounceView.BounceType.Hide) view.maxBoundry - view.viewTranslationDistance else view.viewTranslationDistance - view.minBoundry
        val boundryDistance = view.maxBoundry - view.minBoundry
        val bounceType =
            if (moveDistance > boundryDistance / 4) revertState() else view.currentState
        view.currentState = bounceType
        view.onBounceListener?.invoke(bounceType)
        view.viewTranslationDistance = when (bounceType) {
            CollaborativeBounceView.BounceType.Hide -> view.maxBoundry.toFloat()
            CollaborativeBounceView.BounceType.Expand -> view.minBoundry.toFloat()
        }
        view.onMoveListener?.invoke(
            (view.maxBoundry - view.viewTranslationDistance).toInt(),
            if (view.viewTranslationDistance == view.maxBoundry.toFloat()) 0.0f else 1.0f
        )
    }

    private fun revertState(): CollaborativeBounceView.BounceType {
        return if (view.currentState == CollaborativeBounceView.BounceType.Hide)
            CollaborativeBounceView.BounceType.Expand else CollaborativeBounceView.BounceType.Hide
    }

    private fun moveView(movePosition: Float) {
        var nextPosition =
            movePosition - view.horizentalInitialPosition + view.initialActionDownTranslationY
        if (nextPosition < view.minBoundry) {
            nextPosition = view.minBoundry.toFloat()
        }
        if (nextPosition > view.maxBoundry) {
            nextPosition = view.maxBoundry.toFloat()
        }
        if (view.viewTranslationDistance != nextPosition) {
            view.viewTranslationDistance = nextPosition
            val offsetFromMaxBoundryPercentage =
                (view.maxBoundry - nextPosition) / (view.maxBoundry - view.minBoundry)
            view.onMoveListener?.invoke(
                (view.maxBoundry - movePosition).toInt(),
                offsetFromMaxBoundryPercentage
            )
        }
    }
}