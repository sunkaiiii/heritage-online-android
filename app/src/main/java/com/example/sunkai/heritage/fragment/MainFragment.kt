package com.example.sunkai.heritage.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.ViewPager
import com.example.sunkai.heritage.adapter.MainPageBannerAdapter
import com.example.sunkai.heritage.adapter.MainPageViewPagerAdapter
import com.example.sunkai.heritage.entity.MainPageBanner
import com.example.sunkai.heritage.fragment.baseFragment.BaseGlideFragment
import com.example.sunkai.heritage.fragment.baseFragment.BaseLazyLoadFragment
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.connectWebService.EHeritageApi
import com.example.sunkai.heritage.connectWebService.RequestHelper
import com.example.sunkai.heritage.interfaces.RequestAction
import com.example.sunkai.heritage.tools.BaseOnPageChangeListener
import kotlinx.android.synthetic.main.fragment_main.*


/**
 * 首页
 */
class MainFragment : BaseGlideFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun setNeedChangeThemeColorWidget() {
        changeThemeWidge.add(R.id.mainPageTabLayout)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val manager = activity?.supportFragmentManager ?: return
        initMainPageSlide()
        setToolbar()
        setViewPager(manager)
    }

    private fun setToolbar() {
        fragmentMainToolbar.title = ""
        val activity = activity ?: return
        if (activity is AppCompatActivity) {
            activity.setSupportActionBar(fragmentMainToolbar)
            setHasOptionsMenu(true)
        }
    }

    override fun onStart() {
        super.onStart()
        val activity=activity?:return
        if(activity is AppCompatActivity){
            activity.supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    private fun setViewPager(manager: FragmentManager) {
        val fragments = createFragments()
        val adapter = MainPageViewPagerAdapter(manager, fragments)
        setViewPagerListener(mainPageViewPager)
        mainPageViewPager.adapter = adapter
    }

    private fun setViewPagerListener(mainPageViewPager: ViewPager) {
        mainPageViewPager.addOnPageChangeListener(object : BaseOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                val adapter = mainPageViewPager.adapter ?: return
                if (adapter is MainPageViewPagerAdapter) {
                    val item = adapter.getItem(position)
                    if (item is BaseLazyLoadFragment) {
                        item.lazyLoad()
                    }
                }
            }
        })
    }

    private fun initMainPageSlide() {
        requestHttp(EHeritageApi.GetBanner)

    }

    override fun onTaskReturned(api: RequestHelper, action: RequestAction, response: String) {
        super.onTaskReturned(api, action, response)
        when(api.getRequestApi())
        {
            EHeritageApi.GetBanner->
            {
                val data=fromJsonToList(response,MainPageBanner::class.java)
                setMainPageSlideAdapter(data)
            }
        }
    }

    private fun setMainPageSlideAdapter(data: List<MainPageBanner>) {
        val activity = activity ?: return
        val adapter = MainPageBannerAdapter(activity, data, glide)
        MainPageSlideViewpager.adapter = adapter
        //让他在初始的时候在中间的位置，且保证是第一个页面，可以做到左翻页
        val middleItem = 0 + 4 * 200
        MainPageSlideViewpager.setCurrentItem(middleItem, false)
        MainPageSlideViewpager.addOnPageChangeListener(object : BaseOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                viewpagerRecyclerScrollHandler.removeMessages(SCROLL)
                viewpagerRecyclerScrollHandler.sendEmptyMessageDelayed(SCROLL, DELAY)
            }

        })
        startRecyclerScroll()
    }

    //执行自动翻页
    private fun startRecyclerScroll() {
        viewpagerRecyclerScrollHandler.removeMessages(SCROLL)
        viewpagerRecyclerScrollHandler.sendEmptyMessageDelayed(SCROLL, DELAY)
    }

    private val viewpagerRecyclerScrollHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message?) {
            if (msg?.what == SCROLL) {
                MainPageSlideViewpager.currentItem = MainPageSlideViewpager.currentItem + 1
                startRecyclerScroll()
            }
        }
    }

    private fun createFragments(): List<Fragment> {
        val bottomNewsFragment = BottomNewsFragment()
        val fragments = arrayListOf<Fragment>()
        fragments.add(bottomNewsFragment)
        fragments.add(MainNewsFragment())
        return fragments
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.seach_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onResume() {
        super.onResume()
        viewpagerRecyclerScrollHandler.sendEmptyMessageDelayed(SCROLL, DELAY)
    }

    //view被暂停销毁之后，清除handler里面所有的信息
    override fun onPause() {
        super.onPause()
        viewpagerRecyclerScrollHandler.removeMessages(SCROLL)
    }


    companion object {
        const val SCROLL = 1
        const val DELAY = 3000L
    }
}
