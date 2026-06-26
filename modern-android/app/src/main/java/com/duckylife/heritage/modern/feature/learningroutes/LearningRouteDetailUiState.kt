package com.duckylife.heritage.modern.feature.learningroutes

import androidx.annotation.StringRes
import com.duckylife.heritage.modern.feature.learningroutes.model.LearningRouteDetailUiModel
import com.duckylife.heritage.modern.feature.learningroutes.model.LearningRouteStepUiModel
import com.duckylife.heritage.modern.ui.error.ErrorKind

/**
 * 学习路线详情页 UI 状态。
 *
 * @param route 路线详情；null 表示尚未加载或加载失败。
 * @param completedStepIds 当前已完成的步骤 ID 集合（来自本地进度 + 用户实时勾选）。
 * @param isLoading 是否正在加载路线详情。
 * @param errorKind 整页错误类型；404 表示路线已不存在。
 * @param isLoadingNext 是否正在请求“下一步”推荐。
 * @param nextStepError “下一步”请求失败时的错误类型。
 * @param nextStep “继续下一步”接口返回的下一步内容；null 表示未请求或已完成。
 * @param showRestartConfirmDialog 是否显示“清空进度并重新开始”确认对话框。
 * @param snackbarMessage 一次性轻提示文案资源与参数；完成路线时显示短 snackbar。
 */
data class LearningRouteDetailUiState(
    val route: LearningRouteDetailUiModel? = null,
    val completedStepIds: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val errorKind: ErrorKind? = null,
    val isLoadingNext: Boolean = false,
    val nextStepError: ErrorKind? = null,
    val nextStep: LearningRouteStepUiModel? = null,
    val showRestartConfirmDialog: Boolean = false,
    val snackbarMessage: LearningRouteDetailMessage? = null,
) {
    val totalSteps: Int get() = route?.steps?.size ?: 0

    val completedCount: Int get() = completedStepIds.size.coerceAtMost(totalSteps)

    val percent: Int
        get() = if (totalSteps == 0) 0 else (completedCount * 100 / totalSteps).coerceIn(0, 100)

    val isCompleted: Boolean get() = totalSteps > 0 && percent == 100

    /**
     * 当前应被视作“当前步骤”的步骤：取最后一个已完成的步骤（按 order 最大），
     * 用于写入 [LocalUserSyncRepository.updateProgress] 的 `currentStepId`。
     */
    val currentStep: LearningRouteStepUiModel?
        get() = route?.steps
            ?.filter { it.stepId in completedStepIds }
            ?.maxByOrNull { it.order }
}

/**
 * 详情页一次性提示消息，携带 string resource 与格式化参数。
 */
data class LearningRouteDetailMessage(
    @param:StringRes val resId: Int,
    val args: List<Any> = emptyList(),
)
