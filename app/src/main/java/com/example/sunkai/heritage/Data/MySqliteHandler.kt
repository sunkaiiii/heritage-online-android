package com.example.sunkai.heritage.Data

import android.database.sqlite.SQLiteDatabase

/**
 * Created by sunkai on 2017/12/13.
 */
object MySqliteHandler{
    private val myHelper = MySqlLite(GlobalContext.instance, "heritage.db", null, 2)

    fun GetReadableDatabase():SQLiteDatabase{
        return myHelper.readableDatabase
    }

    fun GetWritableDatabase():SQLiteDatabase{
        return myHelper.writableDatabase
    }

    fun Close(){
        myHelper.close()
    }
}
