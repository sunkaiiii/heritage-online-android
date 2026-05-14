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
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
        assertEquals(listOf("1", "2"), state.banners.map { it.id })
        assertEquals("article-1", state.articles.single().id)
    }

    @Test
    fun refreshPublishesErrorStateWhenRepositoryFails() = runTest {
        val viewModel = ArticlesViewModel(
            repository = FakeHeritageRepository(
                failure = IllegalStateException("network down"),
            ),
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("network down", state.errorMessage)
    }
}
