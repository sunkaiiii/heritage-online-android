package com.example.sunkai.heritage.tools

import android.content.Context.MODE_PRIVATE
import androidx.core.content.ContextCompat
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.value.SETTING
import com.example.sunkai.heritage.value.THEME_COLOR

fun getThemeColor():Int{
    return GlobalContext.instance.getSharedPreferences(SETTING,MODE_PRIVATE).getInt(THEME_COLOR, ContextCompat.getColor(GlobalContext.instance, R.color.colorPrimary))
}