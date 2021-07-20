package com.example.sunkai.heritage.logic

import androidx.paging.*
import com.example.sunkai.heritage.connectWebService.EHeritageApiRetrofitServiceCreator
import com.example.sunkai.heritage.connectWebService.await
import com.example.sunkai.heritage.entity.response.NewsListResponse
import retrofit2.Call
import java.lang.Exception
import kotlin.reflect.KFunction1

class NewsListPageSource(private val listCaller: KFunction1<Int, Call<List<NewsListResponse>>>): PagingSource<Int, NewsListResponse>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NewsListResponse> {
        return try{
            val page = params.key?:1
            val pageSize = params.loadSize
            val repoResponse = listCaller(page).await()
            val prevKey = if(page>1)page-1 else null
            val nextKey = if(repoResponse.isNotEmpty()) page+1 else null
            LoadResult.Page(repoResponse,prevKey,nextKey)
        }catch (e:Exception){
            e.printStackTrace()
            LoadResult.Error(e)
        }

    }

    override fun getRefreshKey(state: PagingState<Int, NewsListResponse>): Int?=null
}