package com.duckylife.heritage.modern.core.data

import com.duckylife.heritage.modern.core.network.V3ContentPageQuery
import com.duckylife.heritage.modern.core.network.api.ContentIntelligenceApi
import com.duckylife.heritage.modern.core.network.dto.SearchResultType
import com.duckylife.heritage.modern.core.network.dto.advanced.AiCardDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ContentDigestKeyFactDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ContentDigestSectionDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ContentIntelligenceDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ContentRefDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphNeighborsDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphNodeDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphNodeType
import com.duckylife.heritage.modern.core.network.dto.advanced.IntelligentSearchResponseDto
import com.duckylife.heritage.modern.core.network.dto.advanced.SectionStatus
import com.duckylife.heritage.modern.core.network.dto.advanced.SectionStatusDto
import com.duckylife.heritage.modern.core.network.dto.advanced.V3ContentPageDto
import com.duckylife.heritage.modern.core.network.dto.advanced.V3PageWarningDto
import com.duckylife.heritage.modern.core.profile.FakeLocalProfileRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondError
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ContentIntelligenceRepositoryTest {

    @Test
    fun mapsReadySectionsFromDto() = runTest {
        val api = FakeContentIntelligenceApi(
            response = V3ContentPageDto(
                aiCard = AiCardDto(hasAi = true, summary = "AI 摘要"),
                graph = GraphNeighborsDto(
                    nodes = listOf(GraphNodeDto(nodeKey = "a1", type = GraphNodeType.Article)),
                ),
                recommendations = listOf(ContentRefDto(type = GraphNodeType.Article, id = "r1")),
                relatedContent = listOf(ContentRefDto(type = GraphNodeType.DirectoryItem, id = "r2")),
                digest = ContentDigestSectionDto(summary = "人工摘要"),
            ),
        )
        val repository = DefaultContentIntelligenceRepository(
            api = api,
            profileRepository = FakeLocalProfileRepository(id = "android_test_profile"),
        )

        val page = repository.loadContentPage(
            ContentIntelligenceRef(SearchResultType.Article, "article-1"),
        )

        assertEquals(SectionStatus.Ready, page.aiSection.status)
        assertEquals("AI 摘要", page.aiSection.data?.summary)
        assertEquals(SectionStatus.Ready, page.graphSection.status)
        assertEquals(SectionStatus.Ready, page.recommendationSection.status)
        assertEquals(SectionStatus.Ready, page.relatedContentSection.status)
        assertEquals(SectionStatus.Ready, page.digestSection.status)
        assertEquals(SearchResultType.Article, page.pageType)
        assertEquals("article-1", page.ref.id)
        assertEquals(true, page.learningRoutesAvailable)
        assertEquals("人工摘要", page.detailDigest?.quickRead)
        assertEquals(2, page.detailRecommendations?.items?.size)
        assertEquals(1, page.detailContext?.related?.size)
        assertEquals(1, page.detailContext?.recommendations?.size)
        assertEquals(false, page.legacyFallback.loadDigest)
        assertEquals(false, page.legacyFallback.loadBlendedRecommendations)
        assertEquals(false, page.legacyFallback.loadContext)
    }

    @Test
    fun mapsDigestKeyFactsToLabelValuePairs() = runTest {
        val api = FakeContentIntelligenceApi(
            response = V3ContentPageDto(
                digest = ContentDigestSectionDto(
                    summary = "摘要",
                    keyFacts = listOf(
                        ContentDigestKeyFactDto(label = "发布时间", value = "2026-06-26"),
                        ContentDigestKeyFactDto(label = "来源", value = "人民日报"),
                    ),
                ),
            ),
        )
        val repository = DefaultContentIntelligenceRepository(
            api = api,
            profileRepository = FakeLocalProfileRepository(),
        )

        val page = repository.loadContentPage(
            ContentIntelligenceRef(SearchResultType.Article, "article-1"),
        )

        assertEquals(2, page.detailDigest?.keyFacts?.size)
        assertEquals("发布时间", page.detailDigest?.keyFacts?.first()?.label)
        assertEquals("2026-06-26", page.detailDigest?.keyFacts?.first()?.value)
    }

    @Test
    fun mapsWarningObjectsToMessages() = runTest {
        val api = FakeContentIntelligenceApi(
            response = V3ContentPageDto(
                digest = ContentDigestSectionDto(summary = "摘要"),
                warnings = listOf(
                    V3PageWarningDto(
                        code = "ai_missing",
                        message = "AI 结果缺失",
                        severity = "info",
                    ),
                ),
            ),
        )
        val repository = DefaultContentIntelligenceRepository(
            api = api,
            profileRepository = FakeLocalProfileRepository(),
        )

        val page = repository.loadContentPage(
            ContentIntelligenceRef(SearchResultType.Inheritor, "inheritor-1"),
        )

        assertEquals(listOf("AI 结果缺失"), page.warnings)
    }

    @Test
    fun mapsEmptyRecommendationsToEmptyStatus() = runTest {
        val api = FakeContentIntelligenceApi(
            response = V3ContentPageDto(
                aiCard = AiCardDto(hasAi = false),
                recommendations = emptyList(),
                digest = ContentDigestSectionDto(summary = "摘要"),
            ),
        )
        val repository = DefaultContentIntelligenceRepository(
            api = api,
            profileRepository = FakeLocalProfileRepository(),
        )

        val page = repository.loadContentPage(
            ContentIntelligenceRef(SearchResultType.DirectoryItem, "item-1"),
        )

        assertEquals(SectionStatus.Empty, page.aiSection.status)
        assertNull(page.aiSection.data)
        assertEquals(SectionStatus.Empty, page.recommendationSection.status)
        assertEquals(SectionStatus.Ready, page.digestSection.status)
    }

    @Test
    fun mapsAiCardReadyWhenHasAiFalseButContentPresent() = runTest {
        val api = FakeContentIntelligenceApi(
            response = V3ContentPageDto(
                aiCard = AiCardDto(
                    hasAi = false,
                    summary = "AI 摘要",
                    highlights = listOf("亮点 1"),
                ),
            ),
        )
        val repository = DefaultContentIntelligenceRepository(
            api = api,
            profileRepository = FakeLocalProfileRepository(),
        )

        val page = repository.loadContentPage(
            ContentIntelligenceRef(SearchResultType.Article, "article-1"),
        )

        assertEquals(SectionStatus.Ready, page.aiSection.status)
        assertEquals("AI 摘要", page.aiSection.data?.summary)
    }

    @Test
    fun respectsExplicitUnavailableStatus() = runTest {
        val api = FakeContentIntelligenceApi(
            response = V3ContentPageDto(
                aiCard = AiCardDto(hasAi = true, summary = "AI 摘要"),
                sectionStatus = listOf(
                    SectionStatusDto(section = "aiCard", status = SectionStatus.Unavailable),
                ),
            ),
        )
        val repository = DefaultContentIntelligenceRepository(
            api = api,
            profileRepository = FakeLocalProfileRepository(),
        )

        val page = repository.loadContentPage(
            ContentIntelligenceRef(SearchResultType.Inheritor, "inheritor-1"),
        )

        assertEquals(SectionStatus.Unavailable, page.aiSection.status)
        assertNull(page.aiSection.data)
    }

    @Test
    fun `unavailable V3 sections keep legacy detail fallback enabled`() = runTest {
        val api = FakeContentIntelligenceApi(
            response = V3ContentPageDto(
                digest = ContentDigestSectionDto(summary = "不应直接显示"),
                recommendations = listOf(ContentRefDto(type = GraphNodeType.Article, id = "r1")),
                sectionStatus = listOf(
                    SectionStatusDto(section = "digest", status = SectionStatus.Unavailable),
                    SectionStatusDto(section = "recommendations", status = SectionStatus.Disabled),
                    SectionStatusDto(section = "relatedContent", status = SectionStatus.Missing),
                    SectionStatusDto(section = "graph", status = SectionStatus.Unavailable),
                ),
            ),
        )
        val repository = DefaultContentIntelligenceRepository(
            api = api,
            profileRepository = FakeLocalProfileRepository(),
        )

        val page = repository.loadContentPage(
            ContentIntelligenceRef(SearchResultType.Article, "article-1"),
        )

        assertNull(page.detailDigest)
        assertNull(page.detailRecommendations)
        assertNull(page.detailContext)
        assertEquals(true, page.legacyFallback.loadDigest)
        assertEquals(true, page.legacyFallback.loadBlendedRecommendations)
        assertEquals(true, page.legacyFallback.loadContext)
    }

    @Test
    fun `V3 503 degrades to unavailable enhancement page without fatal detail error`() = runTest {
        val api = FakeContentIntelligenceApi().apply {
            failure = serviceUnavailableException()
        }
        val repository = DefaultContentIntelligenceRepository(
            api = api,
            profileRepository = FakeLocalProfileRepository(),
        )

        val page = repository.loadContentPage(
            ContentIntelligenceRef(SearchResultType.Article, "article-1"),
        )

        assertEquals(SectionStatus.Unavailable, page.aiSection.status)
        assertEquals(SectionStatus.Unavailable, page.graphSection.status)
        assertEquals(SectionStatus.Unavailable, page.digestSection.status)
        assertEquals(true, page.legacyFallback.loadDigest)
        assertEquals(true, page.legacyFallback.loadBlendedRecommendations)
        assertEquals(true, page.legacyFallback.loadContext)
    }

    @Test
    fun sendsProfileIdForLocalState() = runTest {
        val api = FakeContentIntelligenceApi(response = V3ContentPageDto())
        val repository = DefaultContentIntelligenceRepository(
            api = api,
            profileRepository = FakeLocalProfileRepository(id = "android_profile_42"),
        )

        repository.loadContentPage(
            ContentIntelligenceRef(SearchResultType.Article, "article-1"),
        )

        val query = api.queries.single()
        assertEquals(SearchResultType.Article, query.contentType)
        assertEquals("article-1", query.id)
        assertEquals("android_profile_42", query.profileId)
        assertEquals(true, query.includeLocalState)
    }
}

