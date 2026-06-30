package com.duckylife.heritage.modern.feature.stories

import com.duckylife.heritage.modern.core.network.dto.DataStoryDto
import com.duckylife.heritage.modern.ui.error.ErrorKind

data class StoryUiState(
    val isLoading: Boolean = true,
    val story: DataStoryDto? = null,
    val errorKind: ErrorKind? = null,
)
