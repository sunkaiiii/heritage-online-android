package com.duckylife.heritage.modern.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class SearchResultType(val wireName: String) {
    @SerialName("article")
    Article("article"),

    @SerialName("directoryItem")
    DirectoryItem("directoryItem"),

    @SerialName("inheritor")
    Inheritor("inheritor");

    companion object {
        fun fromWireName(value: String?): SearchResultType? =
            entries.firstOrNull { it.wireName == value }
    }
}

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
