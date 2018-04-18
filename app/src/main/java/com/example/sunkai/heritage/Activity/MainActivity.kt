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
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import com.example.sunkai.heritage.Dialog.NormalWarningDialog
import com.example.sunkai.heritage.Fragment.*
import com.example.sunkai.heritage.Fragment.BaseFragment.BaseLazyLoadFragment
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.Service.PushService
import com.example.sunkai.heritage.tools.BottomNavigationViewHelper
import com.example.sunkai.heritage.value.PUSH_SWITCH
import com.example.sunkai.heritage.value.SETTING
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.ref.WeakReference

/**
 * 此类用于处理登陆
 */

class MainActivity : AppCompatActivity() {

    //让其他Activity可以访问MainActivity的方法，提供了一个弱引用
    companion object {
        var activityRef: WeakReference<MainActivity>? = null
        fun GetViewpagerSelectPosition(): Int {
            val activity = activityRef?.get() ?: return 0
            //防止找不到view的容错，加了个？ 但看了转换的Java代码，似乎没用？
            return activity.bottomNavigationButton?.selectedItemId ?: return 0
        }
    }

    private val viewList: ArrayList<Fragment>

    init {
        viewList = ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityRef = WeakReference(this)
        setContentView(R.layout.activity_main)
        initViews()
        //如果用户打开了推送开关，则开启推送服务
        val sharePrefrence = getSharedPreferences(SETTING, Context.MODE_PRIVATE)
        if (sharePrefrence.getBoolean(PUSH_SWITCH, false)) {
            startPushService()
        }
    }


    private var mBoundService: PushService? = null
    private var mShouldBind = false
    private val mConnection = object : ServiceConnection {
        override fun onServiceDisconnected(p0: ComponentName?) {
            mBoundService = null
        }

        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            mBoundService = (p1 as PushService.LocalBinder).service
        }
    }

    fun startPushService() {
        if (bindService(Intent(this, PushService::class.java), mConnection, Context.BIND_AUTO_CREATE)) {
            mShouldBind = true
        }
    }

    private fun initViews() {
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationButton)
        bottomNavigationButton.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        viewList.add(MainFragment())
        viewList.add(FolkFragment())
        viewList.add(FindFragment())
        viewList.add(PersonFragment())

        val adapter = adapter(viewList, supportFragmentManager)
        activityMainViewpager.adapter = adapter
        activityMainViewpager.addOnPageChangeListener(onPageChangeListener)

        window.setBackgroundDrawable(null)
    }

    //重写onKeyDown方法，监听返回键
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showExitDialog()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun showExitDialog() {
        NormalWarningDialog().setTitle("退出?")
                .setOnSubmitClickListener(object : NormalWarningDialog.onSubmitClickListener {
                    override fun onSubmit(view: View, dialog: NormalWarningDialog) {
                        dialog.dismiss()
                        finish()
                    }
                })
                .setSubmitText("退出")
                .show(supportFragmentManager, "exitDialog")
    }

    override fun onDestroy() {
        doUnbindService()
        stopService(Intent(this, PushService::class.java))
        super.onDestroy()
    }

    fun doUnbindService() {
        if (mShouldBind) {
            unbindService(mConnection)
            mShouldBind = false
        }
    }

    private class adapter(val viewList: ArrayList<Fragment>, manager: android.support.v4.app.FragmentManager) : FragmentPagerAdapter(manager) {
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
            if (Build.VERSION.SDK_INT >= 21) {
                window.statusBarColor = when (position) {
                    1 -> (viewList[position] as FolkFragment).getStatusBarShouldChangeColor()
                    else -> ContextCompat.getColor(applicationContext, R.color.colorPrimaryDark)
                }
            }
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
                2 -> R.id.find_layout
                3 -> R.id.person_layout
                else -> R.id.main_layout
            }
        }
    }

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        activityMainViewpager.currentItem = when (item.itemId) {
            R.id.main_layout -> 0
            R.id.folk_layout -> 1
            R.id.find_layout -> 2
            R.id.person_layout -> 3
            else -> 0
        }
        true
    }
}
