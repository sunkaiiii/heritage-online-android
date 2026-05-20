package com.duckylife.heritage.modern.feature.directory.detail

import com.duckylife.heritage.modern.core.network.dto.DirectoryItemDetailDto
import com.duckylife.heritage.modern.ui.error.ErrorKind

data class DirectoryDetailUiState(
    val isLoading: Boolean = true,
    val item: DirectoryItemDetailDto? = null,
    val errorKind: ErrorKind? = null,
    val isFavorite: Boolean = false,
)
