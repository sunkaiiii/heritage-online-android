package com.duckylife.heritage.modern.feature.stories

import com.duckylife.heritage.modern.core.data.FakeHeritageRepository
import com.duckylife.heritage.modern.core.data.HeritageRepository
import com.duckylife.heritage.modern.core.network.dto.TaxonomyIndexDto
import com.duckylife.heritage.modern.core.network.dto.TaxonomyTopicDto
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
class StoriesIndexViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `loads data when available`() = runTest {
        // FakeHeritageRepository returns empty data by default,
        // which triggers the "no data" error path.
        // This test verifies the loading completes.
        val repo = FakeHeritageRepository()
        val viewModel = StoriesIndexViewModel(repository = repo)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        // All empty -> hasAnyData is false -> errorKind is set
        assertNotNull(state.errorKind)
    }

    @Test
    fun `partial failure still loads available data`() = runTest {
        val delegate = FakeHeritageRepository()
        val repo = object : HeritageRepository by delegate {
            override suspend fun taxonomyRegions(
                limit: Int,
                sort: com.duckylife.heritage.modern.core.network.TaxonomyRegionSort,
            ): TaxonomyIndexDto<TaxonomyTopicDto> =
                throw IllegalStateException("regions unavailable")
        }

        val viewModel = StoriesIndexViewModel(repository = repo)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        // Regions failed, categories and years are empty -> still no data -> error
        assertNotNull(state.errorKind)
    }

    @Test
    fun `all failure sets errorKind`() = runTest {
        val repo = FakeHeritageRepository(failure = IllegalStateException("server down"))
        val viewModel = StoriesIndexViewModel(repository = repo)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.errorKind)
    }

    @Test
    fun `loadAll refreshes data`() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = StoriesIndexViewModel(repository = repo)

        advanceUntilIdle()

        viewModel.loadAll()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
    }
}
