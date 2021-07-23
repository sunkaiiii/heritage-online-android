package com.example.sunkai.heritage.logic

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.sunkai.heritage.entity.request.SearchRequest
import com.example.sunkai.heritage.entity.response.ProjectListInformation
import retrofit2.Call
import retrofit2.await
import java.lang.Exception
import kotlin.reflect.KFunction1

class SearchProjectPageSource(private val searchRequest:SearchRequest,private val listCaller: KFunction1<SearchRequest, Call<List<ProjectListInformation>>>):PagingSource<Int, ProjectListInformation>() {
    override fun getRefreshKey(state: PagingState<Int, ProjectListInformation>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ProjectListInformation> {
        return try{
            val page = params.key ?: 1
            searchRequest.page = page
            val repoResponse = listCaller(searchRequest).await()
            val prevKey = if(page>1)page-1 else null
            val nextKey = if(repoResponse.isNotEmpty()) page+1 else null
            LoadResult.Page(repoResponse,prevKey,nextKey)
        }catch (e:Exception){
            e.printStackTrace()
            LoadResult.Error(e)
        }

    }
}