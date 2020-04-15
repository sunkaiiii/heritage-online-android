package com.example.sunkai.heritage.tools

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.view.ViewGroup
import android.widget.EdgeEffect
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.children
import androidx.core.widget.NestedScrollView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.viewpager.widget.ViewPager
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.value.CHANGE_THEME
import com.example.sunkai.heritage.value.SETTING
import com.example.sunkai.heritage.value.THEME_COLOR
import com.example.sunkai.heritage.views.tools.FollowThemeView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout

/**
 * 2018/7/17
 * 用于处理主题切换的工具类
 */
private var themeColor = GlobalContext.instance.getSharedPreferences(SETTING, MODE_PRIVATE).getInt(THEME_COLOR, ContextCompat.getColor(GlobalContext.instance, R.color.colorPrimary))
private var userChoiceThemeColor=GlobalContext.instance.getSharedPreferences(SETTING, MODE_PRIVATE).getInt(THEME_COLOR, ContextCompat.getColor(GlobalContext.instance, R.color.colorPrimary))
private var darkThemeColor = getDarkerColor(getThemeColor())
private var lightThemeColor = getLighterColor(getThemeColor())
private var darkmode = false

fun reloadThemeColor() {
    LocalBroadcastManager.getInstance(GlobalContext.instance).sendBroadcast(Intent(CHANGE_THEME))
}

fun setThemeColor(color: Int) {
    GlobalContext.instance.getSharedPreferences(SETTING, MODE_PRIVATE).edit {
        putInt(THEME_COLOR, color)
    }
    if (!darkmode) {
        themeColor = color
        darkThemeColor = getDarkerColor(themeColor)
        lightThemeColor = getLighterColor(color)
        val intent = Intent(CHANGE_THEME)
        LocalBroadcastManager.getInstance(GlobalContext.instance).sendBroadcast(intent)
    }
}

fun getUserChoiceThemeColor():Int= userChoiceThemeColor

fun getThemeColor(specialDarkmodeColor: Boolean = false): Int {
    return if (specialDarkmodeColor) ContextCompat.getColor(GlobalContext.instance, R.color.deepGrey) else themeColor
}

fun getDarkThemeColor(): Int {
    return darkThemeColor
}

fun getLightThemeColor(): Int {
    return lightThemeColor
}

fun getTransparentColor(color: Int, a: Int = 119): Int {
    return Color.argb(a, Color.red(color), Color.green(color), Color.blue(color))
}

fun getDarkerColor(color: Int): Int {
    val hsvArray = FloatArray(3)
    Color.colorToHSV(color, hsvArray)
    hsvArray[0] += 0.25f
    hsvArray[2] -= 0.25f
    return Color.HSVToColor(hsvArray)
}

fun getLighterColor(color: Int): Int {
    val hsvArray = FloatArray(3)
    Color.colorToHSV(color, hsvArray)
    hsvArray[0] -= 0.25f
    hsvArray[2] += 0.25f
    if (hsvArray[2] >= 1) {
        hsvArray[2] = 0.92f
    }
    if (hsvArray[0] <= 0) {
        hsvArray[0] = 0.08f
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
    textView.setTextColor(getDarkThemeColor())
    tintCompoundDrawables(textView)
}

fun tintTablayout(tabLayout: TabLayout) {
    tabLayout.setBackgroundColor(getThemeColor())
    tabLayout.setSelectedTabIndicatorColor(if (darkmode) getLightThemeColor() else getDarkThemeColor())
}

fun tintFloatActionButton(floatActionButton: FloatingActionButton) {
    floatActionButton.backgroundTintList = ColorStateList.valueOf(getLightThemeColor())
    floatActionButton.rippleColor = getThemeColor()
}

fun tintBottomNavigationView(navigationView: BottomNavigationView) {
    val midGrey = ContextCompat.getColor(GlobalContext.instance, R.color.midGrey)
    val colors = arrayOf(getUserChoiceThemeColor(), midGrey).toIntArray()
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
    val drawalbeList = view.compoundDrawablesRelative
    drawalbeList.forEach {
        it?.setTint(getThemeColor())
    }
    if (drawalbeList.size >= 4) {
        view.setCompoundDrawablesRelative(drawalbeList[0], drawalbeList[1], drawalbeList[2], drawalbeList[3])
    }
}

fun tintViewPager(view: ViewPager) {
    try {
        val color = getThemeColor()
        val leftEdgeFile = ViewPager::class.java.getDeclaredField("mLeftEdge")
        val rightEdgeFiled = ViewPager::class.java.getDeclaredField("mRightEdge")
        leftEdgeFile.isAccessible = true
        rightEdgeFiled.isAccessible = true
        val left = EdgeEffect(view.context)
        val right = EdgeEffect(view.context)
        left.color = color
        right.color = color
        leftEdgeFile.set(view, left)
        rightEdgeFiled.set(view, right)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}


fun tintNestedScrollView(view: NestedScrollView) {
    try {
        val topEdgeEffectField = NestedScrollView::class.java.getDeclaredField("mEdgeGlowTop")
        val bottomEdgeEffectField =NestedScrollView::class.java.getDeclaredField("mEdgeGlowBottom")
        topEdgeEffectField.isAccessible = true
        bottomEdgeEffectField.isAccessible = true
        val topEdgeEffect =  EdgeEffect(view.context)
        val bottomEdgeEffect =  EdgeEffect(view.context)
        topEdgeEffect.color = themeColor
        bottomEdgeEffect.color = themeColor
        topEdgeEffectField.set(view,topEdgeEffect)
        bottomEdgeEffectField.set(view,bottomEdgeEffect)
    }catch (e:Exception){
        e.printStackTrace()
    }
}

fun tintProgressBar(view: ProgressBar?) {
    view?.indeterminateDrawable?.setColorFilter(getThemeColor(), PorterDuff.Mode.MULTIPLY)
}

fun tintToolbar(view: Toolbar) {
    view.setBackgroundColor(getThemeColor())
}

fun loadThemeColor(){
    val config=GlobalContext.instance.resources.configuration
    when (config.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
        Configuration.UI_MODE_NIGHT_NO -> {
            darkmode = false
            themeColor = GlobalContext.instance.getSharedPreferences(SETTING, MODE_PRIVATE).getInt(THEME_COLOR, ContextCompat.getColor(GlobalContext.instance, R.color.colorPrimary))
        } // Night mode is not active, we're using the light theme
        Configuration.UI_MODE_NIGHT_YES -> {
            darkmode = true
            themeColor = ContextCompat.getColor(GlobalContext.instance, R.color.midGrey)
        } // Night mode is active, we're using dark theme
    }
    userChoiceThemeColor=GlobalContext.instance.getSharedPreferences(SETTING, MODE_PRIVATE).getInt(THEME_COLOR, ContextCompat.getColor(GlobalContext.instance, R.color.colorPrimary))
    darkThemeColor = getDarkerColor(getThemeColor())
    lightThemeColor = getLighterColor(getThemeColor())
}

fun forEachAndTintViews(view: ViewGroup) {
    loadThemeColor()
    view.children.forEach {
        if (it is ViewGroup) {
            forEachAndTintViews(it)
        }
        if (it.tag == GlobalContext.instance.getString(R.string.change_theme_view)) {
            it.setBackgroundColor(getThemeColor())
        }
        if (it is FollowThemeView) {
            it.setThemeColor()
        }
    }
}
