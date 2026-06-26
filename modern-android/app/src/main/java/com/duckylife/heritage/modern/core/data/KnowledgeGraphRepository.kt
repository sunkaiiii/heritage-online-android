package com.duckylife.heritage.modern.core.data

import com.duckylife.heritage.modern.core.network.GraphTrailFromContentQuery
import com.duckylife.heritage.modern.core.network.GraphTrailFromTopicQuery
import com.duckylife.heritage.modern.core.network.GraphTrailRandomQuery
import com.duckylife.heritage.modern.core.network.KnowledgeGraphBridgeQuery
import com.duckylife.heritage.modern.core.network.KnowledgeGraphCommunitiesQuery
import com.duckylife.heritage.modern.core.network.KnowledgeGraphEvidenceQuery
import com.duckylife.heritage.modern.core.network.KnowledgeGraphExploreQuery
import com.duckylife.heritage.modern.core.network.KnowledgeGraphAiInferredQuery
import com.duckylife.heritage.modern.core.network.KnowledgeGraphNeighborsQuery
import com.duckylife.heritage.modern.core.network.KnowledgeGraphPathExplainQuery
import com.duckylife.heritage.modern.core.network.KnowledgeGraphSimilarQuery
import com.duckylife.heritage.modern.core.network.TopicGraphMapQuery
import com.duckylife.heritage.modern.core.network.api.KnowledgeGraphApi
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
import com.duckylife.heritage.modern.feature.graph.model.toAiInferredEdgesResult
import com.duckylife.heritage.modern.feature.graph.model.toBridgeResult
import com.duckylife.heritage.modern.feature.graph.model.toEvidenceResult
import com.duckylife.heritage.modern.feature.graph.model.toExploreResult
import com.duckylife.heritage.modern.feature.graph.model.toGraphTrailResult
import com.duckylife.heritage.modern.feature.graph.model.toNeighborsResult
import com.duckylife.heritage.modern.feature.graph.model.toPathExplainResult
import com.duckylife.heritage.modern.feature.graph.model.toSimilarResult
import com.duckylife.heritage.modern.feature.graph.model.toTopicGraphMapResult
import com.duckylife.heritage.modern.feature.graph.model.toUiModel
import javax.inject.Inject

/**
 * 知识图谱只读仓库。
 *
 * 负责把裸 [KnowledgeGraphApi] 调用转换为 UI 友好的领域模型，并完成去重/过滤。
 */
interface KnowledgeGraphRepository {
    suspend fun getCommunities(limit: Int = 12, minSize: Int = 3): List<GraphCommunityUiModel>

    suspend fun loadNeighbors(type: SearchResultType, id: String): GraphNeighborsResult

    suspend fun loadSimilar(type: SearchResultType, id: String): GraphSimilarResult

    suspend fun loadExplore(type: SearchResultType, id: String, depth: Int): GraphExploreResult

    suspend fun loadEvidence(
        type: SearchResultType,
        id: String,
        includeAiInferred: Boolean = false,
    ): GraphEvidenceResult

    suspend fun loadAiInferredEdges(type: SearchResultType, id: String): AiInferredEdgesResult

    suspend fun explainPath(
        fromType: SearchResultType,
        fromId: String,
        toType: GraphNodeType,
        toId: String,
        maxDepth: Int = 3,
    ): PathExplainResult

    suspend fun getBridge(
        fromType: SearchResultType,
        fromId: String,
        toType: GraphNodeType,
        toId: String,
        limit: Int = 10,
    ): BridgeResult

    suspend fun getTopicGraphMap(
        topicType: String,
        topicKey: String,
        limit: Int = 50,
    ): TopicGraphMapResult

    suspend fun getRandomGraphTrail(
        strategy: TrailStrategy = TrailStrategy.Mixed,
        type: SearchResultType? = null,
        limit: Int = 6,
    ): GraphTrailResult

    suspend fun getGraphTrailFromContent(
        contentType: SearchResultType,
        contentId: String,
        strategy: TrailStrategy = TrailStrategy.Mixed,
        limit: Int = 6,
    ): GraphTrailResult

    suspend fun getGraphTrailFromTopic(
        topicType: String,
        topicKey: String,
        strategy: TrailStrategy = TrailStrategy.Representative,
        limit: Int = 6,
    ): GraphTrailResult
}

