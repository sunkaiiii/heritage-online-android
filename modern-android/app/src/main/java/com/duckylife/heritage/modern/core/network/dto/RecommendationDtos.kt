package com.duckylife.heritage.modern.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class BlendedRecommendationResponseDto(
    val items: List<BlendedRecommendationItemDto> = emptyList(),
    val query: BlendedRecommendationQueryDto = BlendedRecommendationQueryDto(),
    val generatedAt: String? = null,
)

@Serializable
data class BlendedRecommendationItemDto(
    val id: String = "",
    val type: String = "",
    val title: String = "",
    val subtitle: String? = null,
    val score: Double = 0.0,
    val reasons: List<String> = emptyList(),
    val scoreBreakdown: RecommendationScoreBreakdownDto = RecommendationScoreBreakdownDto(),
    val category: String? = null,
    val region: String? = null,
    val coverImage: MediaAssetDto? = null,
    val sourceUrl: String = "",
)

@Serializable
data class RecommendationScoreBreakdownDto(
    val explicit: Double = 0.0,
    val inferred: Double = 0.0,
    val embedding: Double = 0.0,
    val sameCategory: Double = 0.0,
    val sameRegion: Double = 0.0,
)

@Serializable
data class BlendedRecommendationQueryDto(
    val type: String = "",
    val id: String = "",
    val limit: Int = 10,
    val ruleWeight: Double = 1.0,
    val semanticWeight: Double = 1.0,
    val sameCategoryWeight: Double = 1.0,
    val sameRegionWeight: Double = 1.0,
    val diversify: Boolean = true,
)
