package com.example.sunkai.heritage.logic

import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.sunkai.heritage.database.entities.NewsDetailContent
import com.example.sunkai.heritage.database.entities.NewsDetailRelevantContent
import com.example.sunkai.heritage.database.entities.SearchHistory
import com.example.sunkai.heritage.database.entities.SearchNewsHistory
import com.example.sunkai.heritage.entity.request.SearchNewsRequest
import com.example.sunkai.heritage.entity.request.SearchRequest
import com.example.sunkai.heritage.entity.response.HeritageProjectStatisticsResponse
import com.example.sunkai.heritage.entity.response.NewsDetail
import com.example.sunkai.heritage.entity.response.NewsListResponse
import com.example.sunkai.heritage.entity.response.ProjectListInformation
import com.example.sunkai.heritage.network.EHeritageApi
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
            pagingSourceFactory = { NewsListPageSource(EHeritageApi::getPeopleList) }
        ).flow
    }

    fun fetchProjectListPageData(): Flow<PagingData<ProjectListInformation>> {
        return Pager(
            config = PagingConfig(COMMON_LIST_PAGE_SIZE),
            pagingSourceFactory = { ProjectListPageSource(EHeritageApi::getProjecrList) }
        ).flow
    }

    fun fetchSearchProjectData(
        request: SearchNewsRequest
    ): Flow<PagingData<NewsListResponse>> {
        return Pager(
            config = PagingConfig((COMMON_LIST_PAGE_SIZE)),
            pagingSourceFactory = { SearchNewsPageSource(request) }
        ).flow
    }

    fun getSearchNewsHistory() =
        EHeritageApplication.newsDetailDatabase.newsListaDao().getSearchNewsHistory()

    fun addSearchNewsHistory(searchNewsHistory: SearchNewsHistory) {
        GlobalScope.launch {
            val existedSearchHistory = EHeritageApplication.newsDetailDatabase.newsListaDao()
                .getSearchNewsHistoryFromValueExisted(searchNewsHistory.searchValue)
            if (existedSearchHistory != null) {
                val updateRecordHistory = SearchNewsHistory(
                    existedSearchHistory.id,
                    existedSearchHistory.searchValue,
                    searchNewsHistory.searchHappenedTime
                )
                EHeritageApplication.newsDetailDatabase.newsListaDao()
                    .updateExistedSearchHistory(updateRecordHistory)
            } else {
                EHeritageApplication.newsDetailDatabase.newsListaDao()
                    .insertSearchNewsRecord(searchNewsHistory)
            }

        }
    }

    fun getNewsDetail(link: String) = liveData(Dispatchers.IO) {
        val content =
            EHeritageApplication.newsDetailDatabase.newsDetailDao().getNewsDetailWithContent(link)
        val detail = if (content != null) {
            NewsDetail(content)
        } else {
            val newsDetail =
                EHeritageApi.getNewsDetail(link).await()
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
                EHeritageApi.getForumsDetail(link).await()
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
                EHeritageApi.getSpecialTopicDetail(link)
                    .await()
            saveIntoDatabase(specialTopic)
            specialTopic
        }
        Result.success(detail)
        emit(detail)
    }

    fun getBanner() = liveData(Dispatchers.IO) {
        val banner = EHeritageApi.getBanner().await()
        Result.success(banner)
        emit(banner)
    }

    fun getPeopleTopBanner() = liveData(Dispatchers.IO) {
        val peopleTopBanner =
            EHeritageApi.getPeopleTopBanner().await()
        Result.success(peopleTopBanner)
        emit(peopleTopBanner)
    }

    fun getPeopleDetail(link: String) = liveData(Dispatchers.IO) {
        val detail =
            EHeritageApi.getPeopleDetail(link).await()
        Result.success(detail)
        emit(detail)
    }

    fun getProjectBasicInformation() = liveData(Dispatchers.IO) {
        val projectInformation =
            EHeritageApi.getProjectBasicInformation().await()
        Result.success(projectInformation)
        emit(projectInformation)
    }

    fun getProjectDetail(link: String) = liveData(Dispatchers.IO) {
        val projectDetail =
            EHeritageApi.getProjectDetail(link).await()
        Result.success(projectDetail)
        emit(projectDetail)
    }

    fun getInheritanceDetail(link: String) = liveData(Dispatchers.IO) {
        val detail =
            EHeritageApi.getInheritanceDetail(link).await()
        Result.success(detail)
        emit(detail)
    }

    fun getSearchCategory() = liveData(Dispatchers.IO) {
        val category =
            EHeritageApi.getSearchCategory().await()
        Result.success(category)
        emit(category)
    }

    fun getSearchResult(request: SearchRequest): Flow<PagingData<ProjectListInformation>> {
        return Pager(
            config = PagingConfig(COMMON_LIST_PAGE_SIZE),
            pagingSourceFactory = { SearchProjectPageSource(request, this::getSearchResultCaller) }
        ).flow
    }

    fun getProjectStatistics() = liveData(Dispatchers.IO) {
        val tempResponse = EHeritageApi.getProjectStatistics().await()
        val statisListByRegion = tempResponse.statisticsByRegion.sortedByDescending { it.value }
        val statisticsResponse = HeritageProjectStatisticsResponse(
            statisListByRegion,
            tempResponse.statisticsByTime,
            tempResponse.statisticsByType
        )
        Result.success(statisticsResponse)
        emit(statisticsResponse)
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
        EHeritageApi.getSearchProjectResult(
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