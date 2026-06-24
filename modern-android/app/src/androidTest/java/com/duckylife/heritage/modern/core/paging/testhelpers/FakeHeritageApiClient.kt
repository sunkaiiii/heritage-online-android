package com.duckylife.heritage.modern.core.paging.testhelpers

import com.duckylife.heritage.modern.core.network.ArticleQuery
import com.duckylife.heritage.modern.core.network.BlendedRecommendationQuery
import com.duckylife.heritage.modern.core.network.DirectoryItemQuery
import com.duckylife.heritage.modern.core.network.DiscoveryDeepDiveQuery
import com.duckylife.heritage.modern.core.network.DiscoverySerendipityQuery
import com.duckylife.heritage.modern.core.network.HeritageApiClient
import com.duckylife.heritage.modern.core.network.InheritorQuery
import com.duckylife.heritage.modern.core.network.*
import com.duckylife.heritage.modern.core.network.LocalUserFavoritesQuery
import com.duckylife.heritage.modern.core.network.LocalUserHistoryQuery
import com.duckylife.heritage.modern.core.network.SearchV2Query
import com.duckylife.heritage.modern.core.network.TaxonomyRegionSort
import com.duckylife.heritage.modern.core.network.TimelineV2Query
import com.duckylife.heritage.modern.core.network.dto.*
import com.duckylife.heritage.modern.core.network.dto.ArticleCategory
import com.duckylife.heritage.modern.core.network.dto.ArticleDetailDto
import com.duckylife.heritage.modern.core.network.dto.ArticleSummaryDto
import com.duckylife.heritage.modern.core.network.dto.BlendedRecommendationResponseDto
import com.duckylife.heritage.modern.core.network.dto.CollectionDto
import com.duckylife.heritage.modern.core.network.dto.CompareResultDto
import com.duckylife.heritage.modern.core.network.dto.ContentDigestDto
import com.duckylife.heritage.modern.core.network.dto.DataStoryDto
import com.duckylife.heritage.modern.core.network.dto.DetailContextDto
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemDetailDto
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemSummaryDto
import com.duckylife.heritage.modern.core.network.dto.DiscoveryDeepDiveDto
import com.duckylife.heritage.modern.core.network.dto.DiscoveryItemDto
import com.duckylife.heritage.modern.core.network.dto.DiscoveryTodayDto
import com.duckylife.heritage.modern.core.network.dto.DiscoveryTrendingDto
import com.duckylife.heritage.modern.core.network.dto.DiscoveryWeeklyDto
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
import com.duckylife.heritage.modern.core.network.dto.SearchResultType
import com.duckylife.heritage.modern.core.network.dto.SearchSuggestionDto
import com.duckylife.heritage.modern.core.network.dto.SearchV2ResponseDto
import com.duckylife.heritage.modern.core.network.dto.TaxonomyCategoryDetailDto
import com.duckylife.heritage.modern.core.network.dto.TaxonomyIndexDto
import com.duckylife.heritage.modern.core.network.dto.TaxonomyKindDto
import com.duckylife.heritage.modern.core.network.dto.TaxonomyRegionDetailDto
import com.duckylife.heritage.modern.core.network.dto.TaxonomyTopicDto
import com.duckylife.heritage.modern.core.network.dto.TimelineV2ResponseDto
import com.duckylife.heritage.modern.core.network.dto.TimelineYearBucketDto
import com.duckylife.heritage.modern.core.network.dto.advanced.FavoriteCreateRequestDto
import com.duckylife.heritage.modern.core.network.dto.advanced.HistoryRecordRequestDto
import com.duckylife.heritage.modern.core.network.dto.advanced.JourneyResponseDto
import com.duckylife.heritage.modern.core.network.dto.advanced.JourneySignalsDto
import com.duckylife.heritage.modern.core.network.dto.advanced.JourneyStrategy
import com.duckylife.heritage.modern.core.network.dto.advanced.LearningProgressUpdateDto
import com.duckylife.heritage.modern.core.network.dto.advanced.LocalFavoriteDto
import com.duckylife.heritage.modern.core.network.dto.advanced.LocalHistoryDto
import com.duckylife.heritage.modern.core.network.dto.advanced.LocalLearningProgressDto
import com.duckylife.heritage.modern.core.network.dto.advanced.LocalUserProfileDto
import com.duckylife.heritage.modern.core.network.dto.advanced.LocalUserSummaryDto
import com.duckylife.heritage.modern.core.network.dto.advanced.*

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

    // Discovery v2
    override suspend fun getDiscoveryToday() = DiscoveryTodayDto()
    override suspend fun getDiscoveryRandom(type: SearchResultType) = DiscoveryItemDto()
    override suspend fun getDiscoveryTrending(limit: Int) = DiscoveryTrendingDto()
    override suspend fun getDiscoveryWeekly() = DiscoveryWeeklyDto()
    override suspend fun getDiscoverySerendipity(query: DiscoverySerendipityQuery) = DiscoveryItemDto()
    override suspend fun getDiscoveryDeepDive(query: DiscoveryDeepDiveQuery) = DiscoveryDeepDiveDto()

    // Data Stories
    override suspend fun getRegionStory(region: String) = DataStoryDto()
    override suspend fun getCategoryStory(category: String) = DataStoryDto()
    override suspend fun getYearStory(year: Int) = DataStoryDto()

    // Taxonomy
    override suspend fun getTaxonomyCategories(limit: Int) = TaxonomyIndexDto<TaxonomyTopicDto>()
    override suspend fun getTaxonomyRegions(limit: Int, sort: TaxonomyRegionSort) =
        TaxonomyIndexDto<TaxonomyTopicDto>()
    override suspend fun getTaxonomyKinds() = TaxonomyIndexDto<TaxonomyKindDto>()
    override suspend fun getTaxonomyCategoryDetail(category: String, limit: Int) =
        TaxonomyCategoryDetailDto()
    override suspend fun getTaxonomyRegionDetail(region: String, limit: Int) =
        TaxonomyRegionDetailDto()

    // Compare
    override suspend fun compareRegions(left: String, right: String, limit: Int) = CompareResultDto()
    override suspend fun compareCategories(left: String, right: String, limit: Int) = CompareResultDto()
    override suspend fun compareKinds(
        left: DirectoryItemKind,
        right: DirectoryItemKind,
        limit: Int,
    ) = CompareResultDto()

    // Content Digest
    override suspend fun getArticleDigest(id: String) = ContentDigestDto()
    override suspend fun getDirectoryItemDigest(id: String) = ContentDigestDto()
    override suspend fun getInheritorDigest(id: String) = ContentDigestDto()

    // Blended Recommendations
    override suspend fun getBlendedRecommendations(
        query: BlendedRecommendationQuery,
    ) = BlendedRecommendationResponseDto()

    override suspend fun currentProfileId(): String = "android_test_profile"

    // Statistics
    override suspend fun getDirectoryStatisticsOverview(kind: DirectoryItemKind) = TODO()
    override suspend fun getDirectoryStatisticsBreakdown(kind: DirectoryItemKind, dimension: DirectoryStatisticDimension, limit: Int) = TODO()

    // Content Intelligence
    override suspend fun getV3ContentPage(query: V3ContentPageQuery) = TODO()
    override suspend fun getContentIntelligence(query: ContentIntelligenceQuery) = TODO()
    override suspend fun getArticleAiCard(id: String) = TODO()
    override suspend fun getDirectoryItemAiCard(id: String) = TODO()
    override suspend fun getInheritorAiCard(id: String) = TODO()

    // Knowledge Graph
    override suspend fun intelligentSearch(query: IntelligentSearchQuery) = TODO()
    override suspend fun getGraphNeighbors(query: KnowledgeGraphNeighborsQuery) = TODO()
    override suspend fun getGraphSimilar(query: KnowledgeGraphSimilarQuery) = TODO()
    override suspend fun getGraphExplore(query: KnowledgeGraphExploreQuery) = TODO()
    override suspend fun getGraphEvidence(query: KnowledgeGraphEvidenceQuery) = TODO()
    override suspend fun getAiInferredEdges(query: KnowledgeGraphAiInferredQuery) = TODO()
    override suspend fun getGraphBridge(query: KnowledgeGraphBridgeQuery) = TODO()
    override suspend fun explainPath(query: KnowledgeGraphPathExplainQuery) = TODO()
    override suspend fun getGraphCommunities(query: KnowledgeGraphCommunitiesQuery) = TODO()
    override suspend fun getTopicGraphMap(query: TopicGraphMapQuery) = TODO()
    override suspend fun getRandomGraphTrail(query: GraphTrailRandomQuery) = TODO()
    override suspend fun getGraphTrailFromContent(query: GraphTrailFromContentQuery) = TODO()
    override suspend fun getGraphTrailFromTopic(query: GraphTrailFromTopicQuery) = TODO()

    // Learning Routes
    override suspend fun getLearningRoutes(query: LearningRoutesListQuery) = TODO()
    override suspend fun getLearningRouteDetail(query: LearningRouteDetailQuery) = TODO()
    override suspend fun buildLearningRoute(query: LearningRouteBuildQuery) = TODO()
    override suspend fun getLearningRouteNextStep(query: LearningRouteNextQuery) = TODO()

    // Spacetime
    override suspend fun getSpacetimeOverview(query: SpacetimeOverviewQuery) = TODO()
    override suspend fun getSpacetimeHeatmap(query: SpacetimeHeatmapQuery) = TODO()
    override suspend fun getSpacetimeRegionTimeline(query: SpacetimeRegionTimelineQuery) = TODO()
    override suspend fun getSpacetimeYearMap(query: SpacetimeYearMapQuery) = TODO()
    override suspend fun getSpacetimeCategoryTimeline(query: SpacetimeCategoryTimelineQuery) = TODO()

    // Analytics
    override suspend fun getAnalyticsFacets(query: AnalyticsFacetsQuery) = TODO()
    override suspend fun getAnalyticsBreakdown(query: AnalyticsBreakdownQuery) = TODO()
    override suspend fun getAnalyticsCrosstab(query: AnalyticsCrosstabQuery) = TODO()
    override suspend fun getAnalyticsCompare(query: AnalyticsCompareQuery) = TODO()
    override suspend fun getAnalyticsOutliers(query: AnalyticsOutliersQuery) = TODO()

    // Rankings
    override suspend fun getRankings() = TODO()
    override suspend fun getRankingDetail(query: RankingDetailQuery) = TODO()
    override suspend fun getRankingContent(query: RankingContentQuery) = TODO()

    // Research
    override suspend fun getResearchPackages() = TODO()
    override suspend fun getResearchPackageDetail(query: ResearchPackageDetailQuery) = TODO()
    override suspend fun getResearchArtifact(query: ResearchArtifactQuery) = TODO()
    override suspend fun getResearchReports() = TODO()
    override suspend fun getResearchReportDetail(query: ResearchReportDetailQuery) = TODO()
    override suspend fun getResearchReportByPackage(query: ResearchReportByPackageQuery) = TODO()

    // Export
    override suspend fun getExportTemplates() = TODO()
    override suspend fun previewExport(request: ExportRequestDto) = TODO()
    override suspend fun exportContent(request: ExportRequestDto) = TODO()

    // LocalUser
    override suspend fun getLocalUserProfile(): LocalUserProfileDto = LocalUserProfileDto(profileId = currentProfileId())
    override suspend fun getLocalUserSummary(): LocalUserSummaryDto = LocalUserSummaryDto(profileId = currentProfileId())
    override suspend fun getLocalUserFavorites(query: LocalUserFavoritesQuery): PagedResult<LocalFavoriteDto> = PagedResult()
    override suspend fun addLocalUserFavorite(request: FavoriteCreateRequestDto): LocalFavoriteDto = LocalFavoriteDto(id = "f1", targetType = request.targetType, targetId = request.targetId)
    override suspend fun removeLocalUserFavorite(targetType: String, targetId: String) = Unit
    override suspend fun getLocalUserHistory(query: LocalUserHistoryQuery): PagedResult<LocalHistoryDto> = PagedResult()
    override suspend fun recordLocalUserHistory(request: HistoryRecordRequestDto): LocalHistoryDto = LocalHistoryDto(id = "h1", targetType = request.targetType, targetId = request.targetId)
    override suspend fun clearLocalUserHistory(): Int = 0
    override suspend fun getLocalUserLearningProgress(): List<LocalLearningProgressDto> = emptyList()
    override suspend fun updateLocalUserLearningProgress(routeId: String, request: LearningProgressUpdateDto): LocalLearningProgressDto = LocalLearningProgressDto(routeId = routeId)
    override suspend fun getLocalUserJourneys(
        strategy: JourneyStrategy,
        limit: Int,
        includeAiInferred: Boolean,
        includeTrail: Boolean,
    ): JourneyResponseDto = JourneyResponseDto()
    override suspend fun getLocalUserJourneySignals(): JourneySignalsDto = JourneySignalsDto()
}
