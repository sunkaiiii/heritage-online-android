package com.example.sunkai.heritage.views.tools

import android.content.Context
import android.util.AttributeSet
import android.widget.EdgeEffect
import androidx.recyclerview.widget.RecyclerView
import com.example.sunkai.heritage.tools.getThemeColor

class FollowThemeEdgeRecyclerView(context: Context, attrs: AttributeSet) : RecyclerView(context, attrs), FollowThemeView {
    //重写RecyclerViewEdgeFactroy的createEdgeEffect方法，使其可以生产对应主题颜色的edge阴影效果
    class EdgeEffectFactory : RecyclerView.EdgeEffectFactory() {
        override fun createEdgeEffect(view: RecyclerView, direction: Int): EdgeEffect {
            val edgeEffect = super.createEdgeEffect(view, direction)
            edgeEffect.color = getThemeColor()
            return edgeEffect
        }
    }

    override fun setThemeColor() {
        edgeEffectFactory = EdgeEffectFactory()
    }

}