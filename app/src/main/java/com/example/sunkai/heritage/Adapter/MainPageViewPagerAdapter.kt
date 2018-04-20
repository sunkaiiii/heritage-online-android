package com.example.sunkai.heritage.Adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class MainPageViewPagerAdapter(manager:FragmentManager, private val fragments:List<Fragment>):FragmentPagerAdapter(manager) {
    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }
}