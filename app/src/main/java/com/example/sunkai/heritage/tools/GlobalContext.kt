package com.example.sunkai.heritage.tools

import android.app.Application
import android.content.Context


/**
 * 全局GlobalContext
 * Created by sunkai on 2017/12/13.
 */
class GlobalContext : Application() {


    override fun onCreate() {
        super.onCreate()
        instance = this
    }


    companion object {
        lateinit var instance: GlobalContext
            private set
    }
}