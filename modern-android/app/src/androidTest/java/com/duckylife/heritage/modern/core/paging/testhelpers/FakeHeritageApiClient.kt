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
import com.duckylife.heritage.modern.core.network.dto.advanced.LocalUserTargetType
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
    override suspend fun getDirectoryStatisticsOverview(kind: DirectoryItemKind) = throw NotImplementedError("FakeHeritageApiClient.getDirectoryStatisticsOverview not implemented")
    override suspend fun getDirectoryStatisticsBreakdown(kind: DirectoryItemKind, dimension: DirectoryStatisticDimension, limit: Int) = throw NotImplementedError("FakeHeritageApiClient.getDirectoryStatisticsBreakdown not implemented")

    // Content Intelligence
    override suspend fun getV3ContentPage(query: V3ContentPageQuery) = throw NotImplementedError("FakeHeritageApiClient.getV3ContentPage not implemented")
    override suspend fun getContentIntelligence(query: ContentIntelligenceQuery) = throw NotImplementedError("FakeHeritageApiClient.getContentIntelligence not implemented")
    override suspend fun getArticleAiCard(id: String) = throw NotImplementedError("FakeHeritageApiClient.getArticleAiCard not implemented")
    override suspend fun getDirectoryItemAiCard(id: String) = throw NotImplementedError("FakeHeritageApiClient.getDirectoryItemAiCard not implemented")
    override suspend fun getInheritorAiCard(id: String) = throw NotImplementedError("FakeHeritageApiClient.getInheritorAiCard not implemented")

    // Knowledge Graph
    override suspend fun intelligentSearch(query: IntelligentSearchQuery) = throw NotImplementedError("FakeHeritageApiClient.intelligentSearch not implemented")
    override suspend fun getGraphNeighbors(query: KnowledgeGraphNeighborsQuery) = throw NotImplementedError("FakeHeritageApiClient.getGraphNeighbors not implemented")
    override suspend fun getGraphSimilar(query: KnowledgeGraphSimilarQuery) = throw NotImplementedError("FakeHeritageApiClient.getGraphSimilar not implemented")
    override suspend fun getGraphExplore(query: KnowledgeGraphExploreQuery) = throw NotImplementedError("FakeHeritageApiClient.getGraphExplore not implemented")
    override suspend fun getGraphEvidence(query: KnowledgeGraphEvidenceQuery) = throw NotImplementedError("FakeHeritageApiClient.getGraphEvidence not implemented")
    override suspend fun getAiInferredEdges(query: KnowledgeGraphAiInferredQuery) = throw NotImplementedError("FakeHeritageApiClient.getAiInferredEdges not implemented")
    override suspend fun getGraphBridge(query: KnowledgeGraphBridgeQuery) = throw NotImplementedError("FakeHeritageApiClient.getGraphBridge not implemented")
    override suspend fun explainPath(query: KnowledgeGraphPathExplainQuery) = throw NotImplementedError("FakeHeritageApiClient.explainPath not implemented")
    override suspend fun getGraphCommunities(query: KnowledgeGraphCommunitiesQuery) = throw NotImplementedError("FakeHeritageApiClient.getGraphCommunities not implemented")
    override suspend fun getTopicGraphMap(query: TopicGraphMapQuery) = throw NotImplementedError("FakeHeritageApiClient.getTopicGraphMap not implemented")
    override suspend fun getRandomGraphTrail(query: GraphTrailRandomQuery) = throw NotImplementedError("FakeHeritageApiClient.getRandomGraphTrail not implemented")
    override suspend fun getGraphTrailFromContent(query: GraphTrailFromContentQuery) = throw NotImplementedError("FakeHeritageApiClient.getGraphTrailFromContent not implemented")
    override suspend fun getGraphTrailFromTopic(query: GraphTrailFromTopicQuery) = throw NotImplementedError("FakeHeritageApiClient.getGraphTrailFromTopic not implemented")

    // Learning Routes
    override suspend fun getLearningRoutes(query: LearningRoutesListQuery) = throw NotImplementedError("FakeHeritageApiClient.getLearningRoutes not implemented")
    override suspend fun getLearningRouteDetail(query: LearningRouteDetailQuery) = throw NotImplementedError("FakeHeritageApiClient.getLearningRouteDetail not implemented")
    override suspend fun buildLearningRoute(query: LearningRouteBuildQuery) = throw NotImplementedError("FakeHeritageApiClient.buildLearningRoute not implemented")
    override suspend fun getLearningRouteNextStep(query: LearningRouteNextQuery) = throw NotImplementedError("FakeHeritageApiClient.getLearningRouteNextStep not implemented")

    // Spacetime
    override suspend fun getSpacetimeOverview(query: SpacetimeOverviewQuery) = throw NotImplementedError("FakeHeritageApiClient.getSpacetimeOverview not implemented")
    override suspend fun getSpacetimeHeatmap(query: SpacetimeHeatmapQuery) = throw NotImplementedError("FakeHeritageApiClient.getSpacetimeHeatmap not implemented")
    override suspend fun getSpacetimeRegionTimeline(query: SpacetimeRegionTimelineQuery) = throw NotImplementedError("FakeHeritageApiClient.getSpacetimeRegionTimeline not implemented")
    override suspend fun getSpacetimeYearMap(query: SpacetimeYearMapQuery) = throw NotImplementedError("FakeHeritageApiClient.getSpacetimeYearMap not implemented")
    override suspend fun getSpacetimeCategoryTimeline(query: SpacetimeCategoryTimelineQuery) = throw NotImplementedError("FakeHeritageApiClient.getSpacetimeCategoryTimeline not implemented")

    // Analytics
    override suspend fun getAnalyticsFacets(query: AnalyticsFacetsQuery) = throw NotImplementedError("FakeHeritageApiClient.getAnalyticsFacets not implemented")
    override suspend fun getAnalyticsBreakdown(query: AnalyticsBreakdownQuery) = throw NotImplementedError("FakeHeritageApiClient.getAnalyticsBreakdown not implemented")
    override suspend fun getAnalyticsCrosstab(query: AnalyticsCrosstabQuery) = throw NotImplementedError("FakeHeritageApiClient.getAnalyticsCrosstab not implemented")
    override suspend fun getAnalyticsCompare(query: AnalyticsCompareQuery) = throw NotImplementedError("FakeHeritageApiClient.getAnalyticsCompare not implemented")
    override suspend fun getAnalyticsOutliers(query: AnalyticsOutliersQuery) = throw NotImplementedError("FakeHeritageApiClient.getAnalyticsOutliers not implemented")

    // Rankings
    override suspend fun getRankings() = throw NotImplementedError("FakeHeritageApiClient.getRankings not implemented")
    override suspend fun getRankingDetail(query: RankingDetailQuery) = throw NotImplementedError("FakeHeritageApiClient.getRankingDetail not implemented")
    override suspend fun getRankingContent(query: RankingContentQuery) = throw NotImplementedError("FakeHeritageApiClient.getRankingContent not implemented")

    // Research
    override suspend fun getResearchPackages() = throw NotImplementedError("FakeHeritageApiClient.getResearchPackages not implemented")
    override suspend fun getResearchPackageDetail(query: ResearchPackageDetailQuery) = throw NotImplementedError("FakeHeritageApiClient.getResearchPackageDetail not implemented")
    override suspend fun getResearchArtifact(query: ResearchArtifactQuery) = throw NotImplementedError("FakeHeritageApiClient.getResearchArtifact not implemented")
    override suspend fun getResearchArtifactBytes(query: ResearchArtifactQuery): ByteArray = throw NotImplementedError("FakeHeritageApiClient.getResearchArtifactBytes not implemented")
    override suspend fun getResearchReports() = throw NotImplementedError("FakeHeritageApiClient.getResearchReports not implemented")
    override suspend fun getResearchReportDetail(query: ResearchReportDetailQuery) = throw NotImplementedError("FakeHeritageApiClient.getResearchReportDetail not implemented")
    override suspend fun getResearchReportByPackage(query: ResearchReportByPackageQuery) = throw NotImplementedError("FakeHeritageApiClient.getResearchReportByPackage not implemented")

    // Export
    override suspend fun getExportTemplates(): List<ExportTemplateDto> = emptyList()
    override suspend fun previewExport(request: ExportRequestDto): ExportPreviewDto = ExportPreviewDto()
    override suspend fun exportContent(request: ExportRequestDto): ExportContentResultDto = ExportContentResultDto()

    // LocalUser
    override suspend fun getLocalUserProfile(): LocalUserProfileDto = LocalUserProfileDto(profileId = currentProfileId())
    override suspend fun getLocalUserSummary(): LocalUserSummaryDto = LocalUserSummaryDto(profileId = currentProfileId())
    override suspend fun getLocalUserFavorites(query: LocalUserFavoritesQuery): PagedResult<LocalFavoriteDto> = PagedResult()
    override suspend fun addLocalUserFavorite(request: FavoriteCreateRequestDto): LocalFavoriteDto = LocalFavoriteDto(id = "f1", targetType = request.targetType, targetId = request.targetId)
    override suspend fun removeLocalUserFavorite(targetType: LocalUserTargetType, targetId: String) = Unit
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
