package com.duckylife.heritage.modern.core.testing.fake

import androidx.paging.PagingData
import com.duckylife.heritage.modern.core.data.ArticleDetailLookup
import com.duckylife.heritage.modern.core.data.DirectoryDetailLookup
import com.duckylife.heritage.modern.core.data.HeritageRepository
import com.duckylife.heritage.modern.core.data.InheritorDetailLookup
import com.duckylife.heritage.modern.core.network.ArticleQuery
import com.duckylife.heritage.modern.core.network.DirectoryItemQuery
import com.duckylife.heritage.modern.core.network.InheritorQuery
import com.duckylife.heritage.modern.core.network.SearchV2Query
import com.duckylife.heritage.modern.core.network.TimelineV2Query
import com.duckylife.heritage.modern.core.network.dto.ArticleCategory
import com.duckylife.heritage.modern.core.network.dto.ArticleDetailDto
import com.duckylife.heritage.modern.core.network.dto.ArticleReferenceDto
import com.duckylife.heritage.modern.core.network.dto.ArticleSummaryDto
import com.duckylife.heritage.modern.core.network.dto.CollectionDto
import com.duckylife.heritage.modern.core.network.dto.DetailContextDto
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemDetailDto
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemSummaryDto
import com.duckylife.heritage.modern.core.network.dto.DirectoryReferenceDto
import com.duckylife.heritage.modern.core.network.dto.DirectoryStatisticDimension
import com.duckylife.heritage.modern.core.network.dto.DirectoryStatisticDimensionDto
import com.duckylife.heritage.modern.core.network.dto.DirectoryStatisticItemDto
import com.duckylife.heritage.modern.core.network.dto.DirectoryStatisticsOverviewDto
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class TestFakeRepository : HeritageRepository {
    val pagedArticleQueries = mutableListOf<ArticleQuery>()

    override suspend fun homeBanners(): List<HomeBannerDto> = listOf(
        HomeBannerDto(id = "b1", sortOrder = 1, targetUrl = "https://example.test/1"),
        HomeBannerDto(id = "b2", sortOrder = 2),
    )

    override fun cachedHomeBanners(): Flow<List<HomeBannerDto>> = flowOf(
        listOf(
            HomeBannerDto(id = "b1", sortOrder = 1),
            HomeBannerDto(id = "b2", sortOrder = 2),
        ),
    )

    override suspend fun refreshHomeBanners(): List<HomeBannerDto> = homeBanners()

    override suspend fun homeFeed(): HomeFeedDto = HomeFeedDto()

    override suspend fun articles(query: ArticleQuery): PagedResult<ArticleSummaryDto> {
        pagedArticleQueries.add(query)
        return PagedResult(
            items = listOf(
                ArticleSummaryDto(id = "a1", category = ArticleCategory.News, title = TestArticleTitle, summary = "摘要"),
            ),
            hasMore = false,
            total = 1,
        )
    }

    override fun pagedArticles(query: ArticleQuery): Flow<PagingData<ArticleSummaryDto>> {
        pagedArticleQueries.add(query)
        return flowOf(PagingData.from(listOf(
            ArticleSummaryDto(id = "a1", category = ArticleCategory.News, title = TestArticleTitle, summary = "摘要"),
        )))
    }

    override suspend fun article(id: String) = ArticleDetailDto(
        id = "a1",
        title = TestArticleDetailTitle,
        summary = "正文摘要",
        relatedArticles = listOf(
            ArticleReferenceDto(title = "关联文章", sourceId = "ra1", detailUrl = "/articles/ra1"),
        ),
    )

    override suspend fun articleBySourceId(sourceId: String, category: ArticleCategory) =
        ArticleDetailDto(id = if (sourceId == "ra1") "ra1" else "a1", title = "关联文章详情", category = category)

    override suspend fun articleBySourceUrl(sourceUrl: String, category: ArticleCategory) =
        ArticleDetailDto(id = "a1", title = TestArticleTitle)

    override fun cachedArticleDetail(lookup: ArticleDetailLookup): Flow<ArticleDetailDto?> =
        flowOf(null)

    override suspend fun refreshArticleDetail(lookup: ArticleDetailLookup) =
        article(lookup.articleId ?: "a1")

    override suspend fun articleContext(id: String) = DetailContextDto()

    override suspend fun directoryItems(query: DirectoryItemQuery): PagedResult<DirectoryItemSummaryDto> =
        PagedResult(items = listOf(
            DirectoryItemSummaryDto(id = "d1", kind = DirectoryItemKind.NationalProject, title = TestDirectoryTitle, summary = "名录摘要"),
        ), hasMore = false, total = 1)

    override fun pagedDirectoryItems(query: DirectoryItemQuery): Flow<PagingData<DirectoryItemSummaryDto>> =
        flowOf(PagingData.from(listOf(
            DirectoryItemSummaryDto(id = "d1", kind = DirectoryItemKind.NationalProject, title = TestDirectoryTitle, summary = "名录摘要"),
        )))

    override suspend fun directoryStatisticsOverview(kind: DirectoryItemKind) =
        DirectoryStatisticsOverviewDto(
            kind = kind.wireName,
            total = 1,
            dimensions = listOf(
                DirectoryStatisticDimensionDto(
                    dimension = "region",
                    items = listOf(DirectoryStatisticItemDto(key = "北京", name = "北京", value = 1)),
                ),
            ),
        )

    override suspend fun directoryStatisticsBreakdown(
        kind: DirectoryItemKind,
        dimension: DirectoryStatisticDimension,
        limit: Int,
    ) = DirectoryStatisticDimensionDto(
        dimension = dimension.wireName,
        items = listOf(DirectoryStatisticItemDto(key = "北京", name = "北京", value = 1)),
    )

    override suspend fun directoryItem(id: String) = DirectoryItemDetailDto(
        id = "d1",
        title = TestDirectoryDetailTitle,
        kind = DirectoryItemKind.NationalProject,
        relatedProjects = listOf(
            DirectoryReferenceDto(title = "关联名录项目", sourceId = "rd1"),
        ),
        relatedInheritors = listOf(
            DirectoryReferenceDto(title = "关联传承人", sourceId = "ri1", kind = "contractingState"),
        ),
    )

    override suspend fun directoryItemBySourceId(sourceId: String, kind: DirectoryItemKind) =
        if (sourceId == "ri1") DirectoryItemDetailDto(id = "ri1", title = "名录关联传承人", kind = kind)
        else DirectoryItemDetailDto(id = "rd1", title = "关联名录详情", kind = kind)

    override fun cachedDirectoryDetail(lookup: DirectoryDetailLookup): Flow<DirectoryItemDetailDto?> = flowOf(null)

    override suspend fun refreshDirectoryDetail(lookup: DirectoryDetailLookup) =
        directoryItem(lookup.itemId ?: "d1")

    override suspend fun directoryItemContext(id: String) = DetailContextDto()

    override suspend fun inheritors(query: InheritorQuery): PagedResult<InheritorSummaryDto> =
        PagedResult(items = listOf(
            InheritorSummaryDto(id = "i1", name = TestInheritorName, category = "传统美术"),
        ), hasMore = false, total = 1)

    override fun pagedInheritors(query: InheritorQuery): Flow<PagingData<InheritorSummaryDto>> =
        flowOf(PagingData.from(listOf(
            InheritorSummaryDto(id = "i1", name = TestInheritorName, category = "传统美术"),
        )))

    override suspend fun inheritor(id: String) = InheritorDetailDto(
        id = "i1",
        name = TestInheritorDetailName,
        relatedProjects = listOf(
            DirectoryReferenceDto(title = "中医诊疗法", sourceId = "rp1", kind = "nationalProject"),
        ),
    )

    override suspend fun inheritorBySourceId(sourceId: String) =
        InheritorDetailDto(id = "i1", name = TestInheritorName)

    override fun cachedInheritorDetail(lookup: InheritorDetailLookup): Flow<InheritorDetailDto?> = flowOf(null)

    override suspend fun refreshInheritorDetail(lookup: InheritorDetailLookup) =
        inheritor(lookup.inheritorId ?: "i1")

    override suspend fun inheritorContext(id: String) = DetailContextDto()

    override suspend fun searchV2(query: SearchV2Query) = SearchV2ResponseDto()
    override suspend fun searchSuggestions(prefix: String, limit: Int): List<SearchSuggestionDto> = emptyList()
    override suspend fun timelineV2(query: TimelineV2Query) = TimelineV2ResponseDto()
    override suspend fun timelineYears(): List<TimelineYearBucketDto> = emptyList()
    override suspend fun exploreIndex() = ExploreIndexDto()
    override suspend fun exploreTopics(type: String?, limit: Int): List<ExploreTopicInfoDto> = emptyList()
    override suspend fun exploreTopic(type: String, key: String, limit: Int) = ExploreTopicV2Dto()
    override suspend fun learningPaths(): List<LearningPathDto> = emptyList()
    override suspend fun learningPathDetail(id: String, limit: Int) = LearningPathDetailDto()
    override suspend fun regionAtlas() = RegionAtlasDto()
    override suspend fun regionAtlasDetail(region: String, limit: Int) = RegionAtlasDetailDto()
    override suspend fun featuredCollections(): List<FeaturedCollectionDto> = emptyList()
    override suspend fun collection(id: String, limit: Int) = CollectionDto()
    override suspend fun topicCollection(type: String, key: String, limit: Int) = CollectionDto()

    companion object {
        const val TestArticleTitle = "测试新闻"
        const val TestArticleDetailTitle = "测试新闻详情"
        const val TestDirectoryTitle = "测试名录项目"
        const val TestDirectoryDetailTitle = "测试名录详情"
        const val TestInheritorName = "测试传承人"
        const val TestInheritorDetailName = "测试传承人详情"
    }
}
