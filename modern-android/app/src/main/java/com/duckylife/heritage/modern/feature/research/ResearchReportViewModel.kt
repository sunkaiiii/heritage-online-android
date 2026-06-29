package com.duckylife.heritage.modern.feature.research

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duckylife.heritage.modern.core.data.ResearchRepository
import com.duckylife.heritage.modern.core.runCatchingCancellable
import com.duckylife.heritage.modern.feature.research.model.ResearchReportDetailUiModel
import com.duckylife.heritage.modern.ui.error.toUiError
import com.duckylife.heritage.modern.ui.state.AsyncState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val KEY_REPORT_ID = "research_report_id"

/**
 * 研究报告详情 UI 状态。
 */
data class ResearchReportUiState(
    val reportId: String = "",
    val detail: AsyncState<ResearchReportDetailUiModel> = AsyncState(),
)

@HiltViewModel
class ResearchReportViewModel @Inject constructor(
    private val repository: ResearchRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _uiState = MutableStateFlow(restoreUiState())
    val uiState: StateFlow<ResearchReportUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        if (_uiState.value.reportId.isNotBlank()) {
            loadDetail()
        }
    }

    fun setReportId(reportId: String) {
        if (reportId.isBlank()) return
        if (_uiState.value.reportId == reportId) return
        _uiState.update { ResearchReportUiState(reportId = reportId) }
        savedStateHandle[KEY_REPORT_ID] = reportId
        loadDetail()
    }

    fun loadDetail() {
        val currentId = _uiState.value.reportId
        if (currentId.isBlank()) return
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(detail = AsyncState(isLoading = true)) }
            runCatchingCancellable { repository.getReportDetail(currentId) }
                .onSuccess { detail ->
                    if (_uiState.value.reportId == currentId) {
                        _uiState.update { it.copy(detail = AsyncState(data = detail)) }
                    }
                }
                .onFailure { throwable ->
                    if (_uiState.value.reportId == currentId) {
                        _uiState.update { it.copy(detail = AsyncState(errorKind = throwable.toUiError().kind)) }
                    }
                }
        }
    }

    fun retry() {
        loadDetail()
    }

    private fun restoreUiState(): ResearchReportUiState {
        val reportId = savedStateHandle.get<String>(KEY_REPORT_ID) ?: ""
        return ResearchReportUiState(reportId = reportId)
    }
}
