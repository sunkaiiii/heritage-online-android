package com.duckylife.heritage.modern.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PagedResult<T>(
    val items: List<T> = emptyList(),
    val page: Int = 1,
    val pageSize: Int = 20,
    val hasMore: Boolean = false,
    val total: Long = 0,
)

@Serializable
data class MediaAssetDto(
    val sourceUrl: String? = null,
    val originalUrl: String? = null,
    val displayUrl: String? = null,
    val thumbnailUrl: String? = null,
    val altText: String? = null,
)

@Serializable
enum class ArticleCategory(val wireName: String) {
    @SerialName("news")
    News("news"),

    @SerialName("forum")
    Forum("forum"),

    @SerialName("specialTopic")
    SpecialTopic("specialTopic"),
}

@Serializable
enum class ArticleContentBlockType {
    @SerialName("text")
    Text,

    @SerialName("image")
    Image,

    @SerialName("heading")
    Heading,
}

@Serializable
data class ArticleContentBlockDto(
    val type: ArticleContentBlockType = ArticleContentBlockType.Text,
    val text: String? = null,
    val image: MediaAssetDto? = null,
)

@Serializable
data class ArticleReferenceDto(
    val title: String? = null,
    val detailUrl: String? = null,
    val sourceId: String? = null,
    val publishedAt: String? = null,
)

@Serializable
data class ArticleSummaryDto(
    val id: String? = null,
    val category: ArticleCategory = ArticleCategory.News,
    val title: String? = null,
    val summary: String? = null,
    val publishedAt: String? = null,
    val coverImage: MediaAssetDto? = null,
    val sourceUrl: String? = null,
)

@Serializable
data class ArticleDetailDto(
    val id: String? = null,
    val category: ArticleCategory = ArticleCategory.News,
    val title: String? = null,
    val summary: String? = null,
    val publishedAt: String? = null,
    val coverImage: MediaAssetDto? = null,
    val sourceUrl: String? = null,
    val sourceName: String? = null,
    val author: String? = null,
    val editor: String? = null,
    val contentBlocks: List<ArticleContentBlockDto> = emptyList(),
    val relatedArticles: List<ArticleReferenceDto> = emptyList(),
)

@Serializable
enum class DirectoryItemKind(val wireName: String) {
    @SerialName("nationalProject")
    NationalProject("nationalProject"),

    @SerialName("culturalEcoZone")
    CulturalEcoZone("culturalEcoZone"),

    @SerialName("productiveProtectionBase")
    ProductiveProtectionBase("productiveProtectionBase"),

    @SerialName("unescoEntry")
    UnescoEntry("unescoEntry"),

    @SerialName("chinaUnescoEntry")
    ChinaUnescoEntry("chinaUnescoEntry"),

    @SerialName("contractingState")
    ContractingState("contractingState"),
}

@Serializable
enum class DirectoryStatisticDimension(val wireName: String) {
    @SerialName("publishedYear")
    PublishedYear("publishedYear"),

    @SerialName("category")
    Category("category"),

    @SerialName("region")
    Region("region"),

    @SerialName("batch")
    Batch("batch"),

    @SerialName("listType")
    ListType("listType"),

    @SerialName("nominationType")
    NominationType("nominationType"),

    @SerialName("protectionUnit")
    ProtectionUnit("protectionUnit"),
}

@Serializable
data class DirectoryStatisticsOverviewDto(
    val kind: String? = null,
    val total: Long = 0,
    val generatedAt: String? = null,
    val dimensions: List<DirectoryStatisticDimensionDto> = emptyList(),
)

@Serializable
data class DirectoryStatisticDimensionDto(
    val dimension: String? = null,
    val items: List<DirectoryStatisticItemDto> = emptyList(),
)

@Serializable
data class DirectoryStatisticItemDto(
    val key: String? = null,
    val name: String? = null,
    val value: Long = 0,
)

@Serializable
data class DirectoryReferenceDto(
    val title: String? = null,
    val detailUrl: String? = null,
    val sourceId: String? = null,
    val kind: String? = null,
    val category: String? = null,
    val region: String? = null,
    val publishedYear: Int? = null,
)

@Serializable
data class DirectoryItemSummaryDto(
    val id: String? = null,
    val kind: DirectoryItemKind = DirectoryItemKind.NationalProject,
    val title: String? = null,
    val summary: String? = null,
    val category: String? = null,
    val region: String? = null,
    val projectCode: String? = null,
    val batch: String? = null,
    val publishedYear: Int? = null,
    val listType: String? = null,
    val coverImage: MediaAssetDto? = null,
    val sourceUrl: String? = null,
)

