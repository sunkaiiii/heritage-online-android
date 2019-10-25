package com.example.sunkai.heritage.tools

import android.app.Application


/**
 * 全局GlobalContext
 * Created by sunkai on 2017/12/13.
 */
class GlobalContext : Application() {

    private val TAG = "GlobalContext"

    override fun onCreate() {
        super.onCreate()
        instance = this
        MakeToast.initToast(this)
    }

    companion object {
        lateinit var instance: GlobalContext
            private set
    }
}