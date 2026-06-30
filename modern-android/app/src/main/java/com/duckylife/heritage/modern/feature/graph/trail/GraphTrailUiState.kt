package com.duckylife.heritage.modern.feature.graph.trail

import com.duckylife.heritage.modern.feature.graph.model.GraphTrailResult
import com.duckylife.heritage.modern.ui.error.ErrorKind

/**
 * 知识漫游页 UI 状态。
 */
data class GraphTrailUiState(
    val isLoading: Boolean = false,
    val trail: GraphTrailResult? = null,
    val errorKind: ErrorKind? = null,
    val canResample: Boolean = false,
)
