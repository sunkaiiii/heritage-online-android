package com.example.sunkai.heritage.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.sunkai.heritage.database.dao.NewsDetailContentDao
import com.example.sunkai.heritage.database.dao.NewsDetailDao
import com.example.sunkai.heritage.database.entities.NewsDetail
import com.example.sunkai.heritage.database.entities.NewsDetailContent

@Database(entities = [NewsDetail::class,NewsDetailContent::class],version = 2)
abstract class NewsDatabase:RoomDatabase() {
    abstract fun newsDetailDao(): NewsDetailDao
    abstract fun newsDetailContentDao(): NewsDetailContentDao
}