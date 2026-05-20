package com.duckylife.heritage.modern.feature.directory

import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind
import com.duckylife.heritage.modern.core.network.dto.DirectoryStatisticDimensionDto
import com.duckylife.heritage.modern.core.network.dto.DirectoryStatisticsOverviewDto
import com.duckylife.heritage.modern.ui.error.ErrorKind

enum class DirectoryTab(val labelRes: Int) {
    List(com.duckylife.heritage.modern.R.string.directory_tab_list),
    Statistics(com.duckylife.heritage.modern.R.string.directory_tab_statistics),
}

data class DirectoryStatisticsState(
    val isLoading: Boolean = false,
    val overview: DirectoryStatisticsOverviewDto? = null,
    val yearBreakdown: DirectoryStatisticDimensionDto? = null,
    val categoryBreakdown: DirectoryStatisticDimensionDto? = null,
    val regionBreakdown: DirectoryStatisticDimensionDto? = null,
    val errorKind: ErrorKind? = null,
)

data class DirectoryUiState(
    val selectedKind: DirectoryItemKind = DirectoryItemKind.NationalProject,
    val searchKeywords: String = "",
    val regionFilter: String = "",
    val categoryFilter: String = "",
    val yearFilter: String = "",
    val listTypeFilter: String = "",
    val selectedTab: DirectoryTab = DirectoryTab.List,
    val statisticsState: DirectoryStatisticsState = DirectoryStatisticsState(),
) {
    val activeFilterCount: Int
        get() = listOf(regionFilter, categoryFilter, yearFilter, listTypeFilter).count { it.isNotBlank() }
}
