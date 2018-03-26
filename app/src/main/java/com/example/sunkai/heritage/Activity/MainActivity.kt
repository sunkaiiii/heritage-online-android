package com.example.sunkai.heritage.Activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.ViewGroup
import com.example.sunkai.heritage.Data.MySqliteHandler
import com.example.sunkai.heritage.Fragment.*
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.Service.PushService
import com.example.sunkai.heritage.tools.BottomNavigationViewHelper
import java.lang.ref.WeakReference

/**
 * 此类用于处理登陆
 */

class MainActivity : AppCompatActivity() {

    companion object {
        var activityRef:WeakReference<MainActivity>?=null
        fun GetViewpagerSelectPosition():Int{
            val activity= activityRef?.get()?:return 0
            return activity.bottomNavigation.selectedItemId
        }
    }

    private val viewList:ArrayList<Fragment>
    init {
        viewList=ArrayList()
    }

    /**
     * 底部的tabBottom
     */
    private lateinit var bottomNavigation:BottomNavigationView


    /**
     * 用于对Fragment进行管理
     */
    private var fragmentManager: android.support.v4.app.FragmentManager? = null

    private lateinit var viewPager:ViewPager

    private lateinit var ad: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityRef= WeakReference(this)
        setContentView(R.layout.activity_main)
        fragmentManager = supportFragmentManager
        initViews()
        startPushService()
    }

    private var mBoundService:PushService?=null
    private var mShouldBind=false
    private val mConnection=object :ServiceConnection{
        override fun onServiceDisconnected(p0: ComponentName?) {
            mBoundService=null
        }

        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            mBoundService=(p1 as PushService.LocalBinder).service
        }
    }
    private fun startPushService() {
        if(bindService(Intent(this,PushService::class.java),mConnection, Context.BIND_AUTO_CREATE)) {
            mShouldBind = true
        }
    }



    private fun initViews() {
        viewPager=findViewById(R.id.activity_main_viewpager)
        bottomNavigation=findViewById(R.id.below_button)
        BottomNavigationViewHelper.disableShiftMode(bottomNavigation)
        bottomNavigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        viewList.add(MainFragment())
        viewList.add(FolkFragment())
        viewList.add(FindFragment())
        viewList.add(PersonFragment())

        val adapter=adapter(viewList,fragmentManager!!)
        viewPager.adapter=adapter
        viewPager.addOnPageChangeListener(onPageChangeListener)

        window.setBackgroundDrawable(null)
    }

    //重写onKeyDown方法，监听返回键
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val builder = AlertDialog.Builder(this@MainActivity).setTitle("退出?").setPositiveButton("退出") { _, _ ->
                ad.dismiss()
                finish()
            }.setNegativeButton("取消") { _, _ -> ad.dismiss() }
            ad = builder.create()
            ad.show()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        MySqliteHandler.Close()
        doUnbindService()
    }

    private fun doUnbindService() {
        if(mShouldBind){
            unbindService(mConnection)
            mShouldBind=false
        }
    }

    private class adapter(val viewList: ArrayList<Fragment>,manager: android.support.v4.app.FragmentManager):FragmentPagerAdapter(manager){
        override fun getItem(position: Int): Fragment {
            return viewList[position]
        }

        override fun getCount(): Int {
            return viewList.size
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {

        }
    }

    private val onPageChangeListener=object :ViewPager.OnPageChangeListener{
        override fun onPageScrollStateChanged(state: Int) {
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        }

        override fun onPageSelected(position: Int) {
            if(Build.VERSION.SDK_INT>=21){
                window.statusBarColor=when(position){
                    1->(viewList[position] as FolkFragment).getStatusBarShouldChangeColor()
                    else->ContextCompat.getColor(applicationContext,R.color.colorPrimaryDark)
                }
            }
            val adapter=viewPager.adapter
            if(adapter is adapter){
                val fragment=adapter.getItem(position)
                if(fragment is BaseLazyLoadFragment){
                    fragment.lazyLoad()
                }
            }
            bottomNavigation.selectedItemId=when(position){
                0->R.id.main_layout
                1->R.id.folk_layout
                2->R.id.find_layout
                3->R.id.person_layout
                else->R.id.main_layout
            }
        }
    }

    private val onNavigationItemSelectedListener= BottomNavigationView.OnNavigationItemSelectedListener { item ->
        viewPager.currentItem=when(item.itemId){
            R.id.main_layout->0
            R.id.folk_layout->1
            R.id.find_layout->2
            R.id.person_layout->3
            else->0
        }
        true
    }
}
