package com.example.sunkai.heritage.views

interface CollaborativeBounceView : CollaborativeView {
    var onBounceListener: ((BounceType) -> Unit)?
    var onMoveListener: ((Int, Float) -> Unit)?
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

    enum class BounceType {
        Expand,
        Hide
    }
}