private class FakeContentIntelligenceApi(
    var response: V3ContentPageDto = V3ContentPageDto(),
) : ContentIntelligenceApi {
    val queries = mutableListOf<V3ContentPageQuery>()
    var failure: Throwable? = null

    override suspend fun getV3ContentPage(query: V3ContentPageQuery): V3ContentPageDto {
        queries += query
        failure?.let { throw it }
        return response
    }

    override suspend fun getContentIntelligence(query: com.duckylife.heritage.modern.core.network.ContentIntelligenceQuery): ContentIntelligenceDto =
        throw NotImplementedError()

    override suspend fun getArticleAiCard(id: String): AiCardDto = throw NotImplementedError()
    override suspend fun getDirectoryItemAiCard(id: String): AiCardDto = throw NotImplementedError()
    override suspend fun getInheritorAiCard(id: String): AiCardDto = throw NotImplementedError()
    override suspend fun intelligentSearch(
        query: com.duckylife.heritage.modern.core.network.IntelligentSearchQuery,
    ): IntelligentSearchResponseDto = throw NotImplementedError()
}

private suspend fun serviceUnavailableException(): Throwable {
    val client = HttpClient(MockEngine { respondError(HttpStatusCode.ServiceUnavailable) }) {
        expectSuccess = true
    }
    return runCatching { client.get("http://test.example/") }.exceptionOrNull()
        ?: error("expected exception")
}
