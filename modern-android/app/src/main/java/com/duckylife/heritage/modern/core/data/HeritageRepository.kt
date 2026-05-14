package com.duckylife.heritage.modern.core.data

import androidx.paging.PagingData
import com.duckylife.heritage.modern.core.network.ArticleQuery
import com.duckylife.heritage.modern.core.network.DirectoryItemQuery
import com.duckylife.heritage.modern.core.network.HeritageApiClient
import com.duckylife.heritage.modern.core.network.InheritorQuery
import com.duckylife.heritage.modern.core.network.dto.ArticleDetailDto
import com.duckylife.heritage.modern.core.network.dto.ArticleSummaryDto
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemDetailDto
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemSummaryDto
import com.duckylife.heritage.modern.core.network.dto.HomeBannerDto
import com.duckylife.heritage.modern.core.network.dto.InheritorDetailDto
import com.duckylife.heritage.modern.core.network.dto.InheritorSummaryDto
import com.duckylife.heritage.modern.core.network.dto.PagedResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface HeritageRepository {
    suspend fun homeBanners(): List<HomeBannerDto>

    suspend fun articles(query: ArticleQuery = ArticleQuery()): PagedResult<ArticleSummaryDto>

    fun pagedArticles(query: ArticleQuery = ArticleQuery()): Flow<PagingData<ArticleSummaryDto>>

    suspend fun article(id: String): ArticleDetailDto

    suspend fun directoryItems(
        query: DirectoryItemQuery = DirectoryItemQuery(),
    ): PagedResult<DirectoryItemSummaryDto>

    suspend fun directoryItem(id: String): DirectoryItemDetailDto

    suspend fun inheritors(query: InheritorQuery = InheritorQuery()): PagedResult<InheritorSummaryDto>

    suspend fun inheritor(id: String): InheritorDetailDto
}

class DefaultHeritageRepository @Inject constructor(
    private val articlePagingRepository: ArticlePagingRepository,
    private val apiClient: HeritageApiClient,
) : HeritageRepository {
    override suspend fun homeBanners(): List<HomeBannerDto> =
        apiClient.getHomeBanners()

    override suspend fun articles(query: ArticleQuery): PagedResult<ArticleSummaryDto> =
        apiClient.getArticles(query)

    override fun pagedArticles(query: ArticleQuery): Flow<PagingData<ArticleSummaryDto>> =
        articlePagingRepository.pagedArticles(query)

    override suspend fun article(id: String): ArticleDetailDto =
        apiClient.getArticle(id)

    override suspend fun directoryItems(
        query: DirectoryItemQuery,
    ): PagedResult<DirectoryItemSummaryDto> =
        apiClient.getDirectoryItems(query)

    override suspend fun directoryItem(id: String): DirectoryItemDetailDto =
        apiClient.getDirectoryItem(id)

    override suspend fun inheritors(query: InheritorQuery): PagedResult<InheritorSummaryDto> =
        apiClient.getInheritors(query)

    override suspend fun inheritor(id: String): InheritorDetailDto =
        apiClient.getInheritor(id)
}
