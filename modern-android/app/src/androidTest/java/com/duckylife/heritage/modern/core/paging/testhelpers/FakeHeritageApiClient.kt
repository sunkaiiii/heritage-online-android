package com.duckylife.heritage.modern.core.paging.testhelpers

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

// 可注入失败、可记录请求的假 API 客户端，供 RemoteMediator 测试共用。
class FakeHeritageApiClient : HeritageApiClient {
    var articlesResult: PagedResult<ArticleSummaryDto> = PagedResult()
    var directoryItemsResult: PagedResult<DirectoryItemSummaryDto> = PagedResult()
    var inheritorsResult: PagedResult<InheritorSummaryDto> = PagedResult()
    val articleRequests = mutableListOf<ArticleQuery>()
    val directoryItemRequests = mutableListOf<DirectoryItemQuery>()
    val inheritorRequests = mutableListOf<InheritorQuery>()
    var failure: Throwable? = null

    override suspend fun getHomeBanners(): List<HomeBannerDto> = emptyList()

    override suspend fun getArticles(query: ArticleQuery): PagedResult<ArticleSummaryDto> {
        failure?.let { throw it }
        articleRequests.add(query)
        return articlesResult
    }

    override suspend fun getArticle(id: String) = ArticleDetailDto()
    override suspend fun getArticleBySourceId(sourceId: String, category: ArticleCategory) = ArticleDetailDto()
    override suspend fun getArticleBySourceUrl(sourceUrl: String, category: ArticleCategory) = ArticleDetailDto()

    override suspend fun getDirectoryItems(query: DirectoryItemQuery): PagedResult<DirectoryItemSummaryDto> {
        failure?.let { throw it }
        directoryItemRequests.add(query)
        return directoryItemsResult
    }

    override suspend fun getDirectoryItem(id: String) = DirectoryItemDetailDto()
    override suspend fun getDirectoryItemBySourceId(sourceId: String, kind: DirectoryItemKind) = DirectoryItemDetailDto()

    override suspend fun getInheritors(query: InheritorQuery): PagedResult<InheritorSummaryDto> {
        failure?.let { throw it }
        inheritorRequests.add(query)
        return inheritorsResult
    }

    override suspend fun getInheritor(id: String) = InheritorDetailDto()
    override suspend fun getInheritorBySourceId(sourceId: String) = InheritorDetailDto()
}
