package com.example.sunkai.heritage.Activity.BaseActivity

import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.ThreadPool
import com.example.sunkai.heritage.tools.getThemeColor

abstract class BaseGlideActivity : AppCompatActivity() {
    protected var isDestroy = true
    protected lateinit var glide: RequestManager
    private val runnableList: MutableList<Runnable>
    protected var changeThemeWidge:MutableList<Int>
    private var ignoreToolbar=false
    init {
        runnableList = arrayListOf()
        changeThemeWidge= arrayListOf()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.navigationBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        }
        isDestroy = false
        glide = Glide.with(this)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setNeedChangeThemeColorWidget()
    }

    override fun onStart() {
        super.onStart()
        val color = getThemeColor()
        if(!ignoreToolbar) {
            supportActionBar?.setBackgroundDrawable(ColorDrawable(color))
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = color
            window.navigationBarColor = color
        }
        changeThemeWidge.forEach {
            findViewById<View>(it).setBackgroundColor(color)
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
        runnableList.forEach { ThreadPool.remove(it) }
        runnableList.clear()
    }

    protected fun requestHttp(runnable: () -> Unit) {
        requestHttp(Runnable(runnable))
    }

    protected fun requestHttp(runnable: Runnable) {
        runnableList.add(runnable)
        ThreadPool.execute(runnable)
    }

    protected fun setIgnoreToolbar(ignore:Boolean){
        this.ignoreToolbar=ignore
    }
    open fun setNeedChangeThemeColorWidget(){}

}