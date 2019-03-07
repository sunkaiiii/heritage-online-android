package com.example.sunkai.heritage.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.sunkai.heritage.activity.base.BaseGlideActivity
import com.example.sunkai.heritage.dialog.NormalWarningDialog
import com.example.sunkai.heritage.fragment.baseFragment.BaseLazyLoadFragment
import com.example.sunkai.heritage.fragment.FindFragment
import com.example.sunkai.heritage.fragment.FolkFragment
import com.example.sunkai.heritage.fragment.MainFragment
import com.example.sunkai.heritage.fragment.PersonFragment
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.service.PushService
import com.example.sunkai.heritage.tools.BaiduLocation
import com.example.sunkai.heritage.tools.MakeToast.toast
import com.example.sunkai.heritage.tools.views.FollowThemeEdgeViewPager
import com.example.sunkai.heritage.tools.getDarkThemeColor
import com.example.sunkai.heritage.tools.getThemeColor
import com.example.sunkai.heritage.value.PUSH_SWITCH
import com.example.sunkai.heritage.value.SETTING
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import com.google.firebase.messaging.FirebaseMessaging
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
        setIgnoreToolbar(true)
    }

    override fun onStart() {
        super.onStart()
        changeStatusBarAndNavigationBar(activityMainViewpager?.currentItem ?: return)
        mainViewPagerRef = WeakReference(activityMainViewpager)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        //当window建立起来之后，初始化里面的view
        bottomNavigationRef = WeakReference(bottomNavigationButton)
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
        //TODO 检查是否在中国
        if (BaiduLocation.isFromChina() || GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
            FirebaseApp.initializeApp(this)?.addIdTokenListener {
                FirebaseMessaging.getInstance().isAutoInitEnabled = true
                toast("you google play")
                FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(OnCompleteListener<InstanceIdResult> { p0 ->
                    if (!p0.isSuccessful) {
                        Log.w("MainActivity", "getinstanceId failed", p0.exception)
                        return@OnCompleteListener
                    }
                    val token = p0.result?.token

                    Log.d("MainActivity", token)
                })
            }

        } else if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED) {
            requestHttp {
                val locationResponse = BaiduLocation.getLocateAdressInfo()
                if (locationResponse != null) {
                    runOnUiThread {
                        //使用的用户如果不在中国，则提醒让其更新Google Play服务a
                        if (!locationResponse.isFromChina()) {
                            GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this)
                        } else {
                            if (bindService(Intent(this, PushService::class.java), mConnection, Context.BIND_AUTO_CREATE)) {
                                mShouldBind = true
                            }
                        }
                    }
                }
            }

        } else {
            if (bindService(Intent(this, PushService::class.java), mConnection, Context.BIND_AUTO_CREATE)) {
                mShouldBind = true
            }
        }
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
        viewList.add(FolkFragment())
        viewList.add(FindFragment())
        viewList.add(PersonFragment())
        val adapter = adapter(viewList, supportFragmentManager)
        activityMainViewpager.adapter = adapter
        activityMainViewpager.addOnPageChangeListener(onPageChangeListener)
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

    private class adapter(val viewList: ArrayList<Fragment>, manager: FragmentManager) : FragmentPagerAdapter(manager) {
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
                changeStatusBarAndNavigationBar(position)
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

    }

    private fun changeStatusBarAndNavigationBar(position: Int) {
        val midGrey = ContextCompat.getColor(this@MainActivity, R.color.midGrey)
        window.statusBarColor = when (position) {
            1 -> {
                //当从别的页面进入民间页的时候，转换状态栏和底栏的颜色
                val color = (viewList[position] as FolkFragment).getStatusBarShouldChangeColor()
                window.navigationBarColor = color
                val folkColors = arrayOf(color, midGrey).toIntArray()
                val folkStates = arrayOf(arrayOf(android.R.attr.state_checked).toIntArray(), arrayOf(-android.R.attr.state_checked).toIntArray())
                val folkColorStateList = ColorStateList(folkStates, folkColors)
                bottomNavigationButton.itemTextColor = folkColorStateList
                bottomNavigationButton.itemIconTintList = folkColorStateList
                color
            }
            else -> {
                getDarkThemeColor()
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
