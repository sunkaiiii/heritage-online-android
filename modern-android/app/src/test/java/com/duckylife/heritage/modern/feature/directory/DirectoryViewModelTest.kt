package com.duckylife.heritage.modern.feature.directory

import com.duckylife.heritage.modern.core.data.FakeHeritageRepository
import com.duckylife.heritage.modern.core.testing.MainDispatcherRule
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DirectoryViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun startsWithNationalProjectKind() = runTest {
        val viewModel = DirectoryViewModel(
            repository = FakeHeritageRepository(),
        )

        assertEquals(DirectoryItemKind.NationalProject, viewModel.uiState.value.selectedKind)
    }

    @Test
    fun selectKindUpdatesSelectedKind() = runTest {
        val viewModel = DirectoryViewModel(
            repository = FakeHeritageRepository(),
        )

        viewModel.selectKind(DirectoryItemKind.CulturalEcoZone)
        advanceUntilIdle()

        assertEquals(DirectoryItemKind.CulturalEcoZone, viewModel.uiState.value.selectedKind)
    }

    @Test
    fun searchKeywordsMappedToQuery() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = DirectoryViewModel(repository = repo)

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
        val viewModel = DirectoryViewModel(repository = repo)

        viewModel.updateRegionFilter("北京")
        advanceUntilIdle()

        viewModel.items.first()
        assertEquals("北京", repo.pagedDirectoryItemQueries.first().region)
    }

    @Test
    fun categoryFilterMappedToQuery() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = DirectoryViewModel(repository = repo)

        viewModel.updateCategoryFilter("传统美术")
        advanceUntilIdle()

        viewModel.items.first()
        assertEquals("传统美术", repo.pagedDirectoryItemQueries.first().category)
    }

    @Test
    fun yearFilterMappedToQuery() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = DirectoryViewModel(repository = repo)

        viewModel.updateYearFilter("2006")
        advanceUntilIdle()

        viewModel.items.first()
        assertEquals(2006, repo.pagedDirectoryItemQueries.first().year)
    }

    @Test
    fun listTypeFilterMappedToQuery() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = DirectoryViewModel(repository = repo)

        viewModel.updateListTypeFilter("representative")
        advanceUntilIdle()

        viewModel.items.first()
        assertEquals("representative", repo.pagedDirectoryItemQueries.first().listType)
    }

    @Test
    fun blankFiltersNotMappedToQuery() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = DirectoryViewModel(repository = repo)

        viewModel.updateRegionFilter("  ")
        viewModel.updateYearFilter("")
        advanceUntilIdle()

        viewModel.items.first()
        val query = repo.pagedDirectoryItemQueries.first()
        assertNull(query.region)
        assertNull(query.year)
    }

    @Test
    fun activeFilterCountReflectsNonBlankFilters() = runTest {
        val viewModel = DirectoryViewModel(repository = FakeHeritageRepository())

        assertEquals(0, viewModel.uiState.value.activeFilterCount)

        viewModel.updateRegionFilter("北京")
        viewModel.updateCategoryFilter("传统美术")
        advanceUntilIdle()

        assertEquals(2, viewModel.uiState.value.activeFilterCount)
    }

    @Test
    fun clearFiltersResetsAllToDefaults() = runTest {
        val viewModel = DirectoryViewModel(repository = FakeHeritageRepository())

        viewModel.updateSearchKeywords("test")
        viewModel.updateRegionFilter("北京")
        viewModel.updateCategoryFilter("music")
        viewModel.updateYearFilter("2020")
        viewModel.updateListTypeFilter("list")
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
}
