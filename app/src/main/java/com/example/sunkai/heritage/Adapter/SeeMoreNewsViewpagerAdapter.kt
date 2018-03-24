package com.example.sunkai.heritage.Adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.example.sunkai.heritage.value.CATEGORIES

/**
 * 更多新闻的viewpager的adapter
 * Created by sunkai on 2018/2/17.
 */
class SeeMoreNewsViewpagerAdapter(manager:FragmentManager):FragmentPagerAdapter(manager) {
    private val fragmentList=ArrayList<Fragment>()
    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getCount(): Int {
        return fragmentList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return CATEGORIES[position]
    }

    fun insertNewFragment(fragment: Fragment){
        fragmentList.add(fragment)
    }
}