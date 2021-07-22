package com.example.sunkai.heritage.logic

import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.sunkai.heritage.network.EHeritageApiRetrofitServiceCreator
import com.example.sunkai.heritage.network.await
import com.example.sunkai.heritage.entity.response.NewsListResponse
import com.example.sunkai.heritage.entity.response.ProjectListInformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import retrofit2.Call
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.reflect.KFunction1

@Singleton
class Repository @Inject constructor() {

    private val COMMON_LIST_PAGE_SIZE = 20

    fun fetchNewsListPageData(listCaller: KFunction1<Int, Call<List<NewsListResponse>>>): Flow<PagingData<NewsListResponse>>{
        return Pager(
            config = PagingConfig(COMMON_LIST_PAGE_SIZE),
            pagingSourceFactory = {NewsListPageSource(listCaller)}
        ).flow
    }


    fun fetchPeopleListPageData():Flow<PagingData<NewsListResponse>>{
        return Pager(
            config = PagingConfig(COMMON_LIST_PAGE_SIZE),
            pagingSourceFactory = {NewsListPageSource(EHeritageApiRetrofitServiceCreator.EhritageService::getPeopleList)}
        ).flow
    }

    fun fetchProjectListPageData():Flow<PagingData<ProjectListInformation>>{
        return Pager(
            config = PagingConfig(COMMON_LIST_PAGE_SIZE),
            pagingSourceFactory = {ProjectListPageSource(EHeritageApiRetrofitServiceCreator.EhritageService::getProjecrList)}
        ).flow
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

    fun getPeopleTopBanner()=liveData(Dispatchers.IO){
        val peopleTopBanner = EHeritageApiRetrofitServiceCreator.EhritageService.getPeopleTopBanner().await()
        Result.success(peopleTopBanner)
        emit(peopleTopBanner)
    }

    fun getPeopleDetail(link:String)=liveData(Dispatchers.IO){
        val detail = EHeritageApiRetrofitServiceCreator.EhritageService.getPeopleDetail(link).await()
        Result.success(detail)
        emit(detail)
    }

    fun getProjectBasicInformation()=liveData(Dispatchers.IO){
        val projectInformation = EHeritageApiRetrofitServiceCreator.EhritageService.getProjectBasicInformation().await()
        Result.success(projectInformation)
        emit(projectInformation)
    }

    fun getProjectDetail(link:String)=liveData(Dispatchers.IO){
        val projectDetail = EHeritageApiRetrofitServiceCreator.EhritageService.getProjectDetail(link).await()
        Result.success(projectDetail)
        emit(projectDetail)
    }

    fun getInheritanceDetail(link:String) = liveData(Dispatchers.IO){
        val detail = EHeritageApiRetrofitServiceCreator.EhritageService.getInheritanceDetail(link).await()
        Result.success(detail)
        emit(detail)
    }
}