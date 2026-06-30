package com.duckylife.heritage.modern.feature.regions

import com.duckylife.heritage.modern.core.network.dto.RegionAtlasDetailDto
import com.duckylife.heritage.modern.core.network.dto.RegionAtlasDto
import com.duckylife.heritage.modern.ui.error.ErrorKind

data class RegionAtlasUiState(
    val isLoading: Boolean = true,
    val atlas: RegionAtlasDto? = null,
    val errorKind: ErrorKind? = null,
)

data class RegionDetailUiState(
    val isLoading: Boolean = true,
    val detail: RegionAtlasDetailDto? = null,
    val errorKind: ErrorKind? = null,
)
