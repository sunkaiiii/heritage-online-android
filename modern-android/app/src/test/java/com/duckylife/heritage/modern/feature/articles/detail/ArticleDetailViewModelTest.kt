package com.duckylife.heritage.modern.feature.articles.detail

import com.duckylife.heritage.modern.core.data.FakeHeritageRepository
import com.duckylife.heritage.modern.core.data.ArticleDetailLookup
import com.duckylife.heritage.modern.core.network.dto.ArticleCategory
import com.duckylife.heritage.modern.core.network.dto.ArticleDetailDto
import com.duckylife.heritage.modern.core.saved.FakeSavedContentRepository
import com.duckylife.heritage.modern.core.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
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
            sourceId = null,
            sourceUrl = null,
            category = ArticleCategory.News,
            repository = FakeHeritageRepository(
                articleDetails = mapOf(
                    "article-1" to ArticleDetailDto(
                        id = "article-1",
                        category = ArticleCategory.News,
                        title = "非遗新闻详情",
                    ),
                ),
            ),
            savedContentRepository = FakeSavedContentRepository(),
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.errorKind)
        assertEquals("article-1", state.article?.id)
        assertEquals("非遗新闻详情", state.article?.title)
    }

    @Test
    fun refreshPublishesErrorStateWhenRepositoryFails() = runTest {
        val viewModel = ArticleDetailViewModel(
            articleId = "article-1",
            sourceId = null,
            sourceUrl = null,
            category = ArticleCategory.News,
            repository = FakeHeritageRepository(
                failure = IllegalStateException("detail unavailable"),
            ),
            savedContentRepository = FakeSavedContentRepository(),
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.errorKind)
    }

    @Test
    fun refreshLoadsArticleDetailBySourceId() = runTest {
        val repository = FakeHeritageRepository(
            articleDetailsBySourceId = mapOf(
                "31566" to ArticleDetailDto(
                    id = "article-2",
                    category = ArticleCategory.News,
                    title = "相关新闻",
                ),
            ),
        )
        val viewModel = ArticleDetailViewModel(
            articleId = null,
            sourceId = "31566",
            sourceUrl = null,
            category = ArticleCategory.News,
            repository = repository,
            savedContentRepository = FakeSavedContentRepository(),
        )

        advanceUntilIdle()

        assertEquals("article-2", viewModel.uiState.value.article?.id)
        assertEquals(listOf("31566" to ArticleCategory.News), repository.articleSourceIdQueries)
    }

    @Test
    fun refreshLoadsArticleDetailBySourceUrl() = runTest {
        val sourceUrl = "http://www.ihchina.cn/news2_details/31566.html"
        val repository = FakeHeritageRepository(
            articleDetailsBySourceUrl = mapOf(
                sourceUrl to ArticleDetailDto(
                    id = "article-3",
                    category = ArticleCategory.News,
                    title = "相关新闻 URL",
                ),
            ),
        )
        val viewModel = ArticleDetailViewModel(
            articleId = null,
            sourceId = null,
            sourceUrl = sourceUrl,
            category = ArticleCategory.News,
            repository = repository,
            savedContentRepository = FakeSavedContentRepository(),
        )

        advanceUntilIdle()

        assertEquals("article-3", viewModel.uiState.value.article?.id)
        assertEquals(listOf(sourceUrl to ArticleCategory.News), repository.articleSourceUrlQueries)
    }

    @Test
    fun showsCachedArticleWhenRefreshFails() = runTest {
        val lookup = ArticleDetailLookup(articleId = "article-1")
        val viewModel = ArticleDetailViewModel(
            articleId = "article-1",
            sourceId = null,
            sourceUrl = null,
            category = ArticleCategory.News,
            repository = FakeHeritageRepository(
                cachedArticleDetails = mapOf(
                    lookup to ArticleDetailDto(
                        id = "article-1",
                        category = ArticleCategory.News,
                        title = "缓存详情",
                    ),
                ),
                failure = IllegalStateException("network down"),
            ),
            savedContentRepository = FakeSavedContentRepository(),
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.errorKind)
        assertEquals("缓存详情", state.article?.title)
    }
}
