package com.example.sunkai.heritage.tools

import android.support.v4.view.ViewPager

/**
 * ViewPager的OnPageChangeListener的基类，重写了两个不常用的方法，简单化无用代码
 * Created by sunkai on 2018/2/17.
 */
abstract class BaseOnPageChangeListener:ViewPager.OnPageChangeListener {
    override fun onPageScrollStateChanged(state: Int) {}

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
}