package com.duckylife.heritage.modern.core.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.duckylife.heritage.modern.core.database.HeritageDatabase
import com.duckylife.heritage.modern.core.database.mapper.queryKey
import com.duckylife.heritage.modern.core.database.mapper.toDto
import com.duckylife.heritage.modern.core.network.DirectoryItemQuery
import com.duckylife.heritage.modern.core.network.HeritageApiClient
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemSummaryDto
import com.duckylife.heritage.modern.core.paging.DirectoryRemoteMediator
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalPagingApi::class)
@Singleton
class DirectoryPagingRepository @Inject constructor(
    private val database: HeritageDatabase,
    private val apiClient: HeritageApiClient,
) {
    fun pagedDirectoryItems(query: DirectoryItemQuery): Flow<PagingData<DirectoryItemSummaryDto>> =
        Pager(
            config = PagingConfig(
                pageSize = query.pageSize,
                initialLoadSize = query.pageSize,
                prefetchDistance = 5,
                enablePlaceholders = false,
            ),
            remoteMediator = DirectoryRemoteMediator(
                query = query,
                database = database,
                apiClient = apiClient,
            ),
            pagingSourceFactory = {
                database.directoryItemDao().pagingSource(query.queryKey())
            },
        ).flow.map { pagingData ->
            pagingData.map { itemEntity -> itemEntity.toDto() }
        }
}
