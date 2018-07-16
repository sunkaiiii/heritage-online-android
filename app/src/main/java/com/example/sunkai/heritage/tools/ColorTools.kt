package com.example.sunkai.heritage.tools

import android.content.Context.MODE_PRIVATE
import android.graphics.Color
import android.graphics.ColorSpace
import android.os.Build
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.alpha
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.value.SETTING
import com.example.sunkai.heritage.value.THEME_COLOR

fun getThemeColor():Int{
    return GlobalContext.instance.getSharedPreferences(SETTING,MODE_PRIVATE).getInt(THEME_COLOR, ContextCompat.getColor(GlobalContext.instance, R.color.colorPrimary))
}

fun getLightThemeColor():Int{
    val color= getThemeColor()
    val r=Color.red(color)
    val g=Color.green(color)
    val b=Color.blue(color)
    return Color.argb(180,r,g,b)
}

fun tintTextView(textView: TextView){
    val drawalbeList=textView.compoundDrawables
    drawalbeList.forEach {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            it?.setTint(getThemeColor())
        }
    }
    if(drawalbeList.size>=4) {
        textView.setCompoundDrawables(drawalbeList[0], drawalbeList[1], drawalbeList[2], drawalbeList[3])
    }
}