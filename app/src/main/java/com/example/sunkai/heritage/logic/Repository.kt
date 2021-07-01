package com.example.sunkai.heritage.logic

import androidx.lifecycle.liveData
import com.example.sunkai.heritage.connectWebService.EHeritageApiRetrofitServiceCreator
import com.example.sunkai.heritage.connectWebService.await
import com.example.sunkai.heritage.entity.response.NewsListResponse
import kotlinx.coroutines.Dispatchers
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor() {
    fun getNewsList(page:Int) = liveData(Dispatchers.IO){
        val result = try{
            val newsList = EHeritageApiRetrofitServiceCreator.EhritageService.getNewsList(page).await()
            Result.success(newsList)
        }catch (e:Exception){
            Result.failure(e)
        }
        emit(result)
    }

    fun getNewsDetail(link:String)=liveData(Dispatchers.IO){
        val newsDetail = EHeritageApiRetrofitServiceCreator.EhritageService.getNewsDetail(link).await()
        Result.success(newsDetail)
        emit(newsDetail)
    }
}