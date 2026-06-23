package com.duckylife.heritage.modern.core.network

import com.duckylife.heritage.modern.core.network.dto.ArticleDetailDto
import com.duckylife.heritage.modern.core.network.dto.ArticleCategory
import com.duckylife.heritage.modern.core.network.dto.ArticleSummaryDto
import com.duckylife.heritage.modern.core.network.dto.BlendedRecommendationQueryDto
import com.duckylife.heritage.modern.core.network.dto.BlendedRecommendationResponseDto
import com.duckylife.heritage.modern.core.network.dto.CollectionDto
import com.duckylife.heritage.modern.core.network.dto.CompareResultDto
import com.duckylife.heritage.modern.core.network.dto.ContentDigestDto
import com.duckylife.heritage.modern.core.network.dto.DataStoryDto
import com.duckylife.heritage.modern.core.network.dto.DetailContextDto
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemDetailDto
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemSummaryDto
import com.duckylife.heritage.modern.core.network.dto.DirectoryStatisticDimension
import com.duckylife.heritage.modern.core.network.dto.DirectoryStatisticDimensionDto
import com.duckylife.heritage.modern.core.network.dto.DirectoryStatisticsOverviewDto
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
import com.duckylife.heritage.modern.core.network.dto.SearchSuggestionDto
import com.duckylife.heritage.modern.core.network.dto.SearchV2ResponseDto
import com.duckylife.heritage.modern.core.network.dto.SearchResultType
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
import com.duckylife.heritage.modern.core.network.dto.advanced.LocalUserSummaryDto
import com.duckylife.heritage.modern.core.network.dto.advanced.AiCardDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ContentIntelligenceDto
import com.duckylife.heritage.modern.core.network.dto.advanced.IntelligentSearchResponseDto
import com.duckylife.heritage.modern.core.network.dto.advanced.V3ContentPageDto
import com.duckylife.heritage.modern.core.network.dto.advanced.AiInferredEdgesDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphBridgeDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphCommunitiesDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphEvidenceDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphExploreDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphNeighborsDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphSimilarDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphTrailDto
import com.duckylife.heritage.modern.core.network.dto.advanced.PathExplainDto
import com.duckylife.heritage.modern.core.network.dto.advanced.TopicGraphMapDto
import com.duckylife.heritage.modern.core.network.dto.advanced.LearningRouteDetailDto
import com.duckylife.heritage.modern.core.network.dto.advanced.LearningRouteNextDto
import com.duckylife.heritage.modern.core.network.dto.advanced.LearningRouteSummaryDto
import com.duckylife.heritage.modern.core.network.dto.advanced.AnalyticsBreakdownDto
import com.duckylife.heritage.modern.core.network.dto.advanced.AnalyticsCompareDto
import com.duckylife.heritage.modern.core.network.dto.advanced.AnalyticsCrosstabDto
import com.duckylife.heritage.modern.core.network.dto.advanced.AnalyticsFacetsDto
import com.duckylife.heritage.modern.core.network.dto.advanced.AnalyticsOutliersDto
import com.duckylife.heritage.modern.core.network.dto.advanced.RankingDefinitionDto
import com.duckylife.heritage.modern.core.network.dto.advanced.RankingDetailDto
import com.duckylife.heritage.modern.core.network.dto.advanced.SpacetimeHeatmapDto
import com.duckylife.heritage.modern.core.network.dto.advanced.SpacetimeOverviewDto
import com.duckylife.heritage.modern.core.network.dto.advanced.SpacetimeRegionMapDto
import com.duckylife.heritage.modern.core.network.dto.advanced.SpacetimeTimelineDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ResearchPackageDetailDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ResearchPackageSummaryDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ResearchReportDetailDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ResearchReportSummaryDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ExportContentResultDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ExportPreviewDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ExportRequestDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ExportTemplateDto
import com.duckylife.heritage.modern.core.network.dto.advanced.*
import com.duckylife.heritage.modern.core.network.api.ContentExportApi
import com.duckylife.heritage.modern.core.network.api.ContentIntelligenceApi
import com.duckylife.heritage.modern.core.network.api.DataExploreApi
import com.duckylife.heritage.modern.core.network.api.KnowledgeGraphApi
import com.duckylife.heritage.modern.core.network.api.LearningRoutesApi
import com.duckylife.heritage.modern.core.network.api.LocalUserApi
import com.duckylife.heritage.modern.core.network.api.ResearchApi
import com.duckylife.heritage.modern.core.profile.LocalProfileRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.accept
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

