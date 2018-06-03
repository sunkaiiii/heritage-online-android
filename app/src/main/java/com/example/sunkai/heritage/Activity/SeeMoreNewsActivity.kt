package com.example.sunkai.heritage.Activity

import android.graphics.Color
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.core.content.ContextCompat
import android.text.TextUtils
import com.example.sunkai.heritage.Activity.BaseActivity.BaseGlideActivity
import com.example.sunkai.heritage.Adapter.MainPageSlideAdapter
import com.example.sunkai.heritage.Adapter.SeeMoreNewsViewpagerAdapter
import com.example.sunkai.heritage.ConnectWebService.HandleMainFragment
import com.example.sunkai.heritage.Data.MainPageSlideNews
import com.example.sunkai.heritage.Fragment.BaseFragment.BaseLazyLoadFragment
import com.example.sunkai.heritage.Fragment.SeeMoreNewsFragment
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.BaseOnPageChangeListener
import com.example.sunkai.heritage.tools.ThreadPool
import com.example.sunkai.heritage.value.CATEGORIES
import com.example.sunkai.heritage.value.CATEGORY
import kotlinx.android.synthetic.main.activity_my_collection.*
import kotlinx.android.synthetic.main.activity_see_more_news.*

/**
 * 首页更多新闻的Activity
 */
class SeeMoreNewsActivity : BaseGlideActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_see_more_news)
        initView()
        val category = intent.getStringExtra(CATEGORY)
        //如果没有取到category，则默认低1个分类
        if (!TextUtils.isEmpty(category)) {
            setPositionToSelectCategory(category)
        } else {
            setPositionToSelectCategory(CATEGORIES[0])
        }
    }

    private fun initView() {
        setSupportActionBar(seeMoreNewsActivityToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val adapter = SeeMoreNewsViewpagerAdapter(supportFragmentManager)
        initViewPager(adapter)
        initTabLayout()
    }




    private fun initTabLayout() {
        seeMoreNewsTablayout.setupWithViewPager(seeMoreNewsViewpager)
        seeMoreNewsTablayout.tabMode = com.google.android.material.tabs.TabLayout.MODE_SCROLLABLE
        seeMoreNewsTablayout.setTabTextColors(ContextCompat.getColor(this,R.color.normalGrey), Color.WHITE)
    }

    private fun initViewPager(adapter: SeeMoreNewsViewpagerAdapter) {
        for (category in CATEGORIES) {
            adapter.insertNewFragment(SeeMoreNewsFragment.newInstances(category))
        }
        seeMoreNewsViewpager.addOnPageChangeListener(object : BaseOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                val fragment = adapter.getItem(position)
                if (fragment is BaseLazyLoadFragment) {
                    fragment.lazyLoad()
                }
            }
        })
        seeMoreNewsViewpager.offscreenPageLimit = CATEGORIES.size
        seeMoreNewsViewpager.adapter = adapter
    }



    private fun setPositionToSelectCategory(category: String) {
        for ((i, findCategory) in CATEGORIES.withIndex()) {
            if (category == findCategory) {
                val adapter = seeMoreNewsViewpager.adapter
                if (adapter is SeeMoreNewsViewpagerAdapter) {
                    val fragment = adapter.getItem(i)
                    if (fragment is BaseLazyLoadFragment) {
                        requestHttp {
                            //在第一次加载的时候，因为viewpager的fragment还没有create，所以无法取得args
                            //于是在第一次启动的时候延迟一段时间再lazyload
                            //目前的方式还不够优雅
                            Thread.sleep(200)
                            runOnUiThread {
                                seeMoreNewsViewpager.currentItem = i
                                fragment.lazyLoad()
                            }
                        }

                    }
                }
                break
            }
        }
    }
}
