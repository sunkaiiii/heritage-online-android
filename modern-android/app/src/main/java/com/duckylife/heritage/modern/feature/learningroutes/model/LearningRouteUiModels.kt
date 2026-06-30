package com.duckylife.heritage.modern.feature.learningroutes.model

import com.duckylife.heritage.modern.core.network.dto.advanced.LearningRouteDifficulty

/**
 * 学习路线列表卡片使用的 UI 模型。
 */
data class LearningRouteSummaryUiModel(
    val routeId: String,
    val title: String,
    val subtitle: String?,
    val description: String?,
    val difficulty: LearningRouteDifficulty,
    val estimatedMinutes: Int,
    val stepCount: Int,
    val tags: List<String>,
    val coverImageUrl: String?,
)

/**
 * 学习路线详情页使用的 UI 模型。
 */
data class LearningRouteDetailUiModel(
    val routeId: String,
    val title: String,
    val description: String?,
    val difficulty: LearningRouteDifficulty,
    val estimatedMinutes: Int,
    val sections: List<LearningRouteSectionUiModel>,
    val steps: List<LearningRouteStepUiModel>,
    val relatedRoutes: List<LearningRouteSummaryUiModel>,
)

/**
 * 路线章节 UI 模型。
 */
data class LearningRouteSectionUiModel(
    val sectionId: String,
    val title: String,
    val description: String?,
    val stepIds: List<String>,
)

/**
 * 路线步骤 UI 模型。
 */
data class LearningRouteStepUiModel(
    val stepId: String,
    val order: Int,
    val title: String,
    val description: String?,
    val targetType: String?,
    val targetId: String?,
    val reason: String?,
    val estimatedMinutes: Int,
    val required: Boolean,
)

/**
 * “继续下一步”接口返回的 UI 模型。
 */
data class LearningRouteNextUiModel(
    val routeId: String?,
    val completed: Boolean,
    val nextStep: LearningRouteStepUiModel?,
    val relatedRoutes: List<LearningRouteSummaryUiModel>,
)
