package com.duckylife.heritage.modern.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class CollectionTopicType(val wireName: String) {
    @SerialName("region")
    Region("region"),

    @SerialName("category")
    Category("category"),

    @SerialName("year")
    Year("year"),
}

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
