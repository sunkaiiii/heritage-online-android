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
}
