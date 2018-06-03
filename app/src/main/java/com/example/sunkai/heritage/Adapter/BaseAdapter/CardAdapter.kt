package com.example.sunkai.heritage.Adapter.BaseAdapter

import androidx.viewpager.widget.PagerAdapter
import androidx.cardview.widget.CardView

/**
 * 首页卡片的adapter
 * Created by sunkai on 2018/2/9.
 */
abstract class CardAdapter: androidx.viewpager.widget.PagerAdapter() {
    companion object {
        const val MAX_ELEVATION_FACTOR:Int=3
    }
    abstract fun getBaseElevation():Float
    abstract fun getCardViewAt(position:Int): androidx.cardview.widget.CardView
}