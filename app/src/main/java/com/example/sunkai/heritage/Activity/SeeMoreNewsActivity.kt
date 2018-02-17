package com.example.sunkai.heritage.Activity

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import com.example.sunkai.heritage.Adapter.SeeMoreNewsViewpagerAdapter
import com.example.sunkai.heritage.Fragment.BaseLazyLoadFragment
import com.example.sunkai.heritage.Fragment.SeeMoreNewsFragment
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.BaseOnPageChangeListener
import com.example.sunkai.heritage.value.CATEGORIES
import com.example.sunkai.heritage.value.CATEGORY
import kotlinx.android.synthetic.main.activity_see_more_news.*

class SeeMoreNewsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_see_more_news)
        initView()
    }

    override fun onStart() {
        super.onStart()
        val category=intent.getStringExtra(CATEGORY)
        if(!TextUtils.isEmpty(category)) {
            setPositionToSelectCategory(category)
        }else{
            setPositionToSelectCategory(CATEGORIES[0])
        }
    }

    private fun initView(){
        val adapter=SeeMoreNewsViewpagerAdapter(supportFragmentManager)
        initViewPager(adapter)
        initTabLayout()
    }

    private fun initTabLayout(){
        seeMoreNewsTablayout.setupWithViewPager(seeMoreNewsViewpager)
        seeMoreNewsTablayout.tabMode=TabLayout.MODE_SCROLLABLE
    }

    private fun initViewPager(adapter: SeeMoreNewsViewpagerAdapter){
        for(category in CATEGORIES){
            adapter.insertNewFragment(SeeMoreNewsFragment.newInstances(category))
        }
        seeMoreNewsViewpager.addOnPageChangeListener(object:BaseOnPageChangeListener(){
            override fun onPageSelected(position: Int) {
                val fragment=adapter.getItem(position)
                if(fragment is BaseLazyLoadFragment){
                    fragment.lazyLoad()
                }
            }
        })
        seeMoreNewsViewpager.offscreenPageLimit= CATEGORIES.size
        seeMoreNewsViewpager.adapter=adapter
    }

    private fun setPositionToSelectCategory(category:String){
        for((i,findCategory) in CATEGORIES.withIndex()){
            if(category==findCategory){
                val adapter=seeMoreNewsViewpager.adapter
                if(adapter is SeeMoreNewsViewpagerAdapter) {
                    val fragment=adapter.getItem(i)
                    if(fragment is BaseLazyLoadFragment) {
                        Thread{
                            //在第一次加载的时候，因为viewpager的fragment还没有create，所以无法取得args
                            //于是在第一次启动的时候延迟一段时间再lazyload
                            //目前的方式还不够优雅
                            Thread.sleep(200)
                            runOnUiThread {
                                seeMoreNewsViewpager.currentItem = i
                                fragment.lazyLoad()
                            }
                        }.start()

                    }
                }
            break
            }
        }
    }
}
