package com.example.sunkai.heritage.logic

import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.sunkai.heritage.database.entities.NewsDetailContent
import com.example.sunkai.heritage.database.entities.NewsDetailRelevantContent
import com.example.sunkai.heritage.database.entities.SearchHistory
import com.example.sunkai.heritage.entity.request.SearchRequest
import com.example.sunkai.heritage.entity.response.NewsDetail
import com.example.sunkai.heritage.entity.response.NewsListResponse
import com.example.sunkai.heritage.entity.response.ProjectListInformation
import com.example.sunkai.heritage.entity.response.SearchCategoryResponse
import com.example.sunkai.heritage.network.EHeritageApiRetrofitServiceCreator
import com.example.sunkai.heritage.network.await
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
        val content =
            EHeritageApplication.newsDetailDatabase.newsDetailDao().getNewsDetailWithContent(link)
        val detail = if (content != null) {
            NewsDetail(content)
        } else {
            val newsDetail =
                EHeritageApiRetrofitServiceCreator.EhritageService.getNewsDetail(link).await()
            saveIntoDatabase(newsDetail)
            newsDetail
        }
        Result.success(detail)
        emit(detail)
    }

    private fun saveIntoDatabase(data: NewsDetail) {
        GlobalScope.launch {
            val database = EHeritageApplication.newsDetailDatabase
            val newsDetailContentList = arrayListOf<NewsDetailContent>()
            val newsRelevantNews = arrayListOf<NewsDetailRelevantContent>()
            data.content.forEach {
                newsDetailContentList.add(
                    NewsDetailContent(
                        null,
                        it.type,
                        it.content,
                        it.compressImg,
                        data.link
                    )
                )
            }
            data.relativeNews.forEach {
                newsRelevantNews.add(
                    NewsDetailRelevantContent(
                        null,
                        it.link,
                        it.title,
                        it.date,
                        data.link
                    )
                )
            }
            val dao = database.newsDetailDao()
            val contentdao = database.newsDetailContentDao()
            val relevantNewsDat = database.newsDetailRelevantNewsDao()
            dao.insert(com.example.sunkai.heritage.database.entities.NewsDetail(data))
            contentdao.insertAll(newsDetailContentList)
            relevantNewsDat.insertAll(newsRelevantNews)
        }
    }

    fun getForumsDetail(link: String) = liveData(Dispatchers.IO) {
        val content =
            EHeritageApplication.newsDetailDatabase.newsDetailDao().getNewsDetailWithContent(link)
        val detail = if (content != null) {
            NewsDetail(content)
        } else {
            val forumsDetail =
                EHeritageApiRetrofitServiceCreator.EhritageService.getForumsDetail(link).await()
            saveIntoDatabase(forumsDetail)
            forumsDetail
        }

        Result.success(detail)
        emit(detail)
    }

    fun getSpecialTopicDetail(link: String) = liveData(Dispatchers.IO) {
        val content =
            EHeritageApplication.newsDetailDatabase.newsDetailDao().getNewsDetailWithContent(link)
        val detail = if (content != null) {
            NewsDetail(content)
        } else {
            val specialTopic =
                EHeritageApiRetrofitServiceCreator.EhritageService.getSpecialTopicDetail(link)
                    .await()
            saveIntoDatabase(specialTopic)
            specialTopic
        }
        Result.success(detail)
        emit(detail)
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
        val category =
            EHeritageApiRetrofitServiceCreator.EhritageService.getSearchCategory().await()
        Result.success(category)
        emit(category)
    }

    fun getSearchResult(request: SearchRequest): Flow<PagingData<ProjectListInformation>> {
        return Pager(
            config = PagingConfig(COMMON_LIST_PAGE_SIZE),
            pagingSourceFactory = { SearchProjectPageSource(request, this::getSearchResultCaller) }
        ).flow
    }

    fun getSearchHistory() =
        EHeritageApplication.newsDetailDatabase.searchHistoryDao().getAllSearchHistory()

    fun addSearchHistory(searchHistory: SearchHistory) {
        GlobalScope.launch {
            EHeritageApplication.newsDetailDatabase.searchHistoryDao().insert(searchHistory)
        }
    }

    fun removeSearchHistory(searchHistory: SearchHistory) {
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