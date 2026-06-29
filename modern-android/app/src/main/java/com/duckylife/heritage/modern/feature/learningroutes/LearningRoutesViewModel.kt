package com.duckylife.heritage.modern.feature.learningroutes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duckylife.heritage.modern.core.data.LearningRoutesRepository
import com.duckylife.heritage.modern.core.network.dto.advanced.LearningRouteDifficulty
import com.duckylife.heritage.modern.core.network.dto.advanced.LearningRouteSeedType
import com.duckylife.heritage.modern.core.profile.LocalUserSyncRepository
import com.duckylife.heritage.modern.core.runCatchingCancellable
import com.duckylife.heritage.modern.ui.error.toUiError
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 学习路线首页 ViewModel。
 *
 * 通过 [SavedStateHandle] 持久化当前选中的难度筛选，保证旋转/进程恢复后状态不变。
 * 列表加载使用单一 Job 跟踪，避免快速切换筛选时旧响应覆盖新结果。
 * 导航事件通过 [Channel] 发送，避免在 Composable 中消费 state 后因离开组合而丢失消费动作。
 */
@HiltViewModel
class LearningRoutesViewModel @Inject constructor(
    private val repository: LearningRoutesRepository,
    private val syncRepository: LocalUserSyncRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LearningRoutesUiState())
    val uiState: StateFlow<LearningRoutesUiState> = _uiState.asStateFlow()

    private val _navigationEvents = Channel<String>(Channel.CONFLATED)
    val navigationEvents: Flow<String> = _navigationEvents.receiveAsFlow()

    private val difficultyKey = "learning_routes_selected_difficulty"
    private var loadRoutesJob: Job? = null

    init {
        val savedDifficulty = savedStateHandle.get<String>(difficultyKey)
            ?.let { raw ->
                LearningRouteDifficulty.entries.firstOrNull { it.wireName == raw }
            }
            ?: LearningRouteDifficulty.All
        _uiState.update { it.copy(selectedDifficulty = savedDifficulty) }
        loadRoutes()
    }

    /**
     * 设置从详情页带过来的 seed；应在 route 初始化时调用一次。
     */
    fun setSeed(seedType: String?, seedId: String?) {
        _uiState.update { it.copy(seedType = seedType, seedId = seedId) }
    }

    /**
     * 切换难度筛选并重新加载列表。
     */
    fun selectDifficulty(difficulty: LearningRouteDifficulty) {
        if (_uiState.value.selectedDifficulty == difficulty) return
        savedStateHandle[difficultyKey] = difficulty.wireName
        _uiState.update {
            it.copy(
                selectedDifficulty = difficulty,
                routes = LearningRoutesSectionState(isLoading = true),
            )
        }
        loadRoutes()
    }

    /**
     * 加载当前难度下的路线列表。
     *
     * 启动新请求前取消旧请求，并在响应到达时校验难度是否仍与发起时一致，
     * 防止快速切换筛选导致旧响应覆盖当前 UI。
     */
    fun loadRoutes() {
        loadRoutesJob?.cancel()
        loadRoutesJob = viewModelScope.launch {
            val requestedDifficulty = _uiState.value.selectedDifficulty
            _uiState.update { it.copy(routes = it.routes.copy(isLoading = true, errorKind = null)) }
            runCatchingCancellable {
                repository.getRoutes(difficulty = requestedDifficulty, limit = LIST_LIMIT)
            }.onSuccess { routes ->
                if (_uiState.value.selectedDifficulty == requestedDifficulty) {
                    _uiState.update { it.copy(routes = LearningRoutesSectionState(data = routes)) }
                }
            }.onFailure { throwable ->
                if (_uiState.value.selectedDifficulty == requestedDifficulty) {
                    _uiState.update {
                        it.copy(
                            routes = it.routes.copy(
                                isLoading = false,
                                errorKind = throwable.toUiError().kind,
                            ),
                        )
                    }
                }
            }
        }
    }

    /**
     * 重试当前列表。
     */
    fun retry() {
        loadRoutes()
    }

    /**
     * 当 seed 存在时，用户点击“为当前内容生成学习路线”触发 build。
     */
    fun buildFromSeed() {
        val seedType = _uiState.value.seedType?.takeIf { it.isNotBlank() } ?: return
        val seedId = _uiState.value.seedId?.takeIf { it.isNotBlank() } ?: return
        val difficulty = _uiState.value.selectedDifficulty
            .takeIf { it != LearningRouteDifficulty.All && it != LearningRouteDifficulty.Unknown }
            ?: LearningRouteDifficulty.Beginner

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isBuildingSeed = true,
                    buildSeedError = null,
                )
            }
            runCatchingCancellable {
                val buildSeed = seedType.toBuildSeed(seedId)
                val detail = repository.buildRoute(
                    seedType = buildSeed.type,
                    seedKey = buildSeed.key,
                    difficulty = difficulty,
                    limit = BUILD_LIMIT,
                )
                // 将临时生成的路线以 0% 进度写入本地，使其出现在“我的 -> 学习”列表。
                // 写入失败时不应静默跳过，否则路线不会出现在“我的 -> 学习”中。
                syncRepository.updateProgress(
                    routeId = detail.routeId,
                    routeTitle = detail.title,
                    completedStepIds = emptyList(),
                    currentStepId = null,
                )
                detail
            }.onSuccess { detail ->
                _uiState.update { it.copy(isBuildingSeed = false) }
                _navigationEvents.send(detail.routeId)
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isBuildingSeed = false,
                        buildSeedError = throwable.toUiError().kind,
                    )
                }
            }
        }
    }

    private fun String.toBuildSeed(seedId: String): BuildSeed {
        val normalizedType = trim()
        val normalizedId = seedId.trim()
        if (normalizedType in CONTENT_SEED_TYPES) {
            return BuildSeed(
                type = LearningRouteSeedType.Content,
                key = "$normalizedType:$normalizedId",
            )
        }
        val seedTypeEnum = LearningRouteSeedType.entries.firstOrNull { it.wireName == normalizedType }
            ?.takeIf { it != LearningRouteSeedType.Unknown }
            ?: LearningRouteSeedType.Content
        return BuildSeed(type = seedTypeEnum, key = normalizedId)
    }

    private data class BuildSeed(
        val type: LearningRouteSeedType,
        val key: String,
    )

    /**
     * 清除 build seed 错误提示。
     */
    fun clearBuildError() {
        _uiState.update { it.copy(buildSeedError = null) }
    }

    companion object {
        private const val LIST_LIMIT = 20
        private const val BUILD_LIMIT = 8
        private val CONTENT_SEED_TYPES = setOf("article", "directoryItem", "inheritor")
    }
}
