package com.example.sunkai.heritage.tools

import android.content.Context.MODE_PRIVATE
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.value.SETTING
import com.example.sunkai.heritage.value.THEME_COLOR
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout

/**
 * 2018/7/17
 * 用于处理主题切换的工具类
 */

private var themeColor = GlobalContext.instance.getSharedPreferences(SETTING, MODE_PRIVATE).getInt(THEME_COLOR, ContextCompat.getColor(GlobalContext.instance, R.color.colorPrimary))
private var darkThemeColor = getDarkerColor(getThemeColor())
private var lightThemeColor = getLighterColor(getThemeColor())

fun getThemeColor(): Int {
    return themeColor
}

fun getDarkThemeColor(): Int {
    return darkThemeColor
}

fun getLightThemeColor(): Int {
    return lightThemeColor
}

fun getDarkerColor(color: Int): Int {
    val hsvArray = FloatArray(3)
    Color.colorToHSV(color, hsvArray)
    hsvArray[1] += 0.25f
    hsvArray[2] -= 0.25f
    return Color.HSVToColor(hsvArray)
}

fun getLighterColor(color: Int): Int {
    val hsvArray = FloatArray(3)
    Color.colorToHSV(color, hsvArray)
    hsvArray[1] -= 0.25f
    hsvArray[2] += 0.25f
    return Color.HSVToColor(hsvArray)
}


fun tintTextView(textView: TextView) {
    val drawalbeList = textView.compoundDrawables
    drawalbeList.forEach {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            it?.setTint(getThemeColor())
        }
    }
    if (drawalbeList.size >= 4) {
        textView.setCompoundDrawables(drawalbeList[0], drawalbeList[1], drawalbeList[2], drawalbeList[3])
    }
}

fun tintTablayout(tabLayout: TabLayout) {
    tabLayout.setBackgroundColor(getThemeColor())
    tabLayout.setSelectedTabIndicatorColor(getDarkThemeColor())
}

fun tintFloatActionButton(floatActionButton: FloatingActionButton) {
    floatActionButton.backgroundTintList = ColorStateList.valueOf(getLightThemeColor())
    floatActionButton.rippleColor = getThemeColor()
}