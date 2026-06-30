package com.duckylife.heritage.modern.feature.search

import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind
import com.duckylife.heritage.modern.core.network.dto.FacetBucketDto
import com.duckylife.heritage.modern.core.network.dto.SearchResultItemDto
import com.duckylife.heritage.modern.core.network.dto.SearchResultType
import com.duckylife.heritage.modern.core.network.dto.SearchSuggestionDto
import com.duckylife.heritage.modern.core.network.dto.advanced.IntelligentSearchItemDto
import com.duckylife.heritage.modern.ui.error.ErrorKind

enum class SearchMode {
    Reference,
    Intelligent,
}

data class SearchUiState(
    val query: String = "",
    val mode: SearchMode = SearchMode.Reference,
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
    val intelligentResults: List<IntelligentSearchItemDto> = emptyList(),
    val intelligentIncludeAi: Boolean = true,
    val intelligentIncludeGraph: Boolean = false,
    val intelligentIncludeHighlights: Boolean = true,
    /** V3 intelligent-search 的 503：允许用户直接回到稳定的资料搜索。 */
    val intelligenceUnavailable: Boolean = false,
    val whyMatchItem: IntelligentSearchItemDto? = null,
) {
    val hasActiveFilters: Boolean
        get() = selectedTypes.isNotEmpty() ||
            regionFilter.isNotBlank() ||
            categoryFilter.isNotBlank() ||
            yearFilter != null ||
            kindFilter != null ||
            hasImageFilter != null ||
            (mode == SearchMode.Intelligent && (
                !intelligentIncludeAi ||
                    intelligentIncludeGraph ||
                    !intelligentIncludeHighlights
                ))

    val activeFilterCount: Int
        get() {
            var count = 0
            if (selectedTypes.isNotEmpty()) count++
            if (regionFilter.isNotBlank()) count++
            if (categoryFilter.isNotBlank()) count++
            if (yearFilter != null) count++
            if (kindFilter != null) count++
            if (hasImageFilter != null) count++
            if (mode == SearchMode.Intelligent) {
                if (!intelligentIncludeAi) count++
                if (intelligentIncludeGraph) count++
                if (!intelligentIncludeHighlights) count++
            }
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
