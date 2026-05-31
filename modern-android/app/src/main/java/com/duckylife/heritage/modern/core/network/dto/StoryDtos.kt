package com.duckylife.heritage.modern.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class DataStoryDto(
    val id: String = "",
    val title: String = "",
    val subtitle: String? = null,
    val heroImage: MediaAssetDto? = null,
    val sections: List<DataStorySectionDto> = emptyList(),
    val relatedTopics: List<ExploreTopicInfoDto> = emptyList(),
    val generatedAt: String? = null,
)

@Serializable
data class DataStorySectionDto(
    val id: String = "",
    val title: String = "",
    val type: String = "",
    val body: String? = null,
    val items: List<DataStoryItemDto> = emptyList(),
)

@Serializable
data class DataStoryItemDto(
    val type: String = "",
    val id: String? = null,
    val title: String = "",
    val summary: String? = null,
    val coverImage: MediaAssetDto? = null,
    val sourceUrl: String = "",
)
