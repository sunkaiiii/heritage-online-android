package com.duckylife.heritage.modern.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class DiscoveryItemDto(
    val id: String? = null,
    val type: String = "",
    val title: String = "",
    val summary: String? = null,
    val category: String? = null,
    val kind: String? = null,
    val region: String? = null,
    val publishedAt: String? = null,
    val publishedYear: Int? = null,
    val coverImage: MediaAssetDto? = null,
    val sourceUrl: String = "",
)

@Serializable
data class DiscoveryTodayDto(
    val featuredDirectoryItem: DiscoveryItemDto? = null,
    val featuredInheritor: DiscoveryItemDto? = null,
    val articles: List<DiscoveryItemDto> = emptyList(),
    val date: String = "",
)

@Serializable
data class DiscoverySectionDto(
    val id: String = "",
    val title: String = "",
    val subtitle: String? = null,
    val items: List<DiscoveryItemDto> = emptyList(),
)

@Serializable
data class DiscoveryWeeklyDto(
    val weekId: String = "",
    val sections: List<DiscoverySectionDto> = emptyList(),
    val generatedAt: String? = null,
)

@Serializable
data class DiscoveryTrendingDto(
    val items: List<DiscoveryItemDto> = emptyList(),
    val generatedAt: String? = null,
)

@Serializable
data class DiscoveryDeepDiveDto(
    val seed: DiscoveryItemDto? = null,
    val related: List<DiscoveryItemDto> = emptyList(),
    val generatedAt: String? = null,
)
