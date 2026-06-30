package com.duckylife.heritage.modern.core.network

import com.duckylife.heritage.modern.core.network.dto.ArticleCategory
import com.duckylife.heritage.modern.core.network.dto.ArticleContentBlockType
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind
import com.duckylife.heritage.modern.core.network.dto.DirectoryStatisticDimension
import com.duckylife.heritage.modern.core.network.dto.SearchResultType
import com.duckylife.heritage.modern.core.network.dto.advanced.JourneyStrategy
import com.duckylife.heritage.modern.core.network.dto.advanced.TrailStrategy
import com.duckylife.heritage.modern.core.profile.FakeLocalProfileRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Test

class KtorHeritageApiClientTest {

    private val fakeProfileRepository = FakeLocalProfileRepository()

    private fun createApiClient(httpClient: HttpClient, baseUrl: String): KtorHeritageApiClient =
        KtorHeritageApiClient(
            httpClient = httpClient,
            baseUrl = baseUrl,
            profileRepository = fakeProfileRepository,
        )

    @Test
    fun getArticlesDecodesPagedResultAndAppliesQuery() = runTest {
        var capturedRequest: HttpRequestData? = null
        val engine = MockEngine { request ->
            capturedRequest = request
            respond(
                content = """
                    {
                      "items": [
                        {
                          "id": "article-1",
                          "category": "news",
                          "title": "非遗新闻",
                          "summary": "summary",
                          "publishedAt": "2026-05-14T00:00:00Z",
                          "coverImage": {
                            "displayUrl": "https://example.test/image.jpg",
                            "altText": "cover"
                          },
                          "sourceUrl": "https://example.test/source"
                        }
                      ],
                      "page": 2,
                      "pageSize": 10,
                      "hasMore": false,
                      "total": 11
                    }
                """.trimIndent(),
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = HttpClient(engine) {
            install(ContentNegotiation) {
                json(HeritageJson)
            }
        }
        val api = createApiClient(httpClient = client, baseUrl = "https://example.test")

        val result = api.getArticles(
            ArticleQuery(
                category = ArticleCategory.News,
                page = 2,
                pageSize = 10,
                keywords = "陶瓷",
            ),
        )

        assertEquals(1, result.items.size)
        assertEquals("article-1", result.items.first().id)
        assertEquals("非遗新闻", result.items.first().title)
        assertEquals(2, result.page)
        assertEquals(10, result.pageSize)
        assertFalse(result.hasMore)
        assertEquals(11, result.total)

        val request = requireNotNull(capturedRequest)
        assertEquals("/api/articles", request.url.encodedPath)
        assertEquals("news", request.url.parameters["category"])
        assertEquals("2", request.url.parameters["page"])
        assertEquals("10", request.url.parameters["pageSize"])
        assertEquals("陶瓷", request.url.parameters["keywords"])
        assertNotNull(result.items.first().coverImage)
    }

    @Test
    fun getArticlesSendsDefaultRequiredCategory() = runTest {
        var capturedRequest: HttpRequestData? = null
        val engine = MockEngine { request ->
            capturedRequest = request
            respond(
                content = """
                    {
                      "items": [],
                      "page": 1,
                      "pageSize": 20,
                      "hasMore": false,
                      "total": 0
                    }
                """.trimIndent(),
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = HttpClient(engine) {
            install(ContentNegotiation) {
                json(HeritageJson)
            }
        }
        val api = createApiClient(httpClient = client, baseUrl = "https://example.test")

        api.getArticles()

        val request = requireNotNull(capturedRequest)
        assertEquals("news", request.url.parameters["category"])
    }

    @Test
    fun getArticleDecodesDetailContentBlocksAndReferences() = runTest {
        var capturedRequest: HttpRequestData? = null
        val engine = MockEngine { request ->
            capturedRequest = request
            respond(
                content = """
                    {
                      "id": "article-1",
                      "category": "news",
                      "title": "非遗新闻详情",
                      "summary": "详情摘要",
                      "publishedAt": "2026-04-22T14:30:00Z",
                      "sourceName": "测试来源",
                      "author": "作者",
                      "editor": "编辑",
                      "sourceUrl": "https://example.test/source",
                      "coverImage": {
                        "displayUrl": "https://example.test/cover.jpg"
                      },
                      "contentBlocks": [
                        {
                          "type": "text",
                          "text": "正文第一段",
                          "image": null
                        },
                        {
                          "type": "image",
                          "text": null,
                          "image": {
                            "displayUrl": "https://example.test/body.jpg"
                          }
                        }
                      ],
                      "relatedArticles": [
                        {
                          "title": "相关新闻",
                          "detailUrl": "https://example.test/related",
                          "sourceId": "31566",
                          "publishedAt": "2026-04-21T14:30:00Z"
                        }
                      ]
                    }
                """.trimIndent(),
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = HttpClient(engine) {
            install(ContentNegotiation) {
                json(HeritageJson)
            }
        }
        val api = createApiClient(httpClient = client, baseUrl = "https://example.test")

        val article = api.getArticle("article-1")

        assertEquals("article-1", article.id)
        assertEquals("非遗新闻详情", article.title)
        assertEquals("测试来源", article.sourceName)
        assertEquals("https://example.test/source", article.sourceUrl)
        assertEquals(2, article.contentBlocks.size)
        assertEquals(ArticleContentBlockType.Text, article.contentBlocks.first().type)
        assertEquals("正文第一段", article.contentBlocks.first().text)
        assertEquals(ArticleContentBlockType.Image, article.contentBlocks[1].type)
        assertEquals("https://example.test/body.jpg", article.contentBlocks[1].image?.displayUrl)
        assertEquals("相关新闻", article.relatedArticles.single().title)

        val request = requireNotNull(capturedRequest)
        assertEquals("/api/articles/article-1", request.url.encodedPath)
    }

    @Test
    fun getArticleBySourceIdSendsCategory() = runTest {
        var capturedRequest: HttpRequestData? = null
        val engine = MockEngine { request ->
            capturedRequest = request
            respond(
                content = """
                    {
                      "id": "article-1",
                      "category": "forum",
                      "title": "论坛文章"
                    }
                """.trimIndent(),
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = HttpClient(engine) {
            install(ContentNegotiation) {
                json(HeritageJson)
            }
        }
        val api = createApiClient(httpClient = client, baseUrl = "https://example.test")

        val article = api.getArticleBySourceId(
            sourceId = "31566",
            category = ArticleCategory.Forum,
        )

        assertEquals("论坛文章", article.title)
        val request = requireNotNull(capturedRequest)
        assertEquals("/api/articles/source/31566", request.url.encodedPath)
        assertEquals("forum", request.url.parameters["category"])
    }

    @Test
    fun getArticleBySourceUrlSendsCategoryAndSourceUrl() = runTest {
        var capturedRequest: HttpRequestData? = null
        val sourceUrl = "http://www.ihchina.cn/news2_details/31566.html"
        val engine = MockEngine { request ->
            capturedRequest = request
            respond(
                content = """
                    {
                      "id": "article-1",
                      "category": "specialTopic",
                      "title": "专题文章"
                    }
                """.trimIndent(),
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = HttpClient(engine) {
            install(ContentNegotiation) {
                json(HeritageJson)
            }
        }
        val api = createApiClient(httpClient = client, baseUrl = "https://example.test")

        val article = api.getArticleBySourceUrl(
            sourceUrl = sourceUrl,
            category = ArticleCategory.SpecialTopic,
        )

        assertEquals("专题文章", article.title)
        val request = requireNotNull(capturedRequest)
        assertEquals("/api/articles/source", request.url.encodedPath)
        assertEquals("specialTopic", request.url.parameters["category"])
        assertEquals(sourceUrl, request.url.parameters["sourceUrl"])
    }

    @Test
    fun getDirectoryStatisticsOverviewDecodesDimensionsAndSendsKind() = runTest {
        var capturedRequest: HttpRequestData? = null
        val engine = MockEngine { request ->
            capturedRequest = request
            respond(
                content = """
                    {
                      "kind": "nationalProject",
                      "total": 950,
                      "generatedAt": "2026-05-20T10:00:00Z",
                      "dimensions": [
                        {
                          "dimension": "publishedYear",
                          "items": [
                            { "key": "2006", "name": "2006", "value": 227 },
                            { "key": "2008", "name": "2008", "value": 398 }
                          ]
                        },
                        {
                          "dimension": "region",
                          "items": [
                            { "key": "浙江省杭州市", "name": "浙江省杭州市", "value": 7 }
                          ]
                        }
                      ]
                    }
                """.trimIndent(),
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = HttpClient(engine) {
            install(ContentNegotiation) {
                json(HeritageJson)
            }
        }
        val api = createApiClient(httpClient = client, baseUrl = "https://example.test")

        val result = api.getDirectoryStatisticsOverview(DirectoryItemKind.NationalProject)

        assertEquals("nationalProject", result.kind)
        assertEquals(950, result.total)
        assertEquals(2, result.dimensions.size)
        assertEquals("publishedYear", result.dimensions.first().dimension)
        assertEquals("2006", result.dimensions.first().items.first().key)
        assertEquals(227, result.dimensions.first().items.first().value)

        val request = requireNotNull(capturedRequest)
        assertEquals("/api/directory-items/statistics", request.url.encodedPath)
        assertEquals("nationalProject", request.url.parameters["kind"])
    }

    @Test
    fun getDirectoryStatisticsBreakdownSendsDimensionAndLimit() = runTest {
        var capturedRequest: HttpRequestData? = null
        val engine = MockEngine { request ->
            capturedRequest = request
            respond(
                content = """
                    {
                      "dimension": "region",
                      "items": [
                        { "key": "西藏自治区", "name": "西藏自治区", "value": 7 },
                        { "key": "内蒙古自治区", "name": "内蒙古自治区", "value": 6 }
                      ]
                    }
                """.trimIndent(),
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = HttpClient(engine) {
            install(ContentNegotiation) {
                json(HeritageJson)
            }
        }
        val api = createApiClient(httpClient = client, baseUrl = "https://example.test")

        val result = api.getDirectoryStatisticsBreakdown(
            kind = DirectoryItemKind.NationalProject,
            dimension = DirectoryStatisticDimension.Region,
            limit = 20,
        )

        assertEquals("region", result.dimension)
        assertEquals(2, result.items.size)
        assertEquals("西藏自治区", result.items.first().name)
        assertEquals(7, result.items.first().value)

        val request = requireNotNull(capturedRequest)
        assertEquals("/api/directory-items/statistics/breakdown", request.url.encodedPath)
        assertEquals("nationalProject", request.url.parameters["kind"])
        assertEquals("region", request.url.parameters["dimension"])
        assertEquals("20", request.url.parameters["limit"])
    }

    @Test
    fun getHomeFeed_calls_api_home_feed_and_decodes() = runTest {
        var capturedRequest: HttpRequestData? = null
        val engine = MockEngine { request ->
            capturedRequest = request
            respond(
                content = """
                    {
                      "banners": [
                        { "id": "b1", "sortOrder": 1 }
                      ],
                      "latestNews": [
                        { "id": "a1", "category": "news", "title": "最新新闻" }
                      ],
                      "latestSpecialTopics": [],
                      "latestForumArticles": [],
                      "featuredDirectoryItems": [],
                      "featuredInheritors": []
                    }
                """.trimIndent(),
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = HttpClient(engine) {
            install(ContentNegotiation) { json(HeritageJson) }
        }
        val api = createApiClient(httpClient = client, baseUrl = "https://example.test")

        val result = api.getHomeFeed()

        assertEquals(1, result.banners.size)
        assertEquals("b1", result.banners.first().id)
        assertEquals(1, result.latestNews.size)
        assertEquals("最新新闻", result.latestNews.first().title)
        assertEquals(0, result.latestForumArticles.size)

        val request = requireNotNull(capturedRequest)
        assertEquals("/api/home/feed", request.url.encodedPath)
    }

    @Test
    fun getArticleContext_calls_context_endpoint_and_decodes_arrays() = runTest {
        var capturedRequest: HttpRequestData? = null
        val engine = MockEngine { request ->
            capturedRequest = request
            respond(
                content = """
                    {
                      "related": [
                        { "id": "r1", "type": "article", "title": "关联" }
                      ],
                      "recommendations": [
                        { "id": "rec1", "source": "semantic", "weight": 0.8, "title": "推荐" }
                      ],
                      "semanticRecommendations": [],
                      "collections": [],
                      "exploreTopics": []
                    }
                """.trimIndent(),
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = HttpClient(engine) {
            install(ContentNegotiation) { json(HeritageJson) }
        }
        val api = createApiClient(httpClient = client, baseUrl = "https://example.test")

        val result = api.getArticleContext("art-1")

        assertEquals(1, result.related.size)
        assertEquals("关联", result.related.first().title)
        assertEquals(1, result.recommendations.size)
        assertEquals(0.8, result.recommendations.first().weight, 0.001)
        assertEquals(0, result.semanticRecommendations.size)

        val request = requireNotNull(capturedRequest)
        assertEquals("/api/articles/art-1/context", request.url.encodedPath)
    }

    @Test
    fun searchV2_sends_types_as_comma_separated() = runTest {
        var capturedRequest: HttpRequestData? = null
        val engine = MockEngine { request ->
            capturedRequest = request
            respond(
                content = """
                    {
                      "items": [
                        { "id": "s1", "type": "article", "title": "搜索结果", "score": 95 }
                      ],
                      "page": 1,
                      "pageSize": 10,
                      "total": 1,
                      "hasMore": false
                    }
                """.trimIndent(),
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = HttpClient(engine) {
            install(ContentNegotiation) { json(HeritageJson) }
        }
        val api = createApiClient(httpClient = client, baseUrl = "https://example.test")

        val result = api.searchV2(
            SearchV2Query(
                keywords = "陶瓷",
                types = setOf(SearchResultType.Article, SearchResultType.DirectoryItem),
                page = 1,
                pageSize = 10,
                region = "浙江",
                category = "传统技艺",
                year = 2020,
                kind = DirectoryItemKind.NationalProject,
                hasImage = true,
            ),
        )

        assertEquals(1, result.items.size)
        assertEquals("搜索结果", result.items.first().title)
        assertEquals(95, result.items.first().score)

        val request = requireNotNull(capturedRequest)
        assertEquals("/api/search/v2", request.url.encodedPath)
        assertEquals("陶瓷", request.url.parameters["keywords"])
        assertEquals("article,directoryItem", request.url.parameters["types"])
        assertEquals("1", request.url.parameters["page"])
        assertEquals("10", request.url.parameters["pageSize"])
        assertEquals("浙江", request.url.parameters["region"])
        assertEquals("传统技艺", request.url.parameters["category"])
        assertEquals("2020", request.url.parameters["year"])
        assertEquals("nationalProject", request.url.parameters["kind"])
        assertEquals("true", request.url.parameters["hasImage"])
    }

    @Test
    fun searchV2_sends_empty_types_as_null() = runTest {
        var capturedRequest: HttpRequestData? = null
        val engine = MockEngine { request ->
            capturedRequest = request
            respond(
                content = """{ "items": [], "page": 1, "pageSize": 20, "total": 0, "hasMore": false }""",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = HttpClient(engine) {
            install(ContentNegotiation) { json(HeritageJson) }
        }
        val api = createApiClient(httpClient = client, baseUrl = "https://example.test")

        api.searchV2(SearchV2Query(keywords = "测试", types = emptySet()))

        val request = requireNotNull(capturedRequest)
        assertEquals(null, request.url.parameters["types"])
    }

    @Test
    fun getTimelineV2_sends_types_as_comma_separated() = runTest {
        var capturedRequest: HttpRequestData? = null
        val engine = MockEngine { request ->
            capturedRequest = request
            respond(
                content = """
                    {
                      "items": [
                        { "id": "t1", "type": "article", "title": "时间线", "year": 2020 }
                      ],
                      "facets": {
                        "types": [{ "key": "article", "count": 5 }],
                        "categories": [],
                        "regions": [],
                        "kinds": []
                      },
                      "page": 1,
                      "pageSize": 20,
                      "total": 1,
                      "hasMore": false
                    }
                """.trimIndent(),
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = HttpClient(engine) {
            install(ContentNegotiation) { json(HeritageJson) }
        }
        val api = createApiClient(httpClient = client, baseUrl = "https://example.test")

        val result = api.getTimelineV2(
            TimelineV2Query(
                year = 2020,
                types = setOf(SearchResultType.Article, SearchResultType.Inheritor),
                region = "北京",
                category = "传统技艺",
            ),
        )

        assertEquals(1, result.items.size)
        assertEquals("时间线", result.items.first().title)
        assertNotNull(result.facets)
        assertEquals(1, result.facets!!.types.size)
        assertEquals("article", result.facets!!.types.first().key)

        val request = requireNotNull(capturedRequest)
        assertEquals("/api/timeline/v2", request.url.encodedPath)
        assertEquals("2020", request.url.parameters["year"])
        assertEquals("article,inheritor", request.url.parameters["types"])
        assertEquals("北京", request.url.parameters["region"])
        assertEquals("传统技艺", request.url.parameters["category"])
    }

    @Test
    fun getTimelineV2_sends_empty_types_as_null() = runTest {
        var capturedRequest: HttpRequestData? = null
        val engine = MockEngine { request ->
            capturedRequest = request
            respond(
                content = """{ "items": [], "page": 1, "pageSize": 20, "total": 0, "hasMore": false }""",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = HttpClient(engine) {
            install(ContentNegotiation) { json(HeritageJson) }
        }
        val api = createApiClient(httpClient = client, baseUrl = "https://example.test")

        api.getTimelineV2(TimelineV2Query(types = emptySet()))

        val request = requireNotNull(capturedRequest)
        assertEquals(null, request.url.parameters["types"])
    }

    @Test
    fun getExploreTopic_encodes_chinese_key_in_path() = runTest {
        var capturedRequest: HttpRequestData? = null
        val engine = MockEngine { request ->
            capturedRequest = request
            respond(
                content = """
                    {
                      "topic": {
                        "type": "category",
                        "key": "传统技艺",
                        "title": "传统技艺"
                      },
                      "stats": [
                        { "name": "项目数", "value": 120 }
                      ],
                      "sections": [
                        {
                          "id": "featured",
                          "title": "代表性项目",
                          "items": [
                            { "id": "e1", "title": "景德镇陶瓷" }
                          ]
                        }
                      ]
                    }
                """.trimIndent(),
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = HttpClient(engine) {
            install(ContentNegotiation) { json(HeritageJson) }
        }
        val api = createApiClient(httpClient = client, baseUrl = "https://example.test")

        val result = api.getExploreTopic("category", "传统技艺", limit = 10)

        assertEquals("传统技艺", result.topic?.title)
        assertEquals(1, result.stats.size)
        assertEquals(120, result.stats.first().value)
        assertEquals(1, result.sections.size)
        assertEquals("景德镇陶瓷", result.sections.first().items.first().title)

        val request = requireNotNull(capturedRequest)
        // 中文 key 应该被编码
        val encodedPath = request.url.encodedPath
        assertEquals("/api/explore/topics/category/%E4%BC%A0%E7%BB%9F%E6%8A%80%E8%89%BA", encodedPath)
        assertEquals("10", request.url.parameters["limit"])
    }

    @Test
    fun getRegionAtlasDetail_encodes_chinese_region_in_path() = runTest {
        var capturedRequest: HttpRequestData? = null
        val engine = MockEngine { request ->
            capturedRequest = request
            respond(
                content = """
                    {
                      "region": "浙江省",
                      "displayName": "浙江省",
                      "stats": {
                        "directoryItemCount": 40,
                        "inheritorCount": 2,
                        "total": 42
                      },
                      "categoryBreakdown": [
                        { "key": "传统技艺", "count": 15 }
                      ],
                      "kindBreakdown": [],
                      "featuredDirectoryItems": [
                        { "id": "ra1", "title": "西湖龙井" }
                      ],
                      "featuredInheritors": [],
                      "relatedArticles": [],
                      "timeline": [],
                      "relatedRegions": []
                    }
                """.trimIndent(),
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = HttpClient(engine) {
            install(ContentNegotiation) { json(HeritageJson) }
        }
        val api = createApiClient(httpClient = client, baseUrl = "https://example.test")

        val result = api.getRegionAtlasDetail("浙江省", limit = 5)

        assertEquals("浙江省", result.region)
        assertEquals(42L, result.stats?.total)
        assertEquals(1, result.featuredDirectoryItems.size)
        assertEquals("西湖龙井", result.featuredDirectoryItems.first().title)

        val request = requireNotNull(capturedRequest)
        // 中文 region 应该被编码
        val encodedPath = request.url.encodedPath
        assertEquals("/api/regions/%E6%B5%99%E6%B1%9F%E7%9C%81/atlas", encodedPath)
        assertEquals("5", request.url.parameters["limit"])
    }

    @Test
    fun getTopicCollection_encodes_chinese_key_in_path() = runTest {
        var capturedRequest: HttpRequestData? = null
        val engine = MockEngine { request ->
            capturedRequest = request
            respond(
                content = """
                    {
                      "id": "tc1",
                      "title": "专题集",
                      "items": []
                    }
                """.trimIndent(),
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = HttpClient(engine) {
            install(ContentNegotiation) { json(HeritageJson) }
        }
        val api = createApiClient(httpClient = client, baseUrl = "https://example.test")

        api.getTopicCollection("category", "传统技艺", limit = 3)

        val request = requireNotNull(capturedRequest)
        // 中文 key 应该被编码
        val encodedPath = request.url.encodedPath
        assertEquals("/api/collections/topic/category/%E4%BC%A0%E7%BB%9F%E6%8A%80%E8%89%BA", encodedPath)
        assertEquals("3", request.url.parameters["limit"])
    }

    @Test
    fun getLearningPathDetail_calls_expected_url() = runTest {
        var capturedRequest: HttpRequestData? = null
        val engine = MockEngine { request ->
            capturedRequest = request
            respond(
                content = """
                    {
                      "id": "lp1",
                      "title": "非遗入门",
                      "steps": [
                        {
                          "id": "s1",
                          "title": "第一步",
                          "items": [
                            { "id": "li1", "title": "学习项目" }
                          ]
                        }
                      ]
                    }
                """.trimIndent(),
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = HttpClient(engine) {
            install(ContentNegotiation) { json(HeritageJson) }
        }
        val api = createApiClient(httpClient = client, baseUrl = "https://example.test")

        val result = api.getLearningPathDetail("lp1", limit = 8)

        assertEquals("非遗入门", result.title)
        assertEquals(1, result.steps.size)
        assertEquals("学习项目", result.steps.first().items.first().title)

        val request = requireNotNull(capturedRequest)
        assertEquals("/api/explore/learning-paths/lp1", request.url.encodedPath)
        assertEquals("8", request.url.parameters["limit"])
    }

    @Test
    fun getCollection_calls_collection_id_with_limit() = runTest {
        var capturedRequest: HttpRequestData? = null
        val engine = MockEngine { request ->
            capturedRequest = request
            respond(
                content = """
                    {
                      "id": "col1",
                      "title": "精选集",
                      "items": [
                        { "id": "ci1", "type": "article", "title": "精选内容" }
                      ]
                    }
                """.trimIndent(),
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = HttpClient(engine) {
            install(ContentNegotiation) { json(HeritageJson) }
        }
        val api = createApiClient(httpClient = client, baseUrl = "https://example.test")

        val result = api.getCollection("col1", limit = 5)

        assertEquals("精选集", result.title)
        assertEquals(1, result.items.size)
        assertEquals("精选内容", result.items.first().title)

        val request = requireNotNull(capturedRequest)
        assertEquals("/api/collections/col1", request.url.encodedPath)
        assertEquals("5", request.url.parameters["limit"])
    }

    // region Discovery v2

    @Test
    fun getDiscoveryToday_calls_correct_endpoint() = runTest {
        var capturedRequest: HttpRequestData? = null
        val engine = MockEngine { request ->
            capturedRequest = request
            respond(
                content = """{ "date": "2026-05-31", "articles": [] }""",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = HttpClient(engine) { install(ContentNegotiation) { json(HeritageJson) } }
        val api = createApiClient(httpClient = client, baseUrl = "https://example.test")

        val result = api.getDiscoveryToday()

        assertEquals("2026-05-31", result.date)
        val request = requireNotNull(capturedRequest)
        assertEquals("/api/discovery/today", request.url.encodedPath)
    }

    @Test
    fun getDiscoveryTrending_sends_limit() = runTest {
        var capturedRequest: HttpRequestData? = null
        val engine = MockEngine { request ->
            capturedRequest = request
            respond(
                content = """{ "items": [] }""",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = HttpClient(engine) { install(ContentNegotiation) { json(HeritageJson) } }
        val api = createApiClient(httpClient = client, baseUrl = "https://example.test")

        api.getDiscoveryTrending(limit = 15)

        val request = requireNotNull(capturedRequest)
        assertEquals("/api/discovery/trending", request.url.encodedPath)
        assertEquals("15", request.url.parameters["limit"])
    }

    @Test
    fun getDiscoveryWeekly_calls_correct_endpoint() = runTest {
        var capturedRequest: HttpRequestData? = null
        val engine = MockEngine { request ->
            capturedRequest = request
            respond(
                content = """{ "weekId": "2026-W22", "sections": [] }""",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = HttpClient(engine) { install(ContentNegotiation) { json(HeritageJson) } }
        val api = createApiClient(httpClient = client, baseUrl = "https://example.test")

        val result = api.getDiscoveryWeekly()

        assertEquals("2026-W22", result.weekId)
        val request = requireNotNull(capturedRequest)
        assertEquals("/api/discovery/weekly", request.url.encodedPath)
    }

    @Test
    fun getDiscoverySerendipity_sends_query_params() = runTest {
        var capturedRequest: HttpRequestData? = null
        val engine = MockEngine { request ->
            capturedRequest = request
            respond(
                content = """{ "id": "s1", "type": "directoryItem", "title": "随机" }""",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = HttpClient(engine) { install(ContentNegotiation) { json(HeritageJson) } }
        val api = createApiClient(httpClient = client, baseUrl = "https://example.test")

        val result = api.getDiscoverySerendipity(
            DiscoverySerendipityQuery(
                type = SearchResultType.DirectoryItem,
                hasImage = true,
                region = "浙江",
                category = "传统技艺",
            ),
        )

        assertEquals("随机", result.title)
        val request = requireNotNull(capturedRequest)
        assertEquals("/api/discovery/serendipity", request.url.encodedPath)
        assertEquals("directoryItem", request.url.parameters["type"])
        assertEquals("true", request.url.parameters["hasImage"])
        assertEquals("浙江", request.url.parameters["region"])
        assertEquals("传统技艺", request.url.parameters["category"])
    }

    @Test
    fun getDiscoveryDeepDive_sends_seed_params() = runTest {
        var capturedRequest: HttpRequestData? = null
        val engine = MockEngine { request ->
            capturedRequest = request
            respond(
                content = """{ "related": [] }""",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = HttpClient(engine) { install(ContentNegotiation) { json(HeritageJson) } }
        val api = createApiClient(httpClient = client, baseUrl = "https://example.test")

        api.getDiscoveryDeepDive(
            DiscoveryDeepDiveQuery(
                seedType = SearchResultType.Article,
                seedId = "abc123",
                limit = 5,
            ),
        )

        val request = requireNotNull(capturedRequest)
        assertEquals("/api/discovery/deep-dive", request.url.encodedPath)
        assertEquals("article", request.url.parameters["seedType"])
        assertEquals("abc123", request.url.parameters["seedId"])
        assertEquals("5", request.url.parameters["limit"])
    }

    // endregion

    // region Data Stories

    @Test
    fun getRegionStory_encodes_chinese_region_in_path() = runTest {
        var capturedRequest: HttpRequestData? = null
        val engine = MockEngine { request ->
            capturedRequest = request
            respond(
                content = """{ "id": "story-1", "title": "浙江故事", "sections": [] }""",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = HttpClient(engine) { install(ContentNegotiation) { json(HeritageJson) } }
        val api = createApiClient(httpClient = client, baseUrl = "https://example.test")

        val result = api.getRegionStory("浙江省")

        assertEquals("浙江故事", result.title)
        val request = requireNotNull(capturedRequest)
        assertEquals("/api/stories/regions/%E6%B5%99%E6%B1%9F%E7%9C%81", request.url.encodedPath)
    }

    @Test
    fun getCategoryStory_encodes_chinese_category_in_path() = runTest {
        var capturedRequest: HttpRequestData? = null
        val engine = MockEngine { request ->
            capturedRequest = request
            respond(
                content = """{ "id": "story-2", "title": "传统技艺故事", "sections": [] }""",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = HttpClient(engine) { install(ContentNegotiation) { json(HeritageJson) } }
        val api = createApiClient(httpClient = client, baseUrl = "https://example.test")

        val result = api.getCategoryStory("传统技艺")

        assertEquals("传统技艺故事", result.title)
        val request = requireNotNull(capturedRequest)
        assertEquals("/api/stories/categories/%E4%BC%A0%E7%BB%9F%E6%8A%80%E8%89%BA", request.url.encodedPath)
    }

    @Test
    fun getYearStory_sends_year_in_path() = runTest {
        var capturedRequest: HttpRequestData? = null
        val engine = MockEngine { request ->
            capturedRequest = request
            respond(
                content = """{ "id": "story-3", "title": "2024年故事", "sections": [] }""",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = HttpClient(engine) { install(ContentNegotiation) { json(HeritageJson) } }
        val api = createApiClient(httpClient = client, baseUrl = "https://example.test")

        val result = api.getYearStory(2024)

        assertEquals("2024年故事", result.title)
        val request = requireNotNull(capturedRequest)
        assertEquals("/api/stories/years/2024", request.url.encodedPath)
    }

    // endregion

    // region Taxonomy

    @Test
    fun getTaxonomyCategories_sends_limit() = runTest {
        var capturedRequest: HttpRequestData? = null
        val engine = MockEngine { request ->
            capturedRequest = request
            respond(
                content = """{ "items": [] }""",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = HttpClient(engine) { install(ContentNegotiation) { json(HeritageJson) } }
        val api = createApiClient(httpClient = client, baseUrl = "https://example.test")

        api.getTaxonomyCategories(limit = 30)

        val request = requireNotNull(capturedRequest)
        assertEquals("/api/taxonomy/categories", request.url.encodedPath)
        assertEquals("30", request.url.parameters["limit"])
    }

    @Test
    fun getTaxonomyRegions_sends_limit_and_sort() = runTest {
        var capturedRequest: HttpRequestData? = null
        val engine = MockEngine { request ->
            capturedRequest = request
            respond(
                content = """{ "items": [] }""",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = HttpClient(engine) { install(ContentNegotiation) { json(HeritageJson) } }
        val api = createApiClient(httpClient = client, baseUrl = "https://example.test")

        api.getTaxonomyRegions(limit = 20, sort = TaxonomyRegionSort.Inheritor)

        val request = requireNotNull(capturedRequest)
        assertEquals("/api/taxonomy/regions", request.url.encodedPath)
        assertEquals("20", request.url.parameters["limit"])
        assertEquals("inheritor", request.url.parameters["sort"])
    }

    @Test
    fun getTaxonomyKinds_calls_correct_endpoint() = runTest {
        var capturedRequest: HttpRequestData? = null
        val engine = MockEngine { request ->
            capturedRequest = request
            respond(
                content = """{ "items": [] }""",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = HttpClient(engine) { install(ContentNegotiation) { json(HeritageJson) } }
        val api = createApiClient(httpClient = client, baseUrl = "https://example.test")

        api.getTaxonomyKinds()

        val request = requireNotNull(capturedRequest)
        assertEquals("/api/taxonomy/kinds", request.url.encodedPath)
    }

    @Test
    fun getTaxonomyCategoryDetail_encodes_chinese_category_in_path() = runTest {
        var capturedRequest: HttpRequestData? = null
        val engine = MockEngine { request ->
            capturedRequest = request
            respond(
                content = """{ "topic": { "key": "传统技艺" }, "stats": {} }""",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = HttpClient(engine) { install(ContentNegotiation) { json(HeritageJson) } }
        val api = createApiClient(httpClient = client, baseUrl = "https://example.test")

        api.getTaxonomyCategoryDetail("传统技艺", limit = 10)

        val request = requireNotNull(capturedRequest)
        assertEquals("/api/taxonomy/category/%E4%BC%A0%E7%BB%9F%E6%8A%80%E8%89%BA", request.url.encodedPath)
        assertEquals("10", request.url.parameters["limit"])
    }

    @Test
    fun getTaxonomyRegionDetail_encodes_chinese_region_in_path() = runTest {
        var capturedRequest: HttpRequestData? = null
        val engine = MockEngine { request ->
            capturedRequest = request
            respond(
                content = """{ "topic": { "key": "浙江省" }, "stats": {} }""",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = HttpClient(engine) { install(ContentNegotiation) { json(HeritageJson) } }
        val api = createApiClient(httpClient = client, baseUrl = "https://example.test")

        api.getTaxonomyRegionDetail("浙江省", limit = 8)

        val request = requireNotNull(capturedRequest)
        assertEquals("/api/taxonomy/region/%E6%B5%99%E6%B1%9F%E7%9C%81", request.url.encodedPath)
        assertEquals("8", request.url.parameters["limit"])
    }

    // endregion

    // region Compare

    @Test
    fun compareRegions_sends_left_right_limit() = runTest {
        var capturedRequest: HttpRequestData? = null
        val engine = MockEngine { request ->
            capturedRequest = request
            respond(
                content = """{ "left": {}, "right": {}, "summary": {} }""",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = HttpClient(engine) { install(ContentNegotiation) { json(HeritageJson) } }
        val api = createApiClient(httpClient = client, baseUrl = "https://example.test")

        api.compareRegions("浙江", "江苏", limit = 5)

        val request = requireNotNull(capturedRequest)
        assertEquals("/api/compare/regions", request.url.encodedPath)
        assertEquals("浙江", request.url.parameters["left"])
        assertEquals("江苏", request.url.parameters["right"])
        assertEquals("5", request.url.parameters["limit"])
    }

    @Test
    fun compareCategories_sends_left_right() = runTest {
        var capturedRequest: HttpRequestData? = null
        val engine = MockEngine { request ->
            capturedRequest = request
            respond(
                content = """{ "left": {}, "right": {}, "summary": {} }""",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = HttpClient(engine) { install(ContentNegotiation) { json(HeritageJson) } }
        val api = createApiClient(httpClient = client, baseUrl = "https://example.test")

        api.compareCategories("传统技艺", "传统美术")

        val request = requireNotNull(capturedRequest)
        assertEquals("/api/compare/categories", request.url.encodedPath)
        assertEquals("传统技艺", request.url.parameters["left"])
        assertEquals("传统美术", request.url.parameters["right"])
    }

    @Test
    fun compareKinds_sends_wire_names() = runTest {
        var capturedRequest: HttpRequestData? = null
        val engine = MockEngine { request ->
            capturedRequest = request
            respond(
                content = """{ "left": {}, "right": {}, "summary": {} }""",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = HttpClient(engine) { install(ContentNegotiation) { json(HeritageJson) } }
        val api = createApiClient(httpClient = client, baseUrl = "https://example.test")

        api.compareKinds(DirectoryItemKind.NationalProject, DirectoryItemKind.UnescoEntry, limit = 3)

        val request = requireNotNull(capturedRequest)
        assertEquals("/api/compare/kinds", request.url.encodedPath)
        assertEquals("nationalProject", request.url.parameters["left"])
        assertEquals("unescoEntry", request.url.parameters["right"])
        assertEquals("3", request.url.parameters["limit"])
    }

    // endregion

    // region Content Digest

    @Test
    fun getArticleDigest_calls_correct_endpoint() = runTest {
        var capturedRequest: HttpRequestData? = null
        val engine = MockEngine { request ->
            capturedRequest = request
            respond(
                content = """{ "type": "article", "id": "a1", "title": "速览" }""",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = HttpClient(engine) { install(ContentNegotiation) { json(HeritageJson) } }
        val api = createApiClient(httpClient = client, baseUrl = "https://example.test")

        val result = api.getArticleDigest("a1")

        assertEquals("速览", result.title)
        val request = requireNotNull(capturedRequest)
        assertEquals("/api/articles/a1/digest", request.url.encodedPath)
    }

    @Test
    fun getDirectoryItemDigest_calls_correct_endpoint() = runTest {
        var capturedRequest: HttpRequestData? = null
        val engine = MockEngine { request ->
            capturedRequest = request
            respond(
                content = """{ "type": "directoryItem", "id": "d1", "title": "名录速览" }""",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = HttpClient(engine) { install(ContentNegotiation) { json(HeritageJson) } }
        val api = createApiClient(httpClient = client, baseUrl = "https://example.test")

        val result = api.getDirectoryItemDigest("d1")

        assertEquals("名录速览", result.title)
        val request = requireNotNull(capturedRequest)
        assertEquals("/api/directory-items/d1/digest", request.url.encodedPath)
    }

    @Test
    fun getInheritorDigest_calls_correct_endpoint() = runTest {
        var capturedRequest: HttpRequestData? = null
        val engine = MockEngine { request ->
            capturedRequest = request
            respond(
                content = """{ "type": "inheritor", "id": "i1", "title": "传承人速览" }""",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = HttpClient(engine) { install(ContentNegotiation) { json(HeritageJson) } }
        val api = createApiClient(httpClient = client, baseUrl = "https://example.test")

        val result = api.getInheritorDigest("i1")

        assertEquals("传承人速览", result.title)
        val request = requireNotNull(capturedRequest)
        assertEquals("/api/inheritors/i1/digest", request.url.encodedPath)
    }

    // endregion

    // region Blended Recommendations

    @Test
    fun getBlendedRecommendations_sends_all_params() = runTest {
        var capturedRequest: HttpRequestData? = null
        val engine = MockEngine { request ->
            capturedRequest = request
            respond(
                content = """{ "items": [], "query": {} }""",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = HttpClient(engine) { install(ContentNegotiation) { json(HeritageJson) } }
        val api = createApiClient(httpClient = client, baseUrl = "https://example.test")

        api.getBlendedRecommendations(
            BlendedRecommendationQuery(
                type = SearchResultType.DirectoryItem,
                id = "abc123",
                limit = 8,
                ruleWeight = 0.5,
                semanticWeight = 0.8,
                sameCategoryWeight = 0.3,
                sameRegionWeight = 0.2,
                diversify = false,
            ),
        )

        val request = requireNotNull(capturedRequest)
        assertEquals("/api/recommendations/blended/directoryItem/abc123", request.url.encodedPath)
        assertEquals("8", request.url.parameters["limit"])
        assertEquals("0.5", request.url.parameters["ruleWeight"])
        assertEquals("0.8", request.url.parameters["semanticWeight"])
        assertEquals("0.3", request.url.parameters["sameCategoryWeight"])
        assertEquals("0.2", request.url.parameters["sameRegionWeight"])
        assertEquals("false", request.url.parameters["diversify"])
    }

    @Test
    fun getLocalUserJourneys_includesStrategyAndFlags() = runTest {
        var capturedRequest: HttpRequestData? = null
        val engine = MockEngine { request ->
            capturedRequest = request
            respond(
                content = """
                    {
                      "strategy": "novelty",
                      "signals": [],
                      "items": [],
                      "warnings": []
                    }
                """.trimIndent(),
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = HttpClient(engine) {
            install(ContentNegotiation) { json(HeritageJson) }
            install(profileHeaderPlugin(fakeProfileRepository))
        }
        val api = createApiClient(httpClient = client, baseUrl = "https://example.test")

        api.getLocalUserJourneys(
            strategy = JourneyStrategy.Novelty,
            limit = 12,
            includeAiInferred = false,
            includeTrail = true,
        )

        val request = requireNotNull(capturedRequest)
        assertEquals("/api/local-user/journeys", request.url.encodedPath)
        assertEquals("novelty", request.url.parameters["strategy"])
        assertEquals("12", request.url.parameters["limit"])
        assertEquals("false", request.url.parameters["includeAiInferred"])
        assertEquals("true", request.url.parameters["includeTrail"])
    }

    @Test
    fun getRandomGraphTrail_decodesWrappedTrailAndAppliesQuery() = runTest {
        var capturedRequest: HttpRequestData? = null
        val engine = MockEngine { request ->
            capturedRequest = request
            respond(
                content = """
                    {
                      "trail": {
                        "trailId": "trail-1",
                        "strategy": "mixed",
                        "title": "随机漫游",
                        "steps": [
                          {
                            "order": 1,
                            "node": {
                              "nodeKey": "article:a1",
                              "type": "article",
                              "mongoId": "a1",
                              "title": "起点"
                            },
                            "stepType": "start"
                          }
                        ],
                        "nodes": [
                          {
                            "nodeKey": "article:a1",
                            "type": "article",
                            "mongoId": "a1",
                            "title": "起点"
                          }
                        ],
                        "edges": [],
                        "topicLabels": ["非遗"],
                        "score": 0.8
                      }
                    }
                """.trimIndent(),
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = HttpClient(engine) {
            install(ContentNegotiation) { json(HeritageJson) }
            install(profileHeaderPlugin(fakeProfileRepository))
        }
        val api = createApiClient(httpClient = client, baseUrl = "https://example.test")

        val result = api.getRandomGraphTrail(
            GraphTrailRandomQuery(
                strategy = TrailStrategy.Mixed,
                type = SearchResultType.Article,
                limit = 6,
            ),
        )

        assertEquals("trail-1", result.trailId)
        assertEquals("随机漫游", result.title)
        assertEquals(1, result.steps.size)
        assertEquals("起点", result.steps.first().node?.title)
        assertEquals(1, result.nodes.size)

        val request = requireNotNull(capturedRequest)
        assertEquals("/api/knowledge-graph/trails/random", request.url.encodedPath)
        assertEquals("mixed", request.url.parameters["strategy"])
        assertEquals("article", request.url.parameters["type"])
        assertEquals("6", request.url.parameters["limit"])
    }

    @Test
    fun getRandomGraphTrail_decodesFlatTrailForCompatibility() = runTest {
        val engine = MockEngine {
            respond(
                content = """
                    {
                      "trailId": "flat-trail",
                      "strategy": "mixed",
                      "title": "兼容漫游",
                      "steps": [
                        {
                          "order": 0,
                          "node": {
                            "nodeKey": "article:a1",
                            "type": "article",
                            "id": "a1",
                            "title": "起点"
                          },
                          "stepType": "start"
                        }
                      ],
                      "nodes": [],
                      "edges": [],
                      "topicLabels": [],
                      "score": 0.5
                    }
                """.trimIndent(),
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = HttpClient(engine) {
            install(ContentNegotiation) { json(HeritageJson) }
            install(profileHeaderPlugin(fakeProfileRepository))
        }
        val api = createApiClient(httpClient = client, baseUrl = "https://example.test")

        val result = api.getRandomGraphTrail(GraphTrailRandomQuery())

        assertEquals("flat-trail", result.trailId)
        assertEquals("兼容漫游", result.title)
        assertEquals("起点", result.steps.first().node?.title)
    }

    @Test
    fun profileHeaderIsSentOnEveryRequest() = runTest {
        var capturedRequest: HttpRequestData? = null
        val engine = MockEngine { request ->
            capturedRequest = request
            respond(
                content = """{"items":[],"page":1,"pageSize":20,"hasMore":false,"total":0}""",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = HttpClient(engine) {
            install(ContentNegotiation) { json(HeritageJson) }
            install(profileHeaderPlugin(fakeProfileRepository))
        }
        val api = createApiClient(httpClient = client, baseUrl = "https://example.test")

        api.getArticles()

        val request = requireNotNull(capturedRequest)
        assertEquals("android_test_profile", request.headers["X-Heritage-Profile-Id"])
    }

    @Test
    fun pathSegmentUsesUrlPathEncoding() = runTest {
        var capturedRequest: HttpRequestData? = null
        val engine = MockEngine { request ->
            capturedRequest = request
            respond(
                content = """{"id":"中/文","category":"news","title":"标题","summary":"","publishedAt":"2026-01-01T00:00:00Z","sourceUrl":"https://example.test/source"}""",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = HttpClient(engine) {
            install(ContentNegotiation) { json(HeritageJson) }
            install(profileHeaderPlugin(fakeProfileRepository))
        }
        val api = createApiClient(httpClient = client, baseUrl = "https://example.test")

        api.getArticle("中/文")

        val request = requireNotNull(capturedRequest)
        assertEquals("/api/articles/%E4%B8%AD%2F%E6%96%87", request.url.encodedPath)
    }

    // endregion
}
