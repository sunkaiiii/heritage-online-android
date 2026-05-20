package com.duckylife.heritage.modern.feature.inheritors

import androidx.lifecycle.SavedStateHandle
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
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

private const val KEY_INH_SEARCH = "inh_search"
private const val KEY_INH_REGION = "inh_region"
private const val KEY_INH_CATEGORY = "inh_category"
private const val KEY_INH_YEAR = "inh_year"
private const val KEY_INH_GENDER = "inh_gender"

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class InheritorsViewModel @Inject constructor(
    private val repository: HeritageRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        InheritorsUiState(
            searchKeywords = savedStateHandle.get<String>(KEY_INH_SEARCH) ?: "",
            regionFilter = savedStateHandle.get<String>(KEY_INH_REGION) ?: "",
            categoryFilter = savedStateHandle.get<String>(KEY_INH_CATEGORY) ?: "",
            yearFilter = savedStateHandle.get<String>(KEY_INH_YEAR) ?: "",
            genderFilter = savedStateHandle.get<String>(KEY_INH_GENDER) ?: "",
        )
    )
    val uiState: StateFlow<InheritorsUiState> = _uiState.asStateFlow()

    val inheritors: Flow<PagingData<InheritorSummaryDto>> =
        uiState
            .debounce(SEARCH_DEBOUNCE_MS)
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
        savedStateHandle[KEY_INH_SEARCH] = keywords
        _uiState.update { it.copy(searchKeywords = keywords) }
    }

    fun applyFilters(regionFilter: String, categoryFilter: String, yearFilter: String, genderFilter: String) {
        savedStateHandle[KEY_INH_REGION] = regionFilter
        savedStateHandle[KEY_INH_CATEGORY] = categoryFilter
        savedStateHandle[KEY_INH_YEAR] = yearFilter
        savedStateHandle[KEY_INH_GENDER] = genderFilter
        _uiState.update {
            it.copy(
                regionFilter = regionFilter,
                categoryFilter = categoryFilter,
                yearFilter = yearFilter,
                genderFilter = genderFilter,
            )
        }
    }

    fun clearFilterField(field: InheritorFilterField) {
        when (field) {
            InheritorFilterField.Region -> { savedStateHandle[KEY_INH_REGION] = ""; _uiState.update { it.copy(regionFilter = "") } }
            InheritorFilterField.Category -> { savedStateHandle[KEY_INH_CATEGORY] = ""; _uiState.update { it.copy(categoryFilter = "") } }
            InheritorFilterField.Year -> { savedStateHandle[KEY_INH_YEAR] = ""; _uiState.update { it.copy(yearFilter = "") } }
            InheritorFilterField.Gender -> { savedStateHandle[KEY_INH_GENDER] = ""; _uiState.update { it.copy(genderFilter = "") } }
        }
    }

    fun clearFilters() {
        savedStateHandle[KEY_INH_SEARCH] = ""
        savedStateHandle[KEY_INH_REGION] = ""
        savedStateHandle[KEY_INH_CATEGORY] = ""
        savedStateHandle[KEY_INH_YEAR] = ""
        savedStateHandle[KEY_INH_GENDER] = ""
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

    companion object {
        private const val SEARCH_DEBOUNCE_MS = 350L
    }
}

enum class InheritorFilterField(val labelRes: Int) {
    Region(com.duckylife.heritage.modern.R.string.filter_field_region),
    Category(com.duckylife.heritage.modern.R.string.filter_field_category),
    Year(com.duckylife.heritage.modern.R.string.filter_field_year),
    Gender(com.duckylife.heritage.modern.R.string.filter_field_gender),
}
