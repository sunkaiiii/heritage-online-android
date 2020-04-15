package com.example.sunkai.heritage.database.dao

import androidx.room.Dao
import androidx.room.Insert
import com.example.sunkai.heritage.database.entities.NewsDetailContent

@Dao
interface NewsDetailContentDao {
    @Insert
    fun insertAll(newsDetail: List<NewsDetailContent>)
}