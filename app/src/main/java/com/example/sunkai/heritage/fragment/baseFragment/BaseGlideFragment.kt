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
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.tools.forEachAndTintViews
import com.example.sunkai.heritage.value.CHANGE_THEME

abstract class BaseGlideFragment : Fragment() {
    protected lateinit var glide: RequestManager
    protected var changeThemeWidge: MutableList<Int>
    private var ignoreToolbar = false
    protected val TAG: String =this.javaClass.simpleName
    private val broadReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            changeWidgeTheme()
        }

    }

    init {
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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


    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(context ?: return).unregisterReceiver(broadReceiver)
    }


    open fun setNeedChangeThemeColorWidget() {}

}