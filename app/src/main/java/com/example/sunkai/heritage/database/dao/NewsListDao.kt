package com.example.sunkai.heritage.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.sunkai.heritage.database.entities.NewsList
import com.example.sunkai.heritage.database.entities.SearchNewsHistory

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

    @Insert
    fun insertSearchNewsRecord(searchRecord:SearchNewsHistory)

    @Query("SELECT * FROM SearchNewsHistory ")
    fun getSearchNewsHistory():LiveData<List<SearchNewsHistory>>

    @Query("SELECT * FROM SearchNewsHistory WHERE searchValue=:searchValue LIMIT 1")
    fun getSearchNewsHistoryFromValueExisted(searchValue:String):SearchNewsHistory?

    @Update
    fun updateExistedSearchHistory(searchRecord:SearchNewsHistory)

}