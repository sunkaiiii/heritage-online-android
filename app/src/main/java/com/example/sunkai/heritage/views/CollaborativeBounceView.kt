package com.example.sunkai.heritage.views

interface CollaborativeBounceView:CollaborativeView {
    fun setOnBounceAction(listener:((BounceType)->Unit))
    fun setBounceBoundry(minBoundry:Int,maxBoundry:Int)
    fun setOnMoveAction(listener:((distance:Int,offsetPercentage:Float)->Unit))
    enum class BounceType {
        Expand,
        Hide
    }
}