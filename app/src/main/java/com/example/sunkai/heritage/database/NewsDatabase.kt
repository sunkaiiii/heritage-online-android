package com.example.sunkai.heritage.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.sunkai.heritage.database.dao.*
import com.example.sunkai.heritage.database.entities.*
import java.io.Serializable


@Database(entities = [NewsDetail::class,
    NewsDetailContent::class,
    NewsList::class,
    SearchHistory::class,
    NewsDetailRelevantContent::class,
                     SearchNewsHistory::class],
        version = 5)
abstract class NewsDatabase : RoomDatabase() {
    abstract fun newsDetailDao(): NewsDetailDao
    abstract fun newsDetailContentDao(): NewsDetailContentDao
    abstract fun newsListaDao(): NewsListDao
    abstract fun newsDetailRelevantNewsDao():NewsDetailContentRelevantNewsDao
    abstract fun searchHistoryDao(): SearchHistoryDao

    enum class NewsListDaoName(val typeName: String) : Serializable {
        NEWS_LIST("newsList"),
        FORUMS_LIST("forumsList"),
        SPECIAL_TOPIC_LIST("specialTopic")
    }
}