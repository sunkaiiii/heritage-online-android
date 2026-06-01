package com.duckylife.heritage.modern.feature.discovery

import com.duckylife.heritage.modern.core.data.FakeHeritageRepository
import com.duckylife.heritage.modern.core.data.HeritageRepository
import com.duckylife.heritage.modern.core.network.dto.ExploreTopicInfoDto
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
class DiscoveryViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `loads all data on init`() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = DiscoveryViewModel(repository = repo)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isAnyLoading)
        // Section-level: today, trending, weekly should have data
        assertTrue(state.today.hasData)
        assertTrue(state.trending.hasData)
        assertTrue(state.weekly.hasData)
        // Classic section should have data
        assertTrue(state.classic.hasData)
        assertNotNull(state.classic.data?.exploreIndex)
        assertNotNull(state.classic.data?.regionAtlas)
    }

    @Test
    fun `serendipity sets item in state`() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = DiscoveryViewModel(repository = repo)

        advanceUntilIdle()

        viewModel.serendipity()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.serendipityLoading)
        assertNotNull(state.serendipityItem)
    }

    @Test
    fun `clearSerendipity resets item`() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = DiscoveryViewModel(repository = repo)

        advanceUntilIdle()

        viewModel.serendipity()
        advanceUntilIdle()
        assertNotNull(viewModel.uiState.value.serendipityItem)

        viewModel.clearSerendipity()
        assertNull(viewModel.uiState.value.serendipityItem)
    }

    @Test
    fun `partial failure still loads available data`() = runTest {
        // Create a repo where exploreTopics and learningPaths fail,
        // but other methods succeed with default DTOs
        val delegate = FakeHeritageRepository()
        val repo = object : HeritageRepository by delegate {
            override suspend fun exploreTopics(type: String?, limit: Int): List<ExploreTopicInfoDto> =
                throw IllegalStateException("topics unavailable")

            override suspend fun learningPaths(): List<com.duckylife.heritage.modern.core.network.dto.LearningPathDto> =
                throw IllegalStateException("paths unavailable")
        }

        val viewModel = DiscoveryViewModel(repository = repo)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        // Today, trending, weekly should still have data
        assertTrue(state.today.hasData)
        assertTrue(state.trending.hasData)
        assertTrue(state.weekly.hasData)
        // Classic section should still load (some sub-items failed but atlas/exploreIndex succeeded)
        assertTrue(state.classic.hasData)
        // Topics and learningPaths failed
        assertTrue(state.classic.data?.topics?.isEmpty() ?: true)
        assertTrue(state.classic.data?.learningPaths?.isEmpty() ?: true)
    }

    @Test
    fun `all failure sets errorKind on sections`() = runTest {
        val repo = FakeHeritageRepository(failure = IllegalStateException("server down"))
        val viewModel = DiscoveryViewModel(repository = repo)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        // All sections should have errors
        assertTrue(state.today.hasError)
        assertTrue(state.trending.hasError)
        assertTrue(state.weekly.hasError)
        assertTrue(state.classic.hasError)
        assertTrue(state.isAllFailed)
    }

    @Test
    fun `loadAll refreshes data`() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = DiscoveryViewModel(repository = repo)

        advanceUntilIdle()

        // Call loadAll again to test refresh
        viewModel.loadAll()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.today.hasData)
        assertTrue(state.trending.hasData)
        assertTrue(state.weekly.hasData)
    }
}
