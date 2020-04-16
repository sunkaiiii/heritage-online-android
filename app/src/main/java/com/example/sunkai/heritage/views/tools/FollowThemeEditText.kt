package com.example.sunkai.heritage.views.tools

import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.example.sunkai.heritage.tools.getDarkThemeColor
import com.example.sunkai.heritage.tools.getLightThemeColor
import com.example.sunkai.heritage.tools.getThemeColor
import com.example.sunkai.heritage.tools.getTransparentColor

class FollowThemeEditText(context: Context, attributeSet: AttributeSet) : AppCompatEditText(context, attributeSet), FollowThemeView {
    override fun setThemeColor() {
        highlightColor = getLightThemeColor()
        backgroundTintList = ColorStateList.valueOf(getTransparentColor(getDarkThemeColor()))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            textSelectHandleRight?.setTint(getThemeColor())
            textSelectHandle?.setTint(getThemeColor())
            textSelectHandleLeft?.setTint(getThemeColor())
            textCursorDrawable?.setTint(getThemeColor())
        }

    }

}