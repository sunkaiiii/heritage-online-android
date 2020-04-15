package com.example.sunkai.heritage.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.sunkai.heritage.database.dao.*
import com.example.sunkai.heritage.database.entities.*
import com.google.gson.JsonSyntaxException
import java.io.Serializable
import java.io.StringReader
import java.lang.reflect.Type


@Database(entities = [NewsDetail::class,
    NewsDetailContent::class,
    NewsList::class],
        version = 2)
abstract class NewsDatabase : RoomDatabase() {
    abstract fun newsDetailDao(): NewsDetailDao
    abstract fun newsDetailContentDao(): NewsDetailContentDao
    abstract fun newsListaDao(): NewsListDao

    enum class NewsListDaoName(val typeName:String):Serializable{
        NEWS_LIST("newsList"),
        FORUMS_LIST("forumsList"),
        SPECIAL_TOPIC_LIST("specialTopic")
    }
}