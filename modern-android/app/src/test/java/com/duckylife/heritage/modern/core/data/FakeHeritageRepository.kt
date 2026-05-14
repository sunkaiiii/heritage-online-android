package com.duckylife.heritage.modern.core.data

import com.duckylife.heritage.modern.core.network.ArticleQuery
import com.duckylife.heritage.modern.core.network.DirectoryItemQuery
import com.duckylife.heritage.modern.core.network.InheritorQuery
import com.duckylife.heritage.modern.core.network.dto.ArticleDetailDto
import com.duckylife.heritage.modern.core.network.dto.ArticleSummaryDto
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemDetailDto
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemSummaryDto
import com.duckylife.heritage.modern.core.network.dto.HomeBannerDto
import com.duckylife.heritage.modern.core.network.dto.InheritorDetailDto
import com.duckylife.heritage.modern.core.network.dto.InheritorSummaryDto
import com.duckylife.heritage.modern.core.network.dto.PagedResult

class FakeHeritageRepository(
    private val banners: List<HomeBannerDto> = emptyList(),
    private val articles: List<ArticleSummaryDto> = emptyList(),
    private val articleDetails: Map<String, ArticleDetailDto> = emptyMap(),
    private val failure: Throwable? = null,
) : HeritageRepository {
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

    override suspend fun article(id: String): ArticleDetailDto {
        failure?.let { throw it }
        return articleDetails[id] ?: error("Missing article detail for $id")
    }

    override suspend fun directoryItems(
        query: DirectoryItemQuery,
    ): PagedResult<DirectoryItemSummaryDto> =
        throw UnsupportedOperationException()

    override suspend fun directoryItem(id: String): DirectoryItemDetailDto =
        throw UnsupportedOperationException()

    override suspend fun inheritors(query: InheritorQuery): PagedResult<InheritorSummaryDto> =
        throw UnsupportedOperationException()

    override suspend fun inheritor(id: String): InheritorDetailDto =
        throw UnsupportedOperationException()
}
