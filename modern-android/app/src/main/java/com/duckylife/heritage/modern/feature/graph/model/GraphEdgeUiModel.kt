package com.duckylife.heritage.modern.feature.graph.model

import com.duckylife.heritage.modern.core.network.dto.advanced.GraphEvidenceSource
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphRelationType

/**
 * 知识图谱边在 UI 层的共享模型。
 *
 * 边必须与两端节点同时存在，后端截断子图时可能产生 dangling 边，需在映射层过滤掉。
 */
data class GraphEdgeUiModel(
    val fromNodeKey: String,
    val toNodeKey: String,
    val relationType: GraphRelationType,
    val label: String? = null,
    val reason: String? = null,
    val source: GraphEvidenceSource? = null,
    val weight: Double? = null,
) {
    val isAiInferred: Boolean
        get() = relationType == GraphRelationType.AiInferred || source == GraphEvidenceSource.Ai
}
