package com.duckylife.heritage.modern.core.network

import com.duckylife.heritage.modern.core.network.dto.ArticleCategory
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
}
