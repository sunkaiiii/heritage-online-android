package com.duckylife.heritage.modern.core.network

import com.duckylife.heritage.modern.core.network.dto.SearchResultType
import com.duckylife.heritage.modern.core.network.dto.advanced.ExportFormat
import com.duckylife.heritage.modern.core.network.dto.advanced.ExportPreviewDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ExportRequestDto
import com.duckylife.heritage.modern.core.network.dto.advanced.FavoriteCreateRequestDto
import com.duckylife.heritage.modern.core.network.dto.advanced.HistoryRecordRequestDto
import com.duckylife.heritage.modern.core.network.dto.advanced.LearningProgressUpdateDto
import com.duckylife.heritage.modern.core.network.dto.advanced.LearningRouteDifficulty
import com.duckylife.heritage.modern.core.network.dto.advanced.LocalUserProfileDto
import io.ktor.client.plugins.api.createClientPlugin
import com.duckylife.heritage.modern.core.network.dto.advanced.ExportScopeType

import com.duckylife.heritage.modern.core.network.dto.PagedResult
import com.duckylife.heritage.modern.core.profile.FakeLocalProfileRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.content.OutgoingContent
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AdvancedApiClientTest {

    private val fakeProfileRepository = FakeLocalProfileRepository()

    private fun createApiClient(httpClient: HttpClient): KtorHeritageApiClient =
        KtorHeritageApiClient(
            httpClient = httpClient,
            baseUrl = "https://example.com",
            profileRepository = fakeProfileRepository,
        )

    @Test
    fun `getLocalUserSummary sends profile header and path`() = runTest {
        var captured: HttpRequestData? = null
        val engine = MockEngine { request ->
            captured = request
            respond(
                content = """
                    {
                        "profileId": "android_test_profile",
                        "favoriteCount": 1,
                        "historyCount": 2,
                        "learningRouteCount": 3
                    }
                """.trimIndent(),
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = createClient(engine)
        val api = createApiClient(client)

        val result = api.getLocalUserSummary()

        assertEquals("android_test_profile", result.profileId)
        val request = requireNotNull(captured)
        assertEquals("/api/local-user/summary", request.url.encodedPath)
        assertEquals("android_test_profile", request.headers["X-Heritage-Profile-Id"])
    }

    @Test
    fun `getLocalUserProfile sends profile header and decodes display name`() = runTest {
        var captured: HttpRequestData? = null
        val engine = MockEngine { request ->
            captured = request
            respond(
                content = """
                    {
                        "profileId": "android_test_profile",
                        "displayName": "Tester",
                        "favoriteCount": 1,
                        "historyCount": 2,
                        "learningRouteCount": 3
                    }
                """.trimIndent(),
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = createClient(engine)
        val api = createApiClient(client)

        val result = api.getLocalUserProfile()

        assertEquals("android_test_profile", result.profileId)
        assertEquals("Tester", result.displayName)
        assertEquals(1L, result.favoriteCount)
        val request = requireNotNull(captured)
        assertEquals("/api/local-user/profile", request.url.encodedPath)
        assertEquals("android_test_profile", request.headers["X-Heritage-Profile-Id"])
    }

    @Test
    fun `getLocalUserFavorites applies query parameters`() = runTest {
        var captured: HttpRequestData? = null
        val engine = MockEngine { request ->
            captured = request
            respond(
                content = """
                    {
                        "items": [
                            {"id": "f1", "targetType": "article", "targetId": "a1"}
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
        val client = createClient(engine)
        val api = createApiClient(client)

        val result = api.getLocalUserFavorites(
            LocalUserFavoritesQuery(targetType = "article", page = 1, pageSize = 10),
        )

        assertEquals(1, result.items.size)
        val request = requireNotNull(captured)
        assertEquals("/api/local-user/favorites", request.url.encodedPath)
        assertEquals("article", request.url.parameters["targetType"])
        assertEquals("1", request.url.parameters["page"])
        assertEquals("10", request.url.parameters["pageSize"])
    }

    @Test
    fun `recordLocalUserHistory serializes request body`() = runTest {
        var captured: HttpRequestData? = null
        val engine = MockEngine { request ->
            captured = request
            respond(
                content = """
                    {
                        "id": "h1",
                        "targetType": "article",
                        "targetId": "a1",
                        "viewCount": 2
                    }
                """.trimIndent(),
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = createClient(engine)
        val api = createApiClient(client)

        val result = api.recordLocalUserHistory(
            HistoryRecordRequestDto(targetType = "article", targetId = "a1", lastPosition = "p1"),
        )

        assertEquals("h1", result.id)
        assertEquals(2, result.viewCount)
        val request = requireNotNull(captured)
        assertEquals("/api/local-user/history", request.url.encodedPath)
        assertEquals("POST", request.method.value)
        val body = request.bodyText()
        assertTrue(body.contains("\"targetType\":\"article\""))
        assertTrue(body.contains("\"lastPosition\":\"p1\""))
    }

    @Test
    fun `updateLocalUserLearningProgress uses put and route id path`() = runTest {
        var captured: HttpRequestData? = null
        val engine = MockEngine { request ->
            captured = request
            respond(
                content = """
                    {
                        "routeId": "route-1",
                        "completedStepIds": ["s1"],
                        "currentStepId": "s2",
                        "percent": 50
                    }
                """.trimIndent(),
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = createClient(engine)
        val api = createApiClient(client)

        val result = api.updateLocalUserLearningProgress(
            routeId = "route-1",
            request = LearningProgressUpdateDto(completedStepIds = listOf("s1"), currentStepId = "s2"),
        )

        assertEquals("route-1", result.routeId)
        assertEquals(listOf("s1"), result.completedStepIds)
        val request = requireNotNull(captured)
        assertEquals("/api/local-user/learning-progress/route-1", request.url.encodedPath)
        assertEquals("PUT", request.method.value)
    }

    @Test
    fun `getV3ContentPage encodes path and query`() = runTest {
        var captured: HttpRequestData? = null
        val engine = MockEngine { request ->
            captured = request
            respond(
                content = """{"pageType":"article","content":null}""",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = createClient(engine)
        val api = createApiClient(client)

        api.getV3ContentPage(
            V3ContentPageQuery(
                contentType = SearchResultType.Article,
                id = "article-1",
                includeAi = true,
                includeGraph = false,
                includeRecommendations = true,
                includeLocalState = true,
                includeDigest = false,
                profileId = "profile-42",
            ),
        )

        val request = requireNotNull(captured)
        assertEquals("/api/v3/pages/article/article-1", request.url.encodedPath)
        assertEquals("true", request.url.parameters["includeAi"])
        assertEquals("false", request.url.parameters["includeGraph"])
        assertEquals("true", request.url.parameters["includeRecommendations"])
        assertEquals("true", request.url.parameters["includeLocalState"])
        assertEquals("false", request.url.parameters["includeDigest"])
        assertEquals("profile-42", request.url.parameters["profileId"])
    }

    @Test
    fun `intelligent search encodes filters and enhancement options`() = runTest {
        var captured: HttpRequestData? = null
        val engine = MockEngine { request ->
            captured = request
            respond(
                content = """{"items":[],"page":2,"pageSize":20,"total":0,"hasMore":false}""",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val api = createApiClient(createClient(engine))

        api.intelligentSearch(
            IntelligentSearchQuery(
                keywords = "剪纸",
                types = setOf(SearchResultType.Article),
                page = 2,
                pageSize = 20,
                region = "北京",
                category = "传统美术",
                includeAi = false,
                includeGraph = true,
                includeHighlights = false,
            ),
        )

        val request = requireNotNull(captured)
        assertEquals("/api/v3/search/intelligent", request.url.encodedPath)
        assertEquals("剪纸", request.url.parameters["q"])
        assertEquals("article", request.url.parameters["types"])
        assertEquals("2", request.url.parameters["page"])
        assertEquals("20", request.url.parameters["pageSize"])
        assertEquals("北京", request.url.parameters["region"])
        assertEquals("传统美术", request.url.parameters["category"])
        assertEquals("false", request.url.parameters["includeAi"])
        assertEquals("true", request.url.parameters["includeGraph"])
        assertEquals("false", request.url.parameters["includeHighlights"])
    }

    @Test
    fun `getGraphNeighbors encodes Chinese path segment`() = runTest {
        var captured: HttpRequestData? = null
        val engine = MockEngine { request ->
            captured = request
            respond(
                content = """{"center":"中国非遗","nodes":[],"edges":[]}""",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = createClient(engine)
        val api = createApiClient(client)

        api.getGraphNeighbors(
            KnowledgeGraphNeighborsQuery(
                contentType = SearchResultType.Article,
                id = "中国非遗",
                limit = 12,
            ),
        )

        val request = requireNotNull(captured)
        assertEquals("/api/knowledge-graph/article/%E4%B8%AD%E5%9B%BD%E9%9D%9E%E9%81%97/neighbors", request.url.encodedPath)
        assertEquals("12", request.url.parameters["limit"])
    }

    @Test
    fun `getLearningRoutes applies query parameters`() = runTest {
        var captured: HttpRequestData? = null
        val engine = MockEngine { request ->
            captured = request
            respond(
                content = "[]",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = createClient(engine)
        val api = createApiClient(client)

        api.getLearningRoutes(
            LearningRoutesListQuery(
                difficulty = LearningRouteDifficulty.Beginner,
                limit = 25,
            ),
        )

        val request = requireNotNull(captured)
        assertEquals("/api/learning-routes", request.url.encodedPath)
        assertEquals("beginner", request.url.parameters["difficulty"])
        assertEquals("25", request.url.parameters["limit"])
    }

    @Test
    fun `getSpacetimeYearMap accepts pre modern year`() = runTest {
        var captured: HttpRequestData? = null
        val engine = MockEngine { request ->
            captured = request
            respond(
                content = """{"year":1850,"regions":[]}""",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = createClient(engine)
        val api = createApiClient(client)

        api.getSpacetimeYearMap(SpacetimeYearMapQuery(year = 1850))

        val request = requireNotNull(captured)
        assertEquals("/api/spacetime/years/1850/map", request.url.encodedPath)
    }

    @Test
    fun `getResearchPackages hits correct path`() = runTest {
        var captured: HttpRequestData? = null
        val engine = MockEngine { request ->
            captured = request
            respond(
                content = """[{"packageId":"pkg-1","title":"Package"}]""",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = createClient(engine)
        val api = createApiClient(client)

        val result = api.getResearchPackages()

        assertEquals(1, result.size)
        val request = requireNotNull(captured)
        assertEquals("/api/research-packages", request.url.encodedPath)
    }

    @Test
    fun `previewExport serializes request and maps md format to Markdown`() = runTest {
        var captured: HttpRequestData? = null
        val engine = MockEngine { request ->
            captured = request
            respond(
                content = """
                    {
                        "scopeType": "search",
                        "format": "md",
                        "estimatedItemCount": 5,
                        "estimatedBytes": 1024
                    }
                """.trimIndent(),
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = createClient(engine)
        val api = createApiClient(client)

        val request = ExportRequestDto(
            scopeType = ExportScopeType.Search,
            query = "陶瓷",
            format = ExportFormat.Csv,
            limit = 10,
        )
        val result = api.previewExport(request)

        assertEquals(ExportFormat.Markdown, result.format)
        assertEquals(5, result.estimatedItemCount)

        val raw = requireNotNull(captured).bodyText()
        val json = HeritageJson.decodeFromString<JsonObject>(raw)
        assertEquals("search", json["scopeType"]?.jsonPrimitive?.content)
        assertEquals("csv", json["format"]?.jsonPrimitive?.content)
        assertEquals("陶瓷", json["query"]?.jsonPrimitive?.content)
        assertEquals(10, json["limit"]?.jsonPrimitive?.int)
    }

    private fun createClient(engine: MockEngine): HttpClient =
        HttpClient(engine) {
            install(ContentNegotiation) {
                json(HeritageJson)
            }
            install(profileHeaderPlugin(fakeProfileRepository))
        }

    private fun HttpRequestData.bodyText(): String = when (val body = body) {
        is OutgoingContent.ByteArrayContent -> body.bytes().decodeToString()
        else -> error("Unsupported body type: $body")
    }
}
