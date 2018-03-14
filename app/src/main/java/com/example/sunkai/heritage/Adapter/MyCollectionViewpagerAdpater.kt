package com.example.sunkai.heritage.Adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.ViewGroup

/**
 * 我的收藏的viewpager adapter
 * Created by sunkai on 2018/3/14.
 */
class MyCollectionViewpagerAdpater(manager:FragmentManager):FragmentPagerAdapter(manager) {
    private val mFragmentList=ArrayList<Fragment>()
    override fun getItem(position: Int): android.support.v4.app.Fragment {
        return mFragmentList[position]
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
    }

    fun insertNewFragment(fragment: android.support.v4.app.Fragment) {
        mFragmentList.add(fragment)
    }
}