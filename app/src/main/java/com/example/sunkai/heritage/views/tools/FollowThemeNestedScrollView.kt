package com.example.sunkai.heritage.views.tools

import android.content.Context
import android.util.AttributeSet
import androidx.core.widget.NestedScrollView
import com.example.sunkai.heritage.tools.tintNestedScrollView

class FollowThemeNestedScrollView(context: Context, attributeSet: AttributeSet) : NestedScrollView(context, attributeSet), FollowThemeView {
    override fun setThemeColor() {
        tintNestedScrollView(this)
    }
}