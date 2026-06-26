package com.duckylife.heritage.modern.feature.graph.topicmap

import com.duckylife.heritage.modern.feature.graph.model.TopicGraphMapResult
import com.duckylife.heritage.modern.ui.error.ErrorKind

/**
 * 主题图谱地图页 UI 状态。
 */
data class TopicGraphMapUiState(
    val isLoading: Boolean = false,
    val result: TopicGraphMapResult? = null,
    val errorKind: ErrorKind? = null,
    val selectedViewMode: TopicGraphMapViewMode = TopicGraphMapViewMode.List,
)

enum class TopicGraphMapViewMode {
    List,
    Overview,
}
