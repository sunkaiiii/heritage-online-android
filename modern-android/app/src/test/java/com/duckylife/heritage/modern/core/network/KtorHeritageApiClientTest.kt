package com.duckylife.heritage.modern.core.network

import com.duckylife.heritage.modern.core.network.dto.ArticleCategory
import com.duckylife.heritage.modern.core.network.dto.ArticleContentBlockType
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind
import com.duckylife.heritage.modern.core.network.dto.DirectoryStatisticDimension
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
        val api = KtorHeritageApiClient(
            httpClient = client,
            baseUrl = "https://example.test",
        )

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
        val api = KtorHeritageApiClient(
            httpClient = client,
            baseUrl = "https://example.test",
        )

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
        val api = KtorHeritageApiClient(
            httpClient = client,
            baseUrl = "https://example.test",
        )

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
        val api = KtorHeritageApiClient(
            httpClient = client,
            baseUrl = "https://example.test",
        )

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
        val api = KtorHeritageApiClient(
            httpClient = client,
            baseUrl = "https://example.test",
        )

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
        val api = KtorHeritageApiClient(
            httpClient = client,
            baseUrl = "https://example.test",
        )

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
        val api = KtorHeritageApiClient(
            httpClient = client,
            baseUrl = "https://example.test",
        )

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
                      "featured": [
                        { "id": "f1", "type": "article", "title": "推荐" }
                      ],
                      "latest": [],
                      "recommendations": []
                    }
                """.trimIndent(),
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = HttpClient(engine) {
            install(ContentNegotiation) { json(HeritageJson) }
        }
        val api = KtorHeritageApiClient(httpClient = client, baseUrl = "https://example.test")

        val result = api.getHomeFeed()

        assertEquals(1, result.banners.size)
        assertEquals("b1", result.banners.first().id)
        assertEquals(1, result.featured.size)
        assertEquals("推荐", result.featured.first().title)
        assertEquals(0, result.latest.size)

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
        val api = KtorHeritageApiClient(httpClient = client, baseUrl = "https://example.test")

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
    fun searchV2_sends_all_query_params() = runTest {
        var capturedRequest: HttpRequestData? = null
        val engine = MockEngine { request ->
            capturedRequest = request
            respond(
                content = """
                    {
                      "items": [
                        { "id": "s1", "type": "article", "title": "搜索结果", "score": 0.95 }
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
        val api = KtorHeritageApiClient(httpClient = client, baseUrl = "https://example.test")

        val result = api.searchV2(
            SearchV2Query(
                keywords = "陶瓷",
                types = "article,directoryItem",
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
        assertEquals(0.95, result.items.first().score, 0.001)

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
    fun getTimelineV2_sends_filters() = runTest {
        var capturedRequest: HttpRequestData? = null
        val engine = MockEngine { request ->
            capturedRequest = request
            respond(
                content = """
                    {
                      "items": [
                        { "id": "t1", "type": "article", "title": "时间线", "year": 2020 }
                      ],
                      "years": [
                        { "year": 2020, "count": 5 }
                      ],
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
        val api = KtorHeritageApiClient(httpClient = client, baseUrl = "https://example.test")

        val result = api.getTimelineV2(
            TimelineV2Query(year = 2020, types = "article", region = "北京"),
        )

        assertEquals(1, result.items.size)
        assertEquals("时间线", result.items.first().title)
        assertEquals(1, result.years.size)
        assertEquals(2020, result.years.first().year)

        val request = requireNotNull(capturedRequest)
        assertEquals("/api/timeline/v2", request.url.encodedPath)
        assertEquals("2020", request.url.parameters["year"])
        assertEquals("article", request.url.parameters["types"])
        assertEquals("北京", request.url.parameters["region"])
    }

    @Test
    fun getExploreTopic_encodes_type_key_and_limit() = runTest {
        var capturedRequest: HttpRequestData? = null
        val engine = MockEngine { request ->
            capturedRequest = request
            respond(
                content = """
                    {
                      "type": "category",
                      "key": "传统技艺",
                      "name": "传统技艺",
                      "stats": [
                        { "name": "项目数", "value": 120 }
                      ],
                      "sections": [
                        {
                          "heading": "代表性项目",
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
        val api = KtorHeritageApiClient(httpClient = client, baseUrl = "https://example.test")

        val result = api.getExploreTopic("category", "传统技艺", limit = 10)

        assertEquals("传统技艺", result.name)
        assertEquals(1, result.stats.size)
        assertEquals(120, result.stats.first().value)
        assertEquals(1, result.sections.size)
        assertEquals("景德镇陶瓷", result.sections.first().items.first().title)

        val request = requireNotNull(capturedRequest)
        assertEquals("/api/explore/topics/category/传统技艺", request.url.encodedPath)
        assertEquals("10", request.url.parameters["limit"])
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
                      "name": "非遗入门",
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
        val api = KtorHeritageApiClient(httpClient = client, baseUrl = "https://example.test")

        val result = api.getLearningPathDetail("lp1", limit = 8)

        assertEquals("非遗入门", result.name)
        assertEquals(1, result.steps.size)
        assertEquals("学习项目", result.steps.first().items.first().title)

        val request = requireNotNull(capturedRequest)
        assertEquals("/api/explore/learning-paths/lp1", request.url.encodedPath)
        assertEquals("8", request.url.parameters["limit"])
    }

    @Test
    fun getRegionAtlasDetail_calls_expected_url_with_limit() = runTest {
        var capturedRequest: HttpRequestData? = null
        val engine = MockEngine { request ->
            capturedRequest = request
            respond(
                content = """
                    {
                      "region": "浙江省",
                      "stats": {
                        "total": 42,
                        "breakdowns": [
                          {
                            "dimension": "category",
                            "items": [
                              { "key": "传统技艺", "name": "传统技艺", "value": 15 }
                            ]
                          }
                        ]
                      },
                      "items": [
                        { "id": "ra1", "title": "西湖龙井" }
                      ]
                    }
                """.trimIndent(),
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = HttpClient(engine) {
            install(ContentNegotiation) { json(HeritageJson) }
        }
        val api = KtorHeritageApiClient(httpClient = client, baseUrl = "https://example.test")

        val result = api.getRegionAtlasDetail("浙江省", limit = 5)

        assertEquals("浙江省", result.region)
        assertEquals(42L, result.stats?.total)
        assertEquals(1, result.items.size)
        assertEquals("西湖龙井", result.items.first().title)

        val request = requireNotNull(capturedRequest)
        assertEquals("/api/regions/浙江省/atlas", request.url.encodedPath)
        assertEquals("5", request.url.parameters["limit"])
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
                      "name": "精选集",
                      "items": [
                        { "id": "ci1", "type": "article", "title": "精选内容" }
                      ],
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
        val api = KtorHeritageApiClient(httpClient = client, baseUrl = "https://example.test")

        val result = api.getCollection("col1", limit = 5)

        assertEquals("精选集", result.name)
        assertEquals(1, result.items.size)
        assertEquals("精选内容", result.items.first().title)

        val request = requireNotNull(capturedRequest)
        assertEquals("/api/collections/col1", request.url.encodedPath)
        assertEquals("5", request.url.parameters["limit"])
    }

    @Test
    fun getTopicCollection_calls_topic_path() = runTest {
        var capturedRequest: HttpRequestData? = null
        val engine = MockEngine { request ->
            capturedRequest = request
            respond(
                content = """
                    {
                      "id": "tc1",
                      "name": "专题集",
                      "items": [],
                      "total": 0,
                      "hasMore": false
                    }
                """.trimIndent(),
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = HttpClient(engine) {
            install(ContentNegotiation) { json(HeritageJson) }
        }
        val api = KtorHeritageApiClient(httpClient = client, baseUrl = "https://example.test")

        api.getTopicCollection("category", "传统技艺", limit = 3)

        val request = requireNotNull(capturedRequest)
        assertEquals("/api/collections/topic/category/传统技艺", request.url.encodedPath)
        assertEquals("3", request.url.parameters["limit"])
    }
}
