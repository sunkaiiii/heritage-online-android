package com.example.sunkai.heritage.logic

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.sunkai.heritage.entity.request.SearchNewsRequest
import com.example.sunkai.heritage.entity.response.NewsListResponse
import com.example.sunkai.heritage.network.EHeritageApi
import com.example.sunkai.heritage.network.await
import java.lang.Exception
import java.time.Year

class SearchNewsPageSource(val request:SearchNewsRequest):PagingSource<Int,NewsListResponse>() {
    override fun getRefreshKey(state: PagingState<Int, NewsListResponse>): Int? =null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NewsListResponse> {
        return try{
            val page = params.key?:1
            val pageSize = params.loadSize
            val repoResponse = EHeritageApi.searchNews(page,request.keywords,request.year).await()
            val prevKey = if(page>1)page-1 else null
            val nextKey = if(repoResponse.isNotEmpty()) page+1 else null
            LoadResult.Page(repoResponse,prevKey,nextKey)
        }catch (e: Exception){
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}