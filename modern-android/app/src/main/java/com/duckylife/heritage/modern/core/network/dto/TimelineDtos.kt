package com.duckylife.heritage.modern.core.network.dto

import kotlinx.serialization.Serializable

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
