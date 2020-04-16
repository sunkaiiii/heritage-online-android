package com.example.sunkai.heritage.views.tools

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.example.sunkai.heritage.tools.getThemeColor

class FollowThemeImageView(context: Context, attributeSet: AttributeSet) : AppCompatImageView(context, attributeSet), FollowThemeView {
    override fun setThemeColor() {
        this.drawable.setTint(getThemeColor())
    }
}