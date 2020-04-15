package com.example.sunkai.heritage.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.sunkai.heritage.database.entities.NewsList

@Dao
interface NewsListDao {
    @Query("SELECT * FROM NewsList WHERE type=:type ORDER BY date DESC")
    fun getAllByType(type: String): List<NewsList>

    @Query("SELECT COUNT(id) FROM NewsList WHERE link=:link LIMIT 1")
    fun getCountNumberByLink(link: String): Int

    @Insert
    fun insert(newsList: NewsList)

    @Insert
    fun insertAll(newsList: List<NewsList>?)

    @Update
    fun update(newsList: NewsList)

}