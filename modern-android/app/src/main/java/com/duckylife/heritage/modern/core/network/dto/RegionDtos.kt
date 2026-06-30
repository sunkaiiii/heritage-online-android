package com.duckylife.heritage.modern.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegionStatisticDto(
    val region: String? = null,
    val displayName: String? = null,
    val directoryItemCount: Long = 0,
    val inheritorCount: Long = 0,
)

@Serializable
data class RegionOverviewDto(
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
