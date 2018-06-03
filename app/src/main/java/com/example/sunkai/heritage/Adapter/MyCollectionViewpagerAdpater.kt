package com.example.sunkai.heritage.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import android.view.ViewGroup

/**
 * 我的收藏的viewpager adapter
 * Created by sunkai on 2018/3/14.
 */
class MyCollectionViewpagerAdpater(manager: androidx.fragment.app.FragmentManager): androidx.fragment.app.FragmentPagerAdapter(manager) {
    private val mFragmentList=ArrayList<androidx.fragment.app.Fragment>()
    override fun getItem(position: Int): androidx.fragment.app.Fragment {
        return mFragmentList[position]
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {

    }

    fun insertNewFragment(fragment: androidx.fragment.app.Fragment) {
        mFragmentList.add(fragment)
    }
}