package com.duckylife.heritage.modern.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class CompareResultDto(
    val left: CompareSideDto = CompareSideDto(),
    val right: CompareSideDto = CompareSideDto(),
    val summary: CompareSummaryDto = CompareSummaryDto(),
    val sharedCategories: List<String> = emptyList(),
    val leftUniqueCategories: List<String> = emptyList(),
    val rightUniqueCategories: List<String> = emptyList(),
    val sharedRegions: List<String> = emptyList(),
    val leftUniqueRegions: List<String> = emptyList(),
    val rightUniqueRegions: List<String> = emptyList(),
    val leftFeaturedItems: List<CollectionItemDto> = emptyList(),
    val rightFeaturedItems: List<CollectionItemDto> = emptyList(),
    val generatedAt: String? = null,
)

@Serializable
data class CompareSideDto(
    val key: String = "",
    val title: String = "",
    val directoryItemCount: Long = 0,
    val inheritorCount: Long = 0,
    val articleCount: Long = 0,
    val total: Long = 0,
    val topCategories: List<TaxonomyCategoryCountDto> = emptyList(),
    val topRegions: List<TaxonomyRegionCountDto> = emptyList(),
)

@Serializable
data class CompareSummaryDto(
    val winnerByDirectoryItems: String? = null,
    val winnerByInheritors: String? = null,
    val winnerByArticles: String? = null,
    val winnerByTotal: String? = null,
)
