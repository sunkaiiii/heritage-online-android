package com.duckylife.heritage.modern.feature.articles

import com.duckylife.heritage.modern.core.data.FakeHeritageRepository
import com.duckylife.heritage.modern.core.testing.MainDispatcherRule
import com.duckylife.heritage.modern.core.network.dto.ArticleCategory
import com.duckylife.heritage.modern.core.network.dto.ArticleSummaryDto
import com.duckylife.heritage.modern.core.network.dto.HomeBannerDto
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
            repository = FakeHeritageRepository(
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
            repository = FakeHeritageRepository(
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
            repository = FakeHeritageRepository(),
        )

        viewModel.selectCategory(ArticleCategory.SpecialTopic)
        advanceUntilIdle()

        assertEquals(ArticleCategory.SpecialTopic, viewModel.uiState.value.selectedCategory)
    }

    @Test
    fun refreshBannersKeepsSelectedCategory() = runTest {
        val viewModel = ArticlesViewModel(
            repository = FakeHeritageRepository(
                banners = listOf(HomeBannerDto(id = "banner-1")),
            ),
        )
        advanceUntilIdle()

        viewModel.selectCategory(ArticleCategory.Forum)
        viewModel.refreshBanners()
        advanceUntilIdle()

        assertEquals(ArticleCategory.Forum, viewModel.uiState.value.selectedCategory)
    }
}
