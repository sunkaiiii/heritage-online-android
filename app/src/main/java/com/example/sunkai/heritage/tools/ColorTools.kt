package com.example.sunkai.heritage.tools

import android.content.Context.MODE_PRIVATE
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.value.SETTING
import com.example.sunkai.heritage.value.THEME_COLOR
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout

/**
 * 2018/7/17
 * 用于处理主题切换的工具类
 */

private var themeColor = GlobalContext.instance.getSharedPreferences(SETTING, MODE_PRIVATE).getInt(THEME_COLOR, ContextCompat.getColor(GlobalContext.instance, R.color.colorPrimary))
private var darkThemeColor = getDarkerColor(getThemeColor())
private var lightThemeColor = getLighterColor(getThemeColor())

fun setThemeColor(color: Int) {
    themeColor = color
    darkThemeColor = getDarkerColor(themeColor)
    lightThemeColor = getLighterColor(color)
    GlobalContext.instance.getSharedPreferences(SETTING, MODE_PRIVATE).edit {
        putInt(THEME_COLOR, color)
    }
}

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
    if (hsvArray[2] >= 1) {
        hsvArray[2] = 0.92f
    }
    if (hsvArray[1] <= 0) {
        hsvArray[1] = 0.08f
    }
    return Color.HSVToColor(hsvArray)
}

fun tintDrawable(drawableResID: Int, color: Int = getThemeColor()): Drawable = tintDrawable(ContextCompat.getDrawable(GlobalContext.instance, drawableResID)!!, color)

fun tintDrawable(drawable: Drawable, color: Int = getThemeColor()): Drawable {
    val tempDrawable = DrawableCompat.wrap(drawable)
    DrawableCompat.setTint(tempDrawable, color)
    return tempDrawable
}

fun getSelectGradientDrawableColor(color: String): Int = getSelectGradientDrawableColor(Color.parseColor(color))

//浅色变深，深色变浅
fun getSelectGradientDrawableColor(color: Int): Int {
    val hsvArray = FloatArray(3)
    Color.colorToHSV(color, hsvArray)
    if (hsvArray[2] > 0.5) {
        hsvArray[2] -= 0.3f
    } else {
        hsvArray[2] += 0.3f
    }
    return Color.HSVToColor(hsvArray)
}

fun tintTextView(textView: TextView) {
    tintCompoundDrawables(textView)
}

fun tintTablayout(tabLayout: TabLayout) {
    tabLayout.setBackgroundColor(getThemeColor())
    tabLayout.setSelectedTabIndicatorColor(getDarkThemeColor())
}

fun tintFloatActionButton(floatActionButton: FloatingActionButton) {
    floatActionButton.backgroundTintList = ColorStateList.valueOf(getLightThemeColor())
    floatActionButton.rippleColor = getThemeColor()
}

fun tintBottomNavigationView(navigationView: BottomNavigationView) {
    val midGrey = ContextCompat.getColor(GlobalContext.instance, R.color.midGrey)
    val colors = arrayOf(themeColor, midGrey).toIntArray()
    val states = arrayOf(arrayOf(android.R.attr.state_checked).toIntArray(), arrayOf(-android.R.attr.state_checked).toIntArray())
    val colorStateList = ColorStateList(states, colors)
    navigationView.itemTextColor = colorStateList
    navigationView.itemIconTintList = colorStateList
}

fun tintSwitch(view: SwitchCompat) {
    tintCompoundDrawables(view)
    val states = arrayOf(arrayOf(android.R.attr.state_checked).toIntArray(), arrayOf(-android.R.attr.state_checked).toIntArray())
    val colors = arrayOf(getThemeColor(), ContextCompat.getColor(GlobalContext.instance, R.color.midGrey)).toIntArray()
    val trackColors = arrayOf(getLightThemeColor(), ContextCompat.getColor(GlobalContext.instance, R.color.lightGrey)).toIntArray()
    view.thumbTintList = ColorStateList(states, colors)
    view.trackTintList = ColorStateList(states, trackColors)
}

private fun tintCompoundDrawables(view: TextView) {
    val drawalbeList = view.compoundDrawables
    drawalbeList.forEach {
        it?.setTint(getThemeColor())
    }
    if (drawalbeList.size >= 4) {
        view.setCompoundDrawables(drawalbeList[0], drawalbeList[1], drawalbeList[2], drawalbeList[3])
    }
}

fun tintRecyclerView(view: RecyclerView) {
    view.adapter?.notifyDataSetChanged()
}