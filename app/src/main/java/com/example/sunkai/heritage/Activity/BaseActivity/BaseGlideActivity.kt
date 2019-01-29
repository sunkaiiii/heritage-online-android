package com.example.sunkai.heritage.activity.baseActivity

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.tools.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.firebase.FirebaseApp

abstract class BaseGlideActivity : AppCompatActivity() {
    protected var isDestroy = true
    protected lateinit var glide: RequestManager
    private val runnableList: MutableList<Runnable>
    protected var changeThemeWidge: MutableList<Int>
    private var ignoreToolbar = false

    init {
        runnableList = arrayListOf()
        changeThemeWidge = arrayListOf()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
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

    protected fun setIgnoreToolbar(ignore: Boolean) {
        this.ignoreToolbar = ignore
    }

    open fun setNeedChangeThemeColorWidget() {}

}