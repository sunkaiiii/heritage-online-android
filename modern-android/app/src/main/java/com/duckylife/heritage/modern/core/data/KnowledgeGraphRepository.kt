package com.duckylife.heritage.modern.core.data

import com.duckylife.heritage.modern.core.network.KnowledgeGraphCommunitiesQuery
import com.duckylife.heritage.modern.core.network.KnowledgeGraphEvidenceQuery
import com.duckylife.heritage.modern.core.network.KnowledgeGraphExploreQuery
import com.duckylife.heritage.modern.core.network.KnowledgeGraphNeighborsQuery
import com.duckylife.heritage.modern.core.network.KnowledgeGraphSimilarQuery
import com.duckylife.heritage.modern.core.network.api.KnowledgeGraphApi
import com.duckylife.heritage.modern.core.network.dto.SearchResultType
import com.duckylife.heritage.modern.feature.graph.model.GraphCommunityUiModel
import com.duckylife.heritage.modern.feature.graph.model.GraphEvidenceResult
import com.duckylife.heritage.modern.feature.graph.model.GraphExploreResult
import com.duckylife.heritage.modern.feature.graph.model.GraphNeighborsResult
import com.duckylife.heritage.modern.feature.graph.model.GraphSimilarResult
import com.duckylife.heritage.modern.feature.graph.model.toEvidenceResult
import com.duckylife.heritage.modern.feature.graph.model.toExploreResult
import com.duckylife.heritage.modern.feature.graph.model.toNeighborsResult
import com.duckylife.heritage.modern.feature.graph.model.toSimilarResult
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
}
