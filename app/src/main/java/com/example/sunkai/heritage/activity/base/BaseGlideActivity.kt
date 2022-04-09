package com.example.sunkai.heritage.activity.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.forEachAndTintViews
import com.example.sunkai.heritage.tools.getDarkThemeColor
import com.example.sunkai.heritage.tools.getThemeColor
import com.example.sunkai.heritage.value.CHANGE_THEME

abstract class BaseGlideActivity : AppCompatActivity() {
    protected lateinit var glide: RequestManager
    private var ignoreToolbar = false
    protected val TAG: String = javaClass.name
    private val broadReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            changeWidgeTheme()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocalBroadcastManager.getInstance(this).registerReceiver(broadReceiver, IntentFilter(CHANGE_THEME))
        glide = Glide.with(this)
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
        val color = ContextCompat.getColor(this, R.color.material_dynamic_primary70)
        window.statusBarColor = color
        window.navigationBarColor = color
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
    }



    protected fun setIgnoreToolbar(ignore: Boolean) {
        this.ignoreToolbar = ignore
    }


}