@Serializable
data class DirectoryItemDetailDto(
    val id: String? = null,
    val kind: DirectoryItemKind = DirectoryItemKind.NationalProject,
    val title: String? = null,
    val summary: String? = null,
    val category: String? = null,
    val region: String? = null,
    val projectCode: String? = null,
    val batch: String? = null,
    val publishedYear: Int? = null,
    val listType: String? = null,
    val coverImage: MediaAssetDto? = null,
    val sourceUrl: String? = null,
    val nominationType: String? = null,
    val protectionUnit: String? = null,
    val gallery: List<MediaAssetDto> = emptyList(),
    val contentBlocks: List<ArticleContentBlockDto> = emptyList(),
    val relatedProjects: List<DirectoryReferenceDto> = emptyList(),
    val relatedInheritors: List<DirectoryReferenceDto> = emptyList(),
    val relatedDocuments: List<DirectoryReferenceDto> = emptyList(),
)

@Serializable
data class InheritorSummaryDto(
    val id: String? = null,
    val name: String? = null,
    val gender: String? = null,
    val birthDateText: String? = null,
    val ethnicity: String? = null,
    val category: String? = null,
    val projectCode: String? = null,
    val projectName: String? = null,
    val region: String? = null,
    val batch: String? = null,
    val description: String? = null,
    val coverImage: MediaAssetDto? = null,
    val sourceUrl: String? = null,
)

@Serializable
data class InheritorDetailDto(
    val id: String? = null,
    val name: String? = null,
    val gender: String? = null,
    val birthDateText: String? = null,
    val ethnicity: String? = null,
    val category: String? = null,
    val projectCode: String? = null,
    val projectName: String? = null,
    val region: String? = null,
    val batch: String? = null,
    val description: String? = null,
    val coverImage: MediaAssetDto? = null,
    val sourceUrl: String? = null,
    val contentBlocks: List<ArticleContentBlockDto> = emptyList(),
    val relatedProjects: List<DirectoryReferenceDto> = emptyList(),
    val relatedInheritors: List<DirectoryReferenceDto> = emptyList(),
)

@Serializable
data class HomeBannerDto(
    val id: String? = null,
    val sortOrder: Int = 0,
    val targetUrl: String? = null,
    val displayImage: MediaAssetDto? = null,
    val mobileImage: MediaAssetDto? = null,
    val desktopImage: MediaAssetDto? = null,
)

@Serializable
data class ProblemDetailsDto(
    val type: String? = null,
    val title: String? = null,
    val status: Int? = null,
    val detail: String? = null,
    val instance: String? = null,
)

// ---------------------------------------------------------------------------
// Home Feed
// ---------------------------------------------------------------------------

@Serializable
data class HomeFeedSummaryDto(
    val totalArticles: Long = 0,
    val totalDirectoryItems: Long = 0,
    val totalInheritors: Long = 0,
    val directoryKindCounts: Map<String, Long> = emptyMap(),
)

@Serializable
data class HomeFeedDto(
    val banners: List<HomeBannerDto> = emptyList(),
    val latestNews: List<ArticleSummaryDto> = emptyList(),
    val latestSpecialTopics: List<ArticleSummaryDto> = emptyList(),
    val latestForumArticles: List<ArticleSummaryDto> = emptyList(),
    val featuredDirectoryItems: List<DirectoryItemSummaryDto> = emptyList(),
    val featuredInheritors: List<InheritorSummaryDto> = emptyList(),
    val summary: HomeFeedSummaryDto? = null,
)

// ---------------------------------------------------------------------------
// Recommendation / Graph / Context
// ---------------------------------------------------------------------------

@Serializable
data class RecommendationDto(
    val id: String? = null,
    val type: String? = null,
    val title: String? = null,
    val subtitle: String? = null,
    val source: String? = null,
    val relationType: String? = null,
    val reason: String? = null,
    val weight: Double = 0.0,
    val category: String? = null,
    val region: String? = null,
    val publishedAt: String? = null,
    val publishedYear: Int? = null,
    val coverImage: MediaAssetDto? = null,
    val sourceUrl: String? = null,
)

@Serializable
data class GraphNodeDto(
    val id: String? = null,
    val type: String? = null,
    val title: String? = null,
    val category: String? = null,
    val region: String? = null,
    val sourceUrl: String? = null,
    val subtitle: String? = null,
    val coverImage: MediaAssetDto? = null,
)

@Serializable
data class GraphEdgeDto(
    @SerialName("from") val fromId: String? = null,
    @SerialName("to") val toId: String? = null,
    val label: String? = null,
    val relationType: String? = null,
    val reason: String? = null,
    val source: String? = null,
    val weight: Double = 0.0,
)

@Serializable
data class GraphDto(
    val nodes: List<GraphNodeDto> = emptyList(),
    val edges: List<GraphEdgeDto> = emptyList(),
)

