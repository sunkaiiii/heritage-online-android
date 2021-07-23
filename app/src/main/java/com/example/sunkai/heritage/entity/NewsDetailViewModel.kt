package com.example.sunkai.heritage.entity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.sunkai.heritage.logic.Repository
import com.example.sunkai.heritage.network.await
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NewsDetailViewModel @Inject constructor(val repository: Repository) : ViewModel() {
    private val newsDetailLink = MutableLiveData<Pair<String, NewsPages>>()

    val newsDetail = Transformations.switchMap(newsDetailLink) { pair ->
        when(pair.second){
            NewsPages.NewsPage->repository.getNewsDetail(pair.first)
            NewsPages.ForumsPage->repository.getForumsDetail(pair.first)
            NewsPages.SpecialTopicPage->repository.getSpecialTopicDetail(pair.first)
        }
    }

    fun loadNewsDetail(link: String, api: NewsPages) {
        newsDetailLink.value = Pair(link, api)
    }
}