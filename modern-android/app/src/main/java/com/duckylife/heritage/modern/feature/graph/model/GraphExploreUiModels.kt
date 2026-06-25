package com.duckylife.heritage.modern.feature.graph.model

import com.duckylife.heritage.modern.core.network.dto.advanced.GraphCommunityDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphEdgeDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphEvidenceDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphEvidenceItemDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphEvidenceSource
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphExploreDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphNeighborsDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphNodeDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphRelationType
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphSimilarDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphSimilarItemDto
import com.duckylife.heritage.modern.feature.graph.format.GraphRelationFormatter

/**
 * 知识图谱首页的热门主题群 UI 模型。
 */
data class GraphCommunityUiModel(
    val communityKey: String,
    val topicType: String,
    val title: String,
    val topicChips: List<String>,
    val contentCount: Int,
    val relationCount: Int,
    val primaryTopicKey: String?,
)

/**
 * “关联”tab 结果。
 */
data class GraphNeighborsResult(
    val centerNodeKey: String?,
    val nodes: List<GraphNodeUiModel>,
    val edges: List<GraphEdgeUiModel>,
)

/**
 * “相似”tab 单条结果。
 */
data class GraphSimilarItemUiModel(
    val node: GraphNodeUiModel,
    val associationLevel: AssociationLevel,
    val reasons: List<String>,
    val sharedTopics: List<String>,
    val sharedNeighborCount: Int,
)

enum class AssociationLevel {
    High,
    Medium,
    Low,
}

/**
 * “相似”tab 结果。
 */
data class GraphSimilarResult(
    val items: List<GraphSimilarItemUiModel>,
)

/**
 * “探索”tab 结果。
 */
data class GraphExploreResult(
    val depth: Int,
    val nodes: List<GraphNodeUiModel>,
    val edges: List<GraphEdgeUiModel>,
)

/**
 * “证据”tab 单条结果。
 */
data class GraphEvidenceUiModel(
    val evidenceId: String?,
    val relationLabel: String,
    val sourceLabel: String,
    val reason: String?,
    val sourceContentTitle: String?,
    val isAiInferred: Boolean,
)

/**
 * “证据”tab 结果。
 */
data class GraphEvidenceResult(
    val evidence: List<GraphEvidenceUiModel>,
    val warnings: List<String>,
)

// -----------------------------------------------------------------------------
// DTO -> UI model mapping
// -----------------------------------------------------------------------------

fun GraphCommunityDto.toUiModel(): GraphCommunityUiModel {
    val uiNodes = nodes.toGraphNodeUiModels()
    val uiEdges = edges.toGraphEdgeUiModels(uiNodes.map { it.nodeKey }.toSet())
    val topicChips = uiNodes
        .filter { !it.isContentNode }
        .mapNotNull { it.title?.takeIf { title -> title.isNotBlank() } }
        .distinct()
        .take(2)
    val primaryTopic = uiNodes.firstOrNull { !it.isContentNode }
    return GraphCommunityUiModel(
        communityKey = communityKey,
        topicType = topicType,
        title = title?.takeIf { it.isNotBlank() }
            ?: primaryTopic?.title?.takeIf { it.isNotBlank() }
            ?: communityKey,
        topicChips = topicChips,
        contentCount = uiNodes.count { it.isContentNode },
        relationCount = uiEdges.size,
        primaryTopicKey = primaryTopic?.nodeKey,
    )
}

fun GraphNeighborsDto.toNeighborsResult(): GraphNeighborsResult {
    val nodeMap = nodes.toGraphNodeUiModels().associateBy { it.nodeKey }
    val availableKeys = nodeMap.keys
    val uiEdges = edges.toGraphEdgeUiModels(availableKeys)
    return GraphNeighborsResult(
        centerNodeKey = center,
        nodes = nodeMap.values.toList(),
        edges = uiEdges,
    )
}

fun GraphSimilarDto.toSimilarResult(): GraphSimilarResult = GraphSimilarResult(
    items = items.map { it.toUiModel() },
)

private fun GraphSimilarItemDto.toUiModel(): GraphSimilarItemUiModel {
    val level = when {
        score >= 0.75 -> AssociationLevel.High
        score >= 0.45 -> AssociationLevel.Medium
        else -> AssociationLevel.Low
    }
    return GraphSimilarItemUiModel(
        node = node.toGraphNodeUiModel(),
        associationLevel = level,
        reasons = reasons.mapNotNull { it.takeIf(String::isNotBlank) },
        sharedTopics = sharedTopics.mapNotNull { it.takeIf(String::isNotBlank) }.take(2),
        sharedNeighborCount = sharedNeighborCount,
    )
}

fun GraphExploreDto.toExploreResult(): GraphExploreResult {
    val nodeMap = nodes.toGraphNodeUiModels().associateBy { it.nodeKey }
    val availableKeys = nodeMap.keys
    return GraphExploreResult(
        depth = depth.coerceIn(1, 2),
        nodes = nodeMap.values.toList(),
        edges = edges.toGraphEdgeUiModels(availableKeys),
    )
}

fun GraphEvidenceDto.toEvidenceResult(): GraphEvidenceResult = GraphEvidenceResult(
    evidence = evidence.map { it.toUiModel() },
    warnings = warnings.mapNotNull { it.takeIf(String::isNotBlank) },
)

private fun GraphEvidenceItemDto.toUiModel(): GraphEvidenceUiModel = GraphEvidenceUiModel(
    evidenceId = evidenceId,
    relationLabel = relationLabel?.takeIf { it.isNotBlank() }
        ?: GraphRelationFormatter.label(relationType),
    sourceLabel = sourceLabel(source),
    reason = reason?.takeIf { it.isNotBlank() },
    sourceContentTitle = sourceContent?.title?.takeIf { it.isNotBlank() },
    isAiInferred = relationType == GraphRelationType.AiInferred || source == GraphEvidenceSource.Ai,
)

private fun sourceLabel(source: GraphEvidenceSource?): String = when (source) {
    GraphEvidenceSource.Explicit -> "原始字段"
    GraphEvidenceSource.Inferred -> "图谱投影"
    GraphEvidenceSource.Embedding -> "语义关联"
    GraphEvidenceSource.Ai -> "AI 推断"
    else -> "其他来源"
}

// -----------------------------------------------------------------------------
// Legacy mapping helpers
// -----------------------------------------------------------------------------

/**
 * 从邻居结果中查找中心节点。
 */
fun GraphNeighborsResult.centerNode(): GraphNodeUiModel? =
    centerNodeKey?.let { key -> nodes.firstOrNull { it.nodeKey == key } }
        ?: nodes.firstOrNull()
