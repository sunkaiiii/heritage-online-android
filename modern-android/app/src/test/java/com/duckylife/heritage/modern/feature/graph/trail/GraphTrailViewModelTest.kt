package com.duckylife.heritage.modern.feature.graph.trail

import com.duckylife.heritage.modern.core.data.KnowledgeGraphRepository
import com.duckylife.heritage.modern.core.network.dto.SearchResultType
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphNodeType
import com.duckylife.heritage.modern.core.network.dto.advanced.TrailStrategy
import com.duckylife.heritage.modern.core.testing.MainDispatcherRule
import com.duckylife.heritage.modern.feature.discovery.GraphTrailSource
import com.duckylife.heritage.modern.feature.graph.model.AiInferredEdgesResult
import com.duckylife.heritage.modern.feature.graph.model.BridgeResult
import com.duckylife.heritage.modern.feature.graph.model.GraphCommunityUiModel
import com.duckylife.heritage.modern.feature.graph.model.GraphEvidenceResult
import com.duckylife.heritage.modern.feature.graph.model.GraphExploreResult
import com.duckylife.heritage.modern.feature.graph.model.GraphNeighborsResult
import com.duckylife.heritage.modern.feature.graph.model.GraphNodeUiModel
import com.duckylife.heritage.modern.feature.graph.model.GraphSimilarResult
import com.duckylife.heritage.modern.feature.graph.model.GraphTrailResult
import com.duckylife.heritage.modern.feature.graph.model.GraphTrailStepUiModel
import com.duckylife.heritage.modern.feature.graph.model.PathExplainResult
import com.duckylife.heritage.modern.feature.graph.model.TopicGraphMapResult
import com.duckylife.heritage.modern.ui.error.ErrorKind
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GraphTrailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fakeRepository = FakeKnowledgeGraphRepository()

    @Test
    fun `random source loads trail and allows resample`() = runTest {
        fakeRepository.trailResult = trailResult("random-1")
        val viewModel = createViewModel(GraphTrailSource.Random)
        advanceUntilIdle()

        assertEquals("random-1", viewModel.uiState.value.trail?.trailId)
        assertTrue(viewModel.uiState.value.canResample)
    }

    @Test
    fun `from content loads with parsed type`() = runTest {
        fakeRepository.trailResult = trailResult("content-1")
        val viewModel = createViewModel(
            GraphTrailSource.FromContent(type = "directoryItem", contentId = "d1"),
        )
        advanceUntilIdle()

        assertEquals("content-1", viewModel.uiState.value.trail?.trailId)
        assertEquals(SearchResultType.DirectoryItem, fakeRepository.lastContentType)
        assertEquals("d1", fakeRepository.lastContentId)
        assertFalse(viewModel.uiState.value.canResample)
    }

    @Test
    fun `from topic loads with topic params`() = runTest {
        fakeRepository.trailResult = trailResult("topic-1")
        val viewModel = createViewModel(
            GraphTrailSource.FromTopic(topicType = "region", topicKey = "zhejiang"),
        )
        advanceUntilIdle()

        assertEquals("topic-1", viewModel.uiState.value.trail?.trailId)
        assertEquals("region", fakeRepository.lastTopicType)
        assertEquals("zhejiang", fakeRepository.lastTopicKey)
    }

    @Test
    fun `resample keeps old trail until new result arrives`() = runTest {
        fakeRepository.trailResult = trailResult("trail-1")
        val viewModel = createViewModel(GraphTrailSource.Random)
        advanceUntilIdle()

        fakeRepository.trailResult = trailResult("trail-2")
        fakeRepository.delayNextLoad = true
        viewModel.resample()

        assertEquals("trail-1", viewModel.uiState.value.trail?.trailId)
        assertTrue(viewModel.uiState.value.isLoading)

        advanceUntilIdle()
        assertEquals("trail-2", viewModel.uiState.value.trail?.trailId)
    }

    @Test
    fun `failure sets error and keeps old trail`() = runTest {
        fakeRepository.trailResult = trailResult("trail-1")
        val viewModel = createViewModel(GraphTrailSource.Random)
        advanceUntilIdle()

        fakeRepository.failure = serviceUnavailableException()
        viewModel.resample()
        advanceUntilIdle()

        assertEquals("trail-1", viewModel.uiState.value.trail?.trailId)
        assertEquals(ErrorKind.ServerError, viewModel.uiState.value.errorKind)
    }

    private suspend fun serviceUnavailableException(): ResponseException {
        val client = HttpClient(MockEngine { respond("", status = HttpStatusCode.ServiceUnavailable) }) {
            expectSuccess = true
        }
        return try {
            client.get("/")
            error("expected exception")
        } catch (e: ResponseException) {
            e
        } finally {
            client.close()
        }
    }

    private fun createViewModel(source: GraphTrailSource): GraphTrailViewModel = GraphTrailViewModel(
        source = source,
        repository = fakeRepository,
    )

    private fun trailResult(id: String) = GraphTrailResult(
        trailId = id,
        strategy = TrailStrategy.Mixed,
        title = "Trail $id",
        subtitle = null,
        startNode = GraphNodeUiModel(
            nodeKey = "article-1",
            type = GraphNodeType.Article,
            id = "a1",
            title = "Article 1",
        ),
        endNode = null,
        steps = listOf(
            GraphTrailStepUiModel(
                order = 0,
                node = GraphNodeUiModel(
                    nodeKey = "article-1",
                    type = GraphNodeType.Article,
                    id = "a1",
                    title = "Article 1",
                ),
                stepType = "start",
                reason = null,
                viaRelationType = com.duckylife.heritage.modern.core.network.dto.advanced.GraphRelationType.Unknown,
            ),
        ),
        nodes = emptyList(),
        edges = emptyList(),
        topicLabels = emptyList(),
        score = 0.0,
    )

    private class FakeKnowledgeGraphRepository : KnowledgeGraphRepository {
        var trailResult = GraphTrailResult(
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
        var delayNextLoad = false
        var lastContentType: SearchResultType? = null
        var lastContentId: String? = null
        var lastTopicType: String? = null
        var lastTopicKey: String? = null

        override suspend fun getCommunities(limit: Int, minSize: Int): List<GraphCommunityUiModel> =
            throw NotImplementedError()

        override suspend fun loadNeighbors(type: SearchResultType, id: String): GraphNeighborsResult =
            throw NotImplementedError()

        override suspend fun loadSimilar(type: SearchResultType, id: String): GraphSimilarResult =
            throw NotImplementedError()

        override suspend fun loadExplore(type: SearchResultType, id: String, depth: Int): GraphExploreResult =
            throw NotImplementedError()

        override suspend fun loadEvidence(
            type: SearchResultType,
            id: String,
            includeAiInferred: Boolean,
        ): GraphEvidenceResult = throw NotImplementedError()

        override suspend fun loadAiInferredEdges(type: SearchResultType, id: String): AiInferredEdgesResult =
            throw NotImplementedError()

        override suspend fun explainPath(
            fromType: SearchResultType,
            fromId: String,
            toType: GraphNodeType,
            toId: String,
            maxDepth: Int,
        ): PathExplainResult = throw NotImplementedError()

        override suspend fun getBridge(
            fromType: SearchResultType,
            fromId: String,
            toType: GraphNodeType,
            toId: String,
            limit: Int,
        ): BridgeResult = throw NotImplementedError()

        override suspend fun getTopicGraphMap(
            topicType: String,
            topicKey: String,
            limit: Int,
        ): TopicGraphMapResult = throw NotImplementedError()

        override suspend fun getRandomGraphTrail(
            strategy: TrailStrategy,
            type: SearchResultType?,
            limit: Int,
        ): GraphTrailResult {
            failure?.let { throw it }
            if (delayNextLoad) {
                delayNextLoad = false
                delay(10)
            }
            return trailResult
        }

        override suspend fun getGraphTrailFromContent(
            contentType: SearchResultType,
            contentId: String,
            strategy: TrailStrategy,
            limit: Int,
        ): GraphTrailResult {
            failure?.let { throw it }
            lastContentType = contentType
            lastContentId = contentId
            return trailResult
        }

        override suspend fun getGraphTrailFromTopic(
            topicType: String,
            topicKey: String,
            strategy: TrailStrategy,
            limit: Int,
        ): GraphTrailResult {
            failure?.let { throw it }
            lastTopicType = topicType
            lastTopicKey = topicKey
            return trailResult
        }
    }
}
