package com.duckylife.heritage.modern.core.network.dto.advanced

import kotlinx.serialization.Serializable

@Serializable
data class GraphNeighborsDto(
    val center: String? = null,
    val nodes: List<GraphNodeDto> = emptyList(),
    val edges: List<GraphEdgeDto> = emptyList(),
    val generatedAt: String? = null,
)

@Serializable
data class GraphSimilarDto(
    val center: String? = null,
    val items: List<GraphSimilarItemDto> = emptyList(),
    val generatedAt: String? = null,
)

@Serializable
data class GraphSimilarItemDto(
    val node: GraphNodeDto,
    val score: Double = 0.0,
    val reasons: List<String> = emptyList(),
    val sharedTopics: List<String> = emptyList(),
    val sharedNeighborCount: Int = 0,
)

@Serializable
data class GraphExploreDto(
    val center: String? = null,
    val depth: Int = 2,
    val nodes: List<GraphNodeDto> = emptyList(),
    val edges: List<GraphEdgeDto> = emptyList(),
    val generatedAt: String? = null,
)

@Serializable
data class GraphEvidenceDto(
    val subjectKind: String? = null,
    val subjectKey: String? = null,
    val evidence: List<GraphEvidenceItemDto> = emptyList(),
    val warnings: List<String> = emptyList(),
    val generatedAt: String? = null,
)

@Serializable
data class GraphEvidenceItemDto(
    val evidenceId: String? = null,
    val relationType: GraphRelationType = GraphRelationType.Unknown,
    val relationLabel: String? = null,
    val source: GraphEvidenceSource = GraphEvidenceSource.Unknown,
    val reason: String? = null,
    val sourceContent: ContentRefDto? = null,
    val provenanceId: String? = null,
)

@Serializable
data class AiInferredEdgesDto(
    val center: String? = null,
    val edges: List<AiInferredEdgeDto> = emptyList(),
    val generatedAt: String? = null,
)

@Serializable
data class AiInferredEdgeDto(
    val from: String,
    val to: String,
    val relationType: GraphRelationType = GraphRelationType.AiInferred,
    val entityType: String? = null,
    val entityName: String? = null,
    val confidence: Double = 0.0,
    val reason: String? = null,
)

@Serializable
data class GraphBridgeDto(
    val from: String? = null,
    val to: String? = null,
    val bridges: List<BridgeItemDto> = emptyList(),
    val generatedAt: String? = null,
)

@Serializable
data class BridgeItemDto(
    val bridgeNode: GraphNodeDto,
    val score: Double = 0.0,
    val reason: String? = null,
    val paths: List<PathDto> = emptyList(),
)

@Serializable
data class PathExplainDto(
    val path: PathDto? = null,
    val steps: List<PathStepDto> = emptyList(),
    val narrative: List<String> = emptyList(),
    val evidence: List<GraphEvidenceItemDto> = emptyList(),
    val warnings: List<String> = emptyList(),
    val generatedAt: String? = null,
)

@Serializable
data class PathDto(
    val found: Boolean = false,
    val nodes: List<GraphNodeDto> = emptyList(),
    val edges: List<GraphEdgeDto> = emptyList(),
    val maxDepth: Int = 3,
)

@Serializable
data class PathStepDto(
    val order: Int,
    val node: GraphNodeDto? = null,
    val edge: GraphEdgeDto? = null,
    val explanation: String? = null,
)

@Serializable
data class GraphCommunitiesDto(
    val communities: List<GraphCommunityDto> = emptyList(),
    val generatedAt: String? = null,
)

@Serializable
data class GraphCommunityDto(
    val communityKey: String,
    val topicType: GraphNodeType,
    val title: String? = null,
    val size: Int = 0,
    val nodes: List<GraphNodeDto> = emptyList(),
    val edges: List<GraphEdgeDto> = emptyList(),
)

@Serializable
data class TopicGraphMapDto(
    val topicType: GraphNodeType? = null,
    val topicKey: String? = null,
    val topic: GraphNodeDto? = null,
    val nodes: List<GraphNodeDto> = emptyList(),
    val edges: List<GraphEdgeDto> = emptyList(),
    val generatedAt: String? = null,
)

@Serializable
data class GraphTrailDto(
    val trailId: String? = null,
    val strategy: TrailStrategy = TrailStrategy.Mixed,
    val title: String? = null,
    val subtitle: String? = null,
    val startNode: GraphNodeDto? = null,
    val endNode: GraphNodeDto? = null,
    val steps: List<GraphTrailStepDto> = emptyList(),
    val nodes: List<GraphNodeDto> = emptyList(),
    val edges: List<GraphEdgeDto> = emptyList(),
    val topicLabels: List<String> = emptyList(),
    val score: Double = 0.0,
)

@Serializable
data class GraphTrailStepDto(
    val order: Int,
    val node: GraphNodeDto? = null,
    val stepType: String? = null,
    val reason: String? = null,
    val viaRelationType: String? = null,
)