interface HeritageApiClient :
    LocalUserApi,
    ContentIntelligenceApi,
    KnowledgeGraphApi,
    LearningRoutesApi,
    DataExploreApi,
    ResearchApi,
    ContentExportApi {
    suspend fun getHomeBanners(): List<HomeBannerDto>

    suspend fun getHomeFeed(): HomeFeedDto

    suspend fun getArticles(query: ArticleQuery = ArticleQuery()): PagedResult<ArticleSummaryDto>

    suspend fun getArticle(id: String): ArticleDetailDto

    suspend fun getArticleBySourceId(
        sourceId: String,
        category: ArticleCategory = ArticleCategory.News,
    ): ArticleDetailDto

    suspend fun getArticleBySourceUrl(
        sourceUrl: String,
        category: ArticleCategory = ArticleCategory.News,
    ): ArticleDetailDto

    suspend fun getArticleContext(id: String): DetailContextDto

    suspend fun getDirectoryItems(
        query: DirectoryItemQuery = DirectoryItemQuery(),
    ): PagedResult<DirectoryItemSummaryDto>

    suspend fun getDirectoryStatisticsOverview(
        kind: DirectoryItemKind = DirectoryItemKind.NationalProject,
    ): DirectoryStatisticsOverviewDto

    suspend fun getDirectoryStatisticsBreakdown(
        kind: DirectoryItemKind = DirectoryItemKind.NationalProject,
        dimension: DirectoryStatisticDimension,
        limit: Int = 50,
    ): DirectoryStatisticDimensionDto

    suspend fun getDirectoryItem(id: String): DirectoryItemDetailDto

    suspend fun getDirectoryItemBySourceId(
        sourceId: String,
        kind: DirectoryItemKind = DirectoryItemKind.NationalProject,
    ): DirectoryItemDetailDto

    suspend fun getDirectoryItemContext(id: String): DetailContextDto

    suspend fun getInheritors(query: InheritorQuery = InheritorQuery()): PagedResult<InheritorSummaryDto>

    suspend fun getInheritor(id: String): InheritorDetailDto

    suspend fun getInheritorBySourceId(sourceId: String): InheritorDetailDto

    suspend fun getInheritorContext(id: String): DetailContextDto

    suspend fun searchV2(query: SearchV2Query): SearchV2ResponseDto

    suspend fun getSearchSuggestions(prefix: String, limit: Int = 10): List<SearchSuggestionDto>

    suspend fun getTimelineV2(query: TimelineV2Query): TimelineV2ResponseDto

    suspend fun getTimelineYears(): List<TimelineYearBucketDto>

    suspend fun getExploreIndex(): ExploreIndexDto

    suspend fun getExploreTopics(type: String? = null, limit: Int = 20): List<ExploreTopicInfoDto>

    suspend fun getExploreTopic(type: String, key: String, limit: Int = 6): ExploreTopicV2Dto

    suspend fun getLearningPaths(): List<LearningPathDto>

    suspend fun getLearningPathDetail(id: String, limit: Int = 6): LearningPathDetailDto

    suspend fun getRegionAtlas(): RegionAtlasDto

    suspend fun getRegionAtlasDetail(region: String, limit: Int = 6): RegionAtlasDetailDto

    suspend fun getFeaturedCollections(): List<FeaturedCollectionDto>

    suspend fun getCollection(id: String, limit: Int = 10): CollectionDto

    suspend fun getTopicCollection(type: String, key: String, limit: Int = 10): CollectionDto

    // Discovery v2
    suspend fun getDiscoveryToday(): DiscoveryTodayDto

    suspend fun getDiscoveryRandom(type: SearchResultType): DiscoveryItemDto

    suspend fun getDiscoveryTrending(limit: Int = 10): DiscoveryTrendingDto

    suspend fun getDiscoveryWeekly(): DiscoveryWeeklyDto

    suspend fun getDiscoverySerendipity(query: DiscoverySerendipityQuery): DiscoveryItemDto

    suspend fun getDiscoveryDeepDive(query: DiscoveryDeepDiveQuery): DiscoveryDeepDiveDto

    // Data Stories
    suspend fun getRegionStory(region: String): DataStoryDto

    suspend fun getCategoryStory(category: String): DataStoryDto

    suspend fun getYearStory(year: Int): DataStoryDto

    // Taxonomy
    suspend fun getTaxonomyCategories(limit: Int = 50): TaxonomyIndexDto<TaxonomyTopicDto>

    suspend fun getTaxonomyRegions(
        limit: Int = 50,
        sort: TaxonomyRegionSort = TaxonomyRegionSort.Total,
    ): TaxonomyIndexDto<TaxonomyTopicDto>

    suspend fun getTaxonomyKinds(): TaxonomyIndexDto<TaxonomyKindDto>

    suspend fun getTaxonomyCategoryDetail(category: String, limit: Int = 6): TaxonomyCategoryDetailDto

    suspend fun getTaxonomyRegionDetail(region: String, limit: Int = 6): TaxonomyRegionDetailDto

    // Compare
    suspend fun compareRegions(left: String, right: String, limit: Int = 6): CompareResultDto

    suspend fun compareCategories(left: String, right: String, limit: Int = 6): CompareResultDto

    suspend fun compareKinds(
        left: DirectoryItemKind,
        right: DirectoryItemKind,
        limit: Int = 6,
    ): CompareResultDto

    // Content Digest
    suspend fun getArticleDigest(id: String): ContentDigestDto

    suspend fun getDirectoryItemDigest(id: String): ContentDigestDto

    suspend fun getInheritorDigest(id: String): ContentDigestDto

    // Blended Recommendations
    suspend fun getBlendedRecommendations(
        query: BlendedRecommendationQuery,
    ): BlendedRecommendationResponseDto

    /**
     * 返回当前设备 profile ID，供需要以 query 参数传入 `profileId` 的接口使用。
     */
    suspend fun currentProfileId(): String
}

