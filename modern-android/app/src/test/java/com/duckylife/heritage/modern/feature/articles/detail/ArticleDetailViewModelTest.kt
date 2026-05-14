package com.duckylife.heritage.modern.feature.articles.detail

import com.duckylife.heritage.modern.core.data.FakeHeritageRepository
import com.duckylife.heritage.modern.core.network.dto.ArticleCategory
import com.duckylife.heritage.modern.core.network.dto.ArticleDetailDto
import com.duckylife.heritage.modern.core.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ArticleDetailViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun refreshLoadsArticleDetail() = runTest {
        val viewModel = ArticleDetailViewModel(
            articleId = "article-1",
            repository = FakeHeritageRepository(
                articleDetails = mapOf(
                    "article-1" to ArticleDetailDto(
                        id = "article-1",
                        category = ArticleCategory.News,
                        title = "非遗新闻详情",
                    ),
                ),
            ),
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
        assertEquals("article-1", state.article?.id)
        assertEquals("非遗新闻详情", state.article?.title)
    }

    @Test
    fun refreshPublishesErrorStateWhenRepositoryFails() = runTest {
        val viewModel = ArticleDetailViewModel(
            articleId = "article-1",
            repository = FakeHeritageRepository(
                failure = IllegalStateException("detail unavailable"),
            ),
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("detail unavailable", state.errorMessage)
    }
}
