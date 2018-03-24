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

        val sharePrefrence=getSharedPreferences("setting",Context.MODE_PRIVATE)
        if(sharePrefrence.getBoolean("pushSwitch",false)) {

        }
    }


    companion object {
        lateinit var instance: GlobalContext
            private set
    }
}