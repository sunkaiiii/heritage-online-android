package com.example.sunkai.heritage.activity

import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.activity.base.BaseGlideActivity
import com.example.sunkai.heritage.fragment.MainFragment
import com.example.sunkai.heritage.fragment.PeopleFragment
import com.example.sunkai.heritage.fragment.ProjectFragment
import com.example.sunkai.heritage.fragment.baseFragment.BaseLazyLoadFragment
import com.example.sunkai.heritage.tools.MakeToast.toast
import com.example.sunkai.heritage.tools.getThemeColor
import com.example.sunkai.heritage.views.tools.FollowThemeEdgeViewPager
import com.example.sunkai.heritage.value.PUSH_SWITCH
import com.example.sunkai.heritage.value.SETTING
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.ref.WeakReference

/**
 * 此类用于处理登陆
 */

class MainActivity : BaseGlideActivity() {

    //让其他Activity可以访问MainActivity的方法，提供了一个弱引用
    companion object {
        var activityRef: WeakReference<MainActivity>? = null
        var bottomNavigationRef: WeakReference<BottomNavigationView>? = null
        fun getViewpagerSelectPosition(): Int {
            val activity = activityRef?.get() ?: return 0
            //防止找不到view的容错，加了个？ 但看了转换的Java代码，似乎没用？
            return activity.bottomNavigationButton.selectedItemId
        }

        var mainViewPagerRef: WeakReference<FollowThemeEdgeViewPager>? = null
    }

    private val viewList: ArrayList<Fragment> = ArrayList()
    private val handler=Handler(Looper.getMainLooper())
    private val PROCESS_EXIT=-23123;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityRef = WeakReference(this)
        setContentView(R.layout.activity_main)
        initViews()
        //如果用户打开了推送开关，则开启推送服务
        val sharePrefrence = getSharedPreferences(SETTING, Context.MODE_PRIVATE)
        if (sharePrefrence.getBoolean(PUSH_SWITCH, false)) {
            //TODO 是否保留推送 未知
        }
        setIgnoreToolbar(true)
    }

    override fun onStart() {
        super.onStart()
        mainViewPagerRef = WeakReference(activityMainViewpager)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        //当window建立起来之后，初始化里面的view
        bottomNavigationRef = WeakReference(bottomNavigationButton)
    }

    override fun setNeedChangeThemeColorWidget() {
        changeThemeWidge.add(R.id.bottomNavigationButton)
    }

    private fun initViews() {
        val themeColor = getThemeColor()
        val midGrey = ContextCompat.getColor(this@MainActivity, R.color.midGrey)
        val colors = arrayOf(themeColor, midGrey).toIntArray()
        val states = arrayOf(arrayOf(android.R.attr.state_checked).toIntArray(), arrayOf(-android.R.attr.state_checked).toIntArray())
        val colorStateList = ColorStateList(states, colors)
        bottomNavigationButton.itemTextColor = colorStateList
        bottomNavigationButton.itemIconTintList = colorStateList
        bottomNavigationButton.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        viewList.add(MainFragment())
        viewList.add(PeopleFragment())
        viewList.add(ProjectFragment())
        val adapter = adapter(viewList, supportFragmentManager)
        activityMainViewpager.adapter = adapter
        activityMainViewpager.addOnPageChangeListener(onPageChangeListener)
    }

    override fun onBackPressed() {
        if(handler.hasMessages(PROCESS_EXIT))
        {
            return super.onBackPressed()
        }
        handler.sendEmptyMessageDelayed(PROCESS_EXIT,1000)
        toast(R.string.exit_info)
        return
    }

    private class adapter(val viewList: ArrayList<Fragment>, manager: FragmentManager) : FragmentPagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            return viewList[position]
        }

        override fun getCount(): Int {
            return viewList.size
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {

        }
    }

    private val onPageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        }

        override fun onPageSelected(position: Int) {
            val themeColor = getThemeColor()
            val midGrey = ContextCompat.getColor(this@MainActivity, R.color.midGrey)
            val colors = arrayOf(themeColor, midGrey).toIntArray()
            val states = arrayOf(arrayOf(android.R.attr.state_checked).toIntArray(), arrayOf(-android.R.attr.state_checked).toIntArray())
            val colorStateList = ColorStateList(states, colors)
            bottomNavigationButton.itemTextColor = colorStateList
            bottomNavigationButton.itemIconTintList = colorStateList
            if (Build.VERSION.SDK_INT >= 21) {
                window.navigationBarColor = themeColor
                val adapter = activityMainViewpager.adapter
                if (adapter is adapter) {
                    val fragment = adapter.getItem(position)
                    if (fragment is BaseLazyLoadFragment) {
                        fragment.lazyLoad()
                    }
                }
                bottomNavigationButton.selectedItemId = when (position) {
                    0 -> R.id.main_layout
                    1 -> R.id.folk_layout
                    2->R.id.project_layout
                    else -> R.id.main_layout
                }
            }
        }

    }


    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        activityMainViewpager.currentItem = when (item.itemId) {
            R.id.main_layout -> 0
            R.id.folk_layout -> 1
            R.id.project_layout->2
            else -> 0
        }
        true
    }
}
