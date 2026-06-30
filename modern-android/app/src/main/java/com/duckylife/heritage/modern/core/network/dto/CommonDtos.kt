package com.duckylife.heritage.modern.core.network.dto

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
data class ProblemDetailsDto(
    val type: String? = null,
    val title: String? = null,
    val status: Int? = null,
    val detail: String? = null,
    val instance: String? = null,
    val traceId: String? = null,
)

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
