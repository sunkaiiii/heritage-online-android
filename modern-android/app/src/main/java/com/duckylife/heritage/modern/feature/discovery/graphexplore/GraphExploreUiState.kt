package com.duckylife.heritage.modern.feature.discovery.graphexplore

import com.duckylife.heritage.modern.feature.discovery.DiscoverySectionState
import com.duckylife.heritage.modern.feature.discovery.GraphTab
import com.duckylife.heritage.modern.feature.graph.model.AiInferredEdgesResult
import com.duckylife.heritage.modern.feature.graph.model.BridgeResult
import com.duckylife.heritage.modern.feature.graph.model.GraphEvidenceResult
import com.duckylife.heritage.modern.feature.graph.model.GraphExploreResult
import com.duckylife.heritage.modern.feature.graph.model.GraphNeighborsResult
import com.duckylife.heritage.modern.feature.graph.model.GraphNodeUiModel
import com.duckylife.heritage.modern.feature.graph.model.GraphSimilarResult
import com.duckylife.heritage.modern.feature.graph.model.PathExplainResult
import com.duckylife.heritage.modern.ui.error.ErrorKind

/**
 * 内容关系图谱探索页的 UI 状态。
 */
data class GraphExploreUiState(
    val isInvalidRoute: Boolean = false,
    val centerNode: GraphNodeUiModel? = null,
    val selectedTab: GraphTab = GraphTab.Neighbors,
    val exploreDepth: Int = 2,
    val includeAiInferred: Boolean = false,
    val neighbors: DiscoverySectionState<GraphNeighborsResult> = DiscoverySectionState(),
    val similar: DiscoverySectionState<GraphSimilarResult> = DiscoverySectionState(),
    val explore: DiscoverySectionState<GraphExploreResult> = DiscoverySectionState(),
    val evidence: DiscoverySectionState<GraphEvidenceResult> = DiscoverySectionState(),
    val aiInferredEdges: DiscoverySectionState<AiInferredEdgesResult> = DiscoverySectionState(),
    val pathExplainSheet: PathExplainSheetState = PathExplainSheetState(),
)

/**
 * 路径解释 bottom sheet 状态。
 */
data class PathExplainSheetState(
    val targetNode: GraphNodeUiModel? = null,
    val isLoading: Boolean = false,
    val result: PathExplainResult? = null,
    val errorKind: ErrorKind? = null,
    val bridge: DiscoverySectionState<BridgeResult> = DiscoverySectionState(),
)
