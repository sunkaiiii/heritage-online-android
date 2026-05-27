package com.duckylife.heritage.modern.feature.collections

import com.duckylife.heritage.modern.core.network.dto.CollectionDto
import com.duckylife.heritage.modern.ui.error.ErrorKind

data class CollectionUiState(
    val isLoading: Boolean = true,
    val collection: CollectionDto? = null,
    val errorKind: ErrorKind? = null,
)
