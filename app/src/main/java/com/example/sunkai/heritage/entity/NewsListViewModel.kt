package com.example.sunkai.heritage.entity

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
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
    private val listCaller=MutableLiveData<KFunction1<Int, Call<List<NewsListResponse>>>>()
    val newsListPagingData = Transformations.switchMap(listCaller){listCaller->
        liveData{
            val news = repository.fetchNewsListPageData(listCaller).cachedIn(viewModelScope).asLiveData(Dispatchers.Main)
            emitSource(news)
        }

    }
   fun setListCaller(listCaller:KFunction1<Int, Call<List<NewsListResponse>>>){
       this.listCaller.value=listCaller
   }
}