package com.example.sunkai.heritage.entity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.sunkai.heritage.fragment.MainFragment
import com.example.sunkai.heritage.logic.Repository
import com.example.sunkai.heritage.network.await
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NewsDetailViewModel @Inject constructor(val repository: Repository) : ViewModel() {
    private val newsDetailLink = MutableLiveData<Pair<String, MainFragment.NewsPages>>()

    val newsDetail = Transformations.switchMap(newsDetailLink) { pair ->
        liveData {
            val detail = pair.second.detailApi(pair.first).await()
            emit(detail)
        }
    }

    fun loadNewsDetail(link: String, api: MainFragment.NewsPages) {
        newsDetailLink.value = Pair(link, api)
    }
}