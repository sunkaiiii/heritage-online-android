package com.example.sunkai.heritage.views

import android.view.MotionEvent
import com.example.sunkai.heritage.entity.CollaborativeViewModel

class CollaborativeBounceIntercepterImpl(val view: CollaborativeBounceView,val viewModel:CollaborativeViewModel) :
    CollaborativeBounceIntercepter {
    override fun dispatchTouchEvent(event: MotionEvent) {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                viewModel.initialActionDownTranslationY.value = view.viewTranslationDistance
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
            if (view.currentState == CollaborativeBounceView.BounceType.Hide){
                view.maxBoundry - (viewModel.viewTranslationDistance.value?.toInt() ?:view.maxBoundry )
            }
            else {
                (viewModel.viewTranslationDistance.value?.toInt() ?: 0) - view.minBoundry
            }
        val boundryDistance = view.maxBoundry - view.minBoundry
        val bounceType =
            if (moveDistance > boundryDistance / 4) revertState() else view.currentState
        view.currentState = bounceType
        view.onBounceListener?.invoke(bounceType)
        viewModel.viewTranslationDistance.value = when (bounceType) {
            CollaborativeBounceView.BounceType.Hide -> view.maxBoundry.toFloat()
            CollaborativeBounceView.BounceType.Expand -> view.minBoundry.toFloat()
        }
        view.onMoveListener?.invoke(
                view.maxBoundry - (viewModel.viewTranslationDistance.value?.toInt()?:0),
            if (viewModel.viewTranslationDistance.value == view.maxBoundry.toFloat()) 0.0f else 1.0f
        )
    }

    private fun revertState(): CollaborativeBounceView.BounceType {
        return if (view.currentState == CollaborativeBounceView.BounceType.Hide)
            CollaborativeBounceView.BounceType.Expand else CollaborativeBounceView.BounceType.Hide
    }

    private fun moveView(movePosition: Float) {
        var nextPosition =
            movePosition - view.horizentalInitialPosition + (viewModel.initialActionDownTranslationY.value?.toInt()?:0)
        if (nextPosition < view.minBoundry) {
            nextPosition = view.minBoundry.toFloat()
        }
        if (nextPosition > view.maxBoundry) {
            nextPosition = view.maxBoundry.toFloat()
        }
        if (viewModel.viewTranslationDistance.value != nextPosition) {
            viewModel.viewTranslationDistance.value = nextPosition
            val offsetFromMaxBoundryPercentage =
                (view.maxBoundry - nextPosition) / (view.maxBoundry - view.minBoundry)
            view.onMoveListener?.invoke(
                (view.maxBoundry - movePosition).toInt(),
                offsetFromMaxBoundryPercentage
            )
        }
    }
}