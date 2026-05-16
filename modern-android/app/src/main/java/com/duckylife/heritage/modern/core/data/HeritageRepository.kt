package com.duckylife.heritage.modern.core.data

import androidx.paging.PagingData
import com.duckylife.heritage.modern.core.database.HeritageDatabase
import com.duckylife.heritage.modern.core.database.mapper.toDto
import com.duckylife.heritage.modern.core.database.mapper.toEntity
import com.duckylife.heritage.modern.core.network.ArticleQuery
import com.duckylife.heritage.modern.core.network.DirectoryItemQuery
import com.duckylife.heritage.modern.core.network.HeritageApiClient
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
import kotlinx.coroutines.flow.map
import javax.inject.Inject

data class ArticleDetailLookup(
    val articleId: String? = null,
    val sourceId: String? = null,
    val sourceUrl: String? = null,
    val category: ArticleCategory = ArticleCategory.News,
)

data class DirectoryDetailLookup(
    val itemId: String? = null,
    val sourceId: String? = null,
    val kind: DirectoryItemKind = DirectoryItemKind.NationalProject,
)

interface HeritageRepository {
    suspend fun homeBanners(): List<HomeBannerDto>

    suspend fun articles(query: ArticleQuery = ArticleQuery()): PagedResult<ArticleSummaryDto>

    fun pagedArticles(query: ArticleQuery = ArticleQuery()): Flow<PagingData<ArticleSummaryDto>>

    suspend fun article(id: String): ArticleDetailDto

    suspend fun articleBySourceId(sourceId: String, category: ArticleCategory): ArticleDetailDto

    suspend fun articleBySourceUrl(sourceUrl: String, category: ArticleCategory): ArticleDetailDto

    fun cachedArticleDetail(lookup: ArticleDetailLookup): Flow<ArticleDetailDto?>

    suspend fun refreshArticleDetail(lookup: ArticleDetailLookup): ArticleDetailDto

    suspend fun directoryItems(
        query: DirectoryItemQuery = DirectoryItemQuery(),
    ): PagedResult<DirectoryItemSummaryDto>

    fun pagedDirectoryItems(query: DirectoryItemQuery = DirectoryItemQuery()): Flow<PagingData<DirectoryItemSummaryDto>>

    suspend fun directoryItem(id: String): DirectoryItemDetailDto

    suspend fun directoryItemBySourceId(sourceId: String, kind: DirectoryItemKind): DirectoryItemDetailDto

    fun cachedDirectoryDetail(lookup: DirectoryDetailLookup): Flow<DirectoryItemDetailDto?>

    suspend fun refreshDirectoryDetail(lookup: DirectoryDetailLookup): DirectoryItemDetailDto

    suspend fun inheritors(query: InheritorQuery = InheritorQuery()): PagedResult<InheritorSummaryDto>

    suspend fun inheritor(id: String): InheritorDetailDto
}