class KtorHeritageApiClient(
    private val httpClient: HttpClient,
    private val baseUrl: String,
    private val profileRepository: LocalProfileRepository,
) : HeritageApiClient,
    LocalUserApi,
    ContentIntelligenceApi,
    KnowledgeGraphApi,
    LearningRoutesApi,
    DataExploreApi,
    ResearchApi,
    ContentExportApi {
    override suspend fun getHomeBanners(): List<HomeBannerDto> =
        httpClient.get(endpoint("api/home-banners")).body()

    override suspend fun getHomeFeed(): HomeFeedDto =
        httpClient.get(endpoint("api/home/feed")).body()

    override suspend fun getArticles(query: ArticleQuery): PagedResult<ArticleSummaryDto> =
        httpClient.get(endpoint("api/articles")) {
            optionalParameter("category", query.category.wireName)
            optionalParameter("page", query.page)
            optionalParameter("pageSize", query.pageSize)
            optionalParameter("year", query.year)
            optionalParameter("keywords", query.keywords)
        }.body()

    override suspend fun getArticle(id: String): ArticleDetailDto =
        httpClient.get(endpoint("api/articles/${pathSegment(id)}")).body()

    override suspend fun getArticleBySourceId(
        sourceId: String,
        category: ArticleCategory,
    ): ArticleDetailDto =
        httpClient.get(endpoint("api/articles/source/${pathSegment(sourceId)}")) {
            optionalParameter("category", category.wireName)
        }.body()

    override suspend fun getArticleBySourceUrl(
        sourceUrl: String,
        category: ArticleCategory,
    ): ArticleDetailDto =
        httpClient.get(endpoint("api/articles/source")) {
            optionalParameter("category", category.wireName)
            optionalParameter("sourceUrl", sourceUrl)
        }.body()

    override suspend fun getArticleContext(id: String): DetailContextDto =
        httpClient.get(endpoint("api/articles/${pathSegment(id)}/context")).body()

    override suspend fun getDirectoryItems(
        query: DirectoryItemQuery,
    ): PagedResult<DirectoryItemSummaryDto> =
        httpClient.get(endpoint("api/directory-items")) {
            optionalParameter("kind", query.kind.wireName)
            optionalParameter("page", query.page)
            optionalParameter("pageSize", query.pageSize)
            optionalParameter("keywords", query.keywords)
            optionalParameter("region", query.region)
            optionalParameter("category", query.category)
            optionalParameter("year", query.year)
            optionalParameter("listType", query.listType)
        }.body()

    override suspend fun getDirectoryStatisticsOverview(
        kind: DirectoryItemKind,
    ): DirectoryStatisticsOverviewDto =
        httpClient.get(endpoint("api/directory-items/statistics")) {
            optionalParameter("kind", kind.wireName)
        }.body()

    override suspend fun getDirectoryStatisticsBreakdown(
        kind: DirectoryItemKind,
        dimension: DirectoryStatisticDimension,
        limit: Int,
    ): DirectoryStatisticDimensionDto =
        httpClient.get(endpoint("api/directory-items/statistics/breakdown")) {
            optionalParameter("kind", kind.wireName)
            optionalParameter("dimension", dimension.wireName)
            optionalParameter("limit", limit)
        }.body()

    override suspend fun getDirectoryItem(id: String): DirectoryItemDetailDto =
        httpClient.get(endpoint("api/directory-items/${pathSegment(id)}")).body()

    override suspend fun getDirectoryItemBySourceId(
        sourceId: String,
        kind: DirectoryItemKind,
    ): DirectoryItemDetailDto =
        httpClient.get(endpoint("api/directory-items/source/${pathSegment(sourceId)}")) {
            optionalParameter("kind", kind.wireName)
        }.body()

    override suspend fun getDirectoryItemContext(id: String): DetailContextDto =
        httpClient.get(endpoint("api/directory-items/${pathSegment(id)}/context")).body()

    override suspend fun getInheritors(query: InheritorQuery): PagedResult<InheritorSummaryDto> =
        httpClient.get(endpoint("api/inheritors")) {
            optionalParameter("page", query.page)
            optionalParameter("pageSize", query.pageSize)
            optionalParameter("keywords", query.keywords)
            optionalParameter("region", query.region)
            optionalParameter("category", query.category)
            optionalParameter("year", query.year)
            optionalParameter("gender", query.gender)
        }.body()

    override suspend fun getInheritor(id: String): InheritorDetailDto =
        httpClient.get(endpoint("api/inheritors/${pathSegment(id)}")).body()

    override suspend fun getInheritorBySourceId(sourceId: String): InheritorDetailDto =
        httpClient.get(endpoint("api/inheritors/source/${pathSegment(sourceId)}")).body()

    override suspend fun getInheritorContext(id: String): DetailContextDto =
        httpClient.get(endpoint("api/inheritors/${pathSegment(id)}/context")).body()

    override suspend fun searchV2(query: SearchV2Query): SearchV2ResponseDto =
        httpClient.get(endpoint("api/search/v2")) {
            optionalParameter("keywords", query.keywords)
            optionalParameter(
                "types",
                query.types.takeIf { it.isNotEmpty() }?.joinToString(",") { it.wireName },
            )
            optionalParameter("page", query.page)
            optionalParameter("pageSize", query.pageSize)
            optionalParameter("region", query.region)
            optionalParameter("category", query.category)
            optionalParameter("year", query.year)
            optionalParameter("kind", query.kind?.wireName)
            optionalParameter("hasImage", query.hasImage)
        }.body()

    override suspend fun getSearchSuggestions(prefix: String, limit: Int): List<SearchSuggestionDto> =
        httpClient.get(endpoint("api/search/suggestions")) {
            optionalParameter("prefix", prefix)
            optionalParameter("limit", limit)
        }.body()

    override suspend fun getTimelineV2(query: TimelineV2Query): TimelineV2ResponseDto =
        httpClient.get(endpoint("api/timeline/v2")) {
            optionalParameter("year", query.year)
            optionalParameter(
                "types",
                query.types.takeIf { it.isNotEmpty() }?.joinToString(",") { it.wireName },
            )
            optionalParameter("page", query.page)
            optionalParameter("pageSize", query.pageSize)
            optionalParameter("category", query.category)
            optionalParameter("region", query.region)
            optionalParameter("kind", query.kind?.wireName)
            optionalParameter("hasImage", query.hasImage)
        }.body()

    override suspend fun getTimelineYears(): List<TimelineYearBucketDto> =
        httpClient.get(endpoint("api/timeline/years")).body()

    override suspend fun getExploreIndex(): ExploreIndexDto =
        httpClient.get(endpoint("api/explore")).body()

    override suspend fun getExploreTopics(type: String?, limit: Int): List<ExploreTopicInfoDto> =
        httpClient.get(endpoint("api/explore/topics")) {
            optionalParameter("type", type)
            optionalParameter("limit", limit)
        }.body()

    override suspend fun getExploreTopic(type: String, key: String, limit: Int): ExploreTopicV2Dto =
        httpClient.get(endpoint("api/explore/topics/${pathSegment(type)}/${pathSegment(key)}")) {
            optionalParameter("limit", limit)
        }.body()

    override suspend fun getLearningPaths(): List<LearningPathDto> =
        httpClient.get(endpoint("api/explore/learning-paths")).body()

    override suspend fun getLearningPathDetail(id: String, limit: Int): LearningPathDetailDto =
        httpClient.get(endpoint("api/explore/learning-paths/${pathSegment(id)}")) {
            optionalParameter("limit", limit)
        }.body()

    override suspend fun getRegionAtlas(): RegionAtlasDto =
        httpClient.get(endpoint("api/regions/atlas")).body()

    override suspend fun getRegionAtlasDetail(region: String, limit: Int): RegionAtlasDetailDto =
        httpClient.get(endpoint("api/regions/${pathSegment(region)}/atlas")) {
            optionalParameter("limit", limit)
        }.body()

    override suspend fun getFeaturedCollections(): List<FeaturedCollectionDto> =
        httpClient.get(endpoint("api/collections/featured")).body()

    override suspend fun getCollection(id: String, limit: Int): CollectionDto =
        httpClient.get(endpoint("api/collections/${pathSegment(id)}")) {
            optionalParameter("limit", limit)
        }.body()

    override suspend fun getTopicCollection(type: String, key: String, limit: Int): CollectionDto =
        httpClient.get(endpoint("api/collections/topic/${pathSegment(type)}/${pathSegment(key)}")) {
            optionalParameter("limit", limit)
        }.body()

    // Discovery v2
    override suspend fun getDiscoveryToday(): DiscoveryTodayDto =
        httpClient.get(endpoint("api/discovery/today")).body()

    override suspend fun getDiscoveryRandom(type: SearchResultType): DiscoveryItemDto =
        httpClient.get(endpoint("api/discovery/random")) {
            optionalParameter("type", type.wireName)
        }.body()

    override suspend fun getDiscoveryTrending(limit: Int): DiscoveryTrendingDto =
        httpClient.get(endpoint("api/discovery/trending")) {
            optionalParameter("limit", limit)
        }.body()

    override suspend fun getDiscoveryWeekly(): DiscoveryWeeklyDto =
        httpClient.get(endpoint("api/discovery/weekly")).body()

    override suspend fun getDiscoverySerendipity(query: DiscoverySerendipityQuery): DiscoveryItemDto =
        httpClient.get(endpoint("api/discovery/serendipity")) {
            optionalParameter("type", query.type.wireName)
            optionalParameter("hasImage", query.hasImage)
            optionalParameter("region", query.region)
            optionalParameter("category", query.category)
        }.body()

    override suspend fun getDiscoveryDeepDive(query: DiscoveryDeepDiveQuery): DiscoveryDeepDiveDto =
        httpClient.get(endpoint("api/discovery/deep-dive")) {
            optionalParameter("seedType", query.seedType.wireName)
            optionalParameter("seedId", query.seedId)
            optionalParameter("limit", query.limit)
        }.body()

    // Data Stories
    override suspend fun getRegionStory(region: String): DataStoryDto =
        httpClient.get(endpoint("api/stories/regions/${pathSegment(region)}")).body()

    override suspend fun getCategoryStory(category: String): DataStoryDto =
        httpClient.get(endpoint("api/stories/categories/${pathSegment(category)}")).body()

    override suspend fun getYearStory(year: Int): DataStoryDto =
        httpClient.get(endpoint("api/stories/years/$year")).body()

    // Taxonomy
    override suspend fun getTaxonomyCategories(limit: Int): TaxonomyIndexDto<TaxonomyTopicDto> =
        httpClient.get(endpoint("api/taxonomy/categories")) {
            optionalParameter("limit", limit)
        }.body()

    override suspend fun getTaxonomyRegions(
        limit: Int,
        sort: TaxonomyRegionSort,
    ): TaxonomyIndexDto<TaxonomyTopicDto> =
        httpClient.get(endpoint("api/taxonomy/regions")) {
            optionalParameter("limit", limit)
            optionalParameter("sort", sort.wireName)
        }.body()

    override suspend fun getTaxonomyKinds(): TaxonomyIndexDto<TaxonomyKindDto> =
        httpClient.get(endpoint("api/taxonomy/kinds")).body()

    override suspend fun getTaxonomyCategoryDetail(
        category: String,
        limit: Int,
    ): TaxonomyCategoryDetailDto =
        httpClient.get(endpoint("api/taxonomy/category/${pathSegment(category)}")) {
            optionalParameter("limit", limit)
        }.body()

    override suspend fun getTaxonomyRegionDetail(
        region: String,
        limit: Int,
    ): TaxonomyRegionDetailDto =
        httpClient.get(endpoint("api/taxonomy/region/${pathSegment(region)}")) {
            optionalParameter("limit", limit)
        }.body()

    // Compare
    override suspend fun compareRegions(
        left: String,
        right: String,
        limit: Int,
    ): CompareResultDto =
        httpClient.get(endpoint("api/compare/regions")) {
            optionalParameter("left", left)
            optionalParameter("right", right)
            optionalParameter("limit", limit)
        }.body()

    override suspend fun compareCategories(
        left: String,
        right: String,
        limit: Int,
    ): CompareResultDto =
        httpClient.get(endpoint("api/compare/categories")) {
            optionalParameter("left", left)
            optionalParameter("right", right)
            optionalParameter("limit", limit)
        }.body()

    override suspend fun compareKinds(
        left: DirectoryItemKind,
        right: DirectoryItemKind,
        limit: Int,
    ): CompareResultDto =
        httpClient.get(endpoint("api/compare/kinds")) {
            optionalParameter("left", left.wireName)
            optionalParameter("right", right.wireName)
            optionalParameter("limit", limit)
        }.body()

    // Content Digest
    override suspend fun getArticleDigest(id: String): ContentDigestDto =
        httpClient.get(endpoint("api/articles/${pathSegment(id)}/digest")).body()

    override suspend fun getDirectoryItemDigest(id: String): ContentDigestDto =
        httpClient.get(endpoint("api/directory-items/${pathSegment(id)}/digest")).body()

    override suspend fun getInheritorDigest(id: String): ContentDigestDto =
        httpClient.get(endpoint("api/inheritors/${pathSegment(id)}/digest")).body()

    // Blended Recommendations
    override suspend fun getBlendedRecommendations(
        query: BlendedRecommendationQuery,
    ): BlendedRecommendationResponseDto =
        httpClient.get(endpoint("api/recommendations/blended/${pathSegment(query.type.wireName)}/${pathSegment(query.id)}")) {
            optionalParameter("limit", query.limit)
            optionalParameter("ruleWeight", query.ruleWeight)
            optionalParameter("semanticWeight", query.semanticWeight)
            optionalParameter("sameCategoryWeight", query.sameCategoryWeight)
            optionalParameter("sameRegionWeight", query.sameRegionWeight)
            optionalParameter("diversify", query.diversify)
        }.body()

    // ── LocalUser API ──

    override suspend fun getLocalUserSummary(): LocalUserSummaryDto =
        httpClient.get(endpoint("api/local-user/summary")).body()

    override suspend fun getLocalUserFavorites(query: LocalUserFavoritesQuery): PagedResult<LocalFavoriteDto> =
        httpClient.get(endpoint("api/local-user/favorites")) {
            optionalParameter("targetType", query.targetType)
            optionalParameter("page", query.page)
            optionalParameter("pageSize", query.pageSize)
        }.body()

    override suspend fun addLocalUserFavorite(request: FavoriteCreateRequestDto): LocalFavoriteDto =
        httpClient.post(endpoint("api/local-user/favorites")) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    override suspend fun removeLocalUserFavorite(targetType: String, targetId: String) {
        httpClient.delete(
            endpoint("api/local-user/favorites/${pathSegment(targetType)}/${pathSegment(targetId)}")
        )
    }

    override suspend fun getLocalUserHistory(query: LocalUserHistoryQuery): PagedResult<LocalHistoryDto> =
        httpClient.get(endpoint("api/local-user/history")) {
            optionalParameter("targetType", query.targetType)
            optionalParameter("page", query.page)
            optionalParameter("pageSize", query.pageSize)
        }.body()

    override suspend fun recordLocalUserHistory(request: HistoryRecordRequestDto): LocalHistoryDto =
        httpClient.post(endpoint("api/local-user/history")) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    override suspend fun clearLocalUserHistory(): Int =
        httpClient.delete(endpoint("api/local-user/history")).body()

    override suspend fun getLocalUserLearningProgress(): List<LocalLearningProgressDto> =
        httpClient.get(endpoint("api/local-user/learning-progress")).body()

    override suspend fun updateLocalUserLearningProgress(
        routeId: String,
        request: LearningProgressUpdateDto,
    ): LocalLearningProgressDto =
        httpClient.put(endpoint("api/local-user/learning-progress/${pathSegment(routeId)}")) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    override suspend fun getLocalUserJourneys(
        strategy: JourneyStrategy,
        limit: Int,
    ): JourneyResponseDto =
        httpClient.get(endpoint("api/local-user/journeys")) {
            optionalParameter("strategy", strategy.wireName)
            optionalParameter("limit", limit)
        }.body()

    override suspend fun getLocalUserJourneySignals(): JourneySignalsDto =
        httpClient.get(endpoint("api/local-user/journeys/signals")).body()

    // ── Content Intelligence API ──

    override suspend fun getV3ContentPage(query: V3ContentPageQuery): V3ContentPageDto =
        httpClient.get(endpoint("api/v3/pages/${v3PagePath(query.contentType)}/${pathSegment(query.id)}")) {
            optionalParameter("profileId", query.profileId)
            optionalParameter("includeAi", query.includeAi)
            optionalParameter("includeGraph", query.includeGraph)
            optionalParameter("includeRecommendations", query.includeRecommendations)
            optionalParameter("includeLocalState", query.includeLocalState)
            optionalParameter("includeDigest", query.includeDigest)
            optionalParameter("includeExportHints", query.includeExportHints)
            optionalParameter("recommendationLimit", query.recommendationLimit)
            optionalParameter("neighborLimit", query.neighborLimit)
        }.body()

    override suspend fun getContentIntelligence(query: ContentIntelligenceQuery): ContentIntelligenceDto =
        httpClient.get(endpoint("api/v3/content/${query.contentType.wireName}/${pathSegment(query.id)}/intelligence")) {
            optionalParameter("includeAi", query.includeAi)
            optionalParameter("includeGraph", query.includeGraph)
            optionalParameter("includeRecommendations", query.includeRecommendations)
            optionalParameter("recommendationLimit", query.recommendationLimit)
            optionalParameter("neighborLimit", query.neighborLimit)
        }.body()

    override suspend fun getArticleAiCard(id: String): AiCardDto =
        httpClient.get(endpoint("api/v3/articles/${pathSegment(id)}/ai-card")).body()

    override suspend fun getDirectoryItemAiCard(id: String): AiCardDto =
        httpClient.get(endpoint("api/v3/directory-items/${pathSegment(id)}/ai-card")).body()

    override suspend fun getInheritorAiCard(id: String): AiCardDto =
        httpClient.get(endpoint("api/v3/inheritors/${pathSegment(id)}/ai-card")).body()

    override suspend fun intelligentSearch(query: IntelligentSearchQuery): IntelligentSearchResponseDto =
        httpClient.get(endpoint("api/v3/search/intelligent")) {
            optionalParameter("q", query.keywords)
            optionalParameter(
                "types",
                query.types.takeIf { it.isNotEmpty() }?.joinToString(",") { it.wireName },
            )
            optionalParameter("page", query.page)
            optionalParameter("pageSize", query.pageSize)
            optionalParameter("region", query.region)
            optionalParameter("category", query.category)
            optionalParameter("year", query.year)
            optionalParameter("kind", query.kind?.wireName)
            optionalParameter("includeAi", query.includeAi)
            optionalParameter("includeGraph", query.includeGraph)
            optionalParameter("includeHighlights", query.includeHighlights)
            optionalParameter("minScore", query.minScore)
        }.body()

    // ── KnowledgeGraph API ──

    override suspend fun getGraphNeighbors(query: KnowledgeGraphNeighborsQuery): GraphNeighborsDto =
        httpClient.get(endpoint("api/knowledge-graph/${query.contentType.wireName}/${pathSegment(query.id)}/neighbors")) {
            optionalParameter("limit", query.limit)
            optionalParameter("relationType", query.relationType?.takeIf { it != GraphRelationType.Unknown }?.wireName)
            optionalParameter("source", query.source?.takeIf { it != GraphEvidenceSource.Unknown }?.wireName)
            optionalParameter("includeTopics", query.includeTopics)
        }.body()

    override suspend fun getGraphSimilar(query: KnowledgeGraphSimilarQuery): GraphSimilarDto =
        httpClient.get(endpoint("api/knowledge-graph/${query.contentType.wireName}/${pathSegment(query.id)}/similar")) {
            optionalParameter("limit", query.limit)
            optionalParameter("includeTopics", query.includeTopics)
        }.body()

    override suspend fun getGraphExplore(query: KnowledgeGraphExploreQuery): GraphExploreDto =
        httpClient.get(endpoint("api/knowledge-graph/${query.contentType.wireName}/${pathSegment(query.id)}/explore")) {
            optionalParameter("depth", query.depth)
            optionalParameter("limit", query.limit)
            optionalParameter("includeTopics", query.includeTopics)
        }.body()

    override suspend fun getGraphEvidence(query: KnowledgeGraphEvidenceQuery): GraphEvidenceDto =
        httpClient.get(endpoint("api/knowledge-graph/${query.contentType.wireName}/${pathSegment(query.id)}/evidence")) {
            optionalParameter("includeAiInferred", query.includeAiInferred)
            optionalParameter("limit", query.limit)
        }.body()

    override suspend fun getAiInferredEdges(query: KnowledgeGraphAiInferredQuery): AiInferredEdgesDto =
        httpClient.get(endpoint("api/knowledge-graph/${query.contentType.wireName}/${pathSegment(query.id)}/ai-inferred")) {
            optionalParameter("entityType", query.entityType)
            optionalParameter("minConfidence", query.minConfidence)
            optionalParameter("includeStale", query.includeStale)
            optionalParameter("limit", query.limit)
        }.body()

    override suspend fun getGraphBridge(query: KnowledgeGraphBridgeQuery): GraphBridgeDto =
        httpClient.get(endpoint("api/knowledge-graph/bridge")) {
            optionalParameter("fromType", query.fromType.wireName)
            optionalParameter("fromId", query.fromId)
            optionalParameter("toType", query.toType.wireName)
            optionalParameter("toId", query.toId)
            optionalParameter("limit", query.limit)
        }.body()

    override suspend fun explainPath(query: KnowledgeGraphPathExplainQuery): PathExplainDto =
        httpClient.get(endpoint("api/knowledge-graph/path/explain")) {
            optionalParameter("fromType", query.fromType.wireName)
            optionalParameter("fromId", query.fromId)
            optionalParameter("toType", query.toType.wireName)
            optionalParameter("toId", query.toId)
            optionalParameter("maxDepth", query.maxDepth)
            optionalParameter("includeAiInferred", query.includeAiInferred)
        }.body()

    override suspend fun getGraphCommunities(query: KnowledgeGraphCommunitiesQuery): GraphCommunitiesDto =
        httpClient.get(endpoint("api/knowledge-graph/communities")) {
            optionalParameter("limit", query.limit)
            optionalParameter("minSize", query.minSize)
        }.body()

    override suspend fun getTopicGraphMap(query: TopicGraphMapQuery): TopicGraphMapDto =
        httpClient.get(
            endpoint("api/knowledge-graph/topics/${pathSegment(query.topicType)}/${pathSegment(query.topicKey)}/map")
        ) {
            optionalParameter("limit", query.limit)
        }.body()

    override suspend fun getRandomGraphTrail(query: GraphTrailRandomQuery): GraphTrailDto =
        httpClient.get(endpoint("api/knowledge-graph/trails/random")) {
            optionalParameter("strategy", query.strategy.takeIf { it != TrailStrategy.Unknown }?.wireName)
            optionalParameter("type", query.type?.wireName)
            optionalParameter("limit", query.limit)
        }.body()

    override suspend fun getGraphTrailFromContent(query: GraphTrailFromContentQuery): GraphTrailDto =
        httpClient.get(
            endpoint("api/knowledge-graph/trails/from/${query.contentType.wireName}/${pathSegment(query.id)}")
        ) {
            optionalParameter("strategy", query.strategy.takeIf { it != TrailStrategy.Unknown }?.wireName)
            optionalParameter("limit", query.limit)
            optionalParameter("includeTopics", query.includeTopics)
        }.body()

    override suspend fun getGraphTrailFromTopic(query: GraphTrailFromTopicQuery): GraphTrailDto =
        httpClient.get(
            endpoint("api/knowledge-graph/trails/topic/${pathSegment(query.topicType)}/${pathSegment(query.topicKey)}")
        ) {
            optionalParameter("strategy", query.strategy.takeIf { it != TrailStrategy.Unknown }?.wireName)
            optionalParameter("limit", query.limit)
        }.body()

    // ── LearningRoutes API ──

    override suspend fun getLearningRoutes(query: LearningRoutesListQuery): List<LearningRouteSummaryDto> =
        httpClient.get(endpoint("api/learning-routes")) {
            optionalParameter("difficulty", query.difficulty.takeIf { it != LearningRouteDifficulty.Unknown }?.wireName)
            optionalParameter("limit", query.limit)
        }.body()

    override suspend fun getLearningRouteDetail(query: LearningRouteDetailQuery): LearningRouteDetailDto =
        httpClient.get(endpoint("api/learning-routes/${pathSegment(query.routeId)}")) {
            optionalParameter("limit", query.limit)
            optionalParameter("includeAi", query.includeAi)
        }.body()

    override suspend fun buildLearningRoute(query: LearningRouteBuildQuery): LearningRouteDetailDto =
        httpClient.get(endpoint("api/learning-routes/build")) {
            optionalParameter("seedType", query.seedType.takeIf { it != LearningRouteSeedType.Unknown }?.wireName)
            optionalParameter("seedKey", query.seedKey)
            optionalParameter("difficulty", query.difficulty.takeIf { it != LearningRouteDifficulty.Unknown }?.wireName)
            optionalParameter("limit", query.limit)
            optionalParameter("includeArticles", query.includeArticles)
            optionalParameter("includeDirectoryItems", query.includeDirectoryItems)
            optionalParameter("includeInheritors", query.includeInheritors)
        }.body()

    override suspend fun getLearningRouteNextStep(query: LearningRouteNextQuery): LearningRouteNextDto =
        httpClient.get(endpoint("api/learning-routes/${pathSegment(query.routeId)}/next")) {
            optionalParameter(
                "completedStepIds",
                query.completedStepIds.takeIf { it.isNotEmpty() }?.joinToString(",") { it },
            )
            optionalParameter("profileId", query.profileId)
        }.body()

    // ── DataExplore API ──

    override suspend fun getSpacetimeOverview(query: SpacetimeOverviewQuery): SpacetimeOverviewDto =
        httpClient.get(endpoint("api/spacetime/overview")) {
            optionalParameter("fromYear", query.fromYear)
            optionalParameter("toYear", query.toYear)
            optionalParameter("region", query.region)
            optionalParameter("category", query.category)
            optionalParameter("kind", query.kind)
            optionalParameter("targetType", query.targetType)
            optionalParameter("limit", query.limit)
        }.body()

    override suspend fun getSpacetimeHeatmap(query: SpacetimeHeatmapQuery): SpacetimeHeatmapDto =
        httpClient.get(endpoint("api/spacetime/heatmap")) {
            optionalParameter("x", query.x.takeIf { it != SpacetimeDimension.Unknown }?.wireName)
            optionalParameter("y", query.y.takeIf { it != SpacetimeDimension.Unknown }?.wireName)
            optionalParameter("targetType", query.targetType)
            optionalParameter("fromYear", query.fromYear)
            optionalParameter("toYear", query.toYear)
            optionalParameter("limit", query.limit)
        }.body()

    override suspend fun getSpacetimeRegionTimeline(query: SpacetimeRegionTimelineQuery): SpacetimeTimelineDto =
        httpClient.get(endpoint("api/spacetime/regions/${pathSegment(query.region)}/timeline")).body()

    override suspend fun getSpacetimeYearMap(query: SpacetimeYearMapQuery): SpacetimeRegionMapDto =
        httpClient.get(endpoint("api/spacetime/years/${query.year}/map")).body()

    override suspend fun getSpacetimeCategoryTimeline(query: SpacetimeCategoryTimelineQuery): SpacetimeTimelineDto =
        httpClient.get(endpoint("api/spacetime/categories/${pathSegment(query.category)}/timeline")).body()

    override suspend fun getAnalyticsFacets(query: AnalyticsFacetsQuery): AnalyticsFacetsDto =
        httpClient.get(endpoint("api/analytics/facets")) {
            applyAnalyticsFilters(query.filters)
        }.body()

    override suspend fun getAnalyticsBreakdown(query: AnalyticsBreakdownQuery): AnalyticsBreakdownDto =
        httpClient.get(endpoint("api/analytics/breakdown")) {
            optionalParameter("groupBy", query.groupBy.takeIf { it != AnalyticsDimension.Unknown }?.wireName)
            optionalParameter("limit", query.limit)
            applyAnalyticsFilters(query.filters)
        }.body()

    override suspend fun getAnalyticsCrosstab(query: AnalyticsCrosstabQuery): AnalyticsCrosstabDto =
        httpClient.get(endpoint("api/analytics/crosstab")) {
            optionalParameter("x", query.x.takeIf { it != AnalyticsDimension.Unknown }?.wireName)
            optionalParameter("y", query.y.takeIf { it != AnalyticsDimension.Unknown }?.wireName)
            optionalParameter("limit", query.limit)
            applyAnalyticsFilters(query.filters)
        }.body()

    override suspend fun getAnalyticsCompare(query: AnalyticsCompareQuery): AnalyticsCompareDto =
        httpClient.get(endpoint("api/analytics/compare")) {
            optionalParameter("dimension", query.dimension.takeIf { it != AnalyticsDimension.Unknown }?.wireName)
            optionalParameter("keys", query.keys.takeIf { it.isNotEmpty() }?.joinToString(","))
            optionalParameter("metric", query.metric.takeIf { it != RankingMetric.Unknown }?.wireName)
            applyAnalyticsFilters(query.filters)
        }.body()

    override suspend fun getAnalyticsOutliers(query: AnalyticsOutliersQuery): List<AnalyticsOutliersDto> =
        httpClient.get(endpoint("api/analytics/outliers")) {
            optionalParameter("dimension", query.dimension.takeIf { it != AnalyticsDimension.Unknown }?.wireName)
            optionalParameter("metric", query.metric.takeIf { it != RankingMetric.Unknown }?.wireName)
            optionalParameter("limit", query.limit)
            applyAnalyticsFilters(query.filters)
        }.body()

    override suspend fun getRankings(): List<RankingDefinitionDto> =
        httpClient.get(endpoint("api/rankings")).body()

    override suspend fun getRankingDetail(query: RankingDetailQuery): RankingDetailDto =
        httpClient.get(endpoint("api/rankings/${pathSegment(query.rankingId)}")) {
            optionalParameter("targetType", query.targetType)
            optionalParameter("region", query.region)
            optionalParameter("category", query.category)
            optionalParameter("year", query.year)
            optionalParameter("limit", query.limit)
        }.body()

    override suspend fun getRankingContent(query: RankingContentQuery): RankingDetailDto =
        httpClient.get(endpoint("api/rankings/content")) {
            optionalParameter("metric", query.metric.takeIf { it != RankingMetric.Unknown }?.wireName)
            optionalParameter("targetType", query.targetType)
            optionalParameter("region", query.region)
            optionalParameter("category", query.category)
            optionalParameter("year", query.year)
            optionalParameter("limit", query.limit)
        }.body()

    // ── Research API ──

    override suspend fun getResearchPackages(): List<ResearchPackageSummaryDto> =
        httpClient.get(endpoint("api/research-packages")).body()

    override suspend fun getResearchPackageDetail(query: ResearchPackageDetailQuery): ResearchPackageDetailDto =
        httpClient.get(endpoint("api/research-packages/${pathSegment(query.packageId)}")).body()

    override suspend fun getResearchArtifact(query: ResearchArtifactQuery): String =
        httpClient.get(
            endpoint("api/research-packages/${pathSegment(query.packageId)}/artifacts/${pathSegment(query.artifactName)}")
        ).bodyAsText()

    override suspend fun getResearchReports(): List<ResearchReportSummaryDto> =
        httpClient.get(endpoint("api/research-reports")).body()

    override suspend fun getResearchReportDetail(query: ResearchReportDetailQuery): ResearchReportDetailDto =
        httpClient.get(endpoint("api/research-reports/${pathSegment(query.reportId)}")).body()

    override suspend fun getResearchReportByPackage(query: ResearchReportByPackageQuery): ResearchReportDetailDto =
        httpClient.get(endpoint("api/research-packages/${pathSegment(query.packageId)}/research-report")).body()

    // ── ContentExport API ──

    override suspend fun getExportTemplates(): List<ExportTemplateDto> =
        httpClient.get(endpoint("api/exports/templates")).body()

    override suspend fun previewExport(request: ExportRequestDto): ExportPreviewDto =
        httpClient.post(endpoint("api/exports/preview")) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    override suspend fun exportContent(request: ExportRequestDto): ExportContentResultDto =
        httpClient.post(endpoint("api/exports/content")) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    override suspend fun currentProfileId(): String = profileRepository.currentProfileId()

    private fun endpoint(path: String): String = "${baseUrl.trimEnd('/')}/${path.trimStart('/')}"

    private fun pathSegment(value: String): String =
        java.net.URLEncoder.encode(value, "UTF-8")
            .replace("+", "%20")

    private fun v3PagePath(type: SearchResultType): String = when (type) {
        SearchResultType.Article -> "article"
        SearchResultType.DirectoryItem -> "directory-item"
        SearchResultType.Inheritor -> "inheritor"
    }

    private fun HttpRequestBuilder.applyAnalyticsFilters(filters: AnalyticsFilters) {
        optionalParameter("targetType", filters.targetType)
        optionalParameter("region", filters.region)
        optionalParameter("category", filters.category)
        optionalParameter("year", filters.year)
        optionalParameter("kind", filters.kind)
        optionalParameter("hasImage", filters.hasImage)
        optionalParameter("hasAiResult", filters.hasAiResult)
    }
}

