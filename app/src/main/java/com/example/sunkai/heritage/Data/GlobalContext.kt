package com.example.sunkai.heritage.Data

import android.app.Application
import android.content.Context

/**
 * Created by sunkai on 2017/12/13.
 */
class GlobalContext : Application() {

    override fun onCreate() {
        // TODO Auto-generated method stub
        super.onCreate()
        instance = this
    }

    companion object {
        var instance: GlobalContext? = null
            private set
    }
}