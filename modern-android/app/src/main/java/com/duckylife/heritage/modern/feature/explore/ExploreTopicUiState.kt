package com.duckylife.heritage.modern.feature.explore

import com.duckylife.heritage.modern.core.network.dto.ExploreTopicV2Dto
import com.duckylife.heritage.modern.ui.error.ErrorKind

data class ExploreTopicUiState(
    val isLoading: Boolean = true,
    val topic: ExploreTopicV2Dto? = null,
    val errorKind: ErrorKind? = null,
)
