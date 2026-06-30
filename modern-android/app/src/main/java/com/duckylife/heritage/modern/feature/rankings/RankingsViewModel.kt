package com.duckylife.heritage.modern.feature.rankings

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duckylife.heritage.modern.core.data.DataExploreRepository
import com.duckylife.heritage.modern.core.network.dto.advanced.RankingMetric
import com.duckylife.heritage.modern.core.runCatchingCancellable
import com.duckylife.heritage.modern.feature.rankings.model.RankingDefinitionUiModel
import com.duckylife.heritage.modern.feature.rankings.model.RankingDetailUiModel
import com.duckylife.heritage.modern.feature.rankings.model.RankingFilters
import com.duckylife.heritage.modern.ui.state.AsyncState
import com.duckylife.heritage.modern.ui.error.toUiError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val KEY_RANKING_ID = "ranking_detail_id"
private const val KEY_FILTERS = "ranking_detail_filters"

/**
 * 排行榜列表 UI 状态。
 */
data class RankingsUiState(
    val definitions: AsyncState<List<RankingDefinitionUiModel>> = AsyncState(),
    val content: AsyncState<RankingDetailUiModel> = AsyncState(),
)

/**
 * 排行榜详情 UI 状态。
 */
data class RankingDetailUiState(
    val rankingId: String = "",
    val filters: RankingFilters = RankingFilters(),
    val detail: AsyncState<RankingDetailUiModel> = AsyncState(),
)

@HiltViewModel
class RankingsViewModel @Inject constructor(
    private val repository: DataExploreRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RankingsUiState())
    val uiState: StateFlow<RankingsUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadRankings()
    }

    fun loadRankings() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(definitions = AsyncState(isLoading = true)) }
            runCatchingCancellable { repository.getRankings() }
                .onSuccess { definitions ->
                    _uiState.update { it.copy(definitions = AsyncState(data = definitions)) }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(definitions = AsyncState(errorKind = throwable.toUiError().kind)) }
                }
        }
    }

    fun retry() {
        loadRankings()
    }

    fun loadRankingContent(metric: RankingMetric, filters: RankingFilters) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(content = AsyncState(isLoading = true)) }
            runCatchingCancellable { repository.getRankingContent(metric, filters) }
                .onSuccess { content ->
                    _uiState.update { it.copy(content = AsyncState(data = content)) }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(content = AsyncState(errorKind = throwable.toUiError().kind)) }
                }
        }
    }

    fun clearRankingContent() {
        _uiState.update { it.copy(content = AsyncState()) }
    }
}

@HiltViewModel
class RankingDetailViewModel @Inject constructor(
    private val repository: DataExploreRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _uiState = MutableStateFlow(restoreUiState())
    val uiState: StateFlow<RankingDetailUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadDetail()
    }

    fun setRankingId(rankingId: String) {
        if (rankingId.isBlank()) return
        if (_uiState.value.rankingId == rankingId) return
        _uiState.update { it.copy(rankingId = rankingId) }
        savedStateHandle[KEY_RANKING_ID] = rankingId
        loadDetail()
    }

    fun updateFilters(filters: RankingFilters) {
        _uiState.update { it.copy(filters = filters) }
        savedStateHandle[KEY_FILTERS] = filters
        loadDetail()
    }

    fun loadDetail() {
        val currentId = _uiState.value.rankingId
        if (currentId.isBlank()) return
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            val currentFilters = _uiState.value.filters
            _uiState.update { it.copy(detail = AsyncState(isLoading = true)) }
            runCatchingCancellable { repository.getRankingDetail(rankingId = currentId, filters = currentFilters) }
                .onSuccess { detail ->
                    if (_uiState.value.rankingId == currentId && _uiState.value.filters == currentFilters) {
                        _uiState.update { it.copy(detail = AsyncState(data = detail)) }
                    }
                }
                .onFailure { throwable ->
                    if (_uiState.value.rankingId == currentId && _uiState.value.filters == currentFilters) {
                        _uiState.update { it.copy(detail = AsyncState(errorKind = throwable.toUiError().kind)) }
                    }
                }
        }
    }

    fun retry() {
        loadDetail()
    }

    private fun restoreUiState(): RankingDetailUiState {
        val rankingId = savedStateHandle.get<String>(KEY_RANKING_ID) ?: ""
        val filters = savedStateHandle.get<RankingFilters>(KEY_FILTERS) ?: RankingFilters()
        return RankingDetailUiState(rankingId = rankingId, filters = filters)
    }
}
