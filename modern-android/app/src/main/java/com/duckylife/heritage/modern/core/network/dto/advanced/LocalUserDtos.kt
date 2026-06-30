package com.duckylife.heritage.modern.core.network.dto.advanced

import com.duckylife.heritage.modern.core.network.dto.PagedResult
import kotlinx.serialization.Serializable

@Serializable
data class LocalUserProfileDto(
    val profileId: String,
    val displayName: String = "",
    val favoriteCount: Long = 0L,
    val historyCount: Long = 0L,
    val learningRouteCount: Long = 0L,
    val generatedAt: String? = null,
)

@Serializable
data class LocalUserSummaryDto(
    val profileId: String,
    val favoriteCount: Int = 0,
    val historyCount: Int = 0,
    val learningRouteCount: Int = 0,
    val recentFavorites: List<LocalFavoriteDto> = emptyList(),
    val recentHistory: List<LocalHistoryDto> = emptyList(),
    val recentProgress: List<LocalLearningProgressDto> = emptyList(),
    val generatedAt: String? = null,
)

@Serializable
data class LocalFavoriteDto(
    val id: String,
    val targetType: LocalUserTargetType,
    val targetId: String,
    val titleSnapshot: String? = null,
    val coverImageUrlSnapshot: String? = null,
    val tags: List<String> = emptyList(),
    val note: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

@Serializable
data class FavoriteCreateRequestDto(
    val targetType: LocalUserTargetType,
    val targetId: String,
    val tags: List<String> = emptyList(),
    val note: String? = null,
)

@Serializable
data class LocalHistoryDto(
    val id: String,
    val targetType: LocalUserTargetType,
    val targetId: String,
    val titleSnapshot: String? = null,
    val viewedAt: String? = null,
    val viewCount: Int = 1,
    val lastPosition: String? = null,
)

typealias LocalViewHistoryDto = LocalHistoryDto

@Serializable
data class HistoryRecordRequestDto(
    val targetType: LocalUserTargetType,
    val targetId: String,
    val lastPosition: String? = null,
)

typealias LocalViewHistoryCreateRequestDto = HistoryRecordRequestDto

@Serializable
data class LocalLearningProgressDto(
    val id: String? = null,
    val routeId: String,
    val routeTitle: String? = null,
    val completedStepIds: List<String> = emptyList(),
    val currentStepId: String? = null,
    val percent: Int = 0,
    val startedAt: String? = null,
    val updatedAt: String? = null,
    val completedAt: String? = null,
)

@Serializable
data class LearningProgressUpdateDto(
    val completedStepIds: List<String> = emptyList(),
    val currentStepId: String? = null,
)

@Serializable
data class JourneySignalsDto(
    val profileId: String? = null,
    val signals: List<String> = emptyList(),
    val warning: String? = null,
    val generatedAt: String? = null,
)

@Serializable
data class JourneyResponseDto(
    val profileId: String? = null,
    val strategy: JourneyStrategy = JourneyStrategy.Balanced,
    val signals: List<String> = emptyList(),
    val items: List<JourneyItemDto> = emptyList(),
    val trail: GraphTrailDto? = null,
    val warnings: List<String> = emptyList(),
    val generatedAt: String? = null,
)

@Serializable
data class JourneyItemDto(
    val node: GraphNodeDto,
    val score: Double? = null,
    val scoreBreakdown: JourneyScoreBreakdownDto? = null,
    val reasons: List<String> = emptyList(),
    val evidence: List<String> = emptyList(),
    val isPreviouslyViewed: Boolean = false,
    val isFavorite: Boolean = false,
)

@Serializable
data class JourneyScoreBreakdownDto(
    val favoriteAffinity: Double = 0.0,
    val historyAffinity: Double = 0.0,
    val learningProgressAffinity: Double = 0.0,
    val graphAffinity: Double = 0.0,
    val noveltyBoost: Double = 0.0,
    val recentViewedPenalty: Double = 0.0,
    val aiEvidenceBoost: Double = 0.0,
    val total: Double = 0.0,
)
