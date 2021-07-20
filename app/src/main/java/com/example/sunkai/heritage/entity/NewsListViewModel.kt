package com.example.sunkai.heritage.entity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.sunkai.heritage.connectWebService.EHeritageApiRetrofitServiceCreator
import com.example.sunkai.heritage.connectWebService.await
import com.example.sunkai.heritage.entity.response.NewsListResponse
import com.example.sunkai.heritage.logic.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import retrofit2.Call
import javax.inject.Inject
import kotlin.reflect.KFunction1

@HiltViewModel
class NewsListViewModel @Inject constructor(val repository: Repository) : ViewModel() {
   fun getNewsListPagingData(listCaller: KFunction1<Int, Call<List<NewsListResponse>>>): Flow<PagingData<NewsListResponse>>{
       return repository.fetchListPageData(listCaller).cachedIn(viewModelScope)
   }
}