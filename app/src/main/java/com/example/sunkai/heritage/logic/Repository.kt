package com.example.sunkai.heritage.logic

import androidx.lifecycle.liveData
import com.example.sunkai.heritage.connectWebService.EHeritageApiRetrofitServiceCreator
import com.example.sunkai.heritage.connectWebService.await
import com.example.sunkai.heritage.entity.response.NewsListResponse
import kotlinx.coroutines.Dispatchers
import java.lang.Exception

object Repository {
    fun getNewsList(page:Int) = liveData(Dispatchers.IO){
        val result = try{
            val newsList = EHeritageApiRetrofitServiceCreator.EhritageService.getNewsList(page).await()
            Result.success(newsList)
        }catch (e:Exception){
            Result.failure(e)
        }
        emit(result)
    }
}