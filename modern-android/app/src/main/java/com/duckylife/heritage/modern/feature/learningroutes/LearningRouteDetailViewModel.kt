package com.duckylife.heritage.modern.feature.learningroutes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.core.data.LearningRoutesRepository
import com.duckylife.heritage.modern.core.profile.LocalUserSyncRepository
import com.duckylife.heritage.modern.core.runCatchingCancellable
import com.duckylife.heritage.modern.ui.error.ErrorKind
import com.duckylife.heritage.modern.ui.error.toUiError
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 学习路线详情页 ViewModel。
 *
 * - 通过 [AssistedInject] 接收 [routeId]。
 * - 监听 [LocalUserSyncRepository.learningProgress] 获取已完成步骤。
 * - 用户勾选/取消勾选步骤后立即更新本地状态，并以 600ms debounce 写入本地同步仓库；
 *   快速连续点击只会在停顿后发送最终状态。
 * - 第一次完成路线时通过 [snackbarMessage] 触发短提示。
 */
@HiltViewModel(assistedFactory = LearningRouteDetailViewModel.Factory::class)
class LearningRouteDetailViewModel @AssistedInject constructor(
    @Assisted("routeId") private val routeId: String,
    private val repository: LearningRoutesRepository,
    private val syncRepository: LocalUserSyncRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LearningRouteDetailUiState())
    val uiState: StateFlow<LearningRouteDetailUiState> = _uiState.asStateFlow()

    private var loadRouteJob: Job? = null
    private var progressWriteJob: Job? = null
    private var hasPendingWrite = false

    init {
        observeProgress()
        loadRoute()
    }

    /**
     * 监听本地同步仓库的学习进度，把属于本路线的已完成步骤合并到 UI 状态。
     *
     * 当用户正在连续操作（有未完成的 debounce 写入）时，不覆盖用户本地选择，
     * 避免远程/本地旧进度回退已勾选的 checkbox。
     */
    private fun observeProgress() {
        viewModelScope.launch {
            syncRepository.learningProgress().collect { progressList ->
                val progress = progressList.find { it.routeId == routeId }
                val syncedIds = progress?.completedStepIds?.toSet().orEmpty()

                _uiState.update { current ->
                    if (hasPendingWrite) {
                        // 保留用户当前选择；仅记录已同步值供后续合并使用。
                        current
                    } else {
                        val wasCompleted = current.isCompleted
                        val next = current.copy(completedStepIds = syncedIds)
                        if (!wasCompleted && next.isCompleted) {
                            next.copy(snackbarMessage = buildCompletionMessage(next))
                        } else {
                            next
                        }
                    }
                }
            }
        }
    }

    /**
     * 加载路线详情。
     *
     * 启动新请求前取消旧请求，避免刷新时旧响应覆盖新 UI。
     */
    fun loadRoute() {
        loadRouteJob?.cancel()
        loadRouteJob = viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, errorKind = null, snackbarMessage = null)
            }
            runCatchingCancellable {
                repository.getRouteDetail(routeId = routeId)
            }.onSuccess { route ->
                _uiState.update {
                    it.copy(
                        route = route,
                        isLoading = false,
                        errorKind = null,
                    )
                }
            }.onFailure { throwable ->
                val error = throwable.toUiError()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorKind = error.kind,
                    )
                }
            }
        }
    }

    /**
     * 刷新路线详情。
     */
    fun refresh() = loadRoute()

    /**
     * 用户点击步骤 checkbox。
     *
     * 立即更新本地完成集合，并重新调度 debounce 写入。
     */
    fun onStepChecked(stepId: String, checked: Boolean) {
        _uiState.update { current ->
            val nextIds = if (checked) {
                current.completedStepIds + stepId
            } else {
                current.completedStepIds - stepId
            }
            val wasCompleted = current.isCompleted
            val next = current.copy(completedStepIds = nextIds)
            if (!wasCompleted && next.isCompleted) {
                next.copy(snackbarMessage = buildCompletionMessage(next))
            } else {
                next
            }
        }
        scheduleProgressWrite()
    }

    /**
     * 请求“下一步”推荐。
     *
     * 使用当前已完成步骤集合调用 [LearningRoutesRepository.getNextStep]。
     */
    fun loadNextStep() {
        val current = _uiState.value
        if (current.isLoadingNext || current.isCompleted) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoadingNext = true, nextStepError = null, nextStep = null)
            }
            runCatchingCancellable {
                repository.getNextStep(
                    routeId = routeId,
                    completedStepIds = current.validCompletedStepIds.toList(),
                )
            }.onSuccess { result ->
                _uiState.update {
                    it.copy(
                        isLoadingNext = false,
                        nextStep = result.nextStep,
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoadingNext = false,
                        nextStepError = throwable.toUiError().kind,
                    )
                }
            }
        }
    }

    /**
     * 显示“再学一次”确认对话框。
     */
    fun showRestartConfirmation() {
        _uiState.update { it.copy(showRestartConfirmDialog = true) }
    }

    /**
     * 取消“再学一次”确认对话框。
     */
    fun dismissRestartConfirmation() {
        _uiState.update { it.copy(showRestartConfirmDialog = false) }
    }

    /**
     * 确认清空本地已完成步骤并重新开始。
     */
    fun confirmRestart() {
        progressWriteJob?.cancel()
        hasPendingWrite = false
        _uiState.update { it.copy(completedStepIds = emptySet(), showRestartConfirmDialog = false) }
        viewModelScope.launch {
            runCatchingCancellable {
                syncRepository.updateProgress(
                    routeId = routeId,
                    routeTitle = _uiState.value.route?.title,
                    completedStepIds = emptyList(),
                    currentStepId = null,
                )
            }
        }
    }

    /**
     *  snackbar 已消费。
     */
    fun consumeSnackbarMessage() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    /**
     * 安排 debounce 进度写入。
     *
     * 连续调用会取消旧 Job 并重新延迟 600ms，保证只写入最终状态。
     */
    private fun scheduleProgressWrite() {
        progressWriteJob?.cancel()
        hasPendingWrite = true
        progressWriteJob = viewModelScope.launch {
            delay(PROGRESS_DEBOUNCE_MS)
            val current = _uiState.value
            runCatchingCancellable {
                syncRepository.updateProgress(
                    routeId = routeId,
                    routeTitle = current.route?.title,
                    completedStepIds = current.validCompletedStepIds.toList(),
                    currentStepId = current.currentStep?.stepId,
                )
            }.onSuccess {
                hasPendingWrite = false
            }.onFailure {
                // 写失败时保留本地状态；LocalUserSyncRepository 会把操作加入 pending queue
                // 后续 syncNow / 重试时会再次尝试。这里仅标记为不再 pending UI 覆盖逻辑。
                hasPendingWrite = false
            }
        }
    }

    private fun buildCompletionMessage(state: LearningRouteDetailUiState): LearningRouteDetailMessage {
        val total = state.totalSteps
        return if (total > 0) {
            LearningRouteDetailMessage(
                resId = R.string.learning_route_completed_steps_format,
                args = listOf(total, total),
            )
        } else {
            LearningRouteDetailMessage(resId = R.string.learning_route_completed_all)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(@Assisted("routeId") routeId: String): LearningRouteDetailViewModel
    }

    companion object {
        private const val PROGRESS_DEBOUNCE_MS = 600L
    }
}
