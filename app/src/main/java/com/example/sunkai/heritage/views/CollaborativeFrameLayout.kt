package com.example.sunkai.heritage.views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout

class CollaborativeFrameLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), CollaborativeBounceView {
    private var _dispatchTouchEventHandler: ((View, MotionEvent) -> Unit)? = null
    private var _onBounceListener: ((CollaborativeBounceView.BounceType) -> Unit)? = null
    private var _onMoveListener: ((Int, Float) -> Unit)? = null
    override var minBoundry = -1
    override var maxBoundry = -1
    override var initialActionDownTranslationY = -1f
    override var horizentalInitialPosition = -1f
    override var autoBounce: Boolean = false
    override var viewTranslationDistance
        get() = translationY
        set(value) {
            translationY = value
        }
    override var currentState: CollaborativeBounceView.BounceType =
        CollaborativeBounceView.BounceType.Hide
    override var dispatchTouchEventHandler: ((View, MotionEvent) -> Unit)?
        get() = _dispatchTouchEventHandler
        set(value) {
            _dispatchTouchEventHandler = value
        }
    override var onBounceListener: ((CollaborativeBounceView.BounceType) -> Unit)?
        get() = _onBounceListener
        set(value) {
            _onBounceListener = value
        }
    override var onMoveListener: ((Int, Float) -> Unit)?
        get() = _onMoveListener
        set(value) {
            _onMoveListener = value
        }

    init {
        isClickable = true
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        validateFields()
        val event = ev ?: return super.dispatchTouchEvent(ev)
        intercepter.dispatchTouchEvent(event)
        return super.dispatchTouchEvent(event)
    }

    private fun validateFields() {

    }
}