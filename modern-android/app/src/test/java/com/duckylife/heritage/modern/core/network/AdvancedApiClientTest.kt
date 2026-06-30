package com.duckylife.heritage.modern.core.network

import com.duckylife.heritage.modern.core.network.dto.SearchResultType
import com.duckylife.heritage.modern.core.network.dto.advanced.ExportFormat
import com.duckylife.heritage.modern.core.network.dto.advanced.ExportContentResultDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ExportPreviewDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ExportRequestDto
import com.duckylife.heritage.modern.core.network.dto.advanced.FavoriteCreateRequestDto
import com.duckylife.heritage.modern.core.network.dto.advanced.HistoryRecordRequestDto
import com.duckylife.heritage.modern.core.network.LearningRouteBuildQuery
import com.duckylife.heritage.modern.core.network.LearningRouteDetailQuery
import com.duckylife.heritage.modern.core.network.LearningRouteNextQuery
import com.duckylife.heritage.modern.core.network.dto.advanced.LearningProgressUpdateDto
import com.duckylife.heritage.modern.core.network.dto.advanced.LearningRouteDifficulty
import com.duckylife.heritage.modern.core.network.dto.advanced.LearningRouteSeedType
import com.duckylife.heritage.modern.core.network.dto.advanced.LocalUserProfileDto
import com.duckylife.heritage.modern.core.network.dto.advanced.AnalyticsDimension
import com.duckylife.heritage.modern.core.network.dto.advanced.ContentTargetType
import com.duckylife.heritage.modern.core.network.dto.advanced.LocalUserTargetType
import com.duckylife.heritage.modern.core.network.dto.advanced.RankingMetric
import com.duckylife.heritage.modern.core.network.dto.advanced.SpacetimeDimension
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
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import io.ktor.client.plugins.ResponseException
import io.ktor.http.HttpStatusCode
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
            LocalUserFavoritesQuery(targetType = LocalUserTargetType.Article, page = 1, pageSize = 10),
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
            HistoryRecordRequestDto(targetType = LocalUserTargetType.Article, targetId = "a1", lastPosition = "p1"),
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
    fun `getLearningRouteDetail encodes route id and query params`() = runTest {
        var captured: HttpRequestData? = null
        val engine = MockEngine { request ->
            captured = request
            respond(
                content = """
                    {
                        "routeId": "route-1",
                        "title": "Route",
                        "difficulty": "intermediate",
                        "estimatedMinutes": 30,
                        "sections": [],
                        "steps": [],
                        "relatedRoutes": []
                    }
                """.trimIndent(),
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = createClient(engine)
        val api = createApiClient(client)

        val result = api.getLearningRouteDetail(
            LearningRouteDetailQuery(routeId = "route 1", limit = 15, includeAi = false),
        )

        assertEquals("route-1", result.routeId)
        val request = requireNotNull(captured)
        assertEquals("/api/learning-routes/route%201", request.url.encodedPath)
        assertEquals("15", request.url.parameters["limit"])
        assertEquals("false", request.url.parameters["includeAi"])
    }

    @Test
    fun `buildLearningRoute sends seed params as GET query`() = runTest {
        var captured: HttpRequestData? = null
        val engine = MockEngine { request ->
            captured = request
            respond(
                content = """
                    {
                        "routeId": "built-1",
                        "title": "Built",
                        "difficulty": "beginner",
                        "estimatedMinutes": 20,
                        "sections": [],
                        "steps": [],
                        "relatedRoutes": []
                    }
                """.trimIndent(),
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = createClient(engine)
        val api = createApiClient(client)

        val result = api.buildLearningRoute(
            LearningRouteBuildQuery(
                seedType = LearningRouteSeedType.Content,
                seedKey = "article:中文",
                difficulty = LearningRouteDifficulty.Beginner,
                limit = 8,
            ),
        )

        assertEquals("built-1", result.routeId)
        val request = requireNotNull(captured)
        assertEquals("/api/learning-routes/build", request.url.encodedPath)
        assertEquals("content", request.url.parameters["seedType"])
        assertEquals("article:中文", request.url.parameters["seedKey"])
        assertEquals("beginner", request.url.parameters["difficulty"])
        assertEquals("8", request.url.parameters["limit"])
        assertEquals("true", request.url.parameters["includeArticles"])
    }

    @Test
    fun `getLearningRouteNextStep passes profileId and completedStepIds`() = runTest {
        var captured: HttpRequestData? = null
        val engine = MockEngine { request ->
            captured = request
            respond(
                content = """
                    {
                        "routeId": "route-1",
                        "completed": false,
                        "nextStep": {"stepId": "step2", "order": 2, "title": "Next"}
                    }
                """.trimIndent(),
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = createClient(engine)
        val api = createApiClient(client)

        val result = api.getLearningRouteNextStep(
            LearningRouteNextQuery(
                routeId = "route-1",
                completedStepIds = listOf("step1", "step2"),
                profileId = "android_test_profile",
            ),
        )

        assertEquals("step2", result.nextStep?.stepId)
        val request = requireNotNull(captured)
        assertEquals("/api/learning-routes/route-1/next", request.url.encodedPath)
        assertEquals("android_test_profile", request.url.parameters["profileId"])
        assertEquals("step1,step2", request.url.parameters["completedStepIds"])
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
    fun `getResearchPackages decodes list wrapper`() = runTest {
        var captured: HttpRequestData? = null
        val engine = MockEngine { request ->
            captured = request
            respond(
                content = """{"packages":[{"packageId":"pkg-1","status":"succeeded"}],"totalCount":1}""",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = createClient(engine)
        val api = createApiClient(client)

        val result = api.getResearchPackages()

        assertEquals(1, result.packages.size)
        assertEquals(1, result.totalCount)
        val request = requireNotNull(captured)
        assertEquals("/api/research-packages", request.url.encodedPath)
    }

    @Test
    fun `getResearchPackageDetail encodes packageId path segment`() = runTest {
        var captured: HttpRequestData? = null
        val engine = MockEngine { request ->
            captured = request
            respond(
                content = """{"packageId":"中文包","status":"succeeded"}""",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = createClient(engine)
        val api = createApiClient(client)

        api.getResearchPackageDetail(ResearchPackageDetailQuery(packageId = "中文包"))

        val request = requireNotNull(captured)
        assertEquals("/api/research-packages/%E4%B8%AD%E6%96%87%E5%8C%85", request.url.encodedPath)
    }

    @Test
    fun `getResearchArtifact encodes packageId and artifactName path segments`() = runTest {
        var captured: HttpRequestData? = null
        val engine = MockEngine { request ->
            captured = request
            respond(
                content = "artifact body",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Text.Plain.toString()),
            )
        }
        val client = createClient(engine)
        val api = createApiClient(client)

        api.getResearchArtifact(
            ResearchArtifactQuery(packageId = "中文包", artifactName = "报告.json"),
        )

        val request = requireNotNull(captured)
        assertEquals(
            "/api/research-packages/%E4%B8%AD%E6%96%87%E5%8C%85/artifacts/%E6%8A%A5%E5%91%8A.json",
            request.url.encodedPath,
        )
    }

    @Test
    fun `getResearchReportDetail encodes reportId path segment`() = runTest {
        var captured: HttpRequestData? = null
        val engine = MockEngine { request ->
            captured = request
            respond(
                content = """{"reportId":"中文报告","status":"succeeded","title":""}""",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = createClient(engine)
        val api = createApiClient(client)

        api.getResearchReportDetail(ResearchReportDetailQuery(reportId = "中文报告"))

        val request = requireNotNull(captured)
        assertEquals("/api/research-reports/%E4%B8%AD%E6%96%87%E6%8A%A5%E5%91%8A", request.url.encodedPath)
    }

    @Test
    fun `getResearchReportByPackage encodes packageId path segment`() = runTest {
        var captured: HttpRequestData? = null
        val engine = MockEngine { request ->
            captured = request
            respond(
                content = """{"reportId":"r1","packageId":"中文包","status":"succeeded","title":""}""",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = createClient(engine)
        val api = createApiClient(client)

        api.getResearchReportByPackage(ResearchReportByPackageQuery(packageId = "中文包"))

        val request = requireNotNull(captured)
        assertEquals(
            "/api/research-packages/%E4%B8%AD%E6%96%87%E5%8C%85/research-report",
            request.url.encodedPath,
        )
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

    @Test
    fun `exportContent serializes ids request and returns content`() = runTest {
        var captured: HttpRequestData? = null
        val engine = MockEngine { request ->
            captured = request
            respond(
                content = """
                    {
                        "format": "markdown",
                        "itemCount": 1,
                        "content": "# Title"
                    }
                """.trimIndent(),
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = createClient(engine)
        val api = createApiClient(client)

        val request = ExportRequestDto(
            scopeType = ExportScopeType.Ids,
            format = ExportFormat.Markdown,
            ids = listOf("article-1"),
            targetType = "article",
        )
        val result = api.exportContent(request)

        assertEquals("# Title", result.content)
        assertEquals(1, result.itemCount)
        assertEquals(ExportFormat.Markdown, result.format)

        val raw = requireNotNull(captured).bodyText()
        val json = HeritageJson.decodeFromString<JsonObject>(raw)
        assertEquals("ids", json["scopeType"]?.jsonPrimitive?.content)
        assertEquals("article", json["targetType"]?.jsonPrimitive?.content)
        assertEquals("article-1", json["ids"]?.jsonArray?.firstOrNull()?.jsonPrimitive?.content)
    }

    @Test(expected = ResponseException::class)
    fun `exportContent throws ResponseException on 413`() = runTest {
        val engine = MockEngine { _ ->
            respond(
                content = "",
                status = HttpStatusCode.PayloadTooLarge,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = createClient(engine)
        val api = createApiClient(client)

        api.exportContent(
            ExportRequestDto(
                scopeType = ExportScopeType.Ids,
                format = ExportFormat.Json,
                ids = listOf("article-1"),
            ),
        )
    }

    private fun createClient(engine: MockEngine): HttpClient =
        HttpClient(engine) {
            expectSuccess = true
            install(ContentNegotiation) {
                json(HeritageJson)
            }
            install(profileHeaderPlugin(fakeProfileRepository))
        }

    private fun HttpRequestData.bodyText(): String = when (val body = body) {
        is OutgoingContent.ByteArrayContent -> body.bytes().decodeToString()
        else -> error("Unsupported body type: $body")
    }

    // ── DataExplore API ──

    @Test
    fun `getSpacetimeOverview encodes filters and limit`() = runTest {
        var captured: HttpRequestData? = null
        val engine = MockEngine { request ->
            captured = request
            respond(
                content = """{"metrics":{"total":5},"topRegions":[],"topCategories":[],"yearTimeline":[]}""",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = createClient(engine)
        val api = createApiClient(client)

        api.getSpacetimeOverview(
            SpacetimeOverviewQuery(fromYear = 2000, toYear = 2024, region = "浙江", limit = 50),
        )

        val request = requireNotNull(captured)
        assertEquals("/api/spacetime/overview", request.url.encodedPath)
        assertEquals("2000", request.url.parameters["fromYear"])
        assertEquals("2024", request.url.parameters["toYear"])
        assertEquals("浙江", request.url.parameters["region"])
        assertEquals("50", request.url.parameters["limit"])
    }

    @Test
    fun `getSpacetimeHeatmap encodes dimensions and targetType`() = runTest {
        var captured: HttpRequestData? = null
        val engine = MockEngine { request ->
            captured = request
            respond(
                content = """{"x":"region","y":"category","cells":[]}""",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = createClient(engine)
        val api = createApiClient(client)

        api.getSpacetimeHeatmap(
            SpacetimeHeatmapQuery(
                x = SpacetimeDimension.Region,
                y = SpacetimeDimension.Category,
                targetType = ContentTargetType.Article,
            ),
        )

        val request = requireNotNull(captured)
        assertEquals("/api/spacetime/heatmap", request.url.encodedPath)
        assertEquals("region", request.url.parameters["x"])
        assertEquals("category", request.url.parameters["y"])
        assertEquals("article", request.url.parameters["targetType"])
    }

    @Test
    fun `getSpacetimeRegionTimeline encodes Chinese region`() = runTest {
        var captured: HttpRequestData? = null
        val engine = MockEngine { request ->
            captured = request
            respond(
                content = """{"key":"浙江","buckets":[]}""",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = createClient(engine)
        val api = createApiClient(client)

        api.getSpacetimeRegionTimeline(SpacetimeRegionTimelineQuery(region = "浙江"))

        val request = requireNotNull(captured)
        assertEquals("/api/spacetime/regions/%E6%B5%99%E6%B1%9F/timeline", request.url.encodedPath)
    }

    @Test
    fun `getSpacetimeCategoryTimeline encodes Chinese category`() = runTest {
        var captured: HttpRequestData? = null
        val engine = MockEngine { request ->
            captured = request
            respond(
                content = """{"key":"传统技艺","buckets":[]}""",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = createClient(engine)
        val api = createApiClient(client)

        api.getSpacetimeCategoryTimeline(SpacetimeCategoryTimelineQuery(category = "传统技艺"))

        val request = requireNotNull(captured)
        assertEquals("/api/spacetime/categories/%E4%BC%A0%E7%BB%9F%E6%8A%80%E8%89%BA/timeline", request.url.encodedPath)
    }

    @Test
    fun `getAnalyticsFacets applies filter parameters`() = runTest {
        var captured: HttpRequestData? = null
        val engine = MockEngine { request ->
            captured = request
            respond(
                content = """{"regions":[{"key":"浙江","count":3}],"categories":[],"years":[],"kinds":[],"targetTypes":[]}""",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = createClient(engine)
        val api = createApiClient(client)

        api.getAnalyticsFacets(
            AnalyticsFacetsQuery(filters = AnalyticsFilters(region = "浙江", category = "传统技艺", year = 2024)),
        )

        val request = requireNotNull(captured)
        assertEquals("/api/analytics/facets", request.url.encodedPath)
        assertEquals("浙江", request.url.parameters["region"])
        assertEquals("传统技艺", request.url.parameters["category"])
        assertEquals("2024", request.url.parameters["year"])
    }

    @Test
    fun `getAnalyticsBreakdown encodes groupBy and limit`() = runTest {
        var captured: HttpRequestData? = null
        val engine = MockEngine { request ->
            captured = request
            respond(
                content = """{"groupBy":"region","buckets":[],"total":0}""",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = createClient(engine)
        val api = createApiClient(client)

        api.getAnalyticsBreakdown(
            AnalyticsBreakdownQuery(groupBy = AnalyticsDimension.Region, limit = 25),
        )

        val request = requireNotNull(captured)
        assertEquals("/api/analytics/breakdown", request.url.encodedPath)
        assertEquals("region", request.url.parameters["groupBy"])
        assertEquals("25", request.url.parameters["limit"])
    }

    @Test
    fun `getAnalyticsCrosstab encodes x y dimensions`() = runTest {
        var captured: HttpRequestData? = null
        val engine = MockEngine { request ->
            captured = request
            respond(
                content = """{"x":"region","y":"year","cells":[],"xBuckets":[],"yBuckets":[]}""",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = createClient(engine)
        val api = createApiClient(client)

        api.getAnalyticsCrosstab(
            AnalyticsCrosstabQuery(
                x = AnalyticsDimension.Region,
                y = AnalyticsDimension.Year,
                filters = AnalyticsFilters(targetType = ContentTargetType.All),
            ),
        )

        val request = requireNotNull(captured)
        assertEquals("/api/analytics/crosstab", request.url.encodedPath)
        assertEquals("region", request.url.parameters["x"])
        assertEquals("year", request.url.parameters["y"])
    }

    @Test
    fun `getAnalyticsCompare encodes keys as comma separated`() = runTest {
        var captured: HttpRequestData? = null
        val engine = MockEngine { request ->
            captured = request
            respond(
                content = """{"dimension":"region","metric":"total","items":[],"winnerKey":"浙江"}""",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = createClient(engine)
        val api = createApiClient(client)

        api.getAnalyticsCompare(
            AnalyticsCompareQuery(
                dimension = AnalyticsDimension.Region,
                keys = listOf("浙江", "江苏"),
                metric = RankingMetric.Total,
            ),
        )

        val request = requireNotNull(captured)
        assertEquals("/api/analytics/compare", request.url.encodedPath)
        assertEquals("浙江,江苏", request.url.parameters["keys"])
        assertEquals("region", request.url.parameters["dimension"])
        assertEquals("total", request.url.parameters["metric"])
    }

    @Test
    fun `getAnalyticsOutliers encodes dimension and metric`() = runTest {
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

        api.getAnalyticsOutliers(
            AnalyticsOutliersQuery(
                dimension = AnalyticsDimension.Region,
                metric = RankingMetric.HiddenGem,
                limit = 10,
            ),
        )

        val request = requireNotNull(captured)
        assertEquals("/api/analytics/outliers", request.url.encodedPath)
        assertEquals("region", request.url.parameters["dimension"])
        assertEquals("hiddenGem", request.url.parameters["metric"])
        assertEquals("10", request.url.parameters["limit"])
    }

    @Test
    fun `getRankings hits correct path`() = runTest {
        var captured: HttpRequestData? = null
        val engine = MockEngine { request ->
            captured = request
            respond(
                content = """[{"rankingId":"top-regions","title":"热门地区"}]""",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = createClient(engine)
        val api = createApiClient(client)

        val result = api.getRankings()

        assertEquals(1, result.size)
        val request = requireNotNull(captured)
        assertEquals("/api/rankings", request.url.encodedPath)
    }

    @Test
    fun `getRankingDetail encodes rankingId and filters`() = runTest {
        var captured: HttpRequestData? = null
        val engine = MockEngine { request ->
            captured = request
            respond(
                content = """{"rankingId":"top-regions","items":[{"rank":1,"title":"浙江"}]}""",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = createClient(engine)
        val api = createApiClient(client)

        api.getRankingDetail(
            RankingDetailQuery(
                rankingId = "top-regions",
                targetType = ContentTargetType.Article,
                region = "浙江",
                limit = 50,
            ),
        )

        val request = requireNotNull(captured)
        assertEquals("/api/rankings/top-regions", request.url.encodedPath)
        assertEquals("article", request.url.parameters["targetType"])
        assertEquals("浙江", request.url.parameters["region"])
        assertEquals("50", request.url.parameters["limit"])
    }

    @Test
    fun `getRankingContent encodes metric and targetType`() = runTest {
        var captured: HttpRequestData? = null
        val engine = MockEngine { request ->
            captured = request
            respond(
                content = """{"rankingId":"","items":[]}""",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = createClient(engine)
        val api = createApiClient(client)

        api.getRankingContent(
            RankingContentQuery(
                metric = RankingMetric.Connectivity,
                targetType = ContentTargetType.All,
                limit = 30,
            ),
        )

        val request = requireNotNull(captured)
        assertEquals("/api/rankings/content", request.url.encodedPath)
        assertEquals("connectivity", request.url.parameters["metric"])
        assertEquals("all", request.url.parameters["targetType"])
        assertEquals("30", request.url.parameters["limit"])
    }

    @Test
    fun `getRankingDetail throws ResponseException on 404`() = runTest {
        val engine = MockEngine { _ ->
            respond(
                content = "{}",
                status = HttpStatusCode.NotFound,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = createClient(engine)
        val api = createApiClient(client)

        val exception = try {
            api.getRankingDetail(RankingDetailQuery(rankingId = "missing"))
            null
        } catch (e: Throwable) {
            e
        }
        assertTrue(exception is ResponseException)
    }
}
