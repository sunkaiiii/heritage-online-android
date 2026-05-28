package com.duckylife.heritage.modern.feature.search

import com.duckylife.heritage.modern.core.data.FakeHeritageRepository
import com.duckylife.heritage.modern.core.data.HeritageRepository
import com.duckylife.heritage.modern.core.network.dto.SearchResultItemDto
import com.duckylife.heritage.modern.core.network.dto.SearchV2ResponseDto
import com.duckylife.heritage.modern.core.testing.MainDispatcherRule
import com.duckylife.heritage.modern.ui.error.ErrorKind
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.advanceTimeBy
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
class SearchViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // region empty data state

    @Test
    fun `blank query does not trigger search`() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = SearchViewModel(repository = repo)

        viewModel.updateQuery("   ")
        viewModel.search()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isSearching)
        assertTrue(state.results.isEmpty())
        assertNull(state.errorKind)
    }

    @Test
    fun `empty search results sets empty list without error`() = runTest {
        val repo = FakeHeritageRepository(
            searchV2Response = SearchV2ResponseDto(
                items = emptyList(),
                total = 0,
                hasMore = false,
            ),
        )
        val viewModel = SearchViewModel(repository = repo)

        viewModel.updateQuery("不存在的关键词")
        viewModel.search()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isSearching)
        assertTrue(state.results.isEmpty())
        assertEquals(0L, state.total)
        assertFalse(state.hasMore)
        assertNull(state.errorKind)
    }

    @Test
    fun `search failure sets errorKind`() = runTest {
        val repo = FakeHeritageRepository(failure = IllegalStateException("network down"))
        val viewModel = SearchViewModel(repository = repo)

        viewModel.updateQuery("test")
        viewModel.search()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isSearching)
        assertNotNull(state.errorKind)
    }

    @Test
    fun `empty suggestions list does not crash`() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = SearchViewModel(repository = repo)

        viewModel.updateQuery("xyz")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.suggestions.isEmpty())
    }

    // endregion

    // region fast state switching

    @Test
    fun `fast query change cancels previous search`() = runTest {
        val repo = SlowSearchRepository(delayMs = 100)
        val viewModel = SearchViewModel(repository = repo)

        viewModel.updateQuery("first")
        viewModel.search()
        advanceTimeBy(50)

        viewModel.updateQuery("second")
        viewModel.search()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isSearching)
        assertNull(state.errorKind)
        assertEquals("second", repo.lastQuery)
    }

    @Test
    fun `fast filter change cancels previous search`() = runTest {
        val repo = SlowSearchRepository(delayMs = 100)
        val viewModel = SearchViewModel(repository = repo)

        viewModel.updateQuery("test")
        viewModel.search()
        advanceTimeBy(50)

        viewModel.updateRegionFilter("北京")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isSearching)
        assertNull(state.errorKind)
    }

    // endregion
}

private class SlowSearchRepository(
    private val delayMs: Long,
    internal val delegate: FakeHeritageRepository = FakeHeritageRepository(
        searchV2Response = SearchV2ResponseDto(
            items = listOf(
                SearchResultItemDto(id = "1", type = "article", title = "Result 1"),
            ),
            total = 1,
            hasMore = false,
        ),
    ),
) : HeritageRepository by delegate {
    var lastQuery: String? = null
        private set

    override suspend fun searchV2(query: com.duckylife.heritage.modern.core.network.SearchV2Query): SearchV2ResponseDto {
        delay(delayMs)
        lastQuery = query.keywords
        return delegate.searchV2(query)
    }
}
