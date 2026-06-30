package com.duckylife.heritage.modern.feature.search

import com.duckylife.heritage.modern.core.data.FakeHeritageRepository
import com.duckylife.heritage.modern.core.data.IntelligentSearchRepository
import com.duckylife.heritage.modern.core.network.IntelligentSearchQuery
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphNodeType
import com.duckylife.heritage.modern.core.network.dto.advanced.IntelligentSearchItemDto
import com.duckylife.heritage.modern.core.network.dto.advanced.IntelligentSearchResponseDto
import com.duckylife.heritage.modern.core.testing.MainDispatcherRule
import com.duckylife.heritage.modern.ui.error.ErrorKind
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondError
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class IntelligentSearchViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `intelligent mode debounces query and sends default options`() = runTest {
        val intelligentRepository = FakeIntelligentSearchRepository(
            response = IntelligentSearchResponseDto(
                items = listOf(IntelligentSearchItemDto(type = GraphNodeType.Article, id = "a1")),
                total = 1,
            ),
        )
        val viewModel = SearchViewModel(
            repository = FakeHeritageRepository(),
            intelligentSearchRepository = intelligentRepository,
        )

        viewModel.updateQuery("剪纸")
        viewModel.selectMode(SearchMode.Intelligent)
        advanceTimeBy(299)
        assertTrue(intelligentRepository.queries.isEmpty())

        advanceTimeBy(1)
        advanceUntilIdle()

        val query = intelligentRepository.queries.single()
        assertEquals("剪纸", query.keywords)
        assertEquals(20, query.pageSize)
        assertTrue(query.includeAi)
        assertFalse(query.includeGraph)
        assertTrue(query.includeHighlights)
        assertEquals(1, viewModel.uiState.value.intelligentResults.size)
    }

    @Test
    fun `intelligent query change cancels pending search`() = runTest {
        val intelligentRepository = FakeIntelligentSearchRepository()
        val viewModel = SearchViewModel(
            repository = FakeHeritageRepository(),
            intelligentSearchRepository = intelligentRepository,
        )

        viewModel.selectMode(SearchMode.Intelligent)
        viewModel.updateQuery("旧关键词")
        advanceTimeBy(100)
        viewModel.updateQuery("新关键词")
        advanceUntilIdle()

        assertEquals(listOf("新关键词"), intelligentRepository.queries.map { it.keywords })
    }

    @Test
    fun `intelligent filters and why match state are retained`() = runTest {
        val intelligentRepository = FakeIntelligentSearchRepository()
        val viewModel = SearchViewModel(
            repository = FakeHeritageRepository(),
            intelligentSearchRepository = intelligentRepository,
        )
        val item = IntelligentSearchItemDto(type = GraphNodeType.Article, id = "a1")

        viewModel.selectMode(SearchMode.Intelligent)
        viewModel.updateQuery("剪纸")
        viewModel.updateIntelligentIncludeAi(false)
        viewModel.updateIntelligentIncludeGraph(true)
        viewModel.updateIntelligentIncludeHighlights(false)
        advanceUntilIdle()

        val query = intelligentRepository.queries.single()
        assertFalse(query.includeAi)
        assertTrue(query.includeGraph)
        assertFalse(query.includeHighlights)
        viewModel.showWhyMatch(item)
        assertEquals(item, viewModel.uiState.value.whyMatchItem)
        viewModel.dismissWhyMatch()
        assertNull(viewModel.uiState.value.whyMatchItem)
    }

    @Test
    fun `intelligent 503 exposes reference search fallback state`() = runTest {
        val viewModel = SearchViewModel(
            repository = FakeHeritageRepository(),
            intelligentSearchRepository = FakeIntelligentSearchRepository(
                failure = serviceUnavailableException(),
            ),
        )

        viewModel.updateQuery("剪纸")
        viewModel.selectMode(SearchMode.Intelligent)
        advanceUntilIdle()

        assertEquals(ErrorKind.ServerError, viewModel.uiState.value.errorKind)
        assertTrue(viewModel.uiState.value.intelligenceUnavailable)
    }
}

private class FakeIntelligentSearchRepository(
    private val response: IntelligentSearchResponseDto = IntelligentSearchResponseDto(),
    private val failure: Throwable? = null,
) : IntelligentSearchRepository {
    val queries = mutableListOf<IntelligentSearchQuery>()

    override suspend fun search(query: IntelligentSearchQuery): IntelligentSearchResponseDto {
        queries += query
        failure?.let { throw it }
        return response
    }
}

private suspend fun serviceUnavailableException(): Throwable {
    val client = HttpClient(MockEngine { respondError(HttpStatusCode.ServiceUnavailable) }) {
        expectSuccess = true
    }
    return runCatching { client.get("http://test.example/") }.exceptionOrNull()
        ?: error("expected exception")
}
