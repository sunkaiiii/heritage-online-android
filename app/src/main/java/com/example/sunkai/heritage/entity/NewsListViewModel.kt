package com.example.sunkai.heritage.entity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.sunkai.heritage.connectWebService.EHeritageApiRetrofitServiceCreator
import com.example.sunkai.heritage.connectWebService.await
import kotlinx.coroutines.Dispatchers

class NewsListViewModel : ViewModel() {
    var pageNumber = 0

    //    val newsListLiveData
    val newsList = liveData(Dispatchers.IO) {
        val result =
            EHeritageApiRetrofitServiceCreator.EhritageService.getNewsList(pageNumber++).await()
        emit(result)
    }
}