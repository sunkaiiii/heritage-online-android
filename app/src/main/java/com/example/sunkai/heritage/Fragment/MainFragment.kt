package com.example.sunkai.heritage.Fragment

import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import com.example.sunkai.heritage.Adapter.MainPageViewPagerAdapter
import com.example.sunkai.heritage.Fragment.BaseFragment.BaseGlideFragment
import com.example.sunkai.heritage.Fragment.BaseFragment.BaseLazyLoadFragment
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.value.MAIN_PAGE_TABLAYOUT_TEXT
import kotlinx.android.synthetic.main.fragment_main.*


/**
 * 首页
 */
class MainFragment : BaseGlideFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val manager=activity?.supportFragmentManager?:return
        setViewPager(manager)
    }

    private fun setViewPager(manager: FragmentManager) {
        mainPageTabLayout.setupWithViewPager(mainPageViewPager)
        val fragments=createFragments()
        val adapter=MainPageViewPagerAdapter(manager,fragments)
        setViewPagerListener(mainPageViewPager)
        mainPageViewPager.adapter=adapter
        mainPageTabLayout.setTabTextColors(ContextCompat.getColor(context?:return,R.color.normalGrey),Color.WHITE )
        MAIN_PAGE_TABLAYOUT_TEXT.withIndex().forEach { mainPageTabLayout.getTabAt(it.index)?.text=it.value }
    }

    private fun setViewPagerListener(mainPageViewPager:ViewPager) {
        mainPageViewPager.addOnPageChangeListener(object:ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {          }

            override fun onPageSelected(position: Int) {
                val adapter=mainPageViewPager.adapter?:return
                if(adapter is MainPageViewPagerAdapter){
                    val item=adapter.getItem(position)
                    if(item is BaseLazyLoadFragment){
                        item.lazyLoad()
                    }
                }
            }

        })
    }

    private fun createFragments(): List<Fragment> {
        val bottomNewsFragment=BottomNewsFragment()
        val fragments= arrayListOf<Fragment>()
        fragments.add(bottomNewsFragment)
        fragments.add(MainNewsFragment())
        return fragments
    }


}
