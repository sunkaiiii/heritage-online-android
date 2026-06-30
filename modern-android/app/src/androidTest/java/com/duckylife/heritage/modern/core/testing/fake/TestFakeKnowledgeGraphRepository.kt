package com.duckylife.heritage.modern.core.testing.fake

import com.duckylife.heritage.modern.core.data.KnowledgeGraphRepository
import com.duckylife.heritage.modern.core.network.dto.SearchResultType
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphNodeType
import com.duckylife.heritage.modern.core.network.dto.advanced.TrailStrategy
import com.duckylife.heritage.modern.feature.graph.model.AiInferredEdgesResult
import com.duckylife.heritage.modern.feature.graph.model.BridgeResult
import com.duckylife.heritage.modern.feature.graph.model.GraphCommunityUiModel
import com.duckylife.heritage.modern.feature.graph.model.GraphEvidenceResult
import com.duckylife.heritage.modern.feature.graph.model.GraphExploreResult
import com.duckylife.heritage.modern.feature.graph.model.GraphNeighborsResult
import com.duckylife.heritage.modern.feature.graph.model.GraphSimilarResult
import com.duckylife.heritage.modern.feature.graph.model.GraphTrailResult
import com.duckylife.heritage.modern.feature.graph.model.PathExplainResult
import com.duckylife.heritage.modern.feature.graph.model.TopicGraphMapResult

/**
 * 供 instrumented test 使用的内存知识图谱仓库，避免访问真实网络。
 */
class TestFakeKnowledgeGraphRepository : KnowledgeGraphRepository {
    var communitiesResult: List<GraphCommunityUiModel> = emptyList()
    var neighborsResult: GraphNeighborsResult = GraphNeighborsResult(
        centerNodeKey = null,
        nodes = emptyList(),
        edges = emptyList(),
    )
    var similarResult: GraphSimilarResult = GraphSimilarResult(emptyList())
    var exploreResult: GraphExploreResult = GraphExploreResult(2, emptyList(), emptyList())
    var evidenceResult: GraphEvidenceResult = GraphEvidenceResult(emptyList(), emptyList())
    var aiInferredEdgesResult: AiInferredEdgesResult = AiInferredEdgesResult(emptyList())
    var pathExplainResult: PathExplainResult = PathExplainResult(
        found = false,
        steps = emptyList(),
        narrative = emptyList(),
        evidence = emptyList(),
        warnings = emptyList(),
    )
    var bridgeResult: BridgeResult = BridgeResult(emptyList())
    var topicGraphMapResult: TopicGraphMapResult = TopicGraphMapResult(
        topicType = GraphNodeType.Unknown,
        topicKey = "",
        topicNode = null,
        nodes = emptyList(),
        edges = emptyList(),
    )
    var graphTrailResult: GraphTrailResult = GraphTrailResult(
        trailId = null,
        strategy = TrailStrategy.Mixed,
        title = null,
        subtitle = null,
        startNode = null,
        endNode = null,
        steps = emptyList(),
        nodes = emptyList(),
        edges = emptyList(),
        topicLabels = emptyList(),
        score = 0.0,
    )
    var failure: Throwable? = null

    override suspend fun getCommunities(limit: Int, minSize: Int): List<GraphCommunityUiModel> {
        failure?.let { throw it }
        return communitiesResult
    }

    override suspend fun loadNeighbors(type: SearchResultType, id: String): GraphNeighborsResult {
        failure?.let { throw it }
        return neighborsResult
    }

    override suspend fun loadSimilar(type: SearchResultType, id: String): GraphSimilarResult {
        failure?.let { throw it }
        return similarResult
    }

    override suspend fun loadExplore(
        type: SearchResultType,
        id: String,
        depth: Int,
    ): GraphExploreResult {
        failure?.let { throw it }
        return exploreResult
    }

    override suspend fun loadEvidence(
        type: SearchResultType,
        id: String,
        includeAiInferred: Boolean,
    ): GraphEvidenceResult {
        failure?.let { throw it }
        return evidenceResult
    }

    override suspend fun loadAiInferredEdges(type: SearchResultType, id: String): AiInferredEdgesResult {
        failure?.let { throw it }
        return aiInferredEdgesResult
    }

    override suspend fun explainPath(
        fromType: SearchResultType,
        fromId: String,
        toType: GraphNodeType,
        toId: String,
        maxDepth: Int,
    ): PathExplainResult {
        failure?.let { throw it }
        return pathExplainResult
    }

    override suspend fun getBridge(
        fromType: SearchResultType,
        fromId: String,
        toType: GraphNodeType,
        toId: String,
        limit: Int,
    ): BridgeResult {
        failure?.let { throw it }
        return bridgeResult
    }

    override suspend fun getTopicGraphMap(
        topicType: GraphNodeType,
        topicKey: String,
        limit: Int,
    ): TopicGraphMapResult {
        failure?.let { throw it }
        return topicGraphMapResult
    }

    override suspend fun getRandomGraphTrail(
        strategy: TrailStrategy,
        type: SearchResultType?,
        limit: Int,
    ): GraphTrailResult {
        failure?.let { throw it }
        return graphTrailResult
    }

    override suspend fun getGraphTrailFromContent(
        contentType: SearchResultType,
        contentId: String,
        strategy: TrailStrategy,
        limit: Int,
    ): GraphTrailResult {
        failure?.let { throw it }
        return graphTrailResult
    }

    override suspend fun getGraphTrailFromTopic(
        topicType: GraphNodeType,
        topicKey: String,
        strategy: TrailStrategy,
        limit: Int,
    ): GraphTrailResult {
        failure?.let { throw it }
        return graphTrailResult
    }
}
