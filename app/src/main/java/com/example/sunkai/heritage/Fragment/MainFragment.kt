package com.example.sunkai.heritage.Fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.view.*
import com.example.sunkai.heritage.Activity.SearchActivity
import com.example.sunkai.heritage.Adapter.MainPageSlideAdapter
import com.example.sunkai.heritage.Adapter.MainPageViewPagerAdapter
import com.example.sunkai.heritage.ConnectWebService.HandleMainFragment
import com.example.sunkai.heritage.Data.MainPageSlideNews
import com.example.sunkai.heritage.Fragment.BaseFragment.BaseGlideFragment
import com.example.sunkai.heritage.Fragment.BaseFragment.BaseLazyLoadFragment
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.BaseOnPageChangeListener
import com.example.sunkai.heritage.tools.runOnUiThread
import com.example.sunkai.heritage.value.MAIN_PAGE_TABLAYOUT_TEXT
import com.example.sunkai.heritage.value.SEARCH_TYPE
import com.example.sunkai.heritage.value.TYPE_BOTTOM_NEWS
import com.example.sunkai.heritage.value.TYPE_NEWS
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

    private fun setViewPager(manager: FragmentManager) {
        mainPageTabLayout.setupWithViewPager(mainPageViewPager)
        val fragments = createFragments()
        val adapter = MainPageViewPagerAdapter(manager, fragments)
        setViewPagerListener(mainPageViewPager)
        mainPageViewPager.adapter = adapter
        mainPageTabLayout.setTabTextColors(ContextCompat.getColor(context
                ?: return, R.color.normalGrey), Color.WHITE)
        MAIN_PAGE_TABLAYOUT_TEXT.withIndex().forEach { mainPageTabLayout.getTabAt(it.index)?.text = it.value }
    }

    private fun setViewPagerListener(mainPageViewPager: ViewPager) {
        mainPageViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

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
        requestHttp {
            val datas = HandleMainFragment.GetMainPageSlideNewsInfo()
            if (datas != null) {
                runOnUiThread {
                    setMainPageSlideAdapter(datas)
                }
            }
        }

    }

    private fun setMainPageSlideAdapter(datas: List<MainPageSlideNews>) {
        val activity = activity ?: return
        val adapter = MainPageSlideAdapter(activity, datas, glide)
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

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.seach_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.search_menu -> {
                val intent = Intent(activity, SearchActivity::class.java)
                intent.putExtra(SEARCH_TYPE, if (mainPageTabLayout.selectedTabPosition == 0) TYPE_BOTTOM_NEWS else TYPE_NEWS)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
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
