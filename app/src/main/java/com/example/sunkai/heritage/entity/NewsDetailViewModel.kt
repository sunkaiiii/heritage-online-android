package com.example.sunkai.heritage.entity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.sunkai.heritage.database.entities.Collection
import com.example.sunkai.heritage.interfaces.CollectionView
import com.example.sunkai.heritage.logic.CollectionHandler
import com.example.sunkai.heritage.logic.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class NewsDetailViewModel @Inject constructor(
    val repository: Repository,
    val collectionHandler: CollectionHandler
) : ViewModel(), CollectionView {
    private val newsDetailLink = MutableLiveData<Pair<String, NewsPages>>()

    val newsDetail = Transformations.switchMap(newsDetailLink) { pair ->
        when (pair.second) {
            NewsPages.NewsPage -> repository.getNewsDetail(pair.first)
            NewsPages.ForumsPage -> repository.getForumsDetail(pair.first)
            NewsPages.SpecialTopicPage -> repository.getSpecialTopicDetail(pair.first)
        }
    }

    val collectionInformation = Transformations.switchMap(newsDetailLink) { pair ->
        getCollection(pair.first)
    }

    val isCollected = Transformations.map(collectionInformation) { collectionInformation ->
        collectionInformation != null
    }

    fun loadNewsDetail(link: String, api: NewsPages) {
        newsDetailLink.value = Pair(link, api)
    }

    override fun addCollection(key: String, content: String, imageLink: String?) {
        if (collectionInformation.value != null) {
            return
        }
        runBlocking(Dispatchers.IO) {
            collectionHandler.addCollection(collectionType(), key,content,imageLink)
        }
    }

    override fun deleteCollection() {
        val collection = collectionInformation.value ?: return
        runBlocking(Dispatchers.IO) {
            collectionHandler.deleteCollection(collection)
        }

    }

    override fun getCollection(key: String): LiveData<Collection?> {
        return collectionHandler.getCollection(key)
    }

    override fun collectionType(): Collection.CollectionType {
        val pair = newsDetailLink.value ?: return Collection.CollectionType.NewsDetail
        return when (pair.second) {
            NewsPages.NewsPage -> Collection.CollectionType.NewsDetail
            NewsPages.ForumsPage -> Collection.CollectionType.ForumsDetail
            NewsPages.SpecialTopicPage -> Collection.CollectionType.SpeicialListDetail
        }
    }
}