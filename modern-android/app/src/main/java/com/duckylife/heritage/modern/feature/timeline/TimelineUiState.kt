package com.duckylife.heritage.modern.feature.timeline

import com.duckylife.heritage.modern.core.network.dto.FacetBucketDto
import com.duckylife.heritage.modern.core.network.dto.SearchResultType
import com.duckylife.heritage.modern.core.network.dto.TimelineItemDto
import com.duckylife.heritage.modern.core.network.dto.TimelineYearBucketDto
import com.duckylife.heritage.modern.ui.error.ErrorKind

data class TimelineUiState(
    val isLoading: Boolean = true,
    val years: List<TimelineYearBucketDto> = emptyList(),
    val selectedYear: Int? = null,
    val items: List<TimelineItemDto> = emptyList(),
    val page: Int = 1,
    val hasMore: Boolean = false,
    val facets: List<FacetBucketDto> = emptyList(),
    val selectedTypes: Set<SearchResultType> = emptySet(),
    val isLoadingMore: Boolean = false,
    val errorKind: ErrorKind? = null,
)
