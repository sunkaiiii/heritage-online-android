package com.example.sunkai.heritage.tools

import android.app.Application
import androidx.room.Room
import com.example.sunkai.heritage.database.NewsDatabase
import com.example.sunkai.heritage.value.NEWS_DETAIL_DATABASE
import com.google.android.material.color.DynamicColors
import dagger.hilt.android.HiltAndroidApp


/**
 * 全局GlobalContext
 * Created by sunkai on 2017/12/13.
 */
@HiltAndroidApp
class EHeritageApplication : Application() {

    private val TAG = "GlobalContext"

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
        instance = this
        MakeToast.initToast(this)
        newsDetailDatabase=Room.databaseBuilder(
                this,
                NewsDatabase::class.java,NEWS_DETAIL_DATABASE
        ).fallbackToDestructiveMigration().build()
    }


    companion object {
        lateinit var instance: EHeritageApplication
            private set
        lateinit var newsDetailDatabase:NewsDatabase
    }
}