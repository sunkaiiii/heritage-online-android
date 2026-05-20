package com.duckylife.heritage.modern.feature.inheritors.detail

import com.duckylife.heritage.modern.core.network.dto.InheritorDetailDto
import com.duckylife.heritage.modern.ui.error.ErrorKind

data class InheritorDetailUiState(
    val isLoading: Boolean = true,
    val item: InheritorDetailDto? = null,
    val errorKind: ErrorKind? = null,
    val isFavorite: Boolean = false,
    val isContentStale: Boolean = false,
)
