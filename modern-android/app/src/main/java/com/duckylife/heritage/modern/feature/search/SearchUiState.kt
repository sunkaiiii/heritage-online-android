package com.duckylife.heritage.modern.feature.search

import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind
import com.duckylife.heritage.modern.core.network.dto.FacetBucketDto
import com.duckylife.heritage.modern.core.network.dto.SearchResultItemDto
import com.duckylife.heritage.modern.core.network.dto.SearchResultType
import com.duckylife.heritage.modern.core.network.dto.SearchSuggestionDto
import com.duckylife.heritage.modern.ui.error.ErrorKind

data class SearchUiState(
    val query: String = "",
    val isSearching: Boolean = false,
    val results: List<SearchResultItemDto> = emptyList(),
    val page: Int = 1,
    val hasMore: Boolean = false,
    val total: Long = 0,
    val facets: SearchFacetsDto? = null,
    val selectedTypes: Set<SearchResultType> = emptySet(),
    val regionFilter: String = "",
    val categoryFilter: String = "",
    val yearFilter: Int? = null,
    val kindFilter: DirectoryItemKind? = null,
    val hasImageFilter: Boolean? = null,
    val suggestions: List<SearchSuggestionDto> = emptyList(),
    val isLoadingSuggestions: Boolean = false,
    val isLoadingMore: Boolean = false,
    val errorKind: ErrorKind? = null,
) {
    val hasActiveFilters: Boolean
        get() = selectedTypes.isNotEmpty() ||
            regionFilter.isNotBlank() ||
            categoryFilter.isNotBlank() ||
            yearFilter != null ||
            kindFilter != null ||
            hasImageFilter != null

    val activeFilterCount: Int
        get() {
            var count = 0
            if (selectedTypes.isNotEmpty()) count++
            if (regionFilter.isNotBlank()) count++
            if (categoryFilter.isNotBlank()) count++
            if (yearFilter != null) count++
            if (kindFilter != null) count++
            if (hasImageFilter != null) count++
            return count
        }
}

data class SearchFacetsDto(
    val types: List<FacetBucketDto> = emptyList(),
    val categories: List<FacetBucketDto> = emptyList(),
    val regions: List<FacetBucketDto> = emptyList(),
    val kinds: List<FacetBucketDto> = emptyList(),
    val years: List<FacetBucketDto> = emptyList(),
)
