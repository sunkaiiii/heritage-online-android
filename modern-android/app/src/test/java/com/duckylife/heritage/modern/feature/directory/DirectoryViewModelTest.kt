package com.duckylife.heritage.modern.feature.directory

import com.duckylife.heritage.modern.core.data.FakeHeritageRepository
import androidx.lifecycle.SavedStateHandle
import com.duckylife.heritage.modern.core.testing.MainDispatcherRule
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind
import com.duckylife.heritage.modern.core.network.dto.DirectoryStatisticDimension
import com.duckylife.heritage.modern.core.network.dto.DirectoryStatisticDimensionDto
import com.duckylife.heritage.modern.core.network.dto.DirectoryStatisticItemDto
import com.duckylife.heritage.modern.core.network.dto.DirectoryStatisticsOverviewDto
import com.duckylife.heritage.modern.ui.error.ErrorKind
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class DirectoryViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun startsWithNationalProjectKind() = runTest {
        val viewModel = DirectoryViewModel(
            savedStateHandle = SavedStateHandle(), repository = FakeHeritageRepository(),
        )
        assertEquals(DirectoryItemKind.NationalProject, viewModel.uiState.value.selectedKind)
    }

    @Test
    fun selectKindUpdatesSelectedKind() = runTest {
        val viewModel = DirectoryViewModel(
            savedStateHandle = SavedStateHandle(), repository = FakeHeritageRepository(),
        )
        viewModel.selectKind(DirectoryItemKind.CulturalEcoZone)
        advanceUntilIdle()
        assertEquals(DirectoryItemKind.CulturalEcoZone, viewModel.uiState.value.selectedKind)
    }

    @Test
    fun searchKeywordsMappedToQuery() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = DirectoryViewModel(repository = repo, savedStateHandle = SavedStateHandle())

        viewModel.selectKind(DirectoryItemKind.UnescoEntry)
        viewModel.updateSearchKeywords("世界遗产")
        advanceUntilIdle()

        viewModel.items.first()
        assertEquals(1, repo.pagedDirectoryItemQueries.size)
        val query = repo.pagedDirectoryItemQueries.first()
        assertEquals(DirectoryItemKind.UnescoEntry, query.kind)
        assertEquals("世界遗产", query.keywords)
    }

    @Test
    fun regionFilterMappedToQuery() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = DirectoryViewModel(repository = repo, savedStateHandle = SavedStateHandle())

        viewModel.applyFilters(regionFilter = "北京", categoryFilter = "", yearFilter = "", listTypeFilter = "")
        advanceUntilIdle()

        viewModel.items.first()
        assertEquals("北京", repo.pagedDirectoryItemQueries.first().region)
    }

    @Test
    fun categoryFilterMappedToQuery() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = DirectoryViewModel(repository = repo, savedStateHandle = SavedStateHandle())

        viewModel.applyFilters(regionFilter = "", categoryFilter = "传统美术", yearFilter = "", listTypeFilter = "")
        advanceUntilIdle()

        viewModel.items.first()
        assertEquals("传统美术", repo.pagedDirectoryItemQueries.first().category)
    }

    @Test
    fun yearFilterMappedToQuery() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = DirectoryViewModel(repository = repo, savedStateHandle = SavedStateHandle())

        viewModel.applyFilters(regionFilter = "", categoryFilter = "", yearFilter = "2006", listTypeFilter = "")
        advanceUntilIdle()

        viewModel.items.first()
        assertEquals(2006, repo.pagedDirectoryItemQueries.first().year)
    }

    @Test
    fun listTypeFilterMappedToQuery() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = DirectoryViewModel(repository = repo, savedStateHandle = SavedStateHandle())

        viewModel.applyFilters(regionFilter = "", categoryFilter = "", yearFilter = "", listTypeFilter = "representative")
        advanceUntilIdle()

        viewModel.items.first()
        assertEquals("representative", repo.pagedDirectoryItemQueries.first().listType)
    }

    @Test
    fun blankFiltersNotMappedToQuery() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = DirectoryViewModel(repository = repo, savedStateHandle = SavedStateHandle())

        viewModel.applyFilters(regionFilter = "  ", categoryFilter = "", yearFilter = "", listTypeFilter = "")
        advanceUntilIdle()

        viewModel.items.first()
        val query = repo.pagedDirectoryItemQueries.first()
        assertNull(query.region)
        assertNull(query.year)
    }

    @Test
    fun activeFilterCountReflectsNonBlankFilters() = runTest {
        val viewModel = DirectoryViewModel(savedStateHandle = SavedStateHandle(), repository = FakeHeritageRepository())

        assertEquals(0, viewModel.uiState.value.activeFilterCount)

        viewModel.applyFilters(regionFilter = "北京", categoryFilter = "传统美术", yearFilter = "", listTypeFilter = "")
        advanceUntilIdle()

        assertEquals(2, viewModel.uiState.value.activeFilterCount)
    }

    @Test
    fun clearFiltersResetsAllToDefaults() = runTest {
        val viewModel = DirectoryViewModel(savedStateHandle = SavedStateHandle(), repository = FakeHeritageRepository())

        viewModel.updateSearchKeywords("test")
        viewModel.applyFilters(regionFilter = "北京", categoryFilter = "music", yearFilter = "2020", listTypeFilter = "list")
        advanceUntilIdle()

        viewModel.clearFilters()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("", state.searchKeywords)
        assertEquals("", state.regionFilter)
        assertEquals("", state.categoryFilter)
        assertEquals("", state.yearFilter)
        assertEquals("", state.listTypeFilter)
        assertEquals(0, state.activeFilterCount)
    }

    // region statistics

    @Test
    fun `selectTab switches to Statistics tab`() = runTest {
        val viewModel = DirectoryViewModel(savedStateHandle = SavedStateHandle(), repository = FakeHeritageRepository())

        viewModel.selectTab(DirectoryTab.Statistics)
        advanceUntilIdle()

        assertEquals(DirectoryTab.Statistics, viewModel.uiState.value.selectedTab)
    }

    @Test
    fun `switching to Statistics tab triggers statistics API calls`() = runTest {
        val repo = FakeHeritageRepository(
            directoryStatisticsOverview = DirectoryStatisticsOverviewDto(
                kind = "nationalProject",
                total = 950,
                generatedAt = "2025-01-01",
            ),
            directoryStatisticsBreakdowns = mapOf(
                DirectoryStatisticDimension.PublishedYear to DirectoryStatisticDimensionDto(
                    dimension = "publishedYear",
                    items = listOf(DirectoryStatisticItemDto(key = "2006", name = "2006", value = 200)),
                ),
                DirectoryStatisticDimension.Category to DirectoryStatisticDimensionDto(
                    dimension = "category",
                    items = listOf(DirectoryStatisticItemDto(key = "music", name = "传统音乐", value = 300)),
                ),
                DirectoryStatisticDimension.Region to DirectoryStatisticDimensionDto(
                    dimension = "region",
                    items = listOf(DirectoryStatisticItemDto(key = "beijing", name = "北京", value = 100)),
                ),
            ),
        )
        val viewModel = DirectoryViewModel(savedStateHandle = SavedStateHandle(), repository = repo)

        viewModel.selectTab(DirectoryTab.Statistics)
        advanceUntilIdle()

        assertEquals(1, repo.directoryStatisticsOverviewQueries.size)
        assertEquals(DirectoryItemKind.NationalProject, repo.directoryStatisticsOverviewQueries.first())
        assertEquals(3, repo.directoryStatisticsBreakdownQueries.size)

        val state = viewModel.uiState.value.statisticsState
        assertEquals(950L, state.overview?.total)
        assertNotNull(state.yearBreakdown)
        assertNotNull(state.categoryBreakdown)
        assertNotNull(state.regionBreakdown)
        assertEquals(false, state.isLoading)
        assertNull(state.errorKind)
    }

    @Test
    fun `changing kind while on Statistics tab re-fetches statistics`() = runTest {
        val repo = FakeHeritageRepository(
            directoryStatisticsOverview = DirectoryStatisticsOverviewDto(total = 100),
            directoryStatisticsBreakdowns = mapOf(
                DirectoryStatisticDimension.PublishedYear to DirectoryStatisticDimensionDto(),
                DirectoryStatisticDimension.Category to DirectoryStatisticDimensionDto(),
                DirectoryStatisticDimension.Region to DirectoryStatisticDimensionDto(),
            ),
        )
        val viewModel = DirectoryViewModel(savedStateHandle = SavedStateHandle(), repository = repo)

        viewModel.selectTab(DirectoryTab.Statistics)
        advanceUntilIdle()
        assertEquals(1, repo.directoryStatisticsOverviewQueries.size)

        viewModel.selectKind(DirectoryItemKind.UnescoEntry)
        advanceUntilIdle()

        assertEquals(2, repo.directoryStatisticsOverviewQueries.size)
        assertEquals(DirectoryItemKind.UnescoEntry, repo.directoryStatisticsOverviewQueries.last())
    }

    @Test
    fun `API failure sets errorKind in statisticsState`() = runTest {
        val repo = FakeHeritageRepository(failure = IOException("Network error"))
        val viewModel = DirectoryViewModel(savedStateHandle = SavedStateHandle(), repository = repo)

        viewModel.selectTab(DirectoryTab.Statistics)
        advanceUntilIdle()

        val state = viewModel.uiState.value.statisticsState
        assertEquals(false, state.isLoading)
        assertNotNull(state.errorKind)
        assertEquals(ErrorKind.NetworkUnavailable, state.errorKind)
    }

    @Test
    fun `empty dimensions produce empty items without crash`() = runTest {
        val repo = FakeHeritageRepository(
            directoryStatisticsOverview = DirectoryStatisticsOverviewDto(
                kind = "nationalProject",
                total = 0,
                dimensions = emptyList(),
            ),
            directoryStatisticsBreakdowns = emptyMap(),
        )
        val viewModel = DirectoryViewModel(savedStateHandle = SavedStateHandle(), repository = repo)

        viewModel.selectTab(DirectoryTab.Statistics)
        advanceUntilIdle()

        val state = viewModel.uiState.value.statisticsState
        assertEquals(false, state.isLoading)
        assertNull(state.errorKind)
        assertNotNull(state.overview)
        assertEquals(0L, state.overview?.total)
        assertTrue(state.yearBreakdown?.items.orEmpty().isEmpty())
        assertTrue(state.categoryBreakdown?.items.orEmpty().isEmpty())
        assertTrue(state.regionBreakdown?.items.orEmpty().isEmpty())
    }

    @Test
    fun `tab state persists through SavedStateHandle`() = runTest {
        val handle = SavedStateHandle()
        val viewModel = DirectoryViewModel(savedStateHandle = handle, repository = FakeHeritageRepository())

        viewModel.selectTab(DirectoryTab.Statistics)
        advanceUntilIdle()
        assertEquals("Statistics", handle.get<String>("dir_tab"))

        val restored = DirectoryViewModel(savedStateHandle = handle, repository = FakeHeritageRepository())
        assertEquals(DirectoryTab.Statistics, restored.uiState.value.selectedTab)
    }

    @Test
    fun `restoring Statistics tab from SavedStateHandle triggers statistics load`() = runTest {
        val overviewDto = DirectoryStatisticsOverviewDto(kind = "nationalProject", total = 42)
        val repo = FakeHeritageRepository(
            directoryStatisticsOverview = overviewDto,
            directoryStatisticsBreakdowns = mapOf(
                DirectoryStatisticDimension.PublishedYear to DirectoryStatisticDimensionDto(dimension = "publishedYear"),
                DirectoryStatisticDimension.Category to DirectoryStatisticDimensionDto(dimension = "category"),
                DirectoryStatisticDimension.Region to DirectoryStatisticDimensionDto(dimension = "region"),
            ),
        )
        val handle = SavedStateHandle(mapOf("dir_tab" to "Statistics"))

        val restored = DirectoryViewModel(savedStateHandle = handle, repository = repo)
        advanceUntilIdle()

        assertEquals(1, repo.directoryStatisticsOverviewQueries.size)
        assertEquals(3, repo.directoryStatisticsBreakdownQueries.size)
        assertEquals(42L, restored.uiState.value.statisticsState.overview?.total)
        assertEquals(false, restored.uiState.value.statisticsState.isLoading)
        assertNull(restored.uiState.value.statisticsState.errorKind)
    }

    @Test
    fun `fast kind switch does not leave error from cancelled request`() = runTest {
        val repo = SlowStatisticsRepository(delayMs = 100)
        val viewModel = DirectoryViewModel(savedStateHandle = SavedStateHandle(), repository = repo)

        viewModel.selectTab(DirectoryTab.Statistics)
        advanceTimeBy(50)

        viewModel.selectKind(DirectoryItemKind.CulturalEcoZone)
        advanceUntilIdle()

        val state = viewModel.uiState.value.statisticsState
        assertEquals(DirectoryItemKind.CulturalEcoZone, viewModel.uiState.value.selectedKind)
        assertEquals(false, state.isLoading)
        assertNull(state.errorKind)
        assertNotNull(state.overview)

        val lastOverview = repo.delegate.directoryStatisticsOverviewQueries.last()
        assertEquals(DirectoryItemKind.CulturalEcoZone, lastOverview)

        val lastBreakdowns = repo.delegate.directoryStatisticsBreakdownQueries.takeLast(3)
        assertTrue(lastBreakdowns.all { it.first == DirectoryItemKind.CulturalEcoZone })
    }

    @Test
    fun `breakdown limits are 50 for year 12 for category 20 for region`() = runTest {
        val repo = FakeHeritageRepository(
            directoryStatisticsOverview = DirectoryStatisticsOverviewDto(total = 1),
            directoryStatisticsBreakdowns = mapOf(
                DirectoryStatisticDimension.PublishedYear to DirectoryStatisticDimensionDto(dimension = "publishedYear"),
                DirectoryStatisticDimension.Category to DirectoryStatisticDimensionDto(dimension = "category"),
                DirectoryStatisticDimension.Region to DirectoryStatisticDimensionDto(dimension = "region"),
            ),
        )
        val viewModel = DirectoryViewModel(savedStateHandle = SavedStateHandle(), repository = repo)

        viewModel.selectTab(DirectoryTab.Statistics)
        advanceUntilIdle()

        val queries = repo.directoryStatisticsBreakdownQueries
        assertEquals(3, queries.size)

        val yearQuery = queries.first { it.second == DirectoryStatisticDimension.PublishedYear }
        assertEquals(50, yearQuery.third)

        val categoryQuery = queries.first { it.second == DirectoryStatisticDimension.Category }
        assertEquals(12, categoryQuery.third)

        val regionQuery = queries.first { it.second == DirectoryStatisticDimension.Region }
        assertEquals(20, regionQuery.third)
    }

    // endregion
}

