package com.example.sunkai.heritage.views.tools

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.Toolbar
import com.example.sunkai.heritage.tools.tintToolbar

class FollowThemeToolbar(context: Context,attributeSet: AttributeSet):Toolbar(context,attributeSet),FollowThemeView {
    override fun setThemeColor() {
        tintToolbar(this)
    }

}