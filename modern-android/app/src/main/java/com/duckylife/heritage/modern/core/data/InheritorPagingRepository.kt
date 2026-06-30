package com.duckylife.heritage.modern.core.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.duckylife.heritage.modern.core.database.HeritageDatabase
import com.duckylife.heritage.modern.core.database.mapper.queryKey
import com.duckylife.heritage.modern.core.database.mapper.toDto
import com.duckylife.heritage.modern.core.network.HeritageApiClient
import com.duckylife.heritage.modern.core.network.InheritorQuery
import com.duckylife.heritage.modern.core.network.dto.InheritorSummaryDto
import com.duckylife.heritage.modern.core.paging.InheritorRemoteMediator
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalPagingApi::class)
@Singleton
class InheritorPagingRepository @Inject constructor(
    private val database: HeritageDatabase,
    private val apiClient: HeritageApiClient,
) {
    fun pagedInheritors(query: InheritorQuery): Flow<PagingData<InheritorSummaryDto>> =
        // Paging 从 Room 读数据，RemoteMediator 负责网络刷新。
        // UI 只收集这个 flow，不需要关心行数据来自缓存还是刚刷新。
        Pager(
            config = PagingConfig(
                pageSize = query.pageSize,
                initialLoadSize = query.pageSize,
                prefetchDistance = 5,
                enablePlaceholders = false,
            ),
            remoteMediator = InheritorRemoteMediator(
                query = query,
                database = database,
                apiClient = apiClient,
            ),
            pagingSourceFactory = {
                database.inheritorDao().pagingSource(query.queryKey())
            },
        ).flow.map { pagingData ->
            pagingData.map { entity -> entity.toDto() }
        }
}
