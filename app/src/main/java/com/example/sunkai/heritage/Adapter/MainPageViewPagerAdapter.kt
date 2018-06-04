package com.example.sunkai.heritage.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class MainPageViewPagerAdapter(manager: FragmentManager, private val fragments:List<Fragment>): FragmentPagerAdapter(manager) {
    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }
}