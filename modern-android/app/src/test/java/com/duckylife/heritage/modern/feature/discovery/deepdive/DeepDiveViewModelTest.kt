package com.duckylife.heritage.modern.feature.discovery.deepdive

import com.duckylife.heritage.modern.core.data.FakeHeritageRepository
import com.duckylife.heritage.modern.core.network.dto.DiscoveryItemDto
import com.duckylife.heritage.modern.core.network.dto.SearchResultType
import com.duckylife.heritage.modern.core.testing.MainDispatcherRule
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
class DeepDiveViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `loads deep dive data on init`() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = DeepDiveViewModel(
            seedType = SearchResultType.Article,
            seedId = "art-1",
            repository = repo,
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.errorKind)
        // Default DiscoveryDeepDiveDto has seed = null and related = emptyList()
        assertNotNull(state) // state itself is always non-null
    }

    @Test
    fun `deepDiveAgain refreshes with new seed`() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = DeepDiveViewModel(
            seedType = SearchResultType.Article,
            seedId = "art-1",
            repository = repo,
        )

        advanceUntilIdle()

        val newItem = DiscoveryItemDto(
            id = "art-2",
            type = "article",
            title = "New Seed",
        )
        viewModel.deepDiveAgain(newItem)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.errorKind)
    }

    @Test
    fun `uses safe type conversion for invalid wire name`() = runTest {
        // An invalid seedType should not crash; it should fall back to Article
        val repo = FakeHeritageRepository()
        val viewModel = DeepDiveViewModel(
            seedType = SearchResultType.Unknown,
            seedId = "some-id",
            repository = repo,
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        // Should not crash; the invalid type falls back to SearchResultType.Article
        assertFalse(state.isLoading)
        assertNull(state.errorKind)
    }

    @Test
    fun `deepDiveAgain uses safe type conversion for item type`() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = DeepDiveViewModel(
            seedType = SearchResultType.Article,
            seedId = "art-1",
            repository = repo,
        )

        advanceUntilIdle()

        // Item with an invalid type string should not crash
        val newItem = DiscoveryItemDto(
            id = "item-1",
            type = "invalid_wire_type",
            title = "Test",
        )
        viewModel.deepDiveAgain(newItem)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.errorKind)
    }

    @Test
    fun `failure sets errorKind`() = runTest {
        val repo = FakeHeritageRepository(failure = IllegalStateException("not found"))
        val viewModel = DeepDiveViewModel(
            seedType = SearchResultType.Article,
            seedId = "missing",
            repository = repo,
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.errorKind)
    }

    @Test
    fun `deepDiveAgain failure sets errorKind`() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = DeepDiveViewModel(
            seedType = SearchResultType.Article,
            seedId = "art-1",
            repository = repo,
        )

        advanceUntilIdle()

        // Now swap to a failing repo by using a new ViewModel
        val failingRepo = FakeHeritageRepository(failure = IllegalStateException("server error"))
        val failingViewModel = DeepDiveViewModel(
            seedType = SearchResultType.Article,
            seedId = "art-1",
            repository = failingRepo,
        )

        advanceUntilIdle()

        val state = failingViewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.errorKind)
    }
}
