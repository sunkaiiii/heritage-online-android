package com.example.sunkai.heritage.database.dao

import androidx.room.*
import com.example.sunkai.heritage.database.entities.NewsDetail
import com.example.sunkai.heritage.database.entities.NewsWithNewsDetailContent

@Dao
interface NewsDetailDao {
    @Query("SELECT * FROM NewsDetail")
    fun getAll():List<NewsDetail>

    @Query("SELECT * FROM NewsDetail WHERE link=:link LIMIT 1")
    fun loadByLink(link:String):NewsDetail

    @Transaction
    @Query("SELECT * FROM NewsDetail WHERE link=:link LIMIT 1")
    fun getNewsDetailWithContent(link:String):NewsWithNewsDetailContent?

    @Delete
    fun delete(newsDetail: NewsDetail)

    @Insert
    fun insert(newsDetail: NewsDetail)

    @Insert
    fun insertAll(vararg newsDetail: NewsDetail)
}