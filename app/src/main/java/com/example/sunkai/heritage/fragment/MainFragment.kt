package com.example.sunkai.heritage.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.adapter.MainPageBannerAdapter
import com.example.sunkai.heritage.adapter.MainPageViewPagerAdapter
import com.example.sunkai.heritage.entity.MainPageBanner
import com.example.sunkai.heritage.entity.MainPageViewModel
import com.example.sunkai.heritage.entity.NewsPages
import com.example.sunkai.heritage.fragment.baseFragment.BaseGlideFragment
import com.example.sunkai.heritage.tools.BaseOnPageChangeListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_main.*


/**
 * 首页
 */
@AndroidEntryPoint
class MainFragment : BaseGlideFragment() {

    private val PAGES = arrayOf(NewsPages.NewsPage, NewsPages.ForumsPage, NewsPages.SpecialTopicPage)
    private var onMenuToggleClicked:MenuToggleClickListener? = null
    private val viewModel by lazy { ViewModelProvider(this).get(MainPageViewModel::class.java) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        viewModel.banner.observe(viewLifecycleOwner,{
            setMainPageSlideAdapter(it)
        })
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun setNeedChangeThemeColorWidget() {
        changeThemeWidge.add(R.id.mainPageTabLayout)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val manager = activity?.supportFragmentManager ?: return
        setViewPager(manager)
        menuImage.setOnClickListener {
            onMenuToggleClicked?.onClick(it)
        }
    }


    override fun onStart() {
        super.onStart()
        val activity = activity ?: return
        if (activity is AppCompatActivity) {
            activity.supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    private fun setViewPager(manager: FragmentManager) {
        val fragments = createFragments()
        val adapter = MainPageViewPagerAdapter(manager, fragments)
        mainPageViewPager.offscreenPageLimit = fragments.size
        mainPageViewPager.adapter = adapter
        mainPageTabLayout.setupWithViewPager(mainPageViewPager)
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
                startRecyclerScroll()
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
        override fun handleMessage(msg: Message) {
            if (msg.what == SCROLL) {
                MainPageSlideViewpager.currentItem = MainPageSlideViewpager.currentItem + 1
                startRecyclerScroll()
            }
        }
    }

    private fun createFragments(): List<Fragment> {
        val fragments = arrayListOf<Fragment>()
        PAGES.forEach {
            val fragment = NewsListFragment()
            val bundle = Bundle()
            bundle.putSerializable(PAGE, it)
            fragment.arguments = bundle
            fragments.add(fragment)
        }
        return fragments
    }

    fun setOnMenuButtonClicked(listner:MenuToggleClickListener){
        this.onMenuToggleClicked = listner
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
        const val PAGE = "page"
    }

    interface MenuToggleClickListener {
        fun onClick(view: View)
    }
}

