package com.duckylife.heritage.modern.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duckylife.heritage.modern.core.data.HeritageRepository
import com.duckylife.heritage.modern.core.data.IntelligentSearchRepository
import com.duckylife.heritage.modern.core.network.IntelligentSearchQuery
import com.duckylife.heritage.modern.core.network.SearchV2Query
import com.duckylife.heritage.modern.core.network.isServiceUnavailable
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind
import com.duckylife.heritage.modern.core.network.dto.SearchResultType
import com.duckylife.heritage.modern.core.network.dto.advanced.IntelligentSearchItemDto
import com.duckylife.heritage.modern.core.network.dto.advanced.IntelligentSearchResponseDto
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.duckylife.heritage.modern.core.runCatchingCancellable
import javax.inject.Inject

private const val INTELLIGENT_SEARCH_DEBOUNCE_MS = 300L
private const val SUGGESTION_DEBOUNCE_MS = 200L
private const val PAGE_SIZE = 20

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: HeritageRepository,
    private val intelligentSearchRepository: IntelligentSearchRepository,
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
                    if (
                        _uiState.value.mode == SearchMode.Reference &&
                        query.isNotBlank() &&
                        _uiState.value.results.isEmpty()
                    ) {
                        loadSuggestions(query)
                    } else {
                        _uiState.update { it.copy(suggestions = emptyList()) }
                    }
                }
        }
    }

    fun updateQuery(query: String) {
        _uiState.update { it.copy(query = query) }
        if (_uiState.value.mode == SearchMode.Intelligent) {
            scheduleIntelligentSearch()
        }
    }

    fun search() {
        if (_uiState.value.mode == SearchMode.Intelligent) {
            scheduleIntelligentSearch()
        } else {
            searchReference()
        }
    }

    fun selectMode(mode: SearchMode) {
        if (_uiState.value.mode == mode) return
        searchJob?.cancel()
        suggestionJob?.cancel()
        _uiState.update {
            it.copy(
                mode = mode,
                isSearching = false,
                isLoadingMore = false,
                results = emptyList(),
                intelligentResults = emptyList(),
                page = 1,
                total = 0,
                hasMore = false,
                suggestions = emptyList(),
                errorKind = null,
                intelligenceUnavailable = false,
                whyMatchItem = null,
            )
        }
        if (_uiState.value.query.isNotBlank()) {
            search()
        }
    }

    fun showWhyMatch(item: IntelligentSearchItemDto) {
        _uiState.update { it.copy(whyMatchItem = item) }
    }

    fun dismissWhyMatch() {
        _uiState.update { it.copy(whyMatchItem = null) }
    }

    fun updateIntelligentIncludeAi(enabled: Boolean) {
        _uiState.update { it.copy(intelligentIncludeAi = enabled) }
        searchForCurrentMode()
    }

    fun updateIntelligentIncludeGraph(enabled: Boolean) {
        _uiState.update { it.copy(intelligentIncludeGraph = enabled) }
        searchForCurrentMode()
    }

    fun updateIntelligentIncludeHighlights(enabled: Boolean) {
        _uiState.update { it.copy(intelligentIncludeHighlights = enabled) }
        searchForCurrentMode()
    }

    private fun searchReference() {
        val query = _uiState.value.query.trim()
        if (query.isBlank()) return

        searchJob?.cancel()
        suggestionJob?.cancel()
        _uiState.update {
            it.copy(
                isSearching = true,
                page = 1,
                results = emptyList(),
                intelligentResults = emptyList(),
                suggestions = emptyList(),
                errorKind = null,
                intelligenceUnavailable = false,
            )
        }

        searchJob = viewModelScope.launch {
            runCatchingCancellable { performReferenceSearch(query, page = 1) }
                .onSuccess { response ->
                    if (_uiState.value.mode == SearchMode.Reference && _uiState.value.query.trim() == query) {
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
                }
                .onFailure { e ->
                    if (_uiState.value.mode == SearchMode.Reference && _uiState.value.query.trim() == query) {
                        _uiState.update {
                            it.copy(
                                isSearching = false,
                                errorKind = e.toUiError().kind,
                            )
                        }
                    }
                }
        }
    }

    fun loadMore() {
        val state = _uiState.value
        if (state.isLoadingMore || !state.hasMore || state.query.isBlank()) return

        val nextPage = state.page + 1
        _uiState.update { it.copy(isLoadingMore = true) }

        searchJob = viewModelScope.launch {
            if (state.mode == SearchMode.Intelligent) {
                runCatchingCancellable { performIntelligentSearch(state.query.trim(), nextPage) }
                    .onSuccess { response ->
                        if (
                            _uiState.value.mode == SearchMode.Intelligent &&
                            _uiState.value.query.trim() == state.query.trim()
                        ) {
                            _uiState.update {
                                it.copy(
                                    isLoadingMore = false,
                                    intelligentResults = it.intelligentResults + response.items,
                                    page = nextPage,
                                    hasMore = response.hasMore,
                                    total = response.total,
                                )
                            }
                        }
                    }
                    .onFailure { error ->
                        if (
                            _uiState.value.mode == SearchMode.Intelligent &&
                            _uiState.value.query.trim() == state.query.trim()
                        ) {
                            _uiState.update { it.copy(isLoadingMore = false, errorKind = error.toUiError().kind) }
                        }
                    }
            } else {
                runCatchingCancellable { performReferenceSearch(state.query.trim(), page = nextPage) }
                    .onSuccess { response ->
                        if (
                            _uiState.value.mode == SearchMode.Reference &&
                            _uiState.value.query.trim() == state.query.trim()
                        ) {
                            _uiState.update {
                                it.copy(
                                    isLoadingMore = false,
                                    results = it.results + response.items,
                                    page = nextPage,
                                    hasMore = response.hasMore,
                                )
                            }
                        }
                    }
                    .onFailure { error ->
                        if (
                            _uiState.value.mode == SearchMode.Reference &&
                            _uiState.value.query.trim() == state.query.trim()
                        ) {
                            _uiState.update { it.copy(isLoadingMore = false, errorKind = error.toUiError().kind) }
                        }
                    }
            }
        }
    }

    fun selectSuggestion(text: String) {
        _uiState.update { it.copy(query = text, suggestions = emptyList()) }
        searchForCurrentMode()
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
        searchForCurrentMode()
    }

    fun updateRegionFilter(region: String) {
        _uiState.update { it.copy(regionFilter = region) }
        searchForCurrentMode()
    }

    fun updateCategoryFilter(category: String) {
        _uiState.update { it.copy(categoryFilter = category) }
        searchForCurrentMode()
    }

    fun updateYearFilter(year: Int?) {
        _uiState.update { it.copy(yearFilter = year) }
        searchForCurrentMode()
    }

    fun updateKindFilter(kind: DirectoryItemKind?) {
        _uiState.update { it.copy(kindFilter = kind) }
        searchForCurrentMode()
    }

    fun updateHasImageFilter(hasImage: Boolean?) {
        _uiState.update { it.copy(hasImageFilter = hasImage) }
        searchForCurrentMode()
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
                intelligentIncludeAi = true,
                intelligentIncludeGraph = false,
                intelligentIncludeHighlights = true,
            )
        }
        searchForCurrentMode()
    }

    fun clearError() {
        _uiState.update { it.copy(errorKind = null, intelligenceUnavailable = false) }
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

    private fun searchForCurrentMode() {
        if (_uiState.value.mode == SearchMode.Intelligent) {
            scheduleIntelligentSearch()
        } else {
            searchReference()
        }
    }

    private fun scheduleIntelligentSearch() {
        val query = _uiState.value.query.trim()
        searchJob?.cancel()
        suggestionJob?.cancel()
        if (query.isBlank()) {
            _uiState.update {
                it.copy(
                    isSearching = false,
                    intelligentResults = emptyList(),
                    page = 1,
                    total = 0,
                    hasMore = false,
                    errorKind = null,
                    suggestions = emptyList(),
                    whyMatchItem = null,
                )
            }
            return
        }
        _uiState.update {
            it.copy(
                isSearching = true,
                results = emptyList(),
                intelligentResults = emptyList(),
                page = 1,
                total = 0,
                hasMore = false,
                errorKind = null,
                intelligenceUnavailable = false,
                suggestions = emptyList(),
                whyMatchItem = null,
            )
        }
        searchJob = viewModelScope.launch {
            kotlinx.coroutines.delay(INTELLIGENT_SEARCH_DEBOUNCE_MS)
            runCatchingCancellable { performIntelligentSearch(query, page = 1) }
                .onSuccess { response ->
                    val current = _uiState.value
                    if (current.mode == SearchMode.Intelligent && current.query.trim() == query) {
                        _uiState.update {
                            it.copy(
                                isSearching = false,
                                intelligentResults = response.items,
                                page = 1,
                                hasMore = response.hasMore,
                                total = response.total,
                            )
                        }
                    }
                }
                .onFailure { error ->
                    if (
                        _uiState.value.mode == SearchMode.Intelligent &&
                        _uiState.value.query.trim() == query
                    ) {
                        _uiState.update {
                            it.copy(
                                isSearching = false,
                                errorKind = error.toUiError().kind,
                                intelligenceUnavailable = error.isServiceUnavailable(),
                            )
                        }
                    }
                }
        }
    }

    private suspend fun performReferenceSearch(query: String, page: Int) =
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

    private suspend fun performIntelligentSearch(
        query: String,
        page: Int,
    ): IntelligentSearchResponseDto {
        val state = _uiState.value
        return intelligentSearchRepository.search(
            IntelligentSearchQuery(
                keywords = query,
                types = state.selectedTypes,
                page = page,
                pageSize = PAGE_SIZE,
                region = state.regionFilter.takeIf { it.isNotBlank() },
                category = state.categoryFilter.takeIf { it.isNotBlank() },
                year = state.yearFilter,
                kind = state.kindFilter,
                includeAi = state.intelligentIncludeAi,
                includeGraph = state.intelligentIncludeGraph,
                includeHighlights = state.intelligentIncludeHighlights,
            ),
        )
    }

    private fun com.duckylife.heritage.modern.core.network.dto.SearchFacetsDto.toSearchFacetsDto() =
        SearchFacetsDto(
            types = types,
            categories = categories,
            regions = regions,
            kinds = kinds,
            years = years,
        )
}
