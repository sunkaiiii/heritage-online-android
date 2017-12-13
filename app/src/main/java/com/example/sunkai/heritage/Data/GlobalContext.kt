package com.example.sunkai.heritage.Data

import android.app.Application
/**
 * Created by sunkai on 2017/12/13.
 */
class GlobalContext : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        var instance: GlobalContext? = null
            private set
    }
}