private class SlowStatisticsRepository(
    private val delayMs: Long,
    internal val delegate: FakeHeritageRepository = FakeHeritageRepository(
        directoryStatisticsOverview = DirectoryStatisticsOverviewDto(kind = "nationalProject", total = 10),
        directoryStatisticsBreakdowns = mapOf(
            DirectoryStatisticDimension.PublishedYear to DirectoryStatisticDimensionDto(dimension = "publishedYear"),
            DirectoryStatisticDimension.Category to DirectoryStatisticDimensionDto(dimension = "category"),
            DirectoryStatisticDimension.Region to DirectoryStatisticDimensionDto(dimension = "region"),
        ),
    ),
) : com.duckylife.heritage.modern.core.data.HeritageRepository by delegate {
    override suspend fun directoryStatisticsOverview(kind: DirectoryItemKind): DirectoryStatisticsOverviewDto {
        delay(delayMs)
        return delegate.directoryStatisticsOverview(kind)
    }

    override suspend fun directoryStatisticsBreakdown(
        kind: DirectoryItemKind,
        dimension: DirectoryStatisticDimension,
        limit: Int,
    ): DirectoryStatisticDimensionDto {
        delay(delayMs)
        return delegate.directoryStatisticsBreakdown(kind, dimension, limit)
    }
}
