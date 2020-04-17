package com.example.sunkai.heritage.database.dao

import androidx.room.Dao
import androidx.room.Insert
import com.example.sunkai.heritage.database.entities.NewsDetailRelevantContent

@Dao
interface NewsDetailContentRelevantNewsDao {
    @Insert
    fun insertAll(newsDetail: List<NewsDetailRelevantContent>)
}