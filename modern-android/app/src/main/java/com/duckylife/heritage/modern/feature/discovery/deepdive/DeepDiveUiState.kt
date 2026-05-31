package com.duckylife.heritage.modern.feature.discovery.deepdive

import com.duckylife.heritage.modern.core.network.dto.DiscoveryDeepDiveDto
import com.duckylife.heritage.modern.core.network.dto.DiscoveryItemDto
import com.duckylife.heritage.modern.ui.error.ErrorKind

data class DeepDiveUiState(
    val isLoading: Boolean = true,
    val seed: DiscoveryItemDto? = null,
    val related: List<DiscoveryItemDto> = emptyList(),
    val errorKind: ErrorKind? = null,
)
