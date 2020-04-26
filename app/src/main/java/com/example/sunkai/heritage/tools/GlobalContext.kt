package com.example.sunkai.heritage.tools

import android.app.Application
import androidx.room.Room
import com.example.sunkai.heritage.database.NewsDatabase
import com.example.sunkai.heritage.value.NEWS_DETAIL_DATABASE


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
        reloadThemeColor()
        newsDetailDatabase=Room.databaseBuilder(
                this,
                NewsDatabase::class.java,NEWS_DETAIL_DATABASE
        ).build()
    }


    companion object {
        lateinit var instance: GlobalContext
            private set
        lateinit var newsDetailDatabase:NewsDatabase
    }
}