package com.example.sunkai.heritage.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.sunkai.heritage.entity.NewsPages
import com.example.sunkai.heritage.fragment.MainFragment
import com.example.sunkai.heritage.fragment.NewsListFragment

class MainPageViewPagerAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {
    private val PAGES =
        arrayOf(NewsPages.NewsPage, NewsPages.ForumsPage, NewsPages.SpecialTopicPage)

    override fun getItemCount(): Int  = PAGES.size

    override fun createFragment(position: Int): Fragment {
        val pageInfo = PAGES[position]
        val fragment = NewsListFragment()
        val bundle = Bundle()
        bundle.putSerializable(MainFragment.PAGE, pageInfo)
        fragment.arguments = bundle
        return fragment
    }


}