package com.example.sunkai.heritage.views.tools

import android.content.Context
import android.util.AttributeSet
import com.example.sunkai.heritage.tools.tintTablayout
import com.google.android.material.tabs.TabLayout

class FollowThemeTabLayout(context: Context,attributeSet: AttributeSet):TabLayout(context,attributeSet),FollowThemeView {
    override fun setThemeColor() {
        tintTablayout(this)
    }

}