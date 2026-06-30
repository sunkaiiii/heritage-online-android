package com.duckylife.heritage.modern.feature.graph.topicmap

import com.duckylife.heritage.modern.core.data.KnowledgeGraphRepository
import com.duckylife.heritage.modern.core.network.dto.SearchResultType
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphNodeType
import com.duckylife.heritage.modern.core.network.dto.advanced.TrailStrategy
import com.duckylife.heritage.modern.core.testing.MainDispatcherRule
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
import com.duckylife.heritage.modern.ui.error.ErrorKind
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TopicGraphMapViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fakeRepository = FakeKnowledgeGraphRepository()

    @Test
    fun `init loads topic map`() = runTest {
        fakeRepository.topicGraphMapResult = TopicGraphMapResult(
            topicType = GraphNodeType.Category,
            topicKey = "folk-art",
            topicNode = null,
            nodes = emptyList(),
            edges = emptyList(),
        )
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(GraphNodeType.Category, viewModel.uiState.value.result?.topicType)
        assertEquals("folk-art", fakeRepository.lastTopicKey)
    }

    @Test
    fun `retry reloads after failure`() = runTest {
        fakeRepository.failure = serviceUnavailableException()
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(ErrorKind.ServerError, viewModel.uiState.value.errorKind)

        fakeRepository.failure = null
        fakeRepository.topicGraphMapResult = TopicGraphMapResult(
            topicType = GraphNodeType.Category,
            topicKey = "folk-art",
            topicNode = null,
            nodes = emptyList(),
            edges = emptyList(),
        )
        viewModel.retry()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.result != null)
        assertNull(viewModel.uiState.value.errorKind)
    }

    @Test
    fun `selectViewMode updates state`() = runTest {
        fakeRepository.topicGraphMapResult = TopicGraphMapResult(
            topicType = GraphNodeType.Category,
            topicKey = "folk-art",
            topicNode = null,
            nodes = emptyList(),
            edges = emptyList(),
        )
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.selectViewMode(TopicGraphMapViewMode.Overview)

        assertEquals(TopicGraphMapViewMode.Overview, viewModel.uiState.value.selectedViewMode)
    }

    @Test
    fun `blank topic key sets bad request`() = runTest {
        val viewModel = createViewModel(topicType = GraphNodeType.Category, topicKey = "")
        advanceUntilIdle()

        assertEquals(ErrorKind.BadRequest, viewModel.uiState.value.errorKind)
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

    private fun createViewModel(
        topicType: GraphNodeType = GraphNodeType.Category,
        topicKey: String = "folk-art",
    ): TopicGraphMapViewModel = TopicGraphMapViewModel(
        topicType = topicType,
        topicKey = topicKey,
        repository = fakeRepository,
    )

    private class FakeKnowledgeGraphRepository : KnowledgeGraphRepository {
        var topicGraphMapResult = TopicGraphMapResult(
            topicType = GraphNodeType.Unknown,
            topicKey = "",
            topicNode = null,
            nodes = emptyList(),
            edges = emptyList(),
        )
        var failure: Throwable? = null
        var lastTopicType: GraphNodeType? = null
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
            topicType: GraphNodeType,
            topicKey: String,
            limit: Int,
        ): TopicGraphMapResult {
            failure?.let { throw it }
            lastTopicType = topicType
            lastTopicKey = topicKey
            return topicGraphMapResult
        }

        override suspend fun getRandomGraphTrail(
            strategy: TrailStrategy,
            type: SearchResultType?,
            limit: Int,
        ): GraphTrailResult = throw NotImplementedError()

        override suspend fun getGraphTrailFromContent(
            contentType: SearchResultType,
            contentId: String,
            strategy: TrailStrategy,
            limit: Int,
        ): GraphTrailResult = throw NotImplementedError()

        override suspend fun getGraphTrailFromTopic(
            topicType: GraphNodeType,
            topicKey: String,
            strategy: TrailStrategy,
            limit: Int,
        ): GraphTrailResult = throw NotImplementedError()
    }
}
