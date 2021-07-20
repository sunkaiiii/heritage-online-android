package com.example.sunkai.heritage.logic

import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.sunkai.heritage.connectWebService.EHeritageApi
import com.example.sunkai.heritage.connectWebService.EHeritageApiRetrofitServiceCreator
import com.example.sunkai.heritage.connectWebService.await
import com.example.sunkai.heritage.entity.response.NewsListResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import retrofit2.Call
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.reflect.KFunction1

@Singleton
class Repository @Inject constructor() {

    private val NEWS_LIST_PAGE_SIZE = 20

    fun fetchListPageData(listCaller: KFunction1<Int, Call<List<NewsListResponse>>>): Flow<PagingData<NewsListResponse>>{
        return Pager(
            config = PagingConfig(NEWS_LIST_PAGE_SIZE),
            pagingSourceFactory = {NewsListPageSource(listCaller)}
        ).flow
    }

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

    fun getBanner()=liveData(Dispatchers.IO){
        val banner = EHeritageApiRetrofitServiceCreator.EhritageService.getBanner().await()
        Result.success(banner)
        emit(banner)
    }
}