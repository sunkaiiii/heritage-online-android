package com.duckylife.heritage.modern.feature.discovery.graphexplore

import com.duckylife.heritage.modern.core.data.KnowledgeGraphRepository
import com.duckylife.heritage.modern.core.network.dto.SearchResultType
import com.duckylife.heritage.modern.core.testing.MainDispatcherRule
import com.duckylife.heritage.modern.feature.discovery.GraphTab
import com.duckylife.heritage.modern.feature.graph.model.AiInferredEdgesResult
import com.duckylife.heritage.modern.feature.graph.model.GraphEdgeUiModel
import com.duckylife.heritage.modern.feature.graph.model.GraphEvidenceResult
import com.duckylife.heritage.modern.feature.graph.model.BridgeResult
import com.duckylife.heritage.modern.feature.graph.model.GraphExploreResult
import com.duckylife.heritage.modern.feature.graph.model.GraphNeighborsResult
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphNodeType
import com.duckylife.heritage.modern.feature.graph.model.GraphNodeUiModel
import com.duckylife.heritage.modern.feature.graph.model.GraphSimilarResult
import com.duckylife.heritage.modern.feature.graph.model.PathExplainResult
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GraphExploreViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fakeRepository = FakeKnowledgeGraphRepository()

    @Test
    fun `invalid route does not load anything`() = runTest {
        val viewModel = createViewModel(contentType = "unknown", contentId = "x")
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isInvalidRoute)
        assertFalse(viewModel.uiState.value.neighbors.isLoading)
        assertNull(fakeRepository.lastLoadedTab)
    }

    @Test
    fun `init loads only neighbors when initial tab is neighbors`() = runTest {
        fakeRepository.neighborsResult = neighborsResult("article-1", "Article 1")
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(GraphTab.Neighbors, viewModel.uiState.value.selectedTab)
        assertTrue(viewModel.uiState.value.neighbors.hasData)
        assertFalse(viewModel.uiState.value.similar.isLoading)
        assertFalse(viewModel.uiState.value.similar.hasData)
        assertEquals(GraphTab.Neighbors, fakeRepository.lastLoadedTab)
    }

    @Test
    fun `init also loads initial tab when it is not neighbors`() = runTest {
        fakeRepository.neighborsResult = neighborsResult("article-1", "Article 1")
        fakeRepository.similarResult = GraphSimilarResult(emptyList())

        val viewModel = createViewModel(initialTab = GraphTab.Similar)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.neighbors.hasData)
        assertTrue(viewModel.uiState.value.similar.hasData)
    }

    @Test
    fun `selecting similar tab loads similar only once`() = runTest {
        fakeRepository.neighborsResult = neighborsResult("article-1", "Article 1")
        val viewModel = createViewModel()
        advanceUntilIdle()
        assertNull(fakeRepository.lastLoadedTabFor(GraphTab.Similar))

        fakeRepository.similarResult = GraphSimilarResult(emptyList())
        viewModel.selectTab(GraphTab.Similar)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.similar.hasData)
        assertEquals(1, fakeRepository.similarLoadCount)

        viewModel.selectTab(GraphTab.Neighbors)
        viewModel.selectTab(GraphTab.Similar)
        advanceUntilIdle()

        assertEquals(1, fakeRepository.similarLoadCount)
    }

    @Test
    fun `retry reloads only current tab`() = runTest {
        fakeRepository.failure = RuntimeException("503")
        val viewModel = createViewModel()
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.neighbors.hasError)
        assertTrue(viewModel.uiState.value.neighbors.hasFatalError)

        fakeRepository.failure = null
        fakeRepository.neighborsResult = neighborsResult("article-1", "Article 1")
        viewModel.retry()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.neighbors.hasData)
        assertFalse(viewModel.uiState.value.similar.isLoading)
    }

    @Test
    fun `refresh reloads current tab`() = runTest {
        fakeRepository.failure = RuntimeException("network")
        val viewModel = createViewModel()
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.neighbors.hasError)
        assertTrue(viewModel.uiState.value.neighbors.hasFatalError)

        fakeRepository.failure = null
        fakeRepository.neighborsResult = neighborsResult("article-1", "Article 1")
        viewModel.refresh()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.neighbors.hasData)
        assertFalse(viewModel.uiState.value.similar.isLoading)
    }

    @Test
    fun `refresh failure preserves cached data and sets section error`() = runTest {
        fakeRepository.neighborsResult = neighborsResult("article-1", "Article 1")
        val viewModel = createViewModel()
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.neighbors.hasData)

        fakeRepository.failure = RuntimeException("network")
        viewModel.refresh()
        advanceUntilIdle()

        val section = viewModel.uiState.value.neighbors
        assertTrue(section.hasData)
        assertNotNull(section.errorKind)
        assertTrue(section.hasError)
        assertFalse(section.hasFatalError)
    }

    @Test
    fun `503 maps to section error without invalidating route`() = runTest {
        fakeRepository.failure = serviceUnavailableException()
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isInvalidRoute)
        assertEquals(ErrorKind.ServerError, viewModel.uiState.value.neighbors.errorKind)
    }

    @Test
    fun `toggle ai inferred loads ai section without reloading evidence`() = runTest {
        fakeRepository.neighborsResult = neighborsResult("article-1", "Article 1")
        fakeRepository.evidenceResult = GraphEvidenceResult(emptyList(), emptyList())
        fakeRepository.aiInferredEdgesResult = AiInferredEdgesResult(emptyList())
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.selectTab(GraphTab.Evidence)
        advanceUntilIdle()
        assertEquals(1, fakeRepository.evidenceLoadCount)
        assertEquals(false, fakeRepository.lastEvidenceIncludeAiInferred)
        assertFalse(viewModel.uiState.value.includeAiInferred)

        viewModel.toggleAiInferred()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.includeAiInferred)
        assertEquals(1, fakeRepository.evidenceLoadCount)
        assertEquals(1, fakeRepository.aiInferredLoadCount)
        assertTrue(viewModel.uiState.value.aiInferredEdges.hasData)
    }

    @Test
    fun `select explore depth reloads explore with selected depth`() = runTest {
        fakeRepository.neighborsResult = neighborsResult("article-1", "Article 1")
        fakeRepository.exploreResult = GraphExploreResult(2, emptyList(), emptyList())
        val viewModel = createViewModel(initialTab = GraphTab.Explore)
        advanceUntilIdle()

        assertEquals(2, fakeRepository.lastExploreDepth)

        fakeRepository.exploreResult = GraphExploreResult(1, emptyList(), emptyList())
        viewModel.selectExploreDepth(1)
        advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.exploreDepth)
        assertEquals(1, fakeRepository.lastExploreDepth)
        assertEquals(2, fakeRepository.exploreLoadCount)
    }

    @Test
    fun `ai inferred failure keeps regular evidence data`() = runTest {
        fakeRepository.neighborsResult = neighborsResult("article-1", "Article 1")
        fakeRepository.evidenceResult = GraphEvidenceResult(emptyList(), emptyList())
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.selectTab(GraphTab.Evidence)
        advanceUntilIdle()
        fakeRepository.aiInferredFailure = serviceUnavailableException()

        viewModel.toggleAiInferred()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.evidence.hasData)
        assertEquals(ErrorKind.ServerError, viewModel.uiState.value.aiInferredEdges.errorKind)
    }

    @Test
    fun `center node is derived from neighbors`() = runTest {
        fakeRepository.neighborsResult = neighborsResult("article-1", "Center Title")
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals("Center Title", viewModel.uiState.value.centerNode?.title)
    }

    @Test
    fun `open path explain loads result`() = runTest {
        fakeRepository.neighborsResult = neighborsResult("article-1", "Center")
        fakeRepository.pathExplainResult = PathExplainResult(
            found = true,
            steps = emptyList(),
            narrative = listOf("Narrative"),
            evidence = emptyList(),
            warnings = emptyList(),
        )
        val viewModel = createViewModel()
        advanceUntilIdle()

        val target = GraphNodeUiModel(
            nodeKey = "article-2",
            type = GraphNodeType.Article,
            id = "a2",
            title = "Target",
        )
        viewModel.openPathExplain(target)
        advanceUntilIdle()

        val sheet = viewModel.uiState.value.pathExplainSheet
        assertFalse(sheet.isLoading)
        assertEquals(true, sheet.result?.found)
        assertEquals(GraphNodeType.Article, fakeRepository.lastPathExplainTargetType)
        assertEquals("a2", fakeRepository.lastPathExplainTargetId)
    }

    @Test
    fun `open path explain with unknown node type sets bad request`() = runTest {
        fakeRepository.neighborsResult = neighborsResult("article-1", "Center")
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.openPathExplain(
            GraphNodeUiModel(
                nodeKey = "unknown-1",
                type = GraphNodeType.Unknown,
                id = "u1",
                title = "Unknown",
            ),
        )
        advanceUntilIdle()

        assertEquals(ErrorKind.BadRequest, viewModel.uiState.value.pathExplainSheet.errorKind)
        assertEquals(0, fakeRepository.pathExplainLoadCount)
    }

    @Test
    fun `dismiss path explain resets state`() = runTest {
        fakeRepository.neighborsResult = neighborsResult("article-1", "Center")
        fakeRepository.pathExplainResult = PathExplainResult(
            found = true,
            steps = emptyList(),
            narrative = emptyList(),
            evidence = emptyList(),
            warnings = emptyList(),
        )
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.openPathExplain(
            GraphNodeUiModel(
                nodeKey = "article-2",
                type = GraphNodeType.Article,
                id = "a2",
                title = "Target",
            ),
        )
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.pathExplainSheet.result != null)

        viewModel.dismissPathExplain()
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.pathExplainSheet.targetNode)
        assertNull(viewModel.uiState.value.pathExplainSheet.result)
    }

    @Test
    fun `load bridge populates bridge section`() = runTest {
        fakeRepository.neighborsResult = neighborsResult("article-1", "Center")
        fakeRepository.bridgeResult = BridgeResult(emptyList())
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.openPathExplain(
            GraphNodeUiModel(
                nodeKey = "article-2",
                type = GraphNodeType.Article,
                id = "a2",
                title = "Target",
            ),
        )
        advanceUntilIdle()
        viewModel.loadBridge()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.pathExplainSheet.bridge.hasData)
        assertEquals(1, fakeRepository.bridgeLoadCount)
    }

    @Test
    fun `path explain failure sets sheet error`() = runTest {
        fakeRepository.neighborsResult = neighborsResult("article-1", "Center")
        fakeRepository.pathExplainFailure = serviceUnavailableException()
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.openPathExplain(
            GraphNodeUiModel(
                nodeKey = "article-2",
                type = GraphNodeType.Article,
                id = "a2",
                title = "Target",
            ),
        )
        advanceUntilIdle()

        assertEquals(ErrorKind.ServerError, viewModel.uiState.value.pathExplainSheet.errorKind)
    }

    @Test
    fun `path explain with blank target id sets bad request`() = runTest {
        fakeRepository.neighborsResult = neighborsResult("article-1", "Center")
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.openPathExplain(
            GraphNodeUiModel(
                nodeKey = "article-2",
                type = GraphNodeType.Article,
                id = null,
                title = "Target",
            ),
        )
        advanceUntilIdle()

        assertEquals(ErrorKind.BadRequest, viewModel.uiState.value.pathExplainSheet.errorKind)
        assertEquals(0, fakeRepository.pathExplainLoadCount)
    }

    @Test
    fun `load bridge with blank target id sets bad request`() = runTest {
        fakeRepository.neighborsResult = neighborsResult("article-1", "Center")
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.openPathExplain(
            GraphNodeUiModel(
                nodeKey = "article-2",
                type = GraphNodeType.Article,
                id = "a2",
                title = "Target",
            ),
        )
        advanceUntilIdle()

        viewModel.loadBridge()
        advanceUntilIdle()
        assertEquals(1, fakeRepository.bridgeLoadCount)

        // Replace target with blank id and retry bridge
        viewModel.openPathExplain(
            GraphNodeUiModel(
                nodeKey = "article-3",
                type = GraphNodeType.Article,
                id = "",
                title = "Target",
            ),
        )
        advanceUntilIdle()
        viewModel.loadBridge()
        advanceUntilIdle()

        assertEquals(ErrorKind.BadRequest, viewModel.uiState.value.pathExplainSheet.bridge.errorKind)
        assertEquals(1, fakeRepository.bridgeLoadCount)
    }

    @Test
    fun `path explain with topic node is rejected because endpoint supports content nodes only`() = runTest {
        fakeRepository.neighborsResult = neighborsResult("article-1", "Center")
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.openPathExplain(
            GraphNodeUiModel(
                nodeKey = "category-folk-art",
                type = GraphNodeType.Category,
                id = "folk-art",
                title = "民间美术",
            ),
        )
        advanceUntilIdle()

        assertEquals(ErrorKind.BadRequest, viewModel.uiState.value.pathExplainSheet.errorKind)
        assertEquals(0, fakeRepository.pathExplainLoadCount)
    }

    @Test
    fun `load bridge with topic node is rejected because endpoint supports content nodes only`() = runTest {
        fakeRepository.neighborsResult = neighborsResult("article-1", "Center")
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.openPathExplain(
            GraphNodeUiModel(
                nodeKey = "category-folk-art",
                type = GraphNodeType.Category,
                id = "folk-art",
                title = "民间美术",
            ),
        )
        advanceUntilIdle()
        viewModel.loadBridge()
        advanceUntilIdle()

        assertEquals(ErrorKind.BadRequest, viewModel.uiState.value.pathExplainSheet.bridge.errorKind)
        assertEquals(0, fakeRepository.bridgeLoadCount)
    }

    private fun createViewModel(
        contentType: String = "article",
        contentId: String = "a1",
        initialTab: GraphTab = GraphTab.Neighbors,
    ): GraphExploreViewModel = GraphExploreViewModel(
        contentType = contentType,
        contentId = contentId,
        initialTab = initialTab,
        repository = fakeRepository,
    )

    private fun neighborsResult(centerKey: String, title: String) = GraphNeighborsResult(
        centerNodeKey = centerKey,
        nodes = listOf(
            GraphNodeUiModel(
                nodeKey = centerKey,
                type = GraphNodeType.Article,
                id = "a1",
                title = title,
            ),
        ),
        edges = emptyList(),
    )

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

    private class FakeKnowledgeGraphRepository : KnowledgeGraphRepository {
        var neighborsResult = GraphNeighborsResult(null, emptyList(), emptyList())
        var similarResult = GraphSimilarResult(emptyList())
        var exploreResult = GraphExploreResult(2, emptyList(), emptyList())
        var evidenceResult = GraphEvidenceResult(emptyList(), emptyList())
        var aiInferredEdgesResult = AiInferredEdgesResult(emptyList())
        var pathExplainResult = PathExplainResult(
            found = true,
            steps = emptyList(),
            narrative = emptyList(),
            evidence = emptyList(),
            warnings = emptyList(),
        )
        var bridgeResult = BridgeResult(emptyList())
        var failure: Throwable? = null
        var aiInferredFailure: Throwable? = null
        var pathExplainFailure: Throwable? = null

        var lastLoadedTab: GraphTab? = null
        private val loadedTabs = mutableMapOf<GraphTab, Int>()
        var similarLoadCount = 0
        var exploreLoadCount = 0
        var lastExploreDepth: Int? = null
        var evidenceLoadCount = 0
        var aiInferredLoadCount = 0
        var lastEvidenceIncludeAiInferred: Boolean? = null
        var pathExplainLoadCount = 0
        var bridgeLoadCount = 0
        var lastPathExplainTargetType: GraphNodeType? = null
        var lastPathExplainTargetId: String? = null

        fun lastLoadedTabFor(tab: GraphTab): Int? = loadedTabs[tab]

        private fun <R> load(tab: GraphTab, block: () -> R): R {
            failure?.let { throw it }
            lastLoadedTab = tab
            loadedTabs[tab] = loadedTabs.getOrDefault(tab, 0) + 1
            return block()
        }

        override suspend fun getCommunities(limit: Int, minSize: Int): List<com.duckylife.heritage.modern.feature.graph.model.GraphCommunityUiModel> =
            throw NotImplementedError()

        override suspend fun loadNeighbors(type: SearchResultType, id: String): GraphNeighborsResult =
            load(GraphTab.Neighbors) { neighborsResult }

        override suspend fun loadSimilar(type: SearchResultType, id: String): GraphSimilarResult =
            load(GraphTab.Similar) {
                similarLoadCount++
                similarResult
            }

        override suspend fun loadExplore(type: SearchResultType, id: String, depth: Int): GraphExploreResult =
            load(GraphTab.Explore) {
                exploreLoadCount++
                lastExploreDepth = depth
                exploreResult
            }

        override suspend fun loadEvidence(
            type: SearchResultType,
            id: String,
            includeAiInferred: Boolean,
        ): GraphEvidenceResult = load(GraphTab.Evidence) {
            evidenceLoadCount++
            lastEvidenceIncludeAiInferred = includeAiInferred
            evidenceResult
        }

        override suspend fun loadAiInferredEdges(type: SearchResultType, id: String): AiInferredEdgesResult {
            aiInferredFailure?.let { throw it }
            aiInferredLoadCount++
            return aiInferredEdgesResult
        }

        override suspend fun explainPath(
            fromType: SearchResultType,
            fromId: String,
            toType: GraphNodeType,
            toId: String,
            maxDepth: Int,
        ): PathExplainResult {
            pathExplainFailure?.let { throw it }
            pathExplainLoadCount++
            lastPathExplainTargetType = toType
            lastPathExplainTargetId = toId
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
            bridgeLoadCount++
            return bridgeResult
        }

        override suspend fun getTopicGraphMap(topicType: String, topicKey: String, limit: Int) =
            throw NotImplementedError()

        override suspend fun getRandomGraphTrail(
            strategy: com.duckylife.heritage.modern.core.network.dto.advanced.TrailStrategy,
            type: SearchResultType?,
            limit: Int,
        ) = throw NotImplementedError()

        override suspend fun getGraphTrailFromContent(
            contentType: SearchResultType,
            contentId: String,
            strategy: com.duckylife.heritage.modern.core.network.dto.advanced.TrailStrategy,
            limit: Int,
        ) = throw NotImplementedError()

        override suspend fun getGraphTrailFromTopic(
            topicType: String,
            topicKey: String,
            strategy: com.duckylife.heritage.modern.core.network.dto.advanced.TrailStrategy,
            limit: Int,
        ) = throw NotImplementedError()
    }
}