@Serializable
data class RelatedSummaryDto(
    val id: String? = null,
    val type: String? = null,
    val title: String? = null,
    val category: String? = null,
    val kind: String? = null,
    val region: String? = null,
    val sourceUrl: String? = null,
)

@Serializable
data class ContextCollectionDto(
    val id: String? = null,
    val title: String? = null,
    val items: List<CollectionItemDto> = emptyList(),
)

@Serializable
data class DetailContextDto(
    val related: List<RelatedSummaryDto> = emptyList(),
    val graph: GraphDto? = null,
    val collections: List<ContextCollectionDto> = emptyList(),
    val exploreTopics: List<ExploreTopicInfoDto> = emptyList(),
    val recommendations: List<RecommendationDto> = emptyList(),
    val semanticRecommendations: List<RecommendationDto> = emptyList(),
)

// ---------------------------------------------------------------------------
// Shared facet / link types
// ---------------------------------------------------------------------------

@Serializable
data class FacetBucketDto(
    val key: String? = null,
    val count: Long = 0,
)

@Serializable
data class ExploreTopicLinkDto(
    val type: String? = null,
    val key: String? = null,
    val title: String? = null,
)

// ---------------------------------------------------------------------------
// Search v2
// ---------------------------------------------------------------------------

@Serializable
data class SearchFacetsDto(
    val types: List<FacetBucketDto> = emptyList(),
    val categories: List<FacetBucketDto> = emptyList(),
    val regions: List<FacetBucketDto> = emptyList(),
    val kinds: List<FacetBucketDto> = emptyList(),
    val years: List<FacetBucketDto> = emptyList(),
)

@Serializable
data class SearchResultItemDto(
    val id: String? = null,
    val type: String? = null,
    val title: String? = null,
    val summary: String? = null,
    val category: String? = null,
    val kind: String? = null,
    val region: String? = null,
    val publishedAt: String? = null,
    val publishedYear: Int? = null,
    val coverImage: MediaAssetDto? = null,
    val sourceUrl: String? = null,
    val highlights: List<String> = emptyList(),
    val matchedFields: List<String> = emptyList(),
    val score: Int = 0,
)

@Serializable
data class SearchV2ResponseDto(
    val items: List<SearchResultItemDto> = emptyList(),
    val page: Int = 1,
    val pageSize: Int = 20,
    val hasMore: Boolean = false,
    val total: Long = 0,
    val facets: SearchFacetsDto? = null,
    val query: String = "",
)

@Serializable
data class SearchSuggestionDto(
    val text: String? = null,
    val type: String? = null,
)

// ---------------------------------------------------------------------------
// Timeline v2
// ---------------------------------------------------------------------------

@Serializable
data class TimelineV2FacetsDto(
    val types: List<FacetBucketDto> = emptyList(),
    val categories: List<FacetBucketDto> = emptyList(),
    val regions: List<FacetBucketDto> = emptyList(),
    val kinds: List<FacetBucketDto> = emptyList(),
)

@Serializable
data class TimelineItemDto(
    val id: String? = null,
    val type: String? = null,
    val title: String? = null,
    val summary: String? = null,
    val category: String? = null,
    val kind: String? = null,
    val region: String? = null,
    val date: String? = null,
    val year: Int? = null,
    val coverImage: MediaAssetDto? = null,
    val sourceUrl: String? = null,
)

@Serializable
data class TimelineYearBucketDto(
    val year: Int = 0,
    val total: Long = 0,
    val articleCount: Long = 0,
    val directoryItemCount: Long = 0,
    val inheritorCount: Long = 0,
)

@Serializable
data class TimelineV2ResponseDto(
    val items: List<TimelineItemDto> = emptyList(),
    val page: Int = 1,
    val pageSize: Int = 20,
    val hasMore: Boolean = false,
    val total: Long = 0,
    val facets: TimelineV2FacetsDto? = null,
)

// ---------------------------------------------------------------------------
// Explore / Learning Path
// ---------------------------------------------------------------------------

@Serializable
data class ExploreTopicInfoDto(
    val type: String? = null,
    val key: String? = null,
    val title: String? = null,
    val subtitle: String? = null,
)

@Serializable
data class ExploreTopicItemDto(
    val id: String? = null,
    val type: String? = null,
    val title: String? = null,
    val summary: String? = null,
    val category: String? = null,
    val region: String? = null,
    val kind: String? = null,
    val year: Int? = null,
    val count: Long = 0,
    val coverImage: MediaAssetDto? = null,
    val sourceUrl: String? = null,
)

@Serializable
data class ExploreTopicSectionDto(
    val id: String? = null,
    val title: String? = null,
    val subtitle: String? = null,
    val items: List<ExploreTopicItemDto> = emptyList(),
)

