package com.duckylife.heritage.modern.feature.graph.hub

import com.duckylife.heritage.modern.core.data.KnowledgeGraphRepository
import com.duckylife.heritage.modern.core.data.RecentContentProvider
import com.duckylife.heritage.modern.core.data.RecentContentRef
import com.duckylife.heritage.modern.core.network.dto.SearchResultType
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphNodeType
import com.duckylife.heritage.modern.core.testing.MainDispatcherRule
import com.duckylife.heritage.modern.feature.graph.model.AiInferredEdgesResult
import com.duckylife.heritage.modern.feature.graph.model.GraphCommunityUiModel
import com.duckylife.heritage.modern.ui.error.ErrorKind
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class KnowledgeGraphHubViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fakeRepository = FakeKnowledgeGraphRepository()
    private val fakeRecentContent = FakeRecentContentProvider()

    @Test
    fun `loads communities on init`() = runTest {
        fakeRepository.communitiesResult = listOf(
            GraphCommunityUiModel(
                communityKey = "c1",
                topicType = GraphNodeType.Category,
                title = "传统技艺",
                topicChips = emptyList(),
                contentCount = 2,
                relationCount = 3,
                primaryTopicKey = "category-1",
            ),
        )

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.communities.isLoading)
        assertEquals(1, state.communities.data?.size)
        assertEquals("传统技艺", state.communities.data?.first()?.title)
    }

    @Test
    fun `empty communities shows empty state`() = runTest {
        fakeRepository.communitiesResult = emptyList()

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.communities.isLoading)
        assertTrue(viewModel.uiState.value.communities.data.isNullOrEmpty())
    }

    @Test
    fun `503 error sets error kind`() = runTest {
        fakeRepository.failure = serviceUnavailableException()

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.communities.isLoading)
        assertEquals(ErrorKind.ServerError, viewModel.uiState.value.communities.errorKind)
    }

    @Test
    fun `retry reloads communities`() = runTest {
        fakeRepository.failure = RuntimeException("503")
        val viewModel = createViewModel()
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.communities.hasError)
        assertTrue(viewModel.uiState.value.communities.hasFatalError)

        fakeRepository.failure = null
        fakeRepository.communitiesResult = listOf(
            GraphCommunityUiModel(
                communityKey = "c1",
                topicType = GraphNodeType.Category,
                title = "传统技艺",
                topicChips = emptyList(),
                contentCount = 1,
                relationCount = 1,
                primaryTopicKey = "category-1",
            ),
        )
        viewModel.retry()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.communities.hasError)
        assertEquals(1, viewModel.uiState.value.communities.data?.size)
    }

    @Test
    fun `recent content is observed`() = runTest {
        fakeRepository.communitiesResult = emptyList()
        fakeRecentContent.emit(RecentContentRef(SearchResultType.Article, "a1", "Article"))

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals("a1", viewModel.uiState.value.recentContent?.id)
        assertEquals("Article", viewModel.uiState.value.recentContent?.title)
    }

    @Test
    fun `no recent content leaves ref null`() = runTest {
        fakeRepository.communitiesResult = emptyList()
        val viewModel = createViewModel()
        advanceUntilIdle()
        assertNull(viewModel.uiState.value.recentContent)
    }

    @Test
    fun `info sheet visibility toggles`() = runTest {
        fakeRepository.communitiesResult = emptyList()
        val viewModel = createViewModel()

        viewModel.showInfoSheet()
        assertTrue(viewModel.uiState.value.isInfoSheetVisible)

        viewModel.dismissInfoSheet()
        assertFalse(viewModel.uiState.value.isInfoSheetVisible)
    }

    private fun createViewModel(): KnowledgeGraphHubViewModel =
        KnowledgeGraphHubViewModel(fakeRepository, fakeRecentContent)

    private class FakeKnowledgeGraphRepository : KnowledgeGraphRepository {
        var communitiesResult: List<GraphCommunityUiModel> = emptyList()
        var failure: Throwable? = null

        override suspend fun getCommunities(limit: Int, minSize: Int): List<GraphCommunityUiModel> {
            failure?.let { throw it }
            return communitiesResult
        }

        override suspend fun loadNeighbors(type: SearchResultType, id: String) =
            throw NotImplementedError()

        override suspend fun loadSimilar(type: SearchResultType, id: String) =
            throw NotImplementedError()

        override suspend fun loadExplore(type: SearchResultType, id: String, depth: Int) =
            throw NotImplementedError()

        override suspend fun loadEvidence(type: SearchResultType, id: String, includeAiInferred: Boolean) =
            throw NotImplementedError()

        override suspend fun loadAiInferredEdges(type: SearchResultType, id: String): AiInferredEdgesResult =
            throw NotImplementedError()

        override suspend fun explainPath(
            fromType: SearchResultType,
            fromId: String,
            toType: GraphNodeType,
            toId: String,
            maxDepth: Int,
        ) = throw NotImplementedError()

        override suspend fun getBridge(
            fromType: SearchResultType,
            fromId: String,
            toType: GraphNodeType,
            toId: String,
            limit: Int,
        ) = throw NotImplementedError()

        override suspend fun getTopicGraphMap(topicType: GraphNodeType, topicKey: String, limit: Int) =
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
            topicType: GraphNodeType,
            topicKey: String,
            strategy: com.duckylife.heritage.modern.core.network.dto.advanced.TrailStrategy,
            limit: Int,
        ) = throw NotImplementedError()
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

    private class FakeRecentContentProvider : RecentContentProvider {
        private val flow = MutableStateFlow<RecentContentRef?>(null)
        fun emit(ref: RecentContentRef?) {
            flow.value = ref
        }

        override fun observeRecentContent() = flow
    }
}
