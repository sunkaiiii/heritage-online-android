package com.example.sunkai.heritage.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import android.view.ViewGroup

/**
 * 我的收藏的viewpager adapter
 * Created by sunkai on 2018/3/14.
 */
class MyCollectionViewpagerAdpater(manager: FragmentManager): FragmentPagerAdapter(manager) {
    private val mFragmentList=ArrayList<Fragment>()
    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {

    }

    fun insertNewFragment(fragment: Fragment) {
        mFragmentList.add(fragment)
    }
}