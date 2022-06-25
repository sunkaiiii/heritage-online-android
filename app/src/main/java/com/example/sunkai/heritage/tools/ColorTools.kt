package com.example.sunkai.heritage.tools

import android.content.Context
import android.content.res.Configuration
import android.graphics.*
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.core.content.ContextCompat

/**
 * 2018/7/17
 * 用于处理主题切换的工具类
 */


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


fun isDarkMode():Boolean{
    val config = EHeritageApplication.instance.resources.configuration
    when (config.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
        Configuration.UI_MODE_NIGHT_NO -> {
            return false
        } // Night mode is not active, we're using the light theme
        Configuration.UI_MODE_NIGHT_YES -> {
            return true
        } // Night mode is active, we're using dark theme
    }
    return false
}

fun getResourceColor(resource:Int)=ContextCompat.getColor(EHeritageApplication.instance,resource)
fun getResourceColorCompose(resource: Int) =androidx.compose.ui.graphics.Color(getResourceColor(resource))

@ColorInt
fun Context.getColorFromAttr(
    @AttrRes attrColor: Int,
    typedValue: TypedValue = TypedValue(),
    resolveRefs: Boolean = true
): Int {
    theme.resolveAttribute(attrColor, typedValue, resolveRefs)
    return typedValue.data
}


@ColorInt
fun getColorFromAttr(@AttrRes attrColor: Int, typedValue: TypedValue= TypedValue(), resolveRefs: Boolean=true):Int{
    EHeritageApplication.instance.theme.resolveAttribute(attrColor,typedValue,resolveRefs)
    return typedValue.data
}


var EHeritageLightColorScheme = lightColorScheme(

)

var EHeritageDarkColorScheme = darkColorScheme(

)