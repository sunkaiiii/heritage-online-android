package com.duckylife.heritage.modern.feature.taxonomy

import com.duckylife.heritage.modern.core.data.FakeHeritageRepository
import com.duckylife.heritage.modern.core.network.dto.TaxonomyCategoryDetailDto
import com.duckylife.heritage.modern.core.network.dto.TaxonomyRegionDetailDto
import com.duckylife.heritage.modern.core.network.dto.TaxonomyTopicDto
import com.duckylife.heritage.modern.core.testing.MainDispatcherRule
import com.duckylife.heritage.modern.ui.error.ErrorKind
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
class TaxonomyDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `category type loads category detail`() = runTest {
        val detail = TaxonomyCategoryDetailDto(
            topic = TaxonomyTopicDto(title = "民间文学"),
        )
        val repository = FakeTaxonomyRepository(categoryDetail = detail)

        val viewModel = TaxonomyDetailViewModel("category", "folklore", repository)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.errorKind)
        assertEquals("民间文学", viewModel.uiState.value.categoryDetail?.topic?.title)
        assertEquals("folklore", repository.capturedCategoryKey)
    }

    @Test
    fun `region type loads region detail`() = runTest {
        val detail = TaxonomyRegionDetailDto(
            topic = TaxonomyTopicDto(title = "北京"),
        )
        val repository = FakeTaxonomyRepository(regionDetail = detail)

        val viewModel = TaxonomyDetailViewModel("region", "beijing", repository)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.errorKind)
        assertEquals("北京", viewModel.uiState.value.regionDetail?.topic?.title)
        assertEquals("beijing", repository.capturedRegionKey)
    }

    @Test
    fun `kind type sets NotFound error`() = runTest {
        val repository = FakeTaxonomyRepository()

        val viewModel = TaxonomyDetailViewModel("kind", "paper_cut", repository)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(ErrorKind.NotFound, viewModel.uiState.value.errorKind)
    }

    @Test
    fun `loadDetail maps error to errorKind`() = runTest {
        val repository = FakeTaxonomyRepository(failure = IllegalStateException("network error"))

        val viewModel = TaxonomyDetailViewModel("category", "folklore", repository)
        advanceUntilIdle()

        assertEquals(ErrorKind.Unknown, viewModel.uiState.value.errorKind)
    }

    @Test
    fun `retry reloads detail`() = runTest {
        val detail = TaxonomyCategoryDetailDto(
            topic = TaxonomyTopicDto(title = "民间文学"),
        )
        val repository = FakeTaxonomyRepository(categoryDetail = detail)

        val viewModel = TaxonomyDetailViewModel("category", "folklore", repository)
        advanceUntilIdle()
        assertEquals(1, repository.categoryCalls)

        viewModel.loadDetail()
        advanceUntilIdle()

        assertEquals(2, repository.categoryCalls)
    }

    private class FakeTaxonomyRepository(
        private val categoryDetail: TaxonomyCategoryDetailDto? = null,
        private val regionDetail: TaxonomyRegionDetailDto? = null,
        private val failure: Throwable? = null,
    ) : com.duckylife.heritage.modern.core.data.HeritageRepository by FakeHeritageRepository() {
        var capturedCategoryKey: String? = null
        var capturedRegionKey: String? = null
        var categoryCalls: Int = 0
        var regionCalls: Int = 0

        override suspend fun taxonomyCategoryDetail(
            category: String,
            limit: Int,
        ): TaxonomyCategoryDetailDto {
            categoryCalls++
            capturedCategoryKey = category
            failure?.let { throw it }
            return categoryDetail ?: TaxonomyCategoryDetailDto()
        }

        override suspend fun taxonomyRegionDetail(
            region: String,
            limit: Int,
        ): TaxonomyRegionDetailDto {
            regionCalls++
            capturedRegionKey = region
            failure?.let { throw it }
            return regionDetail ?: TaxonomyRegionDetailDto()
        }
    }
}
