package com.example.sunkai.heritage.views.tools

import android.content.Context
import android.util.AttributeSet
import androidx.viewpager.widget.ViewPager
import com.example.sunkai.heritage.tools.tintViewPager

class FollowThemeEdgeViewPager(context: Context, attrs: AttributeSet) : ViewPager(context,attrs),FollowThemeView {

    override fun setThemeColor() {
        tintViewPager(this)
    }
}