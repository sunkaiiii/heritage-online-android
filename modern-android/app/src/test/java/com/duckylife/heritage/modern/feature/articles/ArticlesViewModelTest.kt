package com.duckylife.heritage.modern.feature.articles

import com.duckylife.heritage.modern.core.data.FakeHeritageRepository
import androidx.lifecycle.SavedStateHandle
import com.duckylife.heritage.modern.core.testing.MainDispatcherRule
import com.duckylife.heritage.modern.core.network.dto.ArticleCategory
import com.duckylife.heritage.modern.core.network.dto.ArticleSummaryDto
import com.duckylife.heritage.modern.core.network.dto.HomeBannerDto
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ArticlesViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun refreshLoadsBannersAndArticles() = runTest {
        val viewModel = ArticlesViewModel(
            savedStateHandle = SavedStateHandle(), repository = FakeHeritageRepository(
                banners = listOf(
                    HomeBannerDto(id = "2", sortOrder = 2),
                    HomeBannerDto(id = "1", sortOrder = 1),
                ),
                articles = listOf(
                    ArticleSummaryDto(
                        id = "article-1",
                        category = ArticleCategory.News,
                        title = "非遗新闻",
                    ),
                ),
            ),
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoadingBanners)
        assertNull(state.bannerErrorMessage)
        assertEquals(ArticleCategory.News, state.selectedCategory)
        assertEquals(listOf("1", "2"), state.banners.map { it.id })
    }

    @Test
    fun refreshPublishesBannerErrorStateWhenRepositoryFails() = runTest {
        val viewModel = ArticlesViewModel(
            savedStateHandle = SavedStateHandle(), repository = FakeHeritageRepository(
                failure = IllegalStateException("network down"),
            ),
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoadingBanners)
        assertEquals("network down", state.bannerErrorMessage)
    }

    @Test
    fun selectCategoryUpdatesSelectedCategory() = runTest {
        val viewModel = ArticlesViewModel(
            savedStateHandle = SavedStateHandle(), repository = FakeHeritageRepository(),
        )

        viewModel.selectCategory(ArticleCategory.SpecialTopic)
        advanceUntilIdle()

        assertEquals(ArticleCategory.SpecialTopic, viewModel.uiState.value.selectedCategory)
    }

    @Test
    fun refreshBannersKeepsSelectedCategory() = runTest {
        val viewModel = ArticlesViewModel(
            savedStateHandle = SavedStateHandle(), repository = FakeHeritageRepository(
                banners = listOf(HomeBannerDto(id = "banner-1")),
            ),
        )
        advanceUntilIdle()

        viewModel.selectCategory(ArticleCategory.Forum)
        viewModel.refreshBanners()
        advanceUntilIdle()

        assertEquals(ArticleCategory.Forum, viewModel.uiState.value.selectedCategory)
    }

    @Test
    fun updateSearchKeywordsIsReflectedInUiState() = runTest {
        val viewModel = ArticlesViewModel(savedStateHandle = SavedStateHandle(), repository = FakeHeritageRepository())
        viewModel.updateSearchKeywords("非遗")
        advanceUntilIdle()
        assertEquals("非遗", viewModel.uiState.value.searchKeywords)
    }

    @Test
    fun searchKeywordsMappingProducesCorrectQuery() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = ArticlesViewModel(repository = repo, savedStateHandle = SavedStateHandle())

        viewModel.selectCategory(ArticleCategory.Forum)
        viewModel.updateSearchKeywords("陶瓷")
        advanceUntilIdle()

        viewModel.articles.first()
        assertEquals(1, repo.pagedArticleQueries.size)
        val query = repo.pagedArticleQueries.first()
        assertEquals(ArticleCategory.Forum, query.category)
        assertEquals("陶瓷", query.keywords)
    }

    @Test
    fun yearFilterMappingProducesCorrectQuery() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = ArticlesViewModel(repository = repo, savedStateHandle = SavedStateHandle())

        viewModel.applyFilters(searchKeywords = "", yearFilter = "2024")
        advanceUntilIdle()

        viewModel.articles.first()
        assertEquals(1, repo.pagedArticleQueries.size)
        val query = repo.pagedArticleQueries.first()
        assertEquals(2024, query.year)
    }

    @Test
    fun invalidYearIsNotMappedToQuery() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = ArticlesViewModel(repository = repo, savedStateHandle = SavedStateHandle())

        viewModel.applyFilters(searchKeywords = "", yearFilter = "not-a-year")
        advanceUntilIdle()

        viewModel.articles.first()
        val query = repo.pagedArticleQueries.first()
        assertNull(query.year)
    }

    @Test
    fun emptySearchKeywordsNotMappedToQuery() = runTest {
        val repo = FakeHeritageRepository()
        val viewModel = ArticlesViewModel(repository = repo, savedStateHandle = SavedStateHandle())

        viewModel.updateSearchKeywords("  ")
        advanceUntilIdle()

        viewModel.articles.first()
        val query = repo.pagedArticleQueries.first()
        assertNull(query.keywords)
    }

    @Test
    fun clearFiltersResetsAllToDefaults() = runTest {
        val viewModel = ArticlesViewModel(savedStateHandle = SavedStateHandle(), repository = FakeHeritageRepository())

        viewModel.updateSearchKeywords("test")
        viewModel.applyFilters(searchKeywords = "", yearFilter = "2023")
        advanceUntilIdle()

        viewModel.clearFilters()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("", state.searchKeywords)
        assertEquals("", state.yearFilter)
    }
}
