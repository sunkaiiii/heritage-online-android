package com.example.sunkai.heritage.Activity

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import com.example.sunkai.heritage.Adapter.MyCollectionViewpagerAdpater
import com.example.sunkai.heritage.Fragment.BaseFragment.BaseLazyLoadFragment
import com.example.sunkai.heritage.Fragment.MyCollectFragment
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.BaseOnPageChangeListener
import com.example.sunkai.heritage.tools.ThreadPool
import com.example.sunkai.heritage.value.TYPE_FIND
import com.example.sunkai.heritage.value.TYPE_FOCUS_HERITAGE
import com.example.sunkai.heritage.value.TYPE_FOLK
import com.example.sunkai.heritage.value.TYPE_MAIN
import kotlinx.android.synthetic.main.activity_my_collection.*

/**
 * 我的收藏的Activity
 */
class MyCollectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_collection)
        setupViewPager()
        setupTableLayout()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupViewPager() {
        val adapter = MyCollectionViewpagerAdpater(supportFragmentManager)
        for ((typeName, index) in collectionTypes.withIndex()) {
            adapter.insertNewFragment(MyCollectFragment.newInstance(index, typeName))
        }
        myCollectViewpager.addOnPageChangeListener(object : BaseOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                val fragment = adapter.getItem(position)
                if (fragment is BaseLazyLoadFragment) {
                    fragment.lazyLoad()
                }
            }
        })
        myCollectViewpager.adapter = adapter
        //自动加载第一页
        //第一次加载的时候，因为viewpager的fragment还没有create，所以无法取得args
        //不优雅的方法
        ThreadPool.execute {
            Thread.sleep(300)
            runOnUiThread {
                val fragment=adapter.getItem(0)
                if(fragment is BaseLazyLoadFragment){
                    fragment.lazyLoad()
                }
            }
        }
    }

    private fun setupTableLayout() {
        myCollectTabLayout.setupWithViewPager(myCollectViewpager)
        //给tablayout写上文字
        for (i in 0 until collectionTypes.size) {
            myCollectTabLayout.getTabAt(i)?.text = collectionTypes[i]
        }
        myCollectTabLayout.setTabTextColors(Color.GRAY, Color.WHITE)
        myCollectTabLayout.addOnTabSelectedListener(initTabSelectListner())
    }

    private fun initTabSelectListner(): TabLayout.OnTabSelectedListener {
        return object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                val positon = tab?.position ?: return
                myCollectViewpager.setCurrentItem(positon, true)
            }
        }
    }

    companion object {
        val collectionTypes = arrayListOf(TYPE_MAIN, TYPE_FOCUS_HERITAGE, TYPE_FOLK, TYPE_FIND)
    }
}
