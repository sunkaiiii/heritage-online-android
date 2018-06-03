package com.example.sunkai.heritage.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.sunkai.heritage.value.CATEGORIES

/**
 * 更多新闻的viewpager的adapter
 * Created by sunkai on 2018/2/17.
 */
class SeeMoreNewsViewpagerAdapter(manager: androidx.fragment.app.FragmentManager): androidx.fragment.app.FragmentPagerAdapter(manager) {
    private val fragmentList=ArrayList<androidx.fragment.app.Fragment>()
    override fun getItem(position: Int): androidx.fragment.app.Fragment {
        return fragmentList[position]
    }

    override fun getCount(): Int {
        return fragmentList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return CATEGORIES[position]
    }

    fun insertNewFragment(fragment: androidx.fragment.app.Fragment){
        fragmentList.add(fragment)
    }
}