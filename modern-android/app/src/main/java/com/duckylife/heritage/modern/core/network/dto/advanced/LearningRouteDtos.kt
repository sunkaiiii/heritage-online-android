package com.duckylife.heritage.modern.core.network.dto.advanced

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.Serializable

@Serializable
data class LearningRouteSummaryDto(
    val routeId: String,
    val title: String? = null,
    val subtitle: String? = null,
    val description: String? = null,
    val difficulty: LearningRouteDifficulty = LearningRouteDifficulty.Unknown,
    val estimatedMinutes: Int = 0,
    val stepCount: Int = 0,
    val tags: List<String> = emptyList(),
    val coverImageUrl: String? = null,
)

@Serializable
data class LearningRouteDetailDto(
    val routeId: String,
    val title: String? = null,
    val description: String? = null,
    val difficulty: LearningRouteDifficulty = LearningRouteDifficulty.Unknown,
    val estimatedMinutes: Int = 0,
    val sections: List<LearningRouteSectionDto> = emptyList(),
    val steps: List<LearningRouteStepDto> = emptyList(),
    val relatedRoutes: List<LearningRouteSummaryDto> = emptyList(),
    val generatedAt: String? = null,
)

@Serializable
data class LearningRouteSectionDto(
    val sectionId: String,
    val title: String? = null,
    val description: String? = null,
    val stepIds: List<String> = emptyList(),
)

@Serializable
data class LearningRouteStepDto(
    val stepId: String,
    val order: Int = 0,
    val title: String? = null,
    val description: String? = null,
    val targetType: String? = null,
    val targetId: String? = null,
    val content: ContentRefDto? = null,
    val reason: String? = null,
    val estimatedMinutes: Int = 0,
    val required: Boolean = false,
)

@Serializable
data class LearningRouteNextDto(
    val routeId: String? = null,
    val completed: Boolean = false,
    val nextStep: LearningRouteStepDto? = null,
    val relatedRoutes: List<LearningRouteSummaryDto> = emptyList(),
)

@Serializable
data class LearningRouteBuildRequestDto(
    val seedType: LearningRouteSeedType,
    val seedKey: String,
    @EncodeDefault val difficulty: LearningRouteDifficulty = LearningRouteDifficulty.Beginner,
    @EncodeDefault val limit: Int = 8,
    @EncodeDefault val includeArticles: Boolean = true,
    @EncodeDefault val includeDirectoryItems: Boolean = true,
    @EncodeDefault val includeInheritors: Boolean = true,
) {
    init {
        require(seedType != LearningRouteSeedType.Unknown) { "seedType must be specified" }
        require(seedKey.isNotBlank()) { "seedKey must not be blank" }
        require(difficulty != LearningRouteDifficulty.All && difficulty != LearningRouteDifficulty.Unknown) {
            "difficulty must be beginner/intermediate/deep"
        }
        require(limit in 1..20) { "limit must be in 1..20" }
    }
}
