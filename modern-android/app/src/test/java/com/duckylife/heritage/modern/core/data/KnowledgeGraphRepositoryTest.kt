package com.duckylife.heritage.modern.core.data

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
import com.duckylife.heritage.modern.core.network.api.KnowledgeGraphApi
import com.duckylife.heritage.modern.core.network.dto.SearchResultType
import com.duckylife.heritage.modern.core.network.dto.advanced.AiInferredEdgesDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphBridgeDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphCommunitiesDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphCommunityDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphEdgeDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphEvidenceDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphEvidenceItemDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphEvidenceSource
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphExploreDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphNeighborsDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphNodeDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphNodeType
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphRelationType
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphSimilarDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphSimilarItemDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphTrailDto
import com.duckylife.heritage.modern.core.network.dto.advanced.PathExplainDto
import com.duckylife.heritage.modern.core.network.dto.advanced.TopicGraphMapDto
import com.duckylife.heritage.modern.core.network.isServiceUnavailable
import com.duckylife.heritage.modern.feature.graph.model.AssociationLevel
import io.ktor.client.plugins.ResponseException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class KnowledgeGraphRepositoryTest {

    private val fakeApi = FakeKnowledgeGraphApi()
    private val repository: KnowledgeGraphRepository = DefaultKnowledgeGraphRepository(fakeApi)

    @Test
    fun `getCommunities maps size and filters dangling edges`() = runTest {
        fakeApi.communitiesResult = GraphCommunitiesDto(
            communities = listOf(
                GraphCommunityDto(
                    communityKey = "c1",
                    topicType = "category",
                    title = "传统技艺",
                    size = 3,
                    nodes = listOf(
                        GraphNodeDto(nodeKey = "article-1", type = GraphNodeType.Article, id = "a1", title = "Article 1"),
                        GraphNodeDto(nodeKey = "category-1", type = GraphNodeType.Category, id = "c1", title = "传统技艺"),
                    ),
                    edges = listOf(
                        GraphEdgeDto(from = "article-1", to = "category-1", type = GraphRelationType.Topic),
                        GraphEdgeDto(from = "article-1", to = "missing", type = GraphRelationType.RelatedTo),
                    ),
                ),
            ),
        )

        val result = repository.getCommunities()

        assertEquals(1, result.size)
        val community = result.first()
        assertEquals("传统技艺", community.title)
        assertEquals(1, community.contentCount)
        assertEquals(1, community.relationCount)
        assertEquals(listOf("传统技艺"), community.topicChips)
    }

    @Test
    fun `loadNeighbors uses limit 12 and no topics`() = runTest {
        fakeApi.neighborsResult = GraphNeighborsDto(
            center = "article-1",
            nodes = listOf(
                GraphNodeDto(nodeKey = "article-1", type = GraphNodeType.Article, id = "a1", title = "Center"),
                GraphNodeDto(nodeKey = "article-2", type = GraphNodeType.Article, id = "a2", title = "Other"),
            ),
            edges = listOf(
                GraphEdgeDto(from = "article-1", to = "article-2", type = GraphRelationType.RelatedTo),
            ),
        )

        val result = repository.loadNeighbors(SearchResultType.Article, "a1")

        assertEquals("article-1", result.centerNodeKey)
        assertEquals(2, result.nodes.size)
        assertEquals(1, result.edges.size)
        with(fakeApi.capturedNeighborsQuery) {
            assertEquals(SearchResultType.Article, this?.contentType)
            assertEquals("a1", this?.id)
            assertEquals(12, this?.limit)
            assertEquals(false, this?.includeTopics)
        }
    }

    @Test
    fun `loadSimilar maps association levels`() = runTest {
        fakeApi.similarResult = GraphSimilarDto(
            items = listOf(
                GraphSimilarItemDto(
                    node = GraphNodeDto(nodeKey = "article-2", type = GraphNodeType.Article, id = "a2", title = "High"),
                    score = 0.85,
                    reasons = listOf("Shared topic"),
                    sharedTopics = listOf("A", "B"),
                    sharedNeighborCount = 4,
                ),
                GraphSimilarItemDto(
                    node = GraphNodeDto(nodeKey = "article-3", type = GraphNodeType.Article, id = "a3", title = "Low"),
                    score = 0.2,
                ),
            ),
        )

        val result = repository.loadSimilar(SearchResultType.Article, "a1")

        assertEquals(2, result.items.size)
        assertEquals(AssociationLevel.High, result.items[0].associationLevel)
        assertEquals(AssociationLevel.Low, result.items[1].associationLevel)
        assertEquals(listOf("Shared topic"), result.items[0].reasons)
        assertEquals(listOf("A", "B"), result.items[0].sharedTopics)
        assertTrue(fakeApi.capturedSimilarQuery?.includeTopics == true)
    }

    @Test
    fun `loadExplore passes depth and limits`() = runTest {
        fakeApi.exploreResult = GraphExploreDto(depth = 2, nodes = emptyList(), edges = emptyList())

        repository.loadExplore(SearchResultType.DirectoryItem, "d1", depth = 2)

        assertEquals(2, fakeApi.capturedExploreQuery?.depth)
        assertEquals(50, fakeApi.capturedExploreQuery?.limit)
        assertEquals(false, fakeApi.capturedExploreQuery?.includeTopics)
    }

    @Test
    fun `loadEvidence defaults includeAiInferred to false`() = runTest {
        fakeApi.evidenceResult = GraphEvidenceDto(evidence = emptyList())

        repository.loadEvidence(SearchResultType.Inheritor, "i1")

        assertFalse(fakeApi.capturedEvidenceQuery?.includeAiInferred == true)
        assertEquals(20, fakeApi.capturedEvidenceQuery?.limit)
    }

    @Test
    fun `loadEvidence with ai inferred true`() = runTest {
        fakeApi.evidenceResult = GraphEvidenceDto(evidence = emptyList())

        repository.loadEvidence(SearchResultType.Article, "a1", includeAiInferred = true)

        assertTrue(fakeApi.capturedEvidenceQuery?.includeAiInferred == true)
    }

    @Test
    fun `loadAiInferredEdges uses dedicated endpoint query`() = runTest {
        fakeApi.aiInferredResult = AiInferredEdgesDto(
            edges = listOf(
                com.duckylife.heritage.modern.core.network.dto.advanced.AiInferredEdgeDto(
                    from = "a1",
                    to = "topic-1",
                    confidence = 0.82,
                    reason = "AI matched entity",
                ),
            ),
        )

        val result = repository.loadAiInferredEdges(SearchResultType.Article, "a1")

        assertEquals(1, result.edges.size)
        assertEquals(0.82, result.edges.first().confidence, 0.0)
        with(fakeApi.capturedAiInferredQuery) {
            assertEquals(SearchResultType.Article, this?.contentType)
            assertEquals("a1", this?.id)
            assertEquals(false, this?.includeStale)
            assertEquals(50, this?.limit)
        }
    }

    @Test(expected = RuntimeException::class)
    fun `loadNeighbors propagates exceptions`() = runTest {
        fakeApi.failure = RuntimeException("service unavailable")

        repository.loadNeighbors(SearchResultType.Article, "a1")
    }

    private class FakeKnowledgeGraphApi : KnowledgeGraphApi {
        var communitiesResult = GraphCommunitiesDto()
        var neighborsResult = GraphNeighborsDto()
        var similarResult = GraphSimilarDto()
        var exploreResult = GraphExploreDto()
        var evidenceResult = GraphEvidenceDto()
        var aiInferredResult = AiInferredEdgesDto()
        var failure: Throwable? = null

        var capturedNeighborsQuery: KnowledgeGraphNeighborsQuery? = null
        var capturedSimilarQuery: KnowledgeGraphSimilarQuery? = null
        var capturedExploreQuery: KnowledgeGraphExploreQuery? = null
        var capturedEvidenceQuery: KnowledgeGraphEvidenceQuery? = null
        var capturedAiInferredQuery: KnowledgeGraphAiInferredQuery? = null

        private fun <R> maybeFail(result: R): R {
            failure?.let { throw it }
            return result
        }

        override suspend fun getGraphNeighbors(query: KnowledgeGraphNeighborsQuery): GraphNeighborsDto {
            capturedNeighborsQuery = query
            return maybeFail(neighborsResult)
        }

        override suspend fun getGraphSimilar(query: KnowledgeGraphSimilarQuery): GraphSimilarDto {
            capturedSimilarQuery = query
            return maybeFail(similarResult)
        }

        override suspend fun getGraphExplore(query: KnowledgeGraphExploreQuery): GraphExploreDto {
            capturedExploreQuery = query
            return maybeFail(exploreResult)
        }

        override suspend fun getGraphEvidence(query: KnowledgeGraphEvidenceQuery): GraphEvidenceDto {
            capturedEvidenceQuery = query
            return maybeFail(evidenceResult)
        }

        override suspend fun getGraphCommunities(query: KnowledgeGraphCommunitiesQuery): GraphCommunitiesDto =
            maybeFail(communitiesResult)

        override suspend fun getAiInferredEdges(query: KnowledgeGraphAiInferredQuery): AiInferredEdgesDto {
            capturedAiInferredQuery = query
            return maybeFail(aiInferredResult)
        }

        override suspend fun getGraphBridge(query: KnowledgeGraphBridgeQuery): GraphBridgeDto = GraphBridgeDto()
        override suspend fun explainPath(query: KnowledgeGraphPathExplainQuery): PathExplainDto = PathExplainDto()
        override suspend fun getTopicGraphMap(query: TopicGraphMapQuery): TopicGraphMapDto = TopicGraphMapDto()
        override suspend fun getRandomGraphTrail(query: GraphTrailRandomQuery): GraphTrailDto = GraphTrailDto()
        override suspend fun getGraphTrailFromContent(query: GraphTrailFromContentQuery): GraphTrailDto =
            GraphTrailDto()

        override suspend fun getGraphTrailFromTopic(query: GraphTrailFromTopicQuery): GraphTrailDto =
            GraphTrailDto()
    }
}
