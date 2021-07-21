package com.example.sunkai.heritage.activity.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.network.BaseSetting
import com.example.sunkai.heritage.network.EHeritageApi
import com.example.sunkai.heritage.network.RequestHelper
import com.example.sunkai.heritage.interfaces.NetworkRequest
import com.example.sunkai.heritage.interfaces.RequestAction
import com.example.sunkai.heritage.tools.*
import com.example.sunkai.heritage.tools.MakeToast.toast
import com.example.sunkai.heritage.value.CHANGE_THEME
import kotlinx.android.synthetic.main.activity_web_view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

abstract class BaseGlideActivity : AppCompatActivity(), RequestAction {
    protected var isDestroy = true
    protected lateinit var glide: RequestManager
    private val requestMap: MutableMap<NetworkRequest, Job>
    private val runnableList:MutableList<Job>
    private var ignoreToolbar = false
    protected val TAG: String = javaClass.name
    private val handler = Handler(Looper.getMainLooper())
    private val broadReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            changeWidgeTheme()
        }

    }

    init {
        requestMap = HashMap()
        runnableList= arrayListOf()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocalBroadcastManager.getInstance(this).registerReceiver(broadReceiver, IntentFilter(CHANGE_THEME))
        isDestroy = false
        glide = Glide.with(this)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onStart() {
        super.onStart()
        changeWidgeTheme()
    }

    open fun changeWidgeTheme() {
        val decorview = window.decorView
        if (decorview is ViewGroup) {
            forEachAndTintViews(decorview)
        }
        changeSpecificViewTheme()
        if (!ignoreToolbar) {
            supportActionBar?.setBackgroundDrawable(ColorDrawable(getThemeColor()))
        }
        window.statusBarColor = getDarkThemeColor()
        window.navigationBarColor = getThemeColor()
    }

    open fun changeSpecificViewTheme() {
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadReceiver)
        isDestroy = true
        requestMap.forEach {
            it.value.cancel()
        }
        runnableList.forEach {
            it.cancel()
        }
        requestMap.clear()
        runnableList.clear()
    }


    override fun getUIThread(): Handler {
        return handler
    }

    override fun getRunningMap(): Map<NetworkRequest, Job> {
        return requestMap
    }

    protected fun runOnBackGround(runnable: ()->Unit){
        val job = GlobalScope.launch {
            Runnable(runnable).run()
        }
        runnableList.add(job)
    }

    protected fun requestHttp(bean: NetworkRequest, api: EHeritageApi) {
        val requestHelper = RequestHelper(api, bean)
        val job = GlobalScope.launch {
            BaseSetting.requestNetwork(requestHelper, this@BaseGlideActivity)
        }
        requestMap[bean] = job
    }


    override fun onTaskReturned(api: RequestHelper, action: RequestAction, response: String) {
        if (isDestroy && requestMap[api.getRequestBean()] == null && requestMap[api.getRequestBean()]?.isCancelled == true)
            return
    }

    override fun onRequestError(api: RequestHelper, action: RequestAction, ex: Exception) {
        toast(R.string.network_error)
    }

    override fun beforeReuqestStart(request: RequestHelper) {

    }

    override fun onRequestEnd(request: RequestHelper) {

    }

    protected fun setIgnoreToolbar(ignore: Boolean) {
        this.ignoreToolbar = ignore
    }

    //定义泛型方法，简单化Gson的使用
    fun <T> fromJsonToList(s: String, clazz: Class<T>): List<T> {
        return BaseSetting.gsonInstance.fromJson(s, ParameterizedTypeImpl(clazz))
    }

    fun <T> fromJsonToObject(s: String, clazz: Class<T>): T {
        return BaseSetting.gsonInstance.fromJson(s, clazz)
    }

}