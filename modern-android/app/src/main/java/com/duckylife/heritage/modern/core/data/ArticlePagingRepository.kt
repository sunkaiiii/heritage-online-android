package com.duckylife.heritage.modern.core.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.duckylife.heritage.modern.core.database.HeritageDatabase
import com.duckylife.heritage.modern.core.database.mapper.queryKey
import com.duckylife.heritage.modern.core.database.mapper.toDto
import com.duckylife.heritage.modern.core.network.ArticleQuery
import com.duckylife.heritage.modern.core.network.HeritageApiClient
import com.duckylife.heritage.modern.core.network.dto.ArticleSummaryDto
import com.duckylife.heritage.modern.core.paging.ArticleRemoteMediator
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalPagingApi::class)
@Singleton
class ArticlePagingRepository @Inject constructor(
    private val database: HeritageDatabase,
    private val apiClient: HeritageApiClient,
) {
    fun pagedArticles(query: ArticleQuery): Flow<PagingData<ArticleSummaryDto>> =
        Pager(
            config = PagingConfig(
                pageSize = query.pageSize,
                initialLoadSize = query.pageSize,
                prefetchDistance = 5,
                enablePlaceholders = false,
            ),
            remoteMediator = ArticleRemoteMediator(
                query = query,
                database = database,
                apiClient = apiClient,
            ),
            pagingSourceFactory = {
                database.articleDao().pagingSource(query.queryKey())
            },
        ).flow.map { pagingData ->
            pagingData.map { articleEntity -> articleEntity.toDto() }
        }
}