@Serializable
data class ExploreTopicStatDto(
    val name: String? = null,
    val value: Long = 0,
)

@Serializable
data class ExploreTopicV2Dto(
    val topic: ExploreTopicInfoDto? = null,
    val stats: List<ExploreTopicStatDto> = emptyList(),
    val sections: List<ExploreTopicSectionDto> = emptyList(),
    val relatedTopics: List<ExploreTopicLinkDto> = emptyList(),
    val timeline: List<ExploreTopicItemDto> = emptyList(),
    val generatedAt: String? = null,
)

@Serializable
data class ExploreIndexDto(
    val regions: List<ExploreTopicInfoDto> = emptyList(),
    val categories: List<ExploreTopicInfoDto> = emptyList(),
    val years: List<ExploreTopicInfoDto> = emptyList(),
)

@Serializable
data class LearningPathStepDto(
    val id: String? = null,
    val title: String? = null,
    val subtitle: String? = null,
    val topic: ExploreTopicInfoDto? = null,
    val items: List<ExploreTopicItemDto> = emptyList(),
)

@Serializable
data class LearningPathDto(
    val id: String? = null,
    val title: String? = null,
    val subtitle: String? = null,
    val topics: List<ExploreTopicLinkDto> = emptyList(),
    val description: String? = null,
    val coverImage: String? = null,
    val estimatedItemCount: Int = 0,
    val stepCount: Int = 0,
    val tags: List<String> = emptyList(),
)

@Serializable
data class LearningPathDetailDto(
    val id: String? = null,
    val title: String? = null,
    val subtitle: String? = null,
    val description: String? = null,
    val tags: List<String> = emptyList(),
    val steps: List<LearningPathStepDto> = emptyList(),
    val featuredItems: List<ExploreTopicItemDto> = emptyList(),
    val relatedTopics: List<ExploreTopicLinkDto> = emptyList(),
    val generatedAt: String? = null,
)

// ---------------------------------------------------------------------------
// Region Atlas
// ---------------------------------------------------------------------------

@Serializable
data class RegionAtlasItemDto(
    val region: String? = null,
    val displayName: String? = null,
    val directoryItemCount: Long = 0,
    val inheritorCount: Long = 0,
    val total: Long = 0,
    val topCategories: List<FacetBucketDto> = emptyList(),
    val topKinds: List<FacetBucketDto> = emptyList(),
    val coverImage: MediaAssetDto? = null,
)

@Serializable
data class RegionAtlasTotalsDto(
    val directoryItemCount: Long = 0,
    val inheritorCount: Long = 0,
    val regionCount: Int = 0,
)

@Serializable
data class RegionAtlasDto(
    val regions: List<RegionAtlasItemDto> = emptyList(),
    val totals: RegionAtlasTotalsDto? = null,
    val generatedAt: String? = null,
)

@Serializable
data class RegionAtlasDetailStatsDto(
    val directoryItemCount: Long = 0,
    val inheritorCount: Long = 0,
    val total: Long = 0,
)

@Serializable
data class RegionAtlasDetailDto(
    val region: String? = null,
    val displayName: String? = null,
    val stats: RegionAtlasDetailStatsDto? = null,
    val categoryBreakdown: List<FacetBucketDto> = emptyList(),
    val kindBreakdown: List<FacetBucketDto> = emptyList(),
    val featuredDirectoryItems: List<DirectoryItemSummaryDto> = emptyList(),
    val featuredInheritors: List<InheritorSummaryDto> = emptyList(),
    val relatedArticles: List<ArticleSummaryDto> = emptyList(),
    val timeline: List<ExploreTopicItemDto> = emptyList(),
    val relatedRegions: List<ExploreTopicLinkDto> = emptyList(),
    val generatedAt: String? = null,
)

// ---------------------------------------------------------------------------
// Collections
// ---------------------------------------------------------------------------

@Serializable
data class CollectionItemDto(
    val id: String? = null,
    val type: String? = null,
    val title: String? = null,
    val summary: String? = null,
    val category: String? = null,
    val region: String? = null,
    val publishedAt: String? = null,
    val publishedYear: Int? = null,
    val coverImage: MediaAssetDto? = null,
    val sourceUrl: String? = null,
)

@Serializable
data class FeaturedCollectionDto(
    val id: String? = null,
    val title: String? = null,
    val subtitle: String? = null,
    val itemCount: Int = 0,
)

@Serializable
data class CollectionDto(
    val id: String? = null,
    val title: String? = null,
    val subtitle: String? = null,
    val type: String? = null,
    val tags: List<String> = emptyList(),
    val generatedAt: String? = null,
    val items: List<CollectionItemDto> = emptyList(),
)