class DefaultHeritageRepository @Inject constructor(
    private val articlePagingRepository: ArticlePagingRepository,
    private val directoryPagingRepository: DirectoryPagingRepository,
    private val apiClient: HeritageApiClient,
    private val database: HeritageDatabase,
) : HeritageRepository {
    override suspend fun homeBanners(): List<HomeBannerDto> =
        apiClient.getHomeBanners()

    override suspend fun articles(query: ArticleQuery): PagedResult<ArticleSummaryDto> =
        apiClient.getArticles(query)

    override fun pagedArticles(query: ArticleQuery): Flow<PagingData<ArticleSummaryDto>> =
        articlePagingRepository.pagedArticles(query)

    override suspend fun article(id: String): ArticleDetailDto =
        refreshArticleDetail(ArticleDetailLookup(articleId = id))

    override suspend fun articleBySourceId(sourceId: String, category: ArticleCategory): ArticleDetailDto =
        refreshArticleDetail(
            ArticleDetailLookup(
                sourceId = sourceId,
                category = category,
            ),
        )

    override suspend fun articleBySourceUrl(sourceUrl: String, category: ArticleCategory): ArticleDetailDto =
        refreshArticleDetail(
            ArticleDetailLookup(
                sourceUrl = sourceUrl,
                category = category,
            ),
        )

    override fun cachedArticleDetail(lookup: ArticleDetailLookup): Flow<ArticleDetailDto?> {
        val detailDao = database.articleDetailDao()
        val cachedArticle = when {
            !lookup.articleId.isNullOrBlank() -> detailDao.observeById(lookup.articleId)
            !lookup.sourceId.isNullOrBlank() -> detailDao.observeBySourceId(
                sourceId = lookup.sourceId,
                category = lookup.category.wireName,
            )

            !lookup.sourceUrl.isNullOrBlank() -> detailDao.observeBySourceUrl(
                sourceUrl = lookup.sourceUrl,
                category = lookup.category.wireName,
            )

            else -> error("Missing article lookup key")
        }
        return cachedArticle.map { it?.toDto() }
    }

    override suspend fun refreshArticleDetail(lookup: ArticleDetailLookup): ArticleDetailDto {
        val article = when {
            !lookup.articleId.isNullOrBlank() -> apiClient.getArticle(lookup.articleId)
            !lookup.sourceId.isNullOrBlank() -> apiClient.getArticleBySourceId(
                sourceId = lookup.sourceId,
                category = lookup.category,
            )

            !lookup.sourceUrl.isNullOrBlank() -> apiClient.getArticleBySourceUrl(
                sourceUrl = lookup.sourceUrl,
                category = lookup.category,
            )

            else -> error("Missing article lookup key")
        }

        database.articleDetailDao().upsert(
            article.toEntity(
                category = article.category,
                sourceId = lookup.sourceId,
                sourceUrl = lookup.sourceUrl,
                updatedAtEpochMillis = System.currentTimeMillis(),
            ),
        )
        return article
    }

    override suspend fun directoryItems(
        query: DirectoryItemQuery,
    ): PagedResult<DirectoryItemSummaryDto> =
        apiClient.getDirectoryItems(query)

    override fun pagedDirectoryItems(query: DirectoryItemQuery): Flow<PagingData<DirectoryItemSummaryDto>> =
        directoryPagingRepository.pagedDirectoryItems(query)

    override suspend fun directoryItem(id: String): DirectoryItemDetailDto =
        refreshDirectoryDetail(DirectoryDetailLookup(itemId = id))

    override suspend fun directoryItemBySourceId(
        sourceId: String,
        kind: DirectoryItemKind,
    ): DirectoryItemDetailDto =
        refreshDirectoryDetail(
            DirectoryDetailLookup(
                sourceId = sourceId,
                kind = kind,
            ),
        )

    override fun cachedDirectoryDetail(lookup: DirectoryDetailLookup): Flow<DirectoryItemDetailDto?> {
        val detailDao = database.directoryDetailDao()
        val cachedDirectory = when {
            !lookup.itemId.isNullOrBlank() -> detailDao.observeById(lookup.itemId)
            !lookup.sourceId.isNullOrBlank() -> detailDao.observeBySourceId(
                sourceId = lookup.sourceId,
                kind = lookup.kind.wireName,
            )

            else -> error("Missing directory lookup key")
        }
        return cachedDirectory.map { it?.toDto() }
    }

    override suspend fun refreshDirectoryDetail(lookup: DirectoryDetailLookup): DirectoryItemDetailDto {
        val detail = when {
            !lookup.itemId.isNullOrBlank() -> apiClient.getDirectoryItem(lookup.itemId)
            !lookup.sourceId.isNullOrBlank() -> apiClient.getDirectoryItemBySourceId(
                sourceId = lookup.sourceId,
                kind = lookup.kind,
            )

            else -> error("Missing directory lookup key")
        }

        database.directoryDetailDao().upsert(
            detail.toEntity(
                kind = detail.kind,
                sourceId = lookup.sourceId,
                updatedAtEpochMillis = System.currentTimeMillis(),
            ),
        )
        return detail
    }

    override suspend fun inheritors(query: InheritorQuery): PagedResult<InheritorSummaryDto> =
        apiClient.getInheritors(query)

    override suspend fun inheritor(id: String): InheritorDetailDto =
        apiClient.getInheritor(id)
}
