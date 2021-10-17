package com.example.sunkai.heritage.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.sunkai.heritage.database.entities.Collection

@Dao
interface CollectionDao {
    @Query("SELECT * FROM Collection")
    fun getAll():LiveData<List<Collection>>

    @Query("SELECT * FROM Collection WHERE `key`=:key LIMIT 1")
    fun searchOne(key:String):LiveData<Collection?>

    @Query("SELECT * FROM Collection WHERE collectionType=:type")
    fun searhByType(type:Collection.CollectionType):LiveData<List<Collection>>

    @Delete
    fun delete(collection: Collection)

    @Insert
    fun insert(collection: Collection)

    @Insert
    fun insertAll(vararg collection: Collection)
}