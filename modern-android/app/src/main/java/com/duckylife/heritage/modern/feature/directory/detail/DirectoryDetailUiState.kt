package com.duckylife.heritage.modern.feature.directory.detail

import com.duckylife.heritage.modern.core.network.dto.DirectoryItemDetailDto

data class DirectoryDetailUiState(
    val isLoading: Boolean = true,
    val item: DirectoryItemDetailDto? = null,
    val errorMessage: String? = null,
)
