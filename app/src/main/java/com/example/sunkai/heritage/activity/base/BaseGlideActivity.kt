package com.example.sunkai.heritage.activity.base

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.connectWebService.BaseSetting
import com.example.sunkai.heritage.connectWebService.EHeritageApi
import com.example.sunkai.heritage.connectWebService.RequestHelper
import com.example.sunkai.heritage.entity.request.BaseRequest
import com.example.sunkai.heritage.interfaces.RequestAction
import com.example.sunkai.heritage.tools.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

abstract class BaseGlideActivity : AppCompatActivity(), RequestAction {
    protected var isDestroy = true
    protected lateinit var glide: RequestManager
    private val runnableList: MutableList<Job>
    protected var changeThemeWidge: MutableList<Int>
    private var ignoreToolbar = false
    protected val TAG = javaClass.name

    init {
        runnableList = arrayListOf()
        changeThemeWidge = arrayListOf()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isDestroy = false
        glide = Glide.with(this)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setNeedChangeThemeColorWidget()
    }

    override fun onStart() {
        super.onStart()
        changeWidgeTheme()
    }

    open fun changeWidgeTheme() {
        changeWidgeTheme(getThemeColor(), getDarkThemeColor())
    }

    open fun changeWidgeTheme(color: Int, darkColor: Int) {
        if (!ignoreToolbar) {
            supportActionBar?.setBackgroundDrawable(ColorDrawable(color))
        }
        window.statusBarColor = darkColor
        window.navigationBarColor = color
        changeThemeWidge.forEach {
            val view = findViewById<View>(it)
            when (view) {
                is SwitchCompat -> tintSwitch(view)
                is TextView -> tintTextView(view)
                is TabLayout -> tintTablayout(view)
                is FloatingActionButton -> tintFloatActionButton(view)
                is BottomNavigationView -> tintBottomNavigationView(view)
                else -> view?.setBackgroundColor(color)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        isDestroy = true
        runnableList.forEach {
            it.cancel()
        }
        runnableList.clear()
    }

    protected fun requestHttp(runnable: () -> Unit) {
        requestHttp(Runnable(runnable))
    }

    protected fun requestHttp(runnable: Runnable) {
        val job = GlobalScope.launch {
            runnable.run()
        }
        runnableList.add(job)
    }

    override fun <T> getUIThread(): Handler {
        return Handler(Looper.getMainLooper())
    }

    protected fun <T> requestHttp(bean: BaseRequest, api: EHeritageApi, responseType: Class<T>) {
        val requestHelper = RequestHelper(api, responseType)
        BaseSetting.requestNetwork(requestHelper, bean, this)
    }


    override fun <T> onTaskReturned(api: RequestHelper<T>, action: RequestAction, response: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <T> onRequestError(api: RequestHelper<T>, action: RequestAction, ex: Exception) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    protected fun setIgnoreToolbar(ignore: Boolean) {
        this.ignoreToolbar = ignore
    }

    open fun setNeedChangeThemeColorWidget() {}

}