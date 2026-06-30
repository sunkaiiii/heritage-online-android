package com.duckylife.heritage.modern.core.network.api

import com.duckylife.heritage.modern.core.network.GraphTrailFromContentQuery
import com.duckylife.heritage.modern.core.network.GraphTrailFromTopicQuery
import com.duckylife.heritage.modern.core.network.GraphTrailRandomQuery
import com.duckylife.heritage.modern.core.network.KnowledgeGraphAiInferredQuery
import com.duckylife.heritage.modern.core.network.KnowledgeGraphBridgeQuery
import com.duckylife.heritage.modern.core.network.KnowledgeGraphCommunitiesQuery
import com.duckylife.heritage.modern.core.network.KnowledgeGraphEvidenceQuery
import com.duckylife.heritage.modern.core.network.KnowledgeGraphExploreQuery
import com.duckylife.heritage.modern.core.network.KnowledgeGraphNeighborsQuery
import com.duckylife.heritage.modern.core.network.KnowledgeGraphPathExplainQuery
import com.duckylife.heritage.modern.core.network.KnowledgeGraphSimilarQuery
import com.duckylife.heritage.modern.core.network.TopicGraphMapQuery
import com.duckylife.heritage.modern.core.network.dto.advanced.AiInferredEdgesDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphBridgeDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphCommunitiesDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphEvidenceDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphExploreDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphNeighborsDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphSimilarDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphTrailDto
import com.duckylife.heritage.modern.core.network.dto.advanced.PathExplainDto
import com.duckylife.heritage.modern.core.network.dto.advanced.TopicGraphMapDto

/**
 * 知识图谱只读端点契约。
 */
interface KnowledgeGraphApi {
    suspend fun getGraphNeighbors(query: KnowledgeGraphNeighborsQuery): GraphNeighborsDto

    suspend fun getGraphSimilar(query: KnowledgeGraphSimilarQuery): GraphSimilarDto

    suspend fun getGraphExplore(query: KnowledgeGraphExploreQuery): GraphExploreDto

    suspend fun getGraphEvidence(query: KnowledgeGraphEvidenceQuery): GraphEvidenceDto

    suspend fun getAiInferredEdges(query: KnowledgeGraphAiInferredQuery): AiInferredEdgesDto

    suspend fun getGraphBridge(query: KnowledgeGraphBridgeQuery): GraphBridgeDto

    suspend fun explainPath(query: KnowledgeGraphPathExplainQuery): PathExplainDto

    suspend fun getGraphCommunities(query: KnowledgeGraphCommunitiesQuery): GraphCommunitiesDto

    suspend fun getTopicGraphMap(query: TopicGraphMapQuery): TopicGraphMapDto

    suspend fun getRandomGraphTrail(query: GraphTrailRandomQuery): GraphTrailDto

    suspend fun getGraphTrailFromContent(query: GraphTrailFromContentQuery): GraphTrailDto

    suspend fun getGraphTrailFromTopic(query: GraphTrailFromTopicQuery): GraphTrailDto
}
