package com.duckylife.heritage.modern.feature.learning

import com.duckylife.heritage.modern.core.network.dto.LearningPathDetailDto
import com.duckylife.heritage.modern.ui.error.ErrorKind

data class LearningPathUiState(
    val isLoading: Boolean = true,
    val path: LearningPathDetailDto? = null,
    val errorKind: ErrorKind? = null,
)
