package com.example.sunkai.heritage.logic

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.sunkai.heritage.network.await
import com.example.sunkai.heritage.entity.response.ProjectListInformation
import retrofit2.Call
import java.lang.Exception
import kotlin.reflect.KFunction1

class ProjectListPageSource(private val listCaller: KFunction1<Int, Call<List<ProjectListInformation>>>):PagingSource<Int,ProjectListInformation>() {
    override fun getRefreshKey(state: PagingState<Int, ProjectListInformation>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ProjectListInformation> {
        return try{
            val page = params.key?:1
            val pageSize = params.loadSize
            val repoResponse = listCaller(page).await()
            val prevKey = if(page>1)page-1 else null
            val nextKey = if(repoResponse.isNotEmpty()) page+1 else null
            LoadResult.Page(repoResponse,prevKey,nextKey)
        }catch (e: Exception){
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }

}