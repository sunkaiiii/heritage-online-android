package com.example.sunkai.heritage.tools.Views

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.widget.EdgeEffect
import androidx.viewpager.widget.ViewPager
import com.example.sunkai.heritage.tools.Views.FollowThemeEdgeViewPager
import com.example.sunkai.heritage.tools.getThemeColor

class FollowThemeEdgeViewPager : ViewPager {
    constructor(context: Context) : super(context) {
        initEdge()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initEdge()
    }

    fun initEdge(color: Int = getThemeColor()) {
        try {
            val leftEdgeFile = ViewPager::class.java.getDeclaredField("mLeftEdge")
            val rightEdgeFiled = ViewPager::class.java.getDeclaredField("mRightEdge")
            leftEdgeFile.isAccessible = true
            rightEdgeFiled.isAccessible = true
            val left = EdgeEffect(this.context)
            val right = EdgeEffect(this.context)
            left.color = color
            right.color = color
            leftEdgeFile.set(this, left)
            rightEdgeFiled.set(this, right)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}