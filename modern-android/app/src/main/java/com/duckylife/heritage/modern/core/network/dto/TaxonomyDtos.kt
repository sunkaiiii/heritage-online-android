package com.duckylife.heritage.modern.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class TaxonomyTopicDto(
    val type: String = "",
    val key: String = "",
    val title: String = "",
    val subtitle: String? = null,
    val directoryItemCount: Long = 0,
    val inheritorCount: Long = 0,
    val articleCount: Long = 0,
    val total: Long = 0,
    val topRegions: List<TaxonomyRegionCountDto> = emptyList(),
    val topCategories: List<TaxonomyCategoryCountDto> = emptyList(),
    val coverImage: MediaAssetDto? = null,
)

@Serializable
data class TaxonomyRegionCountDto(
    val region: String = "",
    val count: Long = 0,
)

@Serializable
data class TaxonomyCategoryCountDto(
    val category: String = "",
    val count: Long = 0,
)

@Serializable
data class TaxonomyKindDto(
    val key: String = "",
    val title: String = "",
    val directoryItemCount: Long = 0,
    val inheritorCount: Long = 0,
    val total: Long = 0,
)

@Serializable
data class TaxonomyIndexDto<T>(
    val items: List<T> = emptyList(),
    val generatedAt: String? = null,
)

@Serializable
data class TaxonomyStatDto(
    val directoryItemCount: Long = 0,
    val inheritorCount: Long = 0,
    val articleCount: Long = 0,
    val total: Long = 0,
)

@Serializable
data class TaxonomyCategoryDetailDto(
    val topic: TaxonomyTopicDto = TaxonomyTopicDto(),
    val stats: TaxonomyStatDto = TaxonomyStatDto(),
    val topRegions: List<TaxonomyRegionCountDto> = emptyList(),
    val articles: List<ArticleSummaryDto> = emptyList(),
    val directoryItems: List<DirectoryItemSummaryDto> = emptyList(),
    val inheritors: List<InheritorSummaryDto> = emptyList(),
    val relatedCategories: List<String> = emptyList(),
    val recommendedCollections: List<CollectionItemDto> = emptyList(),
    val generatedAt: String? = null,
)

@Serializable
data class TaxonomyRegionDetailDto(
    val topic: TaxonomyTopicDto = TaxonomyTopicDto(),
    val stats: TaxonomyStatDto = TaxonomyStatDto(),
    val topCategories: List<TaxonomyCategoryCountDto> = emptyList(),
    val articles: List<ArticleSummaryDto> = emptyList(),
    val directoryItems: List<DirectoryItemSummaryDto> = emptyList(),
    val inheritors: List<InheritorSummaryDto> = emptyList(),
    val relatedRegions: List<String> = emptyList(),
    val recommendedCollections: List<CollectionItemDto> = emptyList(),
    val generatedAt: String? = null,
)
