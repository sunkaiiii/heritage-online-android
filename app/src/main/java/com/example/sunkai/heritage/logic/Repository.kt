package com.example.sunkai.heritage.logic

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.sunkai.heritage.database.entities.SearchHistory
import com.example.sunkai.heritage.entity.request.SearchRequest
import com.example.sunkai.heritage.network.EHeritageApiRetrofitServiceCreator
import com.example.sunkai.heritage.network.await
import com.example.sunkai.heritage.entity.response.NewsListResponse
import com.example.sunkai.heritage.entity.response.ProjectListInformation
import com.example.sunkai.heritage.entity.response.SearchCategoryResponse
import com.example.sunkai.heritage.tools.EHeritageApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import retrofit2.Call
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.reflect.KFunction1

@Singleton
class Repository @Inject constructor() {

    private val COMMON_LIST_PAGE_SIZE = 20

    fun fetchNewsListPageData(listCaller: KFunction1<Int, Call<List<NewsListResponse>>>): Flow<PagingData<NewsListResponse>> {
        return Pager(
            config = PagingConfig(COMMON_LIST_PAGE_SIZE),
            pagingSourceFactory = { NewsListPageSource(listCaller) }
        ).flow
    }


    fun fetchPeopleListPageData(): Flow<PagingData<NewsListResponse>> {
        return Pager(
            config = PagingConfig(COMMON_LIST_PAGE_SIZE),
            pagingSourceFactory = { NewsListPageSource(EHeritageApiRetrofitServiceCreator.EhritageService::getPeopleList) }
        ).flow
    }

    fun fetchProjectListPageData(): Flow<PagingData<ProjectListInformation>> {
        return Pager(
            config = PagingConfig(COMMON_LIST_PAGE_SIZE),
            pagingSourceFactory = { ProjectListPageSource(EHeritageApiRetrofitServiceCreator.EhritageService::getProjecrList) }
        ).flow
    }


    fun getNewsDetail(link: String) = liveData(Dispatchers.IO) {
        val newsDetail =
            EHeritageApiRetrofitServiceCreator.EhritageService.getNewsDetail(link).await()
        Result.success(newsDetail)
        emit(newsDetail)
    }

    fun getBanner() = liveData(Dispatchers.IO) {
        val banner = EHeritageApiRetrofitServiceCreator.EhritageService.getBanner().await()
        Result.success(banner)
        emit(banner)
    }

    fun getPeopleTopBanner() = liveData(Dispatchers.IO) {
        val peopleTopBanner =
            EHeritageApiRetrofitServiceCreator.EhritageService.getPeopleTopBanner().await()
        Result.success(peopleTopBanner)
        emit(peopleTopBanner)
    }

    fun getPeopleDetail(link: String) = liveData(Dispatchers.IO) {
        val detail =
            EHeritageApiRetrofitServiceCreator.EhritageService.getPeopleDetail(link).await()
        Result.success(detail)
        emit(detail)
    }

    fun getProjectBasicInformation() = liveData(Dispatchers.IO) {
        val projectInformation =
            EHeritageApiRetrofitServiceCreator.EhritageService.getProjectBasicInformation().await()
        Result.success(projectInformation)
        emit(projectInformation)
    }

    fun getProjectDetail(link: String) = liveData(Dispatchers.IO) {
        val projectDetail =
            EHeritageApiRetrofitServiceCreator.EhritageService.getProjectDetail(link).await()
        Result.success(projectDetail)
        emit(projectDetail)
    }

    fun getInheritanceDetail(link: String) = liveData(Dispatchers.IO) {
        val detail =
            EHeritageApiRetrofitServiceCreator.EhritageService.getInheritanceDetail(link).await()
        Result.success(detail)
        emit(detail)
    }

    fun getSearchCategory() = liveData(Dispatchers.IO) {
//        val category = EHeritageApiRetrofitServiceCreator.EhritageService.getSearchCategory().await()
//        Result.success(category)
//        emit(category)
        Thread.sleep(800)
        val response = SearchCategoryResponse(mapOf(Pair("1","1"),Pair("2","2")))
        Result.success(response)
        emit(response)
    }

    fun getSearchResult(request: SearchRequest): Flow<PagingData<ProjectListInformation>> {
        return Pager(
            config = PagingConfig(COMMON_LIST_PAGE_SIZE),
            pagingSourceFactory = { SearchProjectPageSource(request, this::getSearchResultCaller) }
        ).flow
    }

    fun getSearchHistory() = EHeritageApplication.newsDetailDatabase.searchHistoryDao().getAllSearchHistory()

    fun addSearchHistory(searchHistory: SearchHistory){
        GlobalScope.launch {
            EHeritageApplication.newsDetailDatabase.searchHistoryDao().insert(searchHistory)
        }
    }

    fun removeSearchHistory(searchHistory: SearchHistory){
        GlobalScope.launch {
            EHeritageApplication.newsDetailDatabase.searchHistoryDao().delete(searchHistory)
        }
    }

    private fun getSearchResultCaller(request: SearchRequest) =
        EHeritageApiRetrofitServiceCreator.EhritageService.getSearchProjectResult(
            request.num,
            request.title,
            request.type,
            request.rx_time,
            request.cate,
            request.province,
            request.unit,
            request.page
        )
}