package com.duckylife.heritage.modern.feature.discovery

import com.duckylife.heritage.modern.core.data.FakeHeritageRepository
import com.duckylife.heritage.modern.core.data.HeritageRepository
import com.duckylife.heritage.modern.core.network.dto.DiscoveryItemDto
import com.duckylife.heritage.modern.core.network.dto.DiscoveryTodayDto
import com.duckylife.heritage.modern.core.network.dto.DiscoveryTrendingDto
import com.duckylife.heritage.modern.core.network.dto.DiscoveryWeeklyDto
import com.duckylife.heritage.modern.core.network.dto.ExploreIndexDto
import com.duckylife.heritage.modern.core.network.dto.ExploreTopicInfoDto
import com.duckylife.heritage.modern.core.network.dto.FeaturedCollectionDto
import com.duckylife.heritage.modern.core.network.dto.LearningPathDto
import com.duckylife.heritage.modern.core.network.dto.RegionAtlasDto
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
        assertFalse(state.isLoading)
        assertNull(state.errorKind)
        // Default DTOs are non-null, so hasAnyData is true
        assertNotNull(state.today)
        assertNotNull(state.trending)
        assertNotNull(state.weekly)
        assertNotNull(state.exploreIndex)
        assertNotNull(state.regionAtlas)
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
        assertFalse(state.isLoading)
        assertNull(state.errorKind)
        // Topics and learningPaths failed, but other data loaded
        assertTrue(state.topics.isEmpty())
        assertTrue(state.learningPaths.isEmpty())
        // These should still be non-null from the default DTOs
        assertNotNull(state.today)
        assertNotNull(state.trending)
        assertNotNull(state.weekly)
        assertNotNull(state.exploreIndex)
    }

    @Test
    fun `all failure sets errorKind`() = runTest {
        val repo = FakeHeritageRepository(failure = IllegalStateException("server down"))
        val viewModel = DiscoveryViewModel(repository = repo)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.errorKind)
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
        assertFalse(state.isLoading)
        assertNull(state.errorKind)
    }
}