class DefaultKnowledgeGraphRepository @Inject constructor(
    private val api: KnowledgeGraphApi,
) : KnowledgeGraphRepository {

    override suspend fun getCommunities(limit: Int, minSize: Int): List<GraphCommunityUiModel> =
        api.getGraphCommunities(
            KnowledgeGraphCommunitiesQuery(limit = limit, minSize = minSize),
        ).communities.map { it.toUiModel() }

    override suspend fun loadNeighbors(type: SearchResultType, id: String): GraphNeighborsResult =
        api.getGraphNeighbors(
            KnowledgeGraphNeighborsQuery(
                contentType = type,
                id = id,
                limit = 12,
                includeTopics = false,
            ),
        ).toNeighborsResult()

    override suspend fun loadSimilar(type: SearchResultType, id: String): GraphSimilarResult =
        api.getGraphSimilar(
            KnowledgeGraphSimilarQuery(
                contentType = type,
                id = id,
                limit = 12,
                includeTopics = true,
            ),
        ).toSimilarResult()

    override suspend fun loadExplore(
        type: SearchResultType,
        id: String,
        depth: Int,
    ): GraphExploreResult = api.getGraphExplore(
        KnowledgeGraphExploreQuery(
            contentType = type,
            id = id,
            depth = depth,
            limit = 50,
            includeTopics = false,
        ),
    ).toExploreResult()

    override suspend fun loadEvidence(
        type: SearchResultType,
        id: String,
        includeAiInferred: Boolean,
    ): GraphEvidenceResult = api.getGraphEvidence(
        KnowledgeGraphEvidenceQuery(
            contentType = type,
            id = id,
            includeAiInferred = includeAiInferred,
            limit = 20,
        ),
    ).toEvidenceResult()

    override suspend fun loadAiInferredEdges(
        type: SearchResultType,
        id: String,
    ): AiInferredEdgesResult = api.getAiInferredEdges(
        KnowledgeGraphAiInferredQuery(
            contentType = type,
            id = id,
            minConfidence = 0.0,
            includeStale = false,
            limit = 50,
        ),
    ).toAiInferredEdgesResult()

    override suspend fun explainPath(
        fromType: SearchResultType,
        fromId: String,
        toType: GraphNodeType,
        toId: String,
        maxDepth: Int,
    ): PathExplainResult = api.explainPath(
        KnowledgeGraphPathExplainQuery(
            fromType = fromType,
            fromId = fromId,
            toType = toType,
            toId = toId,
            maxDepth = maxDepth.coerceIn(1, 5),
            includeAiInferred = false,
        ),
    ).toPathExplainResult()

    override suspend fun getBridge(
        fromType: SearchResultType,
        fromId: String,
        toType: GraphNodeType,
        toId: String,
        limit: Int,
    ): BridgeResult = api.getGraphBridge(
        KnowledgeGraphBridgeQuery(
            fromType = fromType,
            fromId = fromId,
            toType = toType,
            toId = toId,
            limit = limit.coerceIn(1, 100),
        ),
    ).toBridgeResult()

    override suspend fun getTopicGraphMap(
        topicType: String,
        topicKey: String,
        limit: Int,
    ): TopicGraphMapResult = api.getTopicGraphMap(
        TopicGraphMapQuery(
            topicType = topicType,
            topicKey = topicKey,
            limit = limit.coerceIn(1, 100),
        ),
    ).toTopicGraphMapResult()

    override suspend fun getRandomGraphTrail(
        strategy: TrailStrategy,
        type: SearchResultType?,
        limit: Int,
    ): GraphTrailResult = api.getRandomGraphTrail(
        GraphTrailRandomQuery(
            strategy = strategy,
            type = type,
            limit = limit.coerceIn(3, 10),
        ),
    ).toGraphTrailResult()

    override suspend fun getGraphTrailFromContent(
        contentType: SearchResultType,
        contentId: String,
        strategy: TrailStrategy,
        limit: Int,
    ): GraphTrailResult = api.getGraphTrailFromContent(
        GraphTrailFromContentQuery(
            contentType = contentType,
            id = contentId,
            strategy = strategy,
            limit = limit.coerceIn(3, 10),
        ),
    ).toGraphTrailResult()

    override suspend fun getGraphTrailFromTopic(
        topicType: String,
        topicKey: String,
        strategy: TrailStrategy,
        limit: Int,
    ): GraphTrailResult = api.getGraphTrailFromTopic(
        GraphTrailFromTopicQuery(
            topicType = topicType,
            topicKey = topicKey,
            strategy = strategy,
            limit = limit.coerceIn(3, 10),
        ),
    ).toGraphTrailResult()
}
