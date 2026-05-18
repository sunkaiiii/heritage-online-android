package com.duckylife.heritage.modern.feature.directory

import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind

data class DirectoryUiState(
    val selectedKind: DirectoryItemKind = DirectoryItemKind.NationalProject,
    val searchKeywords: String = "",
)
