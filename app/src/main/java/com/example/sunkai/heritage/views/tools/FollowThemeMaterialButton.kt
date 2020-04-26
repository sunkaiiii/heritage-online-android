package com.example.sunkai.heritage.views.tools

import android.content.Context
import android.util.AttributeSet
import com.example.sunkai.heritage.tools.getDarkThemeColor
import com.google.android.material.button.MaterialButton

class FollowThemeMaterialButton(context: Context, attributeSet: AttributeSet) : MaterialButton(context, attributeSet), FollowThemeView {
    override fun setThemeColor() {
        setBackgroundColor(getDarkThemeColor())
    }

}