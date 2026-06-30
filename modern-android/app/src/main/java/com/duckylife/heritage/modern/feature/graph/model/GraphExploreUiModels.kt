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
import com.duckylife.heritage.modern.core.network.dto.advanced.AiInferredEdgeDto
import com.duckylife.heritage.modern.core.network.dto.advanced.AiInferredEdgesDto
import com.duckylife.heritage.modern.core.network.dto.advanced.BridgeItemDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphBridgeDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphTrailDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphTrailStepDto
import com.duckylife.heritage.modern.core.network.dto.advanced.PathDto
import com.duckylife.heritage.modern.core.network.dto.advanced.PathExplainDto
import com.duckylife.heritage.modern.core.network.dto.advanced.TopicGraphMapDto
import com.duckylife.heritage.modern.core.network.dto.advanced.TrailStrategy
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
    val centerNodeKey: String? = null,
)

/**
 * AI 推断边单条结果。
 */
data class AiInferredEdgeUiModel(
    val fromNodeKey: String,
    val toNodeKey: String,
    val relationType: GraphRelationType,
    val entityName: String?,
    val confidence: Double,
    val reason: String?,
)

/**
 * AI 推断边结果。
 */
data class AiInferredEdgesResult(
    val edges: List<AiInferredEdgeUiModel>,
)

/**
 * “证据”tab 单条结果。
 */
data class GraphEvidenceUiModel(
    val evidenceId: String?,
    val relationType: GraphRelationType,
    val relationLabel: String?,
    val source: GraphEvidenceSource,
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
    return GraphSimilarItemUiModel(
        node = node.toGraphNodeUiModel(),
        associationLevel = GraphRelationFormatter.associationLevel(score),
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
        centerNodeKey = center,
    )
}

fun GraphEvidenceDto.toEvidenceResult(): GraphEvidenceResult = GraphEvidenceResult(
    evidence = evidence.map { it.toUiModel() },
    warnings = warnings.mapNotNull { it.takeIf(String::isNotBlank) },
)

fun AiInferredEdgesDto.toAiInferredEdgesResult(): AiInferredEdgesResult = AiInferredEdgesResult(
    edges = edges.map { it.toUiModel() },
)

private fun AiInferredEdgeDto.toUiModel(): AiInferredEdgeUiModel = AiInferredEdgeUiModel(
    fromNodeKey = from,
    toNodeKey = to,
    relationType = relationType,
    entityName = entityName?.takeIf { it.isNotBlank() },
    confidence = confidence,
    reason = reason?.takeIf { it.isNotBlank() },
)

private fun GraphEvidenceItemDto.toUiModel(): GraphEvidenceUiModel = GraphEvidenceUiModel(
    evidenceId = evidenceId,
    relationType = relationType,
    relationLabel = relationLabel?.takeIf { it.isNotBlank() },
    source = source,
    reason = reason?.takeIf { it.isNotBlank() },
    sourceContentTitle = sourceContent?.title?.takeIf { it.isNotBlank() },
    isAiInferred = relationType == GraphRelationType.AiInferred || source == GraphEvidenceSource.Ai,
)

// -----------------------------------------------------------------------------
// Legacy mapping helpers
// -----------------------------------------------------------------------------

/**
 * 从邻居结果中查找中心节点。
 */
fun GraphNeighborsResult.centerNode(): GraphNodeUiModel? =
    centerNodeKey?.let { key -> nodes.firstOrNull { it.nodeKey == key } }
        ?: nodes.firstOrNull()

// -----------------------------------------------------------------------------
// Path Explain & Bridge UI models
// -----------------------------------------------------------------------------

/**
 * 路径解释结果。
 */
data class PathExplainResult(
    val found: Boolean,
    val steps: List<PathStepUiModel>,
    val narrative: List<String>,
    val evidence: List<GraphEvidenceUiModel>,
    val warnings: List<String>,
)

/**
 * 路径单步。
 */
data class PathStepUiModel(
    val order: Int,
    val node: GraphNodeUiModel,
    val edge: GraphEdgeUiModel?,
    val explanation: String?,
)

/**
 * 桥接节点结果。
 */
data class BridgeResult(
    val bridges: List<BridgeItemUiModel>,
)

/**
 * 单个桥接节点。
 */
data class BridgeItemUiModel(
    val node: GraphNodeUiModel,
    val score: Double,
    val reason: String?,
    val paths: List<PathExplainResult>,
)

/**
 * 主题图谱地图结果。
 */
data class TopicGraphMapResult(
    val topicType: String,
    val topicKey: String,
    val topicNode: GraphNodeUiModel?,
    val nodes: List<GraphNodeUiModel>,
    val edges: List<GraphEdgeUiModel>,
)

/**
 * 图谱漫游单步。
 */
data class GraphTrailStepUiModel(
    val order: Int,
    val node: GraphNodeUiModel,
    val stepType: String?,
    val reason: String?,
    val viaRelationType: GraphRelationType,
)

/**
 * 图谱漫游结果。
 */
data class GraphTrailResult(
    val trailId: String?,
    val strategy: TrailStrategy,
    val title: String?,
    val subtitle: String?,
    val startNode: GraphNodeUiModel?,
    val endNode: GraphNodeUiModel?,
    val steps: List<GraphTrailStepUiModel>,
    val nodes: List<GraphNodeUiModel>,
    val edges: List<GraphEdgeUiModel>,
    val topicLabels: List<String>,
    val score: Double,
)

// -----------------------------------------------------------------------------
// Path Explain & Bridge DTO -> UI mapping
// -----------------------------------------------------------------------------

