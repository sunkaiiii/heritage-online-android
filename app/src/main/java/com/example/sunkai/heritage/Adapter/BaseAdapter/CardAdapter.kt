package com.example.sunkai.heritage.Adapter.BaseAdapter

import android.support.v4.view.PagerAdapter
import android.support.v7.widget.CardView

/**
 * Created by sunkai on 2018/2/9.
 */
abstract class CardAdapter:PagerAdapter() {
    companion object {
        const val MAX_ELEVATION_FACTOR:Int=3
    }
    abstract fun getBaseElevation():Float
    abstract fun getCardViewAt(position:Int):CardView
}