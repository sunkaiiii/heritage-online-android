package com.duckylife.heritage.modern.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duckylife.heritage.modern.core.data.HeritageRepository
import com.duckylife.heritage.modern.core.network.SearchV2Query
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind
import com.duckylife.heritage.modern.core.network.dto.SearchResultType
import com.duckylife.heritage.modern.ui.error.ErrorKind
import com.duckylife.heritage.modern.ui.error.toUiError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.duckylife.heritage.modern.core.runCatchingCancellable
import javax.inject.Inject

private const val SEARCH_DEBOUNCE_MS = 350L
private const val SUGGESTION_DEBOUNCE_MS = 200L
private const val PAGE_SIZE = 20

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: HeritageRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null
    private var suggestionJob: Job? = null

    init {
        // Observe query changes for suggestions
        viewModelScope.launch {
            _uiState
                .map { it.query }
                .drop(1) // Skip initial empty value
                .debounce(SUGGESTION_DEBOUNCE_MS)
                .distinctUntilChanged()
                .collect { query ->
                    if (query.isNotBlank() && _uiState.value.results.isEmpty()) {
                        loadSuggestions(query)
                    } else {
                        _uiState.update { it.copy(suggestions = emptyList()) }
                    }
                }
        }
    }

    fun updateQuery(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    fun search() {
        val query = _uiState.value.query.trim()
        if (query.isBlank()) return

        searchJob?.cancel()
        suggestionJob?.cancel()
        _uiState.update {
            it.copy(
                isSearching = true,
                page = 1,
                results = emptyList(),
                suggestions = emptyList(),
                errorKind = null,
            )
        }

        searchJob = viewModelScope.launch {
            runCatchingCancellable { performSearch(query, page = 1) }
                .onSuccess { response ->
                    _uiState.update {
                        it.copy(
                            isSearching = false,
                            results = response.items,
                            page = 1,
                            hasMore = response.hasMore,
                            total = response.total,
                            facets = response.facets?.toSearchFacetsDto(),
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isSearching = false,
                            errorKind = e.toUiError().kind,
                        )
                    }
                }
        }
    }

    fun loadMore() {
        val state = _uiState.value
        if (state.isLoadingMore || !state.hasMore || state.query.isBlank()) return

        val nextPage = state.page + 1
        _uiState.update { it.copy(isLoadingMore = true) }

        viewModelScope.launch {
            runCatchingCancellable { performSearch(state.query.trim(), page = nextPage) }
                .onSuccess { response ->
                    _uiState.update {
                        it.copy(
                            isLoadingMore = false,
                            results = it.results + response.items,
                            page = nextPage,
                            hasMore = response.hasMore,
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoadingMore = false,
                            errorKind = e.toUiError().kind,
                        )
                    }
                }
        }
    }

    fun selectSuggestion(text: String) {
        _uiState.update { it.copy(query = text, suggestions = emptyList()) }
        search()
    }

    fun toggleType(type: SearchResultType) {
        _uiState.update { state ->
            val newTypes = if (type in state.selectedTypes) {
                state.selectedTypes - type
            } else {
                state.selectedTypes + type
            }
            state.copy(selectedTypes = newTypes)
        }
        search()
    }

    fun updateRegionFilter(region: String) {
        _uiState.update { it.copy(regionFilter = region) }
        search()
    }

    fun updateCategoryFilter(category: String) {
        _uiState.update { it.copy(categoryFilter = category) }
        search()
    }

    fun updateYearFilter(year: Int?) {
        _uiState.update { it.copy(yearFilter = year) }
        search()
    }

    fun updateKindFilter(kind: DirectoryItemKind?) {
        _uiState.update { it.copy(kindFilter = kind) }
        search()
    }

    fun updateHasImageFilter(hasImage: Boolean?) {
        _uiState.update { it.copy(hasImageFilter = hasImage) }
        search()
    }

    fun clearFilters() {
        _uiState.update {
            it.copy(
                selectedTypes = emptySet(),
                regionFilter = "",
                categoryFilter = "",
                yearFilter = null,
                kindFilter = null,
                hasImageFilter = null,
            )
        }
        search()
    }

    fun clearError() {
        _uiState.update { it.copy(errorKind = null) }
    }

    private suspend fun loadSuggestions(prefix: String) {
        suggestionJob?.cancel()
        _uiState.update { it.copy(isLoadingSuggestions = true) }

        suggestionJob = viewModelScope.launch {
            runCatchingCancellable { repository.searchSuggestions(prefix) }
                .onSuccess { suggestions ->
                    _uiState.update {
                        it.copy(
                            suggestions = suggestions,
                            isLoadingSuggestions = false,
                        )
                    }
                }
                .onFailure {
                    _uiState.update { it.copy(isLoadingSuggestions = false) }
                }
        }
    }

    private suspend fun performSearch(query: String, page: Int) =
        repository.searchV2(
            SearchV2Query(
                keywords = query,
                types = _uiState.value.selectedTypes,
                page = page,
                pageSize = PAGE_SIZE,
                region = _uiState.value.regionFilter.takeIf { it.isNotBlank() },
                category = _uiState.value.categoryFilter.takeIf { it.isNotBlank() },
                year = _uiState.value.yearFilter,
                kind = _uiState.value.kindFilter,
                hasImage = _uiState.value.hasImageFilter,
            ),
        )

    private fun com.duckylife.heritage.modern.core.network.dto.SearchFacetsDto.toSearchFacetsDto() =
        SearchFacetsDto(
            types = types,
            categories = categories,
            regions = regions,
            kinds = kinds,
            years = years,
        )
}
