package com.duckylife.heritage.modern.core.paging.testhelpers

import com.duckylife.heritage.modern.core.network.ArticleQuery
import com.duckylife.heritage.modern.core.network.DirectoryItemQuery
import com.duckylife.heritage.modern.core.network.HeritageApiClient
import com.duckylife.heritage.modern.core.network.InheritorQuery
import com.duckylife.heritage.modern.core.network.SearchV2Query
import com.duckylife.heritage.modern.core.network.TimelineV2Query
import com.duckylife.heritage.modern.core.network.dto.ArticleCategory
import com.duckylife.heritage.modern.core.network.dto.ArticleDetailDto
import com.duckylife.heritage.modern.core.network.dto.ArticleSummaryDto
import com.duckylife.heritage.modern.core.network.dto.CollectionDto
import com.duckylife.heritage.modern.core.network.dto.DetailContextDto
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemDetailDto
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemSummaryDto
import com.duckylife.heritage.modern.core.network.dto.ExploreIndexDto
import com.duckylife.heritage.modern.core.network.dto.ExploreTopicInfoDto
import com.duckylife.heritage.modern.core.network.dto.ExploreTopicV2Dto
import com.duckylife.heritage.modern.core.network.dto.FeaturedCollectionDto
import com.duckylife.heritage.modern.core.network.dto.HomeBannerDto
import com.duckylife.heritage.modern.core.network.dto.HomeFeedDto
import com.duckylife.heritage.modern.core.network.dto.InheritorDetailDto
import com.duckylife.heritage.modern.core.network.dto.InheritorSummaryDto
import com.duckylife.heritage.modern.core.network.dto.LearningPathDetailDto
import com.duckylife.heritage.modern.core.network.dto.LearningPathDto
import com.duckylife.heritage.modern.core.network.dto.PagedResult
import com.duckylife.heritage.modern.core.network.dto.RegionAtlasDetailDto
import com.duckylife.heritage.modern.core.network.dto.RegionAtlasDto
import com.duckylife.heritage.modern.core.network.dto.SearchSuggestionDto
import com.duckylife.heritage.modern.core.network.dto.SearchV2ResponseDto
import com.duckylife.heritage.modern.core.network.dto.TimelineV2ResponseDto
import com.duckylife.heritage.modern.core.network.dto.TimelineYearBucketDto

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

    override suspend fun getHomeFeed(): HomeFeedDto = HomeFeedDto()

    override suspend fun getArticles(query: ArticleQuery): PagedResult<ArticleSummaryDto> {
        failure?.let { throw it }
        articleRequests.add(query)
        return articlesResult
    }

    override suspend fun getArticle(id: String) = ArticleDetailDto()
    override suspend fun getArticleBySourceId(sourceId: String, category: ArticleCategory) = ArticleDetailDto()
    override suspend fun getArticleBySourceUrl(sourceUrl: String, category: ArticleCategory) = ArticleDetailDto()
    override suspend fun getArticleContext(id: String) = DetailContextDto()

    override suspend fun getDirectoryItems(query: DirectoryItemQuery): PagedResult<DirectoryItemSummaryDto> {
        failure?.let { throw it }
        directoryItemRequests.add(query)
        return directoryItemsResult
    }

    override suspend fun getDirectoryItem(id: String) = DirectoryItemDetailDto()
    override suspend fun getDirectoryItemBySourceId(sourceId: String, kind: DirectoryItemKind) = DirectoryItemDetailDto()
    override suspend fun getDirectoryItemContext(id: String) = DetailContextDto()

    override suspend fun getInheritors(query: InheritorQuery): PagedResult<InheritorSummaryDto> {
        failure?.let { throw it }
        inheritorRequests.add(query)
        return inheritorsResult
    }

    override suspend fun getInheritor(id: String) = InheritorDetailDto()
    override suspend fun getInheritorBySourceId(sourceId: String) = InheritorDetailDto()
    override suspend fun getInheritorContext(id: String) = DetailContextDto()

    override suspend fun searchV2(query: SearchV2Query) = SearchV2ResponseDto()
    override suspend fun getSearchSuggestions(prefix: String, limit: Int): List<SearchSuggestionDto> = emptyList()
    override suspend fun getTimelineV2(query: TimelineV2Query) = TimelineV2ResponseDto()
    override suspend fun getTimelineYears(): List<TimelineYearBucketDto> = emptyList()
    override suspend fun getExploreIndex() = ExploreIndexDto()
    override suspend fun getExploreTopics(type: String?, limit: Int): List<ExploreTopicInfoDto> = emptyList()
    override suspend fun getExploreTopic(type: String, key: String, limit: Int) = ExploreTopicV2Dto()
    override suspend fun getLearningPaths(): List<LearningPathDto> = emptyList()
    override suspend fun getLearningPathDetail(id: String, limit: Int) = LearningPathDetailDto()
    override suspend fun getRegionAtlas() = RegionAtlasDto()
    override suspend fun getRegionAtlasDetail(region: String, limit: Int) = RegionAtlasDetailDto()
    override suspend fun getFeaturedCollections(): List<FeaturedCollectionDto> = emptyList()
    override suspend fun getCollection(id: String, limit: Int) = CollectionDto()
    override suspend fun getTopicCollection(type: String, key: String, limit: Int) = CollectionDto()
}
