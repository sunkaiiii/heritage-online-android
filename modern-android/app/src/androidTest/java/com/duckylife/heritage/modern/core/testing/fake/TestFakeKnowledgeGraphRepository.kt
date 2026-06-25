package com.duckylife.heritage.modern.core.testing.fake

import com.duckylife.heritage.modern.core.data.KnowledgeGraphRepository
import com.duckylife.heritage.modern.core.network.dto.SearchResultType
import com.duckylife.heritage.modern.feature.graph.model.GraphCommunityUiModel
import com.duckylife.heritage.modern.feature.graph.model.GraphEvidenceResult
import com.duckylife.heritage.modern.feature.graph.model.GraphExploreResult
import com.duckylife.heritage.modern.feature.graph.model.GraphNeighborsResult
import com.duckylife.heritage.modern.feature.graph.model.GraphSimilarResult

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
}
