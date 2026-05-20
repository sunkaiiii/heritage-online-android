package com.duckylife.heritage.modern.feature.directory

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.duckylife.heritage.modern.core.data.HeritageRepository
import com.duckylife.heritage.modern.core.network.DirectoryItemQuery
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemSummaryDto
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

private const val KEY_DIR_KIND = "dir_kind"
private const val KEY_DIR_SEARCH = "dir_search"
private const val KEY_DIR_REGION = "dir_region"
private const val KEY_DIR_CATEGORY = "dir_category"
private const val KEY_DIR_YEAR = "dir_year"
private const val KEY_DIR_LIST_TYPE = "dir_list_type"

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class DirectoryViewModel @Inject constructor(
    private val repository: HeritageRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        DirectoryUiState(
            selectedKind = savedStateHandle.get<String>(KEY_DIR_KIND)
                ?.let { DirectoryItemKind.entries.firstOrNull { k -> k.wireName == it } }
                ?: DirectoryItemKind.NationalProject,
            searchKeywords = savedStateHandle.get<String>(KEY_DIR_SEARCH) ?: "",
            regionFilter = savedStateHandle.get<String>(KEY_DIR_REGION) ?: "",
            categoryFilter = savedStateHandle.get<String>(KEY_DIR_CATEGORY) ?: "",
            yearFilter = savedStateHandle.get<String>(KEY_DIR_YEAR) ?: "",
            listTypeFilter = savedStateHandle.get<String>(KEY_DIR_LIST_TYPE) ?: "",
        )
    )
    val uiState: StateFlow<DirectoryUiState> = _uiState.asStateFlow()

    val items: Flow<PagingData<DirectoryItemSummaryDto>> =
        uiState
            .debounce(SEARCH_DEBOUNCE_MS)
            .map { state ->
                DirectoryItemQuery(
                    kind = state.selectedKind,
                    page = 1,
                    pageSize = 20,
                    keywords = state.searchKeywords.trim().takeIf { it.isNotEmpty() },
                    region = state.regionFilter.trim().takeIf { it.isNotEmpty() },
                    category = state.categoryFilter.trim().takeIf { it.isNotEmpty() },
                    year = state.yearFilter.toIntOrNull(),
                    listType = state.listTypeFilter.trim().takeIf { it.isNotEmpty() },
                )
            }
            .distinctUntilChanged()
            .flatMapLatest { query ->
                repository.pagedDirectoryItems(query)
            }
            .cachedIn(viewModelScope)

    fun selectKind(kind: DirectoryItemKind) {
        savedStateHandle[KEY_DIR_KIND] = kind.wireName
        _uiState.update { it.copy(selectedKind = kind) }
    }

    fun updateSearchKeywords(keywords: String) {
        savedStateHandle[KEY_DIR_SEARCH] = keywords
        _uiState.update { it.copy(searchKeywords = keywords) }
    }

    fun applyFilters(regionFilter: String, categoryFilter: String, yearFilter: String, listTypeFilter: String) {
        savedStateHandle[KEY_DIR_REGION] = regionFilter
        savedStateHandle[KEY_DIR_CATEGORY] = categoryFilter
        savedStateHandle[KEY_DIR_YEAR] = yearFilter
        savedStateHandle[KEY_DIR_LIST_TYPE] = listTypeFilter
        _uiState.update {
            it.copy(
                regionFilter = regionFilter,
                categoryFilter = categoryFilter,
                yearFilter = yearFilter,
                listTypeFilter = listTypeFilter,
            )
        }
    }

    fun clearFilterField(field: DirectoryFilterField) {
        when (field) {
            DirectoryFilterField.Region -> { savedStateHandle[KEY_DIR_REGION] = ""; _uiState.update { it.copy(regionFilter = "") } }
            DirectoryFilterField.Category -> { savedStateHandle[KEY_DIR_CATEGORY] = ""; _uiState.update { it.copy(categoryFilter = "") } }
            DirectoryFilterField.Year -> { savedStateHandle[KEY_DIR_YEAR] = ""; _uiState.update { it.copy(yearFilter = "") } }
            DirectoryFilterField.ListType -> { savedStateHandle[KEY_DIR_LIST_TYPE] = ""; _uiState.update { it.copy(listTypeFilter = "") } }
        }
    }

    fun clearFilters() {
        savedStateHandle[KEY_DIR_SEARCH] = ""
        savedStateHandle[KEY_DIR_REGION] = ""
        savedStateHandle[KEY_DIR_CATEGORY] = ""
        savedStateHandle[KEY_DIR_YEAR] = ""
        savedStateHandle[KEY_DIR_LIST_TYPE] = ""
        _uiState.update {
            it.copy(
                searchKeywords = "",
                regionFilter = "",
                categoryFilter = "",
                yearFilter = "",
                listTypeFilter = "",
            )
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_MS = 350L
    }
}

enum class DirectoryFilterField(val labelRes: Int) {
    Region(com.duckylife.heritage.modern.R.string.filter_field_region),
    Category(com.duckylife.heritage.modern.R.string.filter_field_category),
    Year(com.duckylife.heritage.modern.R.string.filter_field_year),
    ListType(com.duckylife.heritage.modern.R.string.filter_field_list_type),
}
