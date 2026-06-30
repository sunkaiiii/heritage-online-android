package com.duckylife.heritage.modern.feature.taxonomy

import com.duckylife.heritage.modern.core.data.FakeHeritageRepository
import com.duckylife.heritage.modern.core.data.HeritageRepository
import com.duckylife.heritage.modern.core.network.dto.TaxonomyIndexDto
import com.duckylife.heritage.modern.core.network.dto.TaxonomyKindDto
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
class TaxonomyViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `loads categories regions and kinds on init`() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = TaxonomyViewModel(repository = repo)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.errorKind)
        // Default TaxonomyIndexDto has empty items list
        assertNotNull(state.categories)
        assertNotNull(state.regions)
        assertNotNull(state.kinds)
    }

    @Test
    fun `failure sets errorKind`() = runTest {
        val repo = FakeHeritageRepository(failure = IllegalStateException("server error"))
        val viewModel = TaxonomyViewModel(repository = repo)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.errorKind)
    }

    @Test
    fun `partial failure sets errorKind when categories fail`() = runTest {
        val delegate = FakeHeritageRepository()
        val repo = object : HeritageRepository by delegate {
            override suspend fun taxonomyCategories(limit: Int): TaxonomyIndexDto<TaxonomyTopicDto> =
                throw IllegalStateException("categories unavailable")
        }

        val viewModel = TaxonomyViewModel(repository = repo)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        // TaxonomyViewModel treats any failure as a full error
        assertNotNull(state.errorKind)
    }

    @Test
    fun `partial failure sets errorKind when regions fail`() = runTest {
        val delegate = FakeHeritageRepository()
        val repo = object : HeritageRepository by delegate {
            override suspend fun taxonomyRegions(
                limit: Int,
                sort: com.duckylife.heritage.modern.core.network.TaxonomyRegionSort,
            ): TaxonomyIndexDto<TaxonomyTopicDto> =
                throw IllegalStateException("regions unavailable")
        }

        val viewModel = TaxonomyViewModel(repository = repo)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.errorKind)
    }

    @Test
    fun `partial failure sets errorKind when kinds fail`() = runTest {
        val delegate = FakeHeritageRepository()
        val repo = object : HeritageRepository by delegate {
            override suspend fun taxonomyKinds(): TaxonomyIndexDto<TaxonomyKindDto> =
                throw IllegalStateException("kinds unavailable")
        }

        val viewModel = TaxonomyViewModel(repository = repo)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.errorKind)
    }

    @Test
    fun `loadAll refreshes data`() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = TaxonomyViewModel(repository = repo)

        advanceUntilIdle()

        // Call loadAll again to test refresh
        viewModel.loadAll()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.errorKind)
    }
}
