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
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

interface HeritageApiClient {
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
}

class KtorHeritageApiClient(
    private val httpClient: HttpClient,
    private val baseUrl: String,
) : HeritageApiClient {
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
        httpClient.get(endpoint("api/articles/$id")).body()

    override suspend fun getArticleBySourceId(
        sourceId: String,
        category: ArticleCategory,
    ): ArticleDetailDto =
        httpClient.get(endpoint("api/articles/source/$sourceId")) {
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
        httpClient.get(endpoint("api/articles/$id/context")).body()

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
        httpClient.get(endpoint("api/directory-items/$id")).body()

    override suspend fun getDirectoryItemBySourceId(
        sourceId: String,
        kind: DirectoryItemKind,
    ): DirectoryItemDetailDto =
        httpClient.get(endpoint("api/directory-items/source/$sourceId")) {
            optionalParameter("kind", kind.wireName)
        }.body()

    override suspend fun getDirectoryItemContext(id: String): DetailContextDto =
        httpClient.get(endpoint("api/directory-items/$id/context")).body()

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
        httpClient.get(endpoint("api/inheritors/$id")).body()

    override suspend fun getInheritorBySourceId(sourceId: String): InheritorDetailDto =
        httpClient.get(endpoint("api/inheritors/source/$sourceId")).body()

    override suspend fun getInheritorContext(id: String): DetailContextDto =
        httpClient.get(endpoint("api/inheritors/$id/context")).body()

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
        httpClient.get(endpoint("api/explore/learning-paths/$id")) {
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

    private fun endpoint(path: String): String = "${baseUrl.trimEnd('/')}/${path.trimStart('/')}"

    private fun pathSegment(value: String): String =
        java.net.URLEncoder.encode(value, "UTF-8")
            .replace("+", "%20")
}

fun createHeritageHttpClient(
    config: HeritageApiConfig,
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

    defaultRequest {
        accept(ContentType.Application.Json)
    }
}

fun createHeritageApiClient(
    config: HeritageApiConfig,
): HeritageApiClient = KtorHeritageApiClient(
    httpClient = createHeritageHttpClient(config),
    baseUrl = config.baseUrl,
)

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
