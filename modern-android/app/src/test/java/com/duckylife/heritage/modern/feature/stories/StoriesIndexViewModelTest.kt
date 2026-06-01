package com.duckylife.heritage.modern.feature.stories

import com.duckylife.heritage.modern.core.data.FakeHeritageRepository
import com.duckylife.heritage.modern.core.data.HeritageRepository
import com.duckylife.heritage.modern.core.network.TaxonomyRegionSort
import com.duckylife.heritage.modern.core.network.dto.TaxonomyIndexDto
import com.duckylife.heritage.modern.core.network.dto.TaxonomyTopicDto
import com.duckylife.heritage.modern.core.network.dto.TimelineYearBucketDto
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
    fun `empty data with no failures shows empty state`() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = StoriesIndexViewModel(repository = repo)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.errorKind)
        assertTrue(state.isEmpty)
    }

    @Test
    fun `loads regions when available`() = runTest {
        val delegate = FakeHeritageRepository()
        val repo = object : HeritageRepository by delegate {
            override suspend fun taxonomyRegions(
                limit: Int,
                sort: TaxonomyRegionSort,
            ): TaxonomyIndexDto<TaxonomyTopicDto> =
                TaxonomyIndexDto(items = listOf(TaxonomyTopicDto(key = "浙江", title = "浙江", total = 100)))
        }

        val viewModel = StoriesIndexViewModel(repository = repo)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.errorKind)
        assertFalse(state.isEmpty)
        assertEquals(1, state.regions.size)
        assertEquals("浙江", state.regions.first().key)
    }

    @Test
    fun `loads categories when available`() = runTest {
        val delegate = FakeHeritageRepository()
        val repo = object : HeritageRepository by delegate {
            override suspend fun taxonomyCategories(
                limit: Int,
            ): TaxonomyIndexDto<TaxonomyTopicDto> =
                TaxonomyIndexDto(items = listOf(TaxonomyTopicDto(key = "传统技艺", title = "传统技艺", total = 50)))
        }

        val viewModel = StoriesIndexViewModel(repository = repo)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.errorKind)
        assertFalse(state.isEmpty)
        assertEquals(1, state.categories.size)
        assertEquals("传统技艺", state.categories.first().key)
    }

    @Test
    fun `loads years when available`() = runTest {
        val delegate = FakeHeritageRepository()
        val repo = object : HeritageRepository by delegate {
            override suspend fun timelineYears(): List<TimelineYearBucketDto> =
                listOf(TimelineYearBucketDto(year = 2025, total = 10))
        }

        val viewModel = StoriesIndexViewModel(repository = repo)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.errorKind)
        assertFalse(state.isEmpty)
        assertEquals(1, state.years.size)
        assertEquals(2025, state.years.first().year)
    }

    @Test
    fun `partial failure still loads available data`() = runTest {
        val delegate = FakeHeritageRepository()
        val repo = object : HeritageRepository by delegate {
            override suspend fun taxonomyRegions(
                limit: Int,
                sort: TaxonomyRegionSort,
            ): TaxonomyIndexDto<TaxonomyTopicDto> =
                throw IllegalStateException("regions unavailable")

            override suspend fun taxonomyCategories(
                limit: Int,
            ): TaxonomyIndexDto<TaxonomyTopicDto> =
                TaxonomyIndexDto(items = listOf(TaxonomyTopicDto(key = "传统技艺", title = "传统技艺")))
        }

        val viewModel = StoriesIndexViewModel(repository = repo)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.errorKind) // Not an error because categories succeeded
        assertFalse(state.isEmpty)
        assertTrue(state.regions.isEmpty())
        assertEquals(1, state.categories.size)
    }

    @Test
    fun `all failure sets errorKind`() = runTest {
        val repo = FakeHeritageRepository(failure = IllegalStateException("server down"))
        val viewModel = StoriesIndexViewModel(repository = repo)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.errorKind)
        assertFalse(state.isEmpty) // isEmpty is false when errorKind is set
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
        assertNull(state.errorKind)
    }
}
