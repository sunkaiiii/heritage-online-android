package com.example.sunkai.heritage.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.sunkai.heritage.database.entities.SearchHistory

@Dao
interface SearchHistoryDao {
    @Query("SELECT * FROM SearchHistory ORDER BY id DESC LIMIT 200 ")
    fun getAllSearchHistory():List<SearchHistory>

    @Insert
    fun insert(history: SearchHistory)

    @Delete
    fun delete(history: SearchHistory)
}