package com.example.sunkai.heritage.views.tools

import android.content.Context
import android.util.AttributeSet
import androidx.core.widget.ContentLoadingProgressBar
import com.example.sunkai.heritage.tools.tintProgressBar

class FollowThemeProgressBar(context: Context,attributeSet: AttributeSet): ContentLoadingProgressBar(context,attributeSet),FollowThemeView {
    override fun setThemeColor() {
        tintProgressBar(this)
    }
}