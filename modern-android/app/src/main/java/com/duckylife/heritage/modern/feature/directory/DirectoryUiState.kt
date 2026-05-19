package com.duckylife.heritage.modern.feature.directory

import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind

data class DirectoryUiState(
    val selectedKind: DirectoryItemKind = DirectoryItemKind.NationalProject,
    val searchKeywords: String = "",
    val regionFilter: String = "",
    val categoryFilter: String = "",
    val yearFilter: String = "",
    val listTypeFilter: String = "",
) {
    val activeFilterCount: Int
        get() = listOf(regionFilter, categoryFilter, yearFilter, listTypeFilter).count { it.isNotBlank() }
}
