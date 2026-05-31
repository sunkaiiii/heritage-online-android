package com.duckylife.heritage.modern.feature.compare

import com.duckylife.heritage.modern.core.network.CompareType
import com.duckylife.heritage.modern.core.network.dto.CompareResultDto
import com.duckylife.heritage.modern.ui.error.ErrorKind

data class CompareUiState(
    val selectedType: CompareType = CompareType.Region,
    val leftInput: String = "",
    val rightInput: String = "",
    val isLoading: Boolean = false,
    val result: CompareResultDto? = null,
    val errorKind: ErrorKind? = null,
    // "empty", "same", "invalid_kind" — displayed as localized string
    val errorMessage: String? = null,
)
