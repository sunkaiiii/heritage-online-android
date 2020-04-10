package com.example.sunkai.heritage.views.tools

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.example.sunkai.heritage.tools.tintTextView

class FollowThemeTextView(context: Context,attributeSet: AttributeSet):AppCompatTextView(context,attributeSet),FollowThemeView{
    override fun setThemeColor() {
        tintTextView(this)
    }

}