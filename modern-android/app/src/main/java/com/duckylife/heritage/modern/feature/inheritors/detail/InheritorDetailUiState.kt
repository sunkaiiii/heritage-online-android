package com.duckylife.heritage.modern.feature.inheritors.detail

import com.duckylife.heritage.modern.core.network.dto.InheritorDetailDto

data class InheritorDetailUiState(
    val isLoading: Boolean = true,
    val item: InheritorDetailDto? = null,
    val errorMessage: String? = null,
)
