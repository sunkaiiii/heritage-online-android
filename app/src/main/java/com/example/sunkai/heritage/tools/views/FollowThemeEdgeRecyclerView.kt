package com.example.sunkai.heritage.tools.views

import android.content.Context
import android.util.AttributeSet
import android.widget.EdgeEffect
import androidx.recyclerview.widget.RecyclerView
import com.example.sunkai.heritage.tools.getThemeColor

class FollowThemeEdgeRecyclerView : RecyclerView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    init {
        edgeEffectFactory = EdgeEffectFactory()
    }

    //重写RecyclerViewEdgeFactroy的createEdgeEffect方法，使其可以生产对应主题颜色的edge阴影效果
    class EdgeEffectFactory : RecyclerView.EdgeEffectFactory() {
        override fun createEdgeEffect(view: RecyclerView, direction: Int): EdgeEffect {
            val edgeEffect = super.createEdgeEffect(view, direction)
            edgeEffect.color = getThemeColor()
            return edgeEffect
        }
    }

}