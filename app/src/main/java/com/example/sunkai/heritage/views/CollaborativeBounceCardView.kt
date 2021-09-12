package com.example.sunkai.heritage.views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.google.android.material.card.MaterialCardView

class CollaborativeBounceCardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr), CollaborativeBounceView {
    private var _dispatchTouchEventHandler: ((View, MotionEvent) -> Unit)? = null
    private var _onBounceListener: ((CollaborativeBounceView.BounceType) -> Unit)? = null
    private var _onMoveListener: ((Int, Float) -> Unit)? = null
    override var minBoundry = -1
    override var maxBoundry = -1
    override var initialActionDownTranslationY = -1f
    override var horizentalInitialPosition = -1f
    override var autoBounce: Boolean = true
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
    override var interceptTouchEventBlocker: ((event: MotionEvent) -> Boolean)? = null
    override var interceptMoveEventBlocker: ((MotionEvent, CollaborativeBounceView.MoveOrientation) -> Boolean)? = null

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        validateFields()
        val event = ev ?: return super.dispatchTouchEvent(ev)
        intercepter.dispatchTouchEvent(event)
        return interceptTouchEventBlocker?.let { it(ev) }==true || super.dispatchTouchEvent(event)
    }



    private fun validateFields() {

    }


}