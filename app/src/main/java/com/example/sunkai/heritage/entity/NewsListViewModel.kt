package com.example.sunkai.heritage.entity

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.sunkai.heritage.entity.response.NewsListResponse
import com.example.sunkai.heritage.logic.Repository
import kotlinx.coroutines.Dispatchers
import retrofit2.Call
import kotlin.reflect.KFunction1

abstract class NewsListViewModel(private val repository: Repository, private val listCaller: KFunction1<Int, Call<List<NewsListResponse>>>):ViewModel() {
    val newsListPagingData: LiveData<PagingData<NewsListResponse>> = repository.fetchNewsListPageData(listCaller).cachedIn(viewModelScope).asLiveData(
        Dispatchers.Main)
}