fun createHeritageHttpClient(
    config: HeritageApiConfig,
    profileRepository: LocalProfileRepository? = null,
): HttpClient = HttpClient(OkHttp) {
    expectSuccess = true

    engine {
        if (config.trustSelfSignedCertificates) {
            val trustManager = trustAllCertificatesManager()
            val socketFactory = trustAllSslSocketFactory(trustManager)
            config {
                sslSocketFactory(socketFactory, trustManager)
                hostnameVerifier { _, _ -> true }
            }
        }
    }

    install(ContentNegotiation) {
        json(HeritageJson)
    }

    if (profileRepository != null) {
        install(profileHeaderPlugin(profileRepository))
    }

    defaultRequest {
        accept(ContentType.Application.Json)
    }
}

fun createHeritageApiClient(
    config: HeritageApiConfig,
    profileRepository: LocalProfileRepository,
): HeritageApiClient = KtorHeritageApiClient(
    httpClient = createHeritageHttpClient(config, profileRepository),
    baseUrl = config.baseUrl,
    profileRepository = profileRepository,
)

/**
 * 为所有请求附加 `X-Heritage-Profile-Id` header 的 Ktor 插件。
 *
 * 该插件是 suspend 的，首次请求时会触发 [LocalProfileRepository.currentProfileId] 的生成/读取。
 */
fun profileHeaderPlugin(profileRepository: LocalProfileRepository) = createClientPlugin("ProfileHeader") {
    onRequest { request, _ ->
        val profileId = profileRepository.currentProfileId()
        if (!request.headers.contains("X-Heritage-Profile-Id")) {
            request.header("X-Heritage-Profile-Id", profileId)
        }
    }
}

private fun HttpRequestBuilder.optionalParameter(name: String, value: Any?) {
    if (value != null) {
        parameter(name, value)
    }
}

fun trustAllCertificatesManager(): X509TrustManager =
    object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) = Unit

        override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) = Unit

        override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
    }

fun trustAllSslSocketFactory(trustManager: X509TrustManager): SSLSocketFactory {
    val sslContext = SSLContext.getInstance("TLS")
    sslContext.init(null, arrayOf(trustManager), SecureRandom())
    return sslContext.socketFactory
}
