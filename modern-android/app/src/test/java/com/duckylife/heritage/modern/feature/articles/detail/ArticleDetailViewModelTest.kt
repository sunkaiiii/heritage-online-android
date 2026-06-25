package com.duckylife.heritage.modern.feature.articles.detail

import com.duckylife.heritage.modern.core.data.ArticleDetailLookup
import com.duckylife.heritage.modern.core.data.ContentIntelligencePage
import com.duckylife.heritage.modern.core.data.ContentIntelligenceRef
import com.duckylife.heritage.modern.core.data.FakeHeritageRepository
import com.duckylife.heritage.modern.core.data.IntelligenceSection
import com.duckylife.heritage.modern.core.data.LegacyDetailFallback
import com.duckylife.heritage.modern.core.network.dto.ArticleCategory
import com.duckylife.heritage.modern.core.network.dto.ArticleDetailDto
import com.duckylife.heritage.modern.core.network.dto.BlendedRecommendationItemDto
import com.duckylife.heritage.modern.core.network.dto.BlendedRecommendationResponseDto
import com.duckylife.heritage.modern.core.network.dto.ContentDigestDto
import com.duckylife.heritage.modern.core.network.dto.DetailContextDto
import com.duckylife.heritage.modern.core.network.dto.GraphDto
import com.duckylife.heritage.modern.core.network.dto.RecommendationDto
import com.duckylife.heritage.modern.core.network.dto.SearchResultType
import com.duckylife.heritage.modern.core.network.dto.advanced.AiCardDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ContentDigestSectionDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ContentRefDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphNeighborsDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphNodeType
import com.duckylife.heritage.modern.core.network.dto.advanced.SectionStatus
import com.duckylife.heritage.modern.core.profile.FakeLocalUserSyncRepository
import com.duckylife.heritage.modern.core.saved.FakeSavedContentRepository
import com.duckylife.heritage.modern.core.testing.MainDispatcherRule
import com.duckylife.heritage.modern.feature.detail.intelligence.FakeContentIntelligenceViewModelDelegateFactory
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
            syncRepository = FakeLocalUserSyncRepository(),
            intelligenceDelegateFactory = FakeContentIntelligenceViewModelDelegateFactory(),
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
            syncRepository = FakeLocalUserSyncRepository(),
            intelligenceDelegateFactory = FakeContentIntelligenceViewModelDelegateFactory(),
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
            syncRepository = FakeLocalUserSyncRepository(),
            intelligenceDelegateFactory = FakeContentIntelligenceViewModelDelegateFactory(),
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
            syncRepository = FakeLocalUserSyncRepository(),
            intelligenceDelegateFactory = FakeContentIntelligenceViewModelDelegateFactory(),
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
            syncRepository = FakeLocalUserSyncRepository(),
            intelligenceDelegateFactory = FakeContentIntelligenceViewModelDelegateFactory(),
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.errorKind)
        assertEquals("缓存详情", state.article?.title)
    }

    @Test
    fun refreshAppliesV3DetailEnhancementsWhenDelegateReturnsPage() = runTest {
        val articleId = "article-1"
        val viewModel = ArticleDetailViewModel(
            articleId = articleId,
            sourceId = null,
            sourceUrl = null,
            category = ArticleCategory.News,
            repository = FakeHeritageRepository(
                articleDetails = mapOf(
                    articleId to ArticleDetailDto(
                        id = articleId,
                        category = ArticleCategory.News,
                        title = "非遗新闻详情",
                    ),
                ),
            ),
            savedContentRepository = FakeSavedContentRepository(),
            syncRepository = FakeLocalUserSyncRepository(),
            intelligenceDelegateFactory = FakeContentIntelligenceViewModelDelegateFactory(
                page = v3DetailEnhancementPage(articleId),
            ),
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("来自 V3 的 quick read", state.digest?.quickRead)
        assertEquals(listOf("V3 highlight"), state.digest?.highlights)
        assertEquals("rec-v3", state.blendedRecommendations?.items?.single()?.id)
        assertEquals("ctx-rec-v3", state.context?.recommendations?.single()?.id)
        assertFalse(state.digestLoading)
        assertFalse(state.contextLoading)
        assertFalse(state.blendedLoading)
    }
}

private fun v3DetailEnhancementPage(articleId: String): ContentIntelligencePage =
    ContentIntelligencePage(
        ref = ContentIntelligenceRef(SearchResultType.Article, articleId),
        pageType = SearchResultType.Article,
        aiSection = IntelligenceSection(
            status = SectionStatus.Ready,
            data = AiCardDto(hasAi = true, summary = "V3 AI 摘要"),
        ),
        graphSection = IntelligenceSection(
            status = SectionStatus.Ready,
            data = GraphNeighborsDto(center = articleId),
        ),
        recommendationSection = IntelligenceSection(
            status = SectionStatus.Ready,
            data = listOf(ContentRefDto(type = GraphNodeType.Article, id = "rec-v3")),
        ),
        relatedContentSection = IntelligenceSection(
            status = SectionStatus.Ready,
            data = emptyList(),
        ),
        digestSection = IntelligenceSection(
            status = SectionStatus.Ready,
            data = ContentDigestSectionDto(summary = "V3 摘要"),
        ),
        localState = null,
        sectionStatus = emptyMap(),
        warnings = emptyList(),
        learningRoutesAvailable = true,
        detailDigest = ContentDigestDto(
            type = "article",
            id = articleId,
            title = "V3 Digest",
            quickRead = "来自 V3 的 quick read",
            highlights = listOf("V3 highlight"),
        ),
        detailRecommendations = BlendedRecommendationResponseDto(
            items = listOf(
                BlendedRecommendationItemDto(id = "rec-v3", title = "V3 推荐"),
            ),
        ),
        detailContext = DetailContextDto(
            graph = GraphDto(),
            recommendations = listOf(
                RecommendationDto(id = "ctx-rec-v3", title = "V3 上下文推荐"),
            ),
        ),
        legacyFallback = LegacyDetailFallback(
            loadDigest = false,
            loadBlendedRecommendations = false,
            loadContext = false,
        ),
    )
