package com.example.sunkai.heritage.views.tools

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import com.example.sunkai.heritage.tools.getLightThemeColor
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FollowThemeFloatingActionButton(context: Context, attributes: AttributeSet) : FloatingActionButton(context, attributes), FollowThemeView {
    override fun setThemeColor() {
        backgroundTintList = ColorStateList.valueOf(getLightThemeColor())
    }

}