package com.example.sunkai.heritage.views.tools

import android.content.Context
import android.util.AttributeSet
import com.example.sunkai.heritage.tools.tintBottomNavigationView
import com.google.android.material.bottomnavigation.BottomNavigationView

class FollowThemeBottomNavigationView(context: Context,attributeSet: AttributeSet):BottomNavigationView(context,attributeSet),FollowThemeView {
    override fun setThemeColor() {
        tintBottomNavigationView(this)
    }
}