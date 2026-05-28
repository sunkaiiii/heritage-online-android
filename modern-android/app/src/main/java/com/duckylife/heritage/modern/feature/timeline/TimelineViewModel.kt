package com.duckylife.heritage.modern.feature.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duckylife.heritage.modern.core.data.HeritageRepository
import com.duckylife.heritage.modern.core.network.TimelineV2Query
import com.duckylife.heritage.modern.core.network.dto.SearchResultType
import com.duckylife.heritage.modern.ui.error.toUiError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.duckylife.heritage.modern.core.runCatchingCancellable
import javax.inject.Inject

private const val PAGE_SIZE = 20

@HiltViewModel
class TimelineViewModel @Inject constructor(
    private val repository: HeritageRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TimelineUiState())
    val uiState: StateFlow<TimelineUiState> = _uiState.asStateFlow()

    init {
        loadYears()
    }

    fun selectYear(year: Int?) {
        _uiState.update { it.copy(selectedYear = year, items = emptyList(), page = 1, hasMore = false) }
        if (year != null) {
            loadItems(reset = true)
        }
    }

    fun toggleType(type: SearchResultType) {
        _uiState.update { state ->
            val newTypes = if (type in state.selectedTypes) {
                state.selectedTypes - type
            } else {
                state.selectedTypes + type
            }
            state.copy(selectedTypes = newTypes, items = emptyList(), page = 1, hasMore = false)
        }
        if (_uiState.value.selectedYear != null) {
            loadItems(reset = true)
        }
    }

    fun loadMore() {
        val state = _uiState.value
        if (state.isLoadingMore || !state.hasMore || state.selectedYear == null) return
        loadItems(reset = false)
    }

    fun clearError() {
        _uiState.update { it.copy(errorKind = null) }
    }

    private fun loadYears() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorKind = null) }
            runCatchingCancellable { repository.timelineYears() }
                .onSuccess { years ->
                    _uiState.update { it.copy(isLoading = false, years = years) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorKind = e.toUiError().kind) }
                }
        }
    }

    private fun loadItems(reset: Boolean) {
        val state = _uiState.value
        val year = state.selectedYear ?: return
        val page = if (reset) 1 else state.page + 1

        if (reset) {
            _uiState.update { it.copy(isLoading = true, errorKind = null) }
        } else {
            _uiState.update { it.copy(isLoadingMore = true) }
        }

        viewModelScope.launch {
            runCatchingCancellable {
                repository.timelineV2(
                    TimelineV2Query(
                        year = year,
                        types = state.selectedTypes,
                        page = page,
                        pageSize = PAGE_SIZE,
                    ),
                )
            }
                .onSuccess { response ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            items = if (reset) response.items else it.items + response.items,
                            page = page,
                            hasMore = response.hasMore,
                            facets = response.facets?.types ?: emptyList(),
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            errorKind = e.toUiError().kind,
                        )
                    }
                }
        }
    }
}
