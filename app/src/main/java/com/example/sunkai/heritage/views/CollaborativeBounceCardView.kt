package com.example.sunkai.heritage.views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.google.android.material.card.MaterialCardView

class CollaborativeBounceCardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr), CollaborativeBounceView {
    private var dispatchTouchEventHandler: ((View, MotionEvent) -> Unit)? = null
    private var onBounceListener: ((CollaborativeBounceView.BounceType) -> Unit)? = null
    private var onMoveListener: ((Int, Float) -> Unit)? = null
    private var minBoundry = -1
    private var maxBoundry = -1
    private var initialActionDownTranslationY = -1f
    private var horizentalInitialPosition = -1f
    private var currentState: CollaborativeBounceView.BounceType =
        CollaborativeBounceView.BounceType.Hide

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        validateFields()
        val event = ev ?: return super.dispatchTouchEvent(ev)
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                initialActionDownTranslationY = translationY
                horizentalInitialPosition = event.rawY
            }
            MotionEvent.ACTION_MOVE -> moveView(
                event.rawY
            )
            MotionEvent.ACTION_UP -> onBounce()
        }
        return super.dispatchTouchEvent(event)
    }

    private fun onBounce() {
        val moveDistance =
            if (currentState == CollaborativeBounceView.BounceType.Hide) maxBoundry - translationY else translationY - minBoundry
        val boundryDistance = maxBoundry - minBoundry
        val bounceType =
            if (moveDistance > boundryDistance / 4) revertState() else currentState
        currentState = bounceType
        onBounceListener?.invoke(bounceType)
        translationY = when (bounceType) {
            CollaborativeBounceView.BounceType.Hide -> maxBoundry.toFloat()
            CollaborativeBounceView.BounceType.Expand -> minBoundry.toFloat()
        }
        onMoveListener?.invoke(
            (maxBoundry - translationY).toInt(),
            if (translationY == maxBoundry.toFloat()) 0.0f else 1.0f
        )
    }

    private fun revertState(): CollaborativeBounceView.BounceType {
        return if (currentState == CollaborativeBounceView.BounceType.Hide)
            CollaborativeBounceView.BounceType.Expand else CollaborativeBounceView.BounceType.Hide
    }

    private fun validateFields() {

    }

    override fun setOnBounceAction(listener: (CollaborativeBounceView.BounceType) -> Unit) {
        this.onBounceListener = listener
    }


    override fun setBounceBoundry(minBoundry: Int, maxBoundry: Int) {
        this.minBoundry = minBoundry
        this.maxBoundry = maxBoundry
        translationY = maxBoundry.toFloat()
    }

    override fun setOnMoveAction(listener: (distance: Int, offsetPercentage: Float) -> Unit) {
        this.onMoveListener = listener
    }


    override fun setDispatchTouchEventHandler(eventHandler: (View, MotionEvent) -> Unit) {
        this.dispatchTouchEventHandler = eventHandler
    }

    private fun moveView(movePosition: Float) {
        var nextPosition = movePosition - horizentalInitialPosition + initialActionDownTranslationY
        if (nextPosition < minBoundry) {
            nextPosition = minBoundry.toFloat()
        }
        if (nextPosition > maxBoundry) {
            nextPosition = maxBoundry.toFloat()
        }
        if (translationY != nextPosition) {
            translationY = nextPosition
            val offsetFromMaxBoundryPercentage =
                (maxBoundry - nextPosition) / (maxBoundry - minBoundry)
            onMoveListener?.invoke(
                (maxBoundry - movePosition).toInt(),
                offsetFromMaxBoundryPercentage
            )
        }
    }

}