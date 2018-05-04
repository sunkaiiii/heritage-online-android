package com.example.sunkai.heritage.Fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.*
import com.example.sunkai.heritage.Activity.SearchNewsActivity
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
        setToolbar()
        setViewPager(manager)
    }

    private fun setToolbar(){
        fragmentMainToolbar.title=""
        val activity=activity?:return
        if(activity is AppCompatActivity){
            activity.setSupportActionBar(fragmentMainToolbar)
            setHasOptionsMenu(true)
        }
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

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.seach_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.search_menu->{
                startActivity(Intent(activity,SearchNewsActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
