package com.example.sunkai.heritage.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class MainPageViewPagerAdapter(manager: androidx.fragment.app.FragmentManager, private val fragments:List<androidx.fragment.app.Fragment>): androidx.fragment.app.FragmentPagerAdapter(manager) {
    override fun getItem(position: Int): androidx.fragment.app.Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }
}