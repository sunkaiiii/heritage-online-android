package com.example.sunkai.heritage.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.sunkai.heritage.fragment.ProjectListPageFragment
import com.example.sunkai.heritage.fragment.ProjectStatisticsFragment
class ProjectFragmentViewPagerAdapter(fragment:Fragment): FragmentStateAdapter(fragment)  {
    override fun getItemCount(): Int  = 2

    override fun createFragment(position: Int): Fragment {
        val fragment = if(position == 0){
            ProjectListPageFragment()
        }else{
            ProjectStatisticsFragment()
        }
        return fragment
    }
}