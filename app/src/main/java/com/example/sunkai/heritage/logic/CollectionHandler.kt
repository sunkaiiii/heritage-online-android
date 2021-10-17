package com.example.sunkai.heritage.logic

import androidx.lifecycle.LiveData
import com.example.sunkai.heritage.database.dao.CollectionDao
import com.example.sunkai.heritage.database.entities.Collection
import com.example.sunkai.heritage.tools.EHeritageApplication
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CollectionHandler @Inject constructor() {
    private val collectionDao: CollectionDao = EHeritageApplication.newsDetailDatabase.collectionDao()

    suspend fun addCollection(type: Collection.CollectionType, key: String,content:String,imageLink:String?=null) {
        coroutineScope {
            val newCollection = Collection(type, key,content,imageLink)
            collectionDao.insert(newCollection)
        }
    }

    suspend fun deleteCollection(collection: Collection) {
        coroutineScope {
            collectionDao.delete(collection)
        }
    }

    fun getCollection(key: String): LiveData<Collection?> {
        return collectionDao.searchOne(key)
    }

    fun getALlCollection(): LiveData<List<Collection>> {
        return collectionDao.getAll()
    }

    fun getCollectionByType(collectionType: Collection.CollectionType)=collectionDao.searhByType(collectionType)
}