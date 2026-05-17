package com.duckylife.heritage.modern.core.data

import androidx.paging.PagingData
import com.duckylife.heritage.modern.core.network.ArticleQuery
import com.duckylife.heritage.modern.core.network.DirectoryItemQuery
import com.duckylife.heritage.modern.core.network.InheritorQuery
import com.duckylife.heritage.modern.core.network.dto.ArticleCategory
import com.duckylife.heritage.modern.core.network.dto.ArticleDetailDto
import com.duckylife.heritage.modern.core.network.dto.ArticleSummaryDto
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemDetailDto
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemSummaryDto
import com.duckylife.heritage.modern.core.network.dto.HomeBannerDto
import com.duckylife.heritage.modern.core.network.dto.InheritorDetailDto
import com.duckylife.heritage.modern.core.network.dto.InheritorSummaryDto
import com.duckylife.heritage.modern.core.network.dto.PagedResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeHeritageRepository(
    private val banners: List<HomeBannerDto> = emptyList(),
    private val articles: List<ArticleSummaryDto> = emptyList(),
    private val directoryItems: List<DirectoryItemSummaryDto> = emptyList(),
    private val inheritors: List<InheritorSummaryDto> = emptyList(),
    private val articleDetails: Map<String, ArticleDetailDto> = emptyMap(),
    private val articleDetailsBySourceId: Map<String, ArticleDetailDto> = emptyMap(),
    private val articleDetailsBySourceUrl: Map<String, ArticleDetailDto> = emptyMap(),
    private val cachedArticleDetails: Map<ArticleDetailLookup, ArticleDetailDto?> = emptyMap(),
    private val directoryDetails: Map<String, DirectoryItemDetailDto> = emptyMap(),
    private val directoryDetailsBySourceId: Map<String, DirectoryItemDetailDto> = emptyMap(),
    private val cachedDirectoryDetails: Map<DirectoryDetailLookup, DirectoryItemDetailDto?> = emptyMap(),
    private val inheritorDetails: Map<String, InheritorDetailDto> = emptyMap(),
    private val inheritorDetailsBySourceId: Map<String, InheritorDetailDto> = emptyMap(),
    private val cachedInheritorDetails: Map<InheritorDetailLookup, InheritorDetailDto?> = emptyMap(),
    private val failure: Throwable? = null,
) : HeritageRepository {
    val pagedArticleQueries = mutableListOf<ArticleQuery>()
    val pagedDirectoryItemQueries = mutableListOf<DirectoryItemQuery>()
    val pagedInheritorQueries = mutableListOf<InheritorQuery>()
    val articleSourceIdQueries = mutableListOf<Pair<String, ArticleCategory>>()
    val articleSourceUrlQueries = mutableListOf<Pair<String, ArticleCategory>>()
    val directorySourceIdQueries = mutableListOf<Pair<String, DirectoryItemKind>>()
    val inheritorSourceIdQueries = mutableListOf<String>()

    override suspend fun homeBanners(): List<HomeBannerDto> {
        failure?.let { throw it }
        return banners
    }

    override suspend fun articles(query: ArticleQuery): PagedResult<ArticleSummaryDto> {
        failure?.let { throw it }
        return PagedResult(
            items = articles,
            page = query.page,
            pageSize = query.pageSize,
        )
    }

    override fun pagedArticles(query: ArticleQuery): Flow<PagingData<ArticleSummaryDto>> {
        pagedArticleQueries.add(query)
        return flowOf(PagingData.from(articles))
    }

    override suspend fun article(id: String): ArticleDetailDto {
        failure?.let { throw it }
        return articleDetails[id] ?: error("Missing article detail for $id")
    }

    override suspend fun articleBySourceId(
        sourceId: String,
        category: ArticleCategory,
    ): ArticleDetailDto {
        failure?.let { throw it }
        articleSourceIdQueries.add(sourceId to category)
        return articleDetailsBySourceId[sourceId] ?: error("Missing article detail for source id $sourceId")
    }

    override suspend fun articleBySourceUrl(
        sourceUrl: String,
        category: ArticleCategory,
    ): ArticleDetailDto {
        failure?.let { throw it }
        articleSourceUrlQueries.add(sourceUrl to category)
        return articleDetailsBySourceUrl[sourceUrl] ?: error("Missing article detail for source url $sourceUrl")
    }

    override fun cachedArticleDetail(lookup: ArticleDetailLookup): Flow<ArticleDetailDto?> =
        flowOf(cachedArticleDetails[lookup])

    override suspend fun refreshArticleDetail(lookup: ArticleDetailLookup): ArticleDetailDto =
        when {
            !lookup.articleId.isNullOrBlank() -> article(lookup.articleId)
            !lookup.sourceId.isNullOrBlank() -> articleBySourceId(lookup.sourceId, lookup.category)
            !lookup.sourceUrl.isNullOrBlank() -> articleBySourceUrl(lookup.sourceUrl, lookup.category)
            else -> error("Missing article lookup key")
        }

    override suspend fun directoryItems(
        query: DirectoryItemQuery,
    ): PagedResult<DirectoryItemSummaryDto> {
        failure?.let { throw it }
        return PagedResult(
            items = directoryItems,
            page = query.page,
            pageSize = query.pageSize,
        )
    }

    override fun pagedDirectoryItems(query: DirectoryItemQuery): Flow<PagingData<DirectoryItemSummaryDto>> {
        pagedDirectoryItemQueries.add(query)
        return flowOf(PagingData.from(directoryItems))
    }

    override suspend fun directoryItem(id: String): DirectoryItemDetailDto {
        failure?.let { throw it }
        return directoryDetails[id] ?: error("Missing directory detail for $id")
    }

    override suspend fun directoryItemBySourceId(
        sourceId: String,
        kind: DirectoryItemKind,
    ): DirectoryItemDetailDto {
        failure?.let { throw it }
        directorySourceIdQueries.add(sourceId to kind)
        return directoryDetailsBySourceId[sourceId] ?: error("Missing directory detail for source id $sourceId")
    }

    override fun cachedDirectoryDetail(lookup: DirectoryDetailLookup): Flow<DirectoryItemDetailDto?> =
        flowOf(cachedDirectoryDetails[lookup])

    override suspend fun refreshDirectoryDetail(lookup: DirectoryDetailLookup): DirectoryItemDetailDto =
        when {
            !lookup.itemId.isNullOrBlank() -> directoryItem(lookup.itemId)
            !lookup.sourceId.isNullOrBlank() -> directoryItemBySourceId(lookup.sourceId, lookup.kind)
            else -> error("Missing directory lookup key")
        }

    override suspend fun inheritors(query: InheritorQuery): PagedResult<InheritorSummaryDto> {
        failure?.let { throw it }
        return PagedResult(
            items = inheritors,
            page = query.page,
            pageSize = query.pageSize,
        )
    }

    override fun pagedInheritors(query: InheritorQuery): Flow<PagingData<InheritorSummaryDto>> {
        pagedInheritorQueries.add(query)
        return flowOf(PagingData.from(inheritors))
    }

    override suspend fun inheritor(id: String): InheritorDetailDto {
        failure?.let { throw it }
        return inheritorDetails[id] ?: error("Missing inheritor detail for $id")
    }

    override suspend fun inheritorBySourceId(sourceId: String): InheritorDetailDto {
        failure?.let { throw it }
        inheritorSourceIdQueries.add(sourceId)
        return inheritorDetailsBySourceId[sourceId] ?: error("Missing inheritor detail for source id $sourceId")
    }

    override fun cachedInheritorDetail(lookup: InheritorDetailLookup): Flow<InheritorDetailDto?> =
        flowOf(cachedInheritorDetails[lookup])

    override suspend fun refreshInheritorDetail(lookup: InheritorDetailLookup): InheritorDetailDto =
        when {
            !lookup.inheritorId.isNullOrBlank() -> inheritor(lookup.inheritorId)
            !lookup.sourceId.isNullOrBlank() -> inheritorBySourceId(lookup.sourceId)
            else -> error("Missing inheritor lookup key")
        }
}
