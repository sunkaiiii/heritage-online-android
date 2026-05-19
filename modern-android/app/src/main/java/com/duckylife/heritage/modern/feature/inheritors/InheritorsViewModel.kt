package com.duckylife.heritage.modern.feature.inheritors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.duckylife.heritage.modern.core.data.HeritageRepository
import com.duckylife.heritage.modern.core.network.InheritorQuery
import com.duckylife.heritage.modern.core.network.dto.InheritorSummaryDto
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class InheritorsViewModel @Inject constructor(
    private val repository: HeritageRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(InheritorsUiState())
    val uiState: StateFlow<InheritorsUiState> = _uiState.asStateFlow()

    val inheritors: Flow<PagingData<InheritorSummaryDto>> =
        uiState
            .map { state ->
                InheritorQuery(
                    page = 1,
                    pageSize = 20,
                    keywords = state.searchKeywords.trim().takeIf { it.isNotEmpty() },
                    region = state.regionFilter.trim().takeIf { it.isNotEmpty() },
                    category = state.categoryFilter.trim().takeIf { it.isNotEmpty() },
                    year = state.yearFilter.toIntOrNull(),
                    gender = state.genderFilter.trim().takeIf { it.isNotEmpty() },
                )
            }
            .distinctUntilChanged()
            .flatMapLatest { query ->
                repository.pagedInheritors(query)
            }
            .cachedIn(viewModelScope)

    fun updateSearchKeywords(keywords: String) {
        _uiState.update { it.copy(searchKeywords = keywords) }
    }

    fun updateRegionFilter(region: String) {
        _uiState.update { it.copy(regionFilter = region) }
    }

    fun updateCategoryFilter(category: String) {
        _uiState.update { it.copy(categoryFilter = category) }
    }

    fun updateYearFilter(year: String) {
        _uiState.update { it.copy(yearFilter = year) }
    }

    fun updateGenderFilter(gender: String) {
        _uiState.update { it.copy(genderFilter = gender) }
    }

    fun clearFilters() {
        _uiState.update {
            it.copy(
                searchKeywords = "",
                regionFilter = "",
                categoryFilter = "",
                yearFilter = "",
                genderFilter = "",
            )
        }
    }
}
