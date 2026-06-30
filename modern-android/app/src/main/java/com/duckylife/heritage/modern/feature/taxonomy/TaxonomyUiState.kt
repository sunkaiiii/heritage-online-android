package com.duckylife.heritage.modern.feature.taxonomy

import com.duckylife.heritage.modern.core.network.dto.TaxonomyKindDto
import com.duckylife.heritage.modern.core.network.dto.TaxonomyTopicDto
import com.duckylife.heritage.modern.ui.error.ErrorKind

data class TaxonomyUiState(
    val isLoading: Boolean = true,
    val errorKind: ErrorKind? = null,
    val categories: List<TaxonomyTopicDto> = emptyList(),
    val regions: List<TaxonomyTopicDto> = emptyList(),
    val kinds: List<TaxonomyKindDto> = emptyList(),
)
