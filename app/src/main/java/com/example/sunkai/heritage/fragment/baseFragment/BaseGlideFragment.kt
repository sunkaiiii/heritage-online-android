package com.example.sunkai.heritage.fragment.baseFragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.connectWebService.BaseSetting
import com.example.sunkai.heritage.connectWebService.EHeritageApi
import com.example.sunkai.heritage.connectWebService.RequestHelper
import com.example.sunkai.heritage.entity.request.BaseQueryRequest
import com.example.sunkai.heritage.interfaces.NetworkRequest
import com.example.sunkai.heritage.interfaces.RequestAction
import com.example.sunkai.heritage.tools.*
import com.example.sunkai.heritage.value.CHANGE_THEME
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

abstract class BaseGlideFragment : Fragment(), RequestAction {
    protected lateinit var glide: RequestManager
    private val runnableList: MutableList<Job>
    protected var changeThemeWidge: MutableList<Int>
    private var ignoreToolbar = false
    private val handler: Handler = Handler(Looper.getMainLooper())
    private val broadReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            changeWidgeTheme()
        }

    }

    init {
        runnableList = arrayListOf()
        changeThemeWidge = arrayListOf()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val context = context
        LocalBroadcastManager.getInstance(context
                ?: return).registerReceiver(broadReceiver, IntentFilter(CHANGE_THEME))
        glide = Glide.with(this)
        setNeedChangeThemeColorWidget()
    }


    override fun onStart() {
        super.onStart()
        changeWidgeTheme()
    }

    protected open fun changeWidgeTheme() {
        val view = view
        if (view is ViewGroup) {
            forEachAndTintViews(view)
        }
        changeSpecificViewTheme()
    }


    open fun changeSpecificViewTheme() {
    }

    protected fun requestHttp(runnable: () -> Unit) {
        requestHttp(Runnable(runnable))
    }

    private fun requestHttp(runnable: Runnable) {
        val job = GlobalScope.launch { runnable.run() }
        runnableList.add(job)
    }

    protected fun requestHttp(api: EHeritageApi, bean: NetworkRequest = BaseQueryRequest()) {
        val helper = RequestHelper(api)
        val job = GlobalScope.launch {
            BaseSetting.requestNetwork(helper, bean, this@BaseGlideFragment)
        }
        runnableList.add(job)
    }

    override fun getUIThread(): Handler {
        return handler
    }

    override fun onTaskReturned(api: RequestHelper, action: RequestAction, response: String) {}

    //TODO 提示弹框
    override fun onRequestError(api: RequestHelper, action: RequestAction, ex: Exception) {

    }

    override fun beforeReuqestStart(request: RequestHelper) {
    }

    override fun onRequestEnd(request: RequestHelper) {
    }

    //定义泛型方法，简单化Gson的使用
    fun <T> fromJsonToList(s: String, clazz: Class<T>): List<T> {
        return BaseSetting.gsonInstance.fromJson(s, ParameterizedTypeImpl(clazz))
    }

    fun <T> fromJsonToObject(s: String, clazz: Class<T>): T {
        return BaseSetting.gsonInstance.fromJson(s, clazz)
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(context?:return).unregisterReceiver(broadReceiver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        runnableList.forEach { it.cancel() }
        runnableList.clear()
    }

    open fun setNeedChangeThemeColorWidget() {}

}