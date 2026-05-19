package com.duckylife.heritage.modern.feature.inheritors

import com.duckylife.heritage.modern.core.data.FakeHeritageRepository
import com.duckylife.heritage.modern.core.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class InheritorsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun exposesPagedInheritorsFlow() {
        val viewModel = InheritorsViewModel(
            repository = FakeHeritageRepository(),
        )

        assertNotNull(viewModel.inheritors)
    }

    @Test
    fun searchKeywordsMappedToQuery() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = InheritorsViewModel(repository = repo)

        viewModel.updateSearchKeywords("张三")
        advanceUntilIdle()

        viewModel.inheritors.first()
        assertEquals(1, repo.pagedInheritorQueries.size)
        val query = repo.pagedInheritorQueries.first()
        assertEquals("张三", query.keywords)
    }

    @Test
    fun regionFilterMappedToQuery() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = InheritorsViewModel(repository = repo)

        viewModel.updateRegionFilter("四川")
        advanceUntilIdle()

        viewModel.inheritors.first()
        assertEquals("四川", repo.pagedInheritorQueries.first().region)
    }

    @Test
    fun categoryFilterMappedToQuery() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = InheritorsViewModel(repository = repo)

        viewModel.updateCategoryFilter("传统美术")
        advanceUntilIdle()

        viewModel.inheritors.first()
        assertEquals("传统美术", repo.pagedInheritorQueries.first().category)
    }

    @Test
    fun yearFilterMappedToQuery() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = InheritorsViewModel(repository = repo)

        viewModel.updateYearFilter("2018")
        advanceUntilIdle()

        viewModel.inheritors.first()
        assertEquals(2018, repo.pagedInheritorQueries.first().year)
    }

    @Test
    fun genderFilterMappedToQuery() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = InheritorsViewModel(repository = repo)

        viewModel.updateGenderFilter("男")
        advanceUntilIdle()

        viewModel.inheritors.first()
        assertEquals("男", repo.pagedInheritorQueries.first().gender)
    }

    @Test
    fun blankFiltersNotMappedToQuery() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = InheritorsViewModel(repository = repo)

        viewModel.updateRegionFilter("  ")
        viewModel.updateGenderFilter("")
        advanceUntilIdle()

        viewModel.inheritors.first()
        val query = repo.pagedInheritorQueries.first()
        assertNull(query.region)
        assertNull(query.gender)
    }

    @Test
    fun activeFilterCountReflectsNonBlankFilters() = runTest {
        val viewModel = InheritorsViewModel(repository = FakeHeritageRepository())

        assertEquals(0, viewModel.uiState.value.activeFilterCount)

        viewModel.updateRegionFilter("北京")
        viewModel.updateGenderFilter("女")
        advanceUntilIdle()

        assertEquals(2, viewModel.uiState.value.activeFilterCount)
    }

    @Test
    fun clearFiltersResetsAllToDefaults() = runTest {
        val viewModel = InheritorsViewModel(repository = FakeHeritageRepository())

        viewModel.updateSearchKeywords("test")
        viewModel.updateRegionFilter("湖南")
        viewModel.updateCategoryFilter("传统技艺")
        viewModel.updateYearFilter("2015")
        viewModel.updateGenderFilter("男")
        advanceUntilIdle()

        viewModel.clearFilters()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("", state.searchKeywords)
        assertEquals("", state.regionFilter)
        assertEquals("", state.categoryFilter)
        assertEquals("", state.yearFilter)
        assertEquals("", state.genderFilter)
        assertEquals(0, state.activeFilterCount)
    }
}
