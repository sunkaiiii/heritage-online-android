package com.duckylife.heritage.modern.core.network.dto.advanced

import com.duckylife.heritage.modern.core.network.HeritageJson
import com.duckylife.heritage.modern.core.network.dto.PagedResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AdvancedDtoSerializationTest {

    @Test
    fun `unknown enum wire value falls back to Unknown`() {
        val json = """"not_a_node_type""""
        val decoded = HeritageJson.decodeFromString(GraphNodeTypeSerializer, json)
        assertEquals(GraphNodeType.Unknown, decoded)
    }

    @Test
    fun `LocalUserSummaryDto decodes with minimal fields`() {
        val json = """
            {
                "profileId": "android_abc",
                "favoriteCount": 3,
                "historyCount": 10,
                "learningRouteCount": 1
            }
        """.trimIndent()
        val dto = HeritageJson.decodeFromString(LocalUserSummaryDto.serializer(), json)
        assertEquals("android_abc", dto.profileId)
        assertEquals(3, dto.favoriteCount)
        assertTrue(dto.recentFavorites.isEmpty())
        assertNull(dto.generatedAt)
    }

    @Test
    fun `LocalUserProfileDto decodes with display name and counts`() {
        val json = """
            {
                "profileId": "android_abc",
                "displayName": "Tester",
                "favoriteCount": 3,
                "historyCount": 10,
                "learningRouteCount": 1,
                "generatedAt": "2026-06-24T09:00:00Z"
            }
        """.trimIndent()
        val dto = HeritageJson.decodeFromString(LocalUserProfileDto.serializer(), json)
        assertEquals("android_abc", dto.profileId)
        assertEquals("Tester", dto.displayName)
        assertEquals(3L, dto.favoriteCount)
        assertEquals(10L, dto.historyCount)
        assertEquals(1L, dto.learningRouteCount)
        assertEquals("2026-06-24T09:00:00Z", dto.generatedAt)
    }

    @Test
    fun `LocalHistoryDto decodes with defaults`() {
        val json = """
            {
                "id": "h1",
                "targetType": "article",
                "targetId": "a1"
            }
        """.trimIndent()
        val dto = HeritageJson.decodeFromString(LocalHistoryDto.serializer(), json)
        assertEquals("h1", dto.id)
        assertEquals("article", dto.targetType)
        assertEquals("a1", dto.targetId)
        assertEquals(1, dto.viewCount)
        assertNull(dto.viewedAt)
    }

    @Test
    fun `LocalLearningProgressDto decodes with defaults`() {
        val json = """
            {
                "routeId": "r1"
            }
        """.trimIndent()
        val dto = HeritageJson.decodeFromString(LocalLearningProgressDto.serializer(), json)
        assertEquals("r1", dto.routeId)
        assertNull(dto.id)
        assertTrue(dto.completedStepIds.isEmpty())
        assertEquals(0, dto.percent)
    }

    @Test
    fun `PagedResult LocalFavoriteDto decodes`() {
        val json = """
            {
                "items": [
                    {"id": "f1", "targetType": "article", "targetId": "a1", "titleSnapshot": "News"}
                ],
                "page": 1,
                "pageSize": 20,
                "total": 1,
                "hasMore": false
            }
        """.trimIndent()
        val dto = HeritageJson.decodeFromString(PagedResult.serializer(LocalFavoriteDto.serializer()), json)
        assertEquals(1, dto.items.size)
        assertEquals("article", dto.items.first().targetType)
    }

    @Test
    fun `AiCardDto decodes missing status`() {
        val json = """
            {
                "targetType": "article",
                "targetId": "a1",
                "hasAi": false,
                "status": "missing",
                "summary": null,
                "keywords": []
            }
        """.trimIndent()
        val dto = HeritageJson.decodeFromString(AiCardDto.serializer(), json)
        assertFalse(dto.hasAi)
        assertEquals("missing", dto.status)
        assertTrue(dto.keywords.isEmpty())
    }

    @Test
    fun `V3 content page fixture decodes stale AI mixed sections and local state`() {
        val json = """
            {
                "pageType": "article",
                "digest": {"summary": "摘要", "highlights": ["要点"]},
                "aiCard": {"hasAi": true, "status": "ready", "isStale": true, "keywords": ["剪纸"]},
                "graph": null,
                "recommendations": [{"type": "article", "id": "a2", "title": "相关内容"}],
                "relatedContent": [],
                "localState": {"isFavorite": true, "viewCount": 4, "learningProgressPercent": 25.0},
                "sectionStatus": [
                    {"section": "aiCard", "status": "ready"},
                    {"section": "graph", "status": "unavailable"},
                    {"section": "recommendations", "status": "ready"},
                    {"section": "relatedContent", "status": "missing"},
                    {"section": "digest", "status": "ready"}
                ]
            }
        """.trimIndent()

        val dto = HeritageJson.decodeFromString(V3ContentPageDto.serializer(), json)

        assertTrue(dto.aiCard?.isStale == true)
        assertEquals("剪纸", dto.aiCard?.keywords?.single())
        assertEquals(SectionStatus.Unavailable, dto.sectionStatus[1].status)
        assertEquals(SectionStatus.Missing, dto.sectionStatus[3].status)
        assertEquals(true, dto.localState?.isFavorite)
        assertEquals(4, dto.localState?.viewCount)
        assertEquals(1, dto.recommendations.size)
    }

    @Test
    fun `IntelligentSearchResponseDto decodes item with score breakdown`() {
        val json = """
            {
                "items": [
                    {
                        "type": "directoryItem",
                        "id": "d1",
                        "title": "Project",
                        "score": 12.5,
                        "scoreBreakdown": {"total": 12.5, "titleExact": 5.0},
                        "hasAi": true,
                        "isStale": false
                    }
                ],
                "page": 1,
                "pageSize": 20,
                "total": 1,
                "hasMore": false,
                "query": "test"
            }
        """.trimIndent()
        val dto = HeritageJson.decodeFromString(IntelligentSearchResponseDto.serializer(), json)
        assertEquals(1, dto.items.size)
        assertEquals(GraphNodeType.DirectoryItem, dto.items.first().type)
        assertNotNull(dto.items.first().scoreBreakdown)
    }

    @Test
    fun `GraphNeighborsDto decodes nodes and edges`() {
        val json = """
            {
                "center": "article:a1",
                "nodes": [
                    {"nodeKey": "article:a1", "type": "article", "title": "A"},
                    {"nodeKey": "directoryItem:d1", "type": "directoryItem", "title": "D"}
                ],
                "edges": [
                    {"from": "article:a1", "to": "directoryItem:d1", "type": "RELATED_TO", "weight": 0.8}
                ]
            }
        """.trimIndent()
        val dto = HeritageJson.decodeFromString(GraphNeighborsDto.serializer(), json)
        assertEquals(2, dto.nodes.size)
        assertEquals(1, dto.edges.size)
        assertEquals(GraphRelationType.RelatedTo, dto.edges.first().type)
    }

    @Test
    fun `GraphSimilarDto decodes shared topics`() {
        val json = """
            {
                "center": "article:a1",
                "items": [
                    {
                        "node": {"nodeKey": "article:a2", "type": "article", "title": "B"},
                        "score": 5.0,
                        "reasons": ["共享主题"],
                        "sharedTopics": ["传统音乐"],
                        "sharedNeighborCount": 1
                    }
                ]
            }
        """.trimIndent()
        val dto = HeritageJson.decodeFromString(GraphSimilarDto.serializer(), json)
        assertEquals(1, dto.items.size)
        assertEquals(listOf("传统音乐"), dto.items.first().sharedTopics)
    }

    @Test
    fun `PathExplainDto decodes found false as normal`() {
        val json = """
            {
                "path": {"found": false, "nodes": [], "edges": [], "maxDepth": 3},
                "steps": [],
                "narrative": ["未找到路径"],
                "evidence": [],
                "warnings": ["no_path_found"]
            }
        """.trimIndent()
        val dto = HeritageJson.decodeFromString(PathExplainDto.serializer(), json)
        assertNotNull(dto.path)
        assertFalse(dto.path!!.found)
        assertTrue(dto.narrative.isNotEmpty())
    }

    @Test
    fun `GraphTrailDto decodes steps`() {
        val json = """
            {
                "trailId": "t1",
                "strategy": "mixed",
                "title": "漫游",
                "steps": [
                    {"order": 1, "node": {"nodeKey": "article:a1", "type": "article", "title": "A"}, "stepType": "start", "reason": "起点"}
                ],
                "nodes": [{"nodeKey": "article:a1", "type": "article", "title": "A"}],
                "edges": [],
                "topicLabels": [],
                "score": 10.0
            }
        """.trimIndent()
        val dto = HeritageJson.decodeFromString(GraphTrailDto.serializer(), json)
        assertEquals(1, dto.steps.size)
        assertEquals(TrailStrategy.Mixed, dto.strategy)
    }

    @Test
    fun `LearningRouteDetailDto decodes sections and steps`() {
        val json = """
            {
                "routeId": "r1",
                "title": "路线",
                "difficulty": "beginner",
                "estimatedMinutes": 30,
                "sections": [
                    {"sectionId": "s1", "title": "第一节", "stepIds": ["step1"]}
                ],
                "steps": [
                    {"stepId": "step1", "order": 1, "title": "步骤", "required": true}
                ],
                "relatedRoutes": []
            }
        """.trimIndent()
        val dto = HeritageJson.decodeFromString(LearningRouteDetailDto.serializer(), json)
        assertEquals(LearningRouteDifficulty.Beginner, dto.difficulty)
        assertEquals(1, dto.sections.size)
        assertTrue(dto.steps.first().required)
    }

    @Test
    fun `SpacetimeOverviewDto decodes with empty arrays`() {
        val json = """
            {
                "filters": {"fromYear": 2000, "toYear": 2020},
                "metrics": {"total": 100, "articleCount": 10, "directoryItemCount": 80, "inheritorCount": 10},
                "topRegions": [],
                "topCategories": [],
                "yearTimeline": []
            }
        """.trimIndent()
        val dto = HeritageJson.decodeFromString(SpacetimeOverviewDto.serializer(), json)
        assertEquals(100, dto.metrics?.total)
        assertEquals(2000, dto.filters?.fromYear)
        assertTrue(dto.topRegions.isEmpty())
    }

    @Test
    fun `AnalyticsBreakdownDto decodes buckets`() {
        val json = """
            {
                "groupBy": "region",
                "targetType": "all",
                "buckets": [
                    {"key": "浙江", "label": "浙江", "articleCount": 5, "directoryItemCount": 10, "inheritorCount": 2, "total": 17}
                ],
                "total": 17
            }
        """.trimIndent()
        val dto = HeritageJson.decodeFromString(AnalyticsBreakdownDto.serializer(), json)
        assertEquals(AnalyticsDimension.Region, dto.groupBy)
        assertEquals(1, dto.buckets.size)
        assertEquals(17, dto.buckets.first().total)
    }

    @Test
    fun `RankingDetailDto decodes items and metrics`() {
        val json = """
            {
                "rankingId": "top-regions",
                "title": "热门地区",
                "items": [
                    {
                        "rank": 1,
                        "targetType": "region",
                        "targetId": "浙江",
                        "title": "浙江",
                        "score": 99.0,
                        "metrics": [{"key": "total", "label": "总计", "value": 99.0, "weight": 1.0}],
                        "reasons": ["内容丰富"]
                    }
                ]
            }
        """.trimIndent()
        val dto = HeritageJson.decodeFromString(RankingDetailDto.serializer(), json)
        assertEquals(1, dto.items.size)
        assertEquals(1, dto.items.first().metrics.size)
    }

    @Test
    fun `ResearchPackageDetailDto decodes artifacts and status`() {
        val json = """
            {
                "packageId": "p1",
                "title": "包",
                "status": "succeeded",
                "nodeCount": 10,
                "edgeCount": 20,
                "sourceCount": 5,
                "evidenceCount": 8,
                "artifacts": [
                    {"name": "manifest", "format": "json", "size": 1024}
                ]
            }
        """.trimIndent()
        val dto = HeritageJson.decodeFromString(ResearchPackageDetailDto.serializer(), json)
        assertEquals(ResearchTaskStatus.Succeeded, dto.status)
        assertEquals(1, dto.artifacts.size)
        assertEquals(1024L, dto.artifacts.first().size)
    }

    @Test
    fun `ExportRequestDto decodes with defaults`() {
        val json = """
            {
                "scopeType": "ids",
                "format": "markdown",
                "targetType": "article",
                "ids": ["a1"],
                "includeSources": true,
                "includeImages": false,
                "limit": 20
            }
        """.trimIndent()
        val dto = HeritageJson.decodeFromString(ExportRequestDto.serializer(), json)
        assertEquals(ExportScopeType.Ids, dto.scopeType)
        assertEquals(ExportFormat.Markdown, dto.format)
        assertEquals(listOf("a1"), dto.ids)
        assertFalse(dto.includeAiSummary)
    }

    @Test
    fun `V3ContentPageDto decodes section status list`() {
        val json = """
            {
                "pageType": "article",
                "sectionStatus": [
                    {"section": "aiCard", "status": "ready"},
                    {"section": "graph", "status": "unavailable"}
                ],
                "warnings": []
            }
        """.trimIndent()
        val dto = HeritageJson.decodeFromString(V3ContentPageDto.serializer(), json)
        assertEquals(GraphNodeType.Article, dto.pageType)
        assertEquals(2, dto.sectionStatus.size)
        assertEquals(SectionStatus.Ready, dto.sectionStatus.first().status)
    }
}
