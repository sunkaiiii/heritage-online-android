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

fun getThemeColor(): Int {
    return GlobalContext.instance.getSharedPreferences(SETTING, MODE_PRIVATE).getInt(THEME_COLOR, ContextCompat.getColor(GlobalContext.instance, R.color.colorPrimary))
}

fun getLightThemeColor(): Int {
    val color = getThemeColor()
    val hsvArray=FloatArray(3)
    Color.colorToHSV(color,hsvArray)
    hsvArray[1]-=0.25f
    hsvArray[2]+=0.25f
    return Color.HSVToColor(hsvArray)
}

fun getDarkThemeColor(): Int {
    val color = getThemeColor()
    val hsvArray = FloatArray(3)
    Color.colorToHSV(color, hsvArray)
    hsvArray[1] += 0.3f
    hsvArray[2] -= 0.3f
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
    floatActionButton.rippleColor= getThemeColor()
}