fun PathExplainDto.toPathExplainResult(): PathExplainResult {
    val pathDto = path
    val rawNodes = (pathDto?.nodes ?: emptyList()) + steps.mapNotNull { it.node }
    val nodeMap = rawNodes.toGraphNodeUiModels().associateBy { it.nodeKey }
    val availableKeys = nodeMap.keys
    return PathExplainResult(
        found = pathDto?.found ?: steps.isNotEmpty(),
        steps = steps.mapNotNull { step ->
            val node = step.node?.toGraphNodeUiModel() ?: return@mapNotNull null
            val edge = step.edge
                ?.takeIf { it.from in availableKeys && it.to in availableKeys }
                ?.toGraphEdgeUiModel()
            PathStepUiModel(
                order = step.order,
                node = node,
                edge = edge,
                explanation = step.explanation?.takeIf { it.isNotBlank() },
            )
        }.sortedBy { it.order },
        narrative = narrative.mapNotNull { it.takeIf(String::isNotBlank) },
        evidence = evidence.map { it.toUiModel() },
        warnings = warnings.mapNotNull { it.takeIf(String::isNotBlank) },
    )
}

fun GraphBridgeDto.toBridgeResult(): BridgeResult = BridgeResult(
    bridges = bridges.map { bridge ->
        BridgeItemUiModel(
            node = bridge.bridgeNode.toGraphNodeUiModel(),
            score = bridge.score,
            reason = bridge.reason?.takeIf { it.isNotBlank() },
            paths = bridge.paths.map { it.toPathExplainResult() },
        )
    }.sortedByDescending { it.score },
)

private fun PathDto.toPathExplainResult(): PathExplainResult {
    val nodeMap = nodes.toGraphNodeUiModels().associateBy { it.nodeKey }
    val availableKeys = nodeMap.keys
    return PathExplainResult(
        found = found,
        steps = nodes.mapIndexedNotNull { index, nodeDto ->
            val node = nodeMap[nodeDto.nodeKey] ?: return@mapIndexedNotNull null
            val nextKey = nodes.getOrNull(index + 1)?.nodeKey
            val edge = nextKey?.let {
                edges.firstOrNull { e -> e.from == nodeDto.nodeKey && e.to == nextKey }
                    ?: edges.firstOrNull { e -> e.to == nodeDto.nodeKey && e.from == nextKey }
            }
            PathStepUiModel(
                order = index,
                node = node,
                edge = edge?.takeIf { it.from in availableKeys && it.to in availableKeys }
                    ?.toGraphEdgeUiModel(),
                explanation = edge?.reason?.takeIf { it.isNotBlank() },
            )
        },
        narrative = emptyList(),
        evidence = emptyList(),
        warnings = emptyList(),
    )
}

// -----------------------------------------------------------------------------
// Topic Graph Map DTO -> UI mapping
// -----------------------------------------------------------------------------

fun TopicGraphMapDto.toTopicGraphMapResult(): TopicGraphMapResult {
    val allNodes = (nodes + listOfNotNull(topic)).toGraphNodeUiModels().distinctBy { it.nodeKey }
    val nodeMap = allNodes.associateBy { it.nodeKey }
    val availableKeys = nodeMap.keys
    return TopicGraphMapResult(
        topicType = topicType ?: "",
        topicKey = topicKey ?: "",
        topicNode = topic?.toGraphNodeUiModel(),
        nodes = allNodes,
        edges = edges.toGraphEdgeUiModels(availableKeys),
    )
}

// -----------------------------------------------------------------------------
// Graph Trail DTO -> UI mapping
// -----------------------------------------------------------------------------

fun GraphTrailDto.toGraphTrailResult(): GraphTrailResult {
    val stepNodes = steps.mapNotNull { it.node }
    val allNodes = (nodes + listOfNotNull(startNode, endNode) + stepNodes)
        .toGraphNodeUiModels()
        .distinctBy { it.nodeKey }
    val nodeMap = allNodes.associateBy { it.nodeKey }
    val availableKeys = nodeMap.keys
    val mappedSteps = steps.mapNotNull { step ->
        val node = step.node?.toGraphNodeUiModel() ?: return@mapNotNull null
        GraphTrailStepUiModel(
            order = step.order,
            node = node,
            stepType = step.stepType?.takeIf { it.isNotBlank() },
            reason = step.reason?.takeIf { it.isNotBlank() },
            viaRelationType = GraphRelationType.entries
                .firstOrNull { it.wireName == step.viaRelationType }
                ?: GraphRelationType.Unknown,
        )
    }.sortedBy { it.order }
    // 后端某些漫游接口只返回 nodes 而不返回 steps，用 nodes 兜底生成可浏览的步骤
    val effectiveSteps = mappedSteps.takeIf { it.isNotEmpty() }
        ?: nodes.mapIndexed { index, nodeDto ->
            GraphTrailStepUiModel(
                order = index,
                node = nodeDto.toGraphNodeUiModel(),
                stepType = null,
                reason = null,
                viaRelationType = GraphRelationType.Unknown,
            )
        }
    return GraphTrailResult(
        trailId = trailId,
        strategy = strategy,
        title = title?.takeIf { it.isNotBlank() },
        subtitle = subtitle?.takeIf { it.isNotBlank() },
        startNode = startNode?.toGraphNodeUiModel()?.takeIf { it.nodeKey in availableKeys },
        endNode = endNode?.toGraphNodeUiModel()?.takeIf { it.nodeKey in availableKeys },
        steps = effectiveSteps,
        nodes = allNodes,
        edges = edges.toGraphEdgeUiModels(availableKeys),
        topicLabels = topicLabels.mapNotNull { it.takeIf(String::isNotBlank) }.distinct(),
        score = score,
    )
}
