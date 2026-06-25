package com.duckylife.heritage.modern.feature.graph.hub

import com.duckylife.heritage.modern.core.data.RecentContentRef
import com.duckylife.heritage.modern.feature.discovery.DiscoverySectionState
import com.duckylife.heritage.modern.feature.graph.model.GraphCommunityUiModel

/**
 * 知识图谱首页（Graph Hub）的 UI 状态。
 */
data class KnowledgeGraphHubUiState(
    val communities: DiscoverySectionState<List<GraphCommunityUiModel>> = DiscoverySectionState(),
    val recentContent: RecentContentRef? = null,
    val isInfoSheetVisible: Boolean = false,
) {
    val hasCommunities: Boolean get() = communities.data.orEmpty().isNotEmpty()
}
