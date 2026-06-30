package com.duckylife.heritage.modern.feature.research

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duckylife.heritage.modern.core.data.ResearchRepository
import com.duckylife.heritage.modern.core.runCatchingCancellable
import com.duckylife.heritage.modern.feature.research.model.ResearchPackageItemUiModel
import com.duckylife.heritage.modern.feature.research.model.ResearchReportItemUiModel
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

/**
 * 研究资料库 tab 类型。
 */
enum class ResearchLibraryTab {
    Packages,
    Reports,
}

/**
 * 研究资料库 UI 状态。
 */
data class ResearchLibraryUiState(
    val selectedTab: ResearchLibraryTab = ResearchLibraryTab.Packages,
    val packages: AsyncState<List<ResearchPackageItemUiModel>> = AsyncState(),
    val reports: AsyncState<List<ResearchReportItemUiModel>> = AsyncState(),
)

@HiltViewModel
class ResearchLibraryViewModel @Inject constructor(
    private val repository: ResearchRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResearchLibraryUiState())
    val uiState: StateFlow<ResearchLibraryUiState> = _uiState.asStateFlow()

    private var packagesJob: Job? = null
    private var reportsJob: Job? = null

    init {
        loadPackages()
        loadReports()
    }

    fun selectTab(tab: ResearchLibraryTab) {
        _uiState.update { it.copy(selectedTab = tab) }
        when (tab) {
            ResearchLibraryTab.Packages -> if (_uiState.value.packages.data == null) loadPackages()
            ResearchLibraryTab.Reports -> if (_uiState.value.reports.data == null) loadReports()
        }
    }

    fun refresh() {
        when (_uiState.value.selectedTab) {
            ResearchLibraryTab.Packages -> loadPackages()
            ResearchLibraryTab.Reports -> loadReports()
        }
    }

    fun loadPackages() {
        packagesJob?.cancel()
        packagesJob = viewModelScope.launch {
            _uiState.update { it.copy(packages = AsyncState(isLoading = true)) }
            runCatchingCancellable { repository.getPackages() }
                .onSuccess { packages ->
                    _uiState.update { it.copy(packages = AsyncState(data = packages)) }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(packages = AsyncState(errorKind = throwable.toUiError().kind)) }
                }
        }
    }

    fun loadReports() {
        reportsJob?.cancel()
        reportsJob = viewModelScope.launch {
            _uiState.update { it.copy(reports = AsyncState(isLoading = true)) }
            runCatchingCancellable { repository.getReports() }
                .onSuccess { reports ->
                    _uiState.update { it.copy(reports = AsyncState(data = reports)) }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(reports = AsyncState(errorKind = throwable.toUiError().kind)) }
                }
        }
    }
}
