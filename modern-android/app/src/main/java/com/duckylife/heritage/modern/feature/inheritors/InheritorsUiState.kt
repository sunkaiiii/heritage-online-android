package com.duckylife.heritage.modern.feature.inheritors

data class InheritorsUiState(
    val searchKeywords: String = "",
    val regionFilter: String = "",
    val categoryFilter: String = "",
    val yearFilter: String = "",
    val genderFilter: String = "",
) {
    val activeFilterCount: Int
        get() = listOf(regionFilter, categoryFilter, yearFilter, genderFilter).count { it.isNotBlank() }
}
