package com.duckylife.heritage.modern.feature.learningroutes

import com.duckylife.heritage.modern.core.network.dto.advanced.LearningRouteDifficulty
import com.duckylife.heritage.modern.feature.learningroutes.model.LearningRouteSummaryUiModel
import com.duckylife.heritage.modern.ui.error.ErrorKind

/**
 * 学习路线首页的可加载区块状态。
 */
data class LearningRoutesSectionState<T>(
    val isLoading: Boolean = false,
    val data: T? = null,
    val errorKind: ErrorKind? = null,
) {
    val hasData: Boolean get() = data != null
    val hasError: Boolean get() = errorKind != null
    val hasFatalError: Boolean get() = errorKind != null && !hasData
}

/**
 * 学习路线首页 UI 状态。
 *
 * @param selectedDifficulty 当前选中的难度筛选，通过 [SavedStateHandle] 持久化。
 * @param routes 路线列表加载状态。
 * @param seedType 来自详情页的 seed type，仅用于显示“从此内容生成路线”入口。
 * @param seedId 来自详情页的 seed id。
 * @param isBuildingSeed 是否正在 build 临时路线。
 * @param buildSeedError build 失败的错误类型。
 */
data class LearningRoutesUiState(
    val selectedDifficulty: LearningRouteDifficulty = LearningRouteDifficulty.All,
    val routes: LearningRoutesSectionState<List<LearningRouteSummaryUiModel>> = LearningRoutesSectionState(isLoading = true),
    val seedType: String? = null,
    val seedId: String? = null,
    val isBuildingSeed: Boolean = false,
    val buildSeedError: ErrorKind? = null,
)
