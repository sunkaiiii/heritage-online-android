package com.example.sunkai.heritage.Activity

import android.graphics.Color
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.core.content.ContextCompat
import com.example.sunkai.heritage.Activity.BaseActivity.BaseAutoLoginActivity
import com.example.sunkai.heritage.Adapter.MyCollectionViewpagerAdpater
import com.example.sunkai.heritage.Fragment.BaseFragment.BaseLazyLoadFragment
import com.example.sunkai.heritage.Fragment.MyCollectFragment
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.BaseOnPageChangeListener
import com.example.sunkai.heritage.value.*
import kotlinx.android.synthetic.main.activity_my_collection.*

/**
 * 我的收藏的Activity
 */
class MyCollectionActivity : BaseAutoLoginActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_collection)
        setupViewPager()
        setupTableLayout()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun setNeedChangeThemeColorWidget() {
        changeThemeWidge.add(R.id.myCollectTabLayout)
    }

    private fun setupViewPager() {
        val adapter = MyCollectionViewpagerAdpater(supportFragmentManager)
        for ((index, typeName) in collectionTypes.withIndex()) {
            adapter.insertNewFragment(MyCollectFragment.newInstance(index, typeName.first, typeName.second))
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
        requestHttp {
            Thread.sleep(300)
            runOnUiThread {
                val fragment = adapter.getItem(0)
                if (fragment is BaseLazyLoadFragment) {
                    fragment.lazyLoad()
                }
            }
        }
    }

    private fun setupTableLayout() {
        myCollectTabLayout.setupWithViewPager(myCollectViewpager)
        //给tablayout写上文字
        for ((i, typeName) in collectionTypes.withIndex()) {
            myCollectTabLayout.getTabAt(i)?.text = typeName.first
        }
        myCollectTabLayout.setTabTextColors(ContextCompat.getColor(this, R.color.normalGrey), Color.WHITE)
        myCollectTabLayout.addOnTabSelectedListener(initTabSelectListner())
    }

    private fun initTabSelectListner(): com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
        return object : com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}

            override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}

            override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
                val positon = tab?.position ?: return
                myCollectViewpager.setCurrentItem(positon, true)
            }
        }
    }

    companion object {
        val collectionTypes = arrayOf(Pair(TYPE_MAIN, COLLECT_TYPE_MAIN)
                , Pair(TYPE_FOCUS_HERITAGE, COLLECT_TYPE_FOCUS_HERITAGE)
                , Pair(TYPE_FOLK, COLLECT_TYPE_FOLK)
                , Pair(TYPE_FIND, COLLECT_TYPE_FIND))
    }
}
