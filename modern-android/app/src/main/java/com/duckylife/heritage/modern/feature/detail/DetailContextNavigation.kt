package com.duckylife.heritage.modern.feature.detail

import com.duckylife.heritage.modern.core.network.dto.BlendedRecommendationItemDto
import com.duckylife.heritage.modern.core.network.dto.SearchResultType

sealed interface DetailContextTarget {
    data class Article(val id: String) : DetailContextTarget
    data class DirectoryItem(val id: String) : DetailContextTarget
    data class Inheritor(val id: String) : DetailContextTarget
    data class Collection(val id: String) : DetailContextTarget
    data class Topic(val type: String, val key: String) : DetailContextTarget
}

fun contextItemTarget(id: String?, type: String?): DetailContextTarget? {
    val safeId = id?.takeIf { it.isNotBlank() } ?: return null
    return when (SearchResultType.fromWireName(type)) {
        SearchResultType.Article -> DetailContextTarget.Article(safeId)
        SearchResultType.DirectoryItem -> DetailContextTarget.DirectoryItem(safeId)
        SearchResultType.Inheritor -> DetailContextTarget.Inheritor(safeId)
        null -> null
    }
}

fun BlendedRecommendationItemDto.toDetailContextTarget(): DetailContextTarget? =
    contextItemTarget(id, type)
