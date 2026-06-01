package com.duckylife.heritage.modern.feature.detail

import com.duckylife.heritage.modern.core.network.dto.BlendedRecommendationItemDto

sealed interface DetailContextTarget {
    data class Article(val id: String) : DetailContextTarget
    data class DirectoryItem(val id: String) : DetailContextTarget
    data class Inheritor(val id: String) : DetailContextTarget
    data class Collection(val id: String) : DetailContextTarget
    data class Topic(val type: String, val key: String) : DetailContextTarget
}

fun contextItemTarget(id: String, type: String?): DetailContextTarget? =
    when (type) {
        "article" -> DetailContextTarget.Article(id)
        "directoryItem" -> DetailContextTarget.DirectoryItem(id)
        "inheritor" -> DetailContextTarget.Inheritor(id)
        else -> null
    }

fun BlendedRecommendationItemDto.toDetailContextTarget(): DetailContextTarget? =
    contextItemTarget(id, type)
