package com.example.sunkai.heritage.interfaces

import androidx.lifecycle.LiveData
import com.example.sunkai.heritage.database.entities.Collection

interface CollectionView {
    fun addCollection(key:String)
    fun deleteCollection()
    fun getCollection(key:String): LiveData<Collection?>
    fun collectionType():Collection.CollectionType
}