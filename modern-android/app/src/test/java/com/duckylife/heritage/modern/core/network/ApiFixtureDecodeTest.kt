package com.duckylife.heritage.modern.core.network

import com.duckylife.heritage.modern.core.network.dto.advanced.ContentTargetType
import com.duckylife.heritage.modern.core.network.dto.advanced.ExportFormat
import com.duckylife.heritage.modern.core.network.dto.advanced.ExportScopeType
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphNeighborsDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphTrailDto
import com.duckylife.heritage.modern.core.network.dto.advanced.LearningRouteDetailDto
import com.duckylife.heritage.modern.core.network.dto.advanced.LearningRouteDifficulty
import com.duckylife.heritage.modern.core.network.dto.advanced.LearningRouteNextDto
import com.duckylife.heritage.modern.core.network.dto.advanced.LearningRouteSummaryDto
import com.duckylife.heritage.modern.core.network.dto.advanced.RankingDefinitionDto
import com.duckylife.heritage.modern.core.network.dto.advanced.RankingDetailDto
import com.duckylife.heritage.modern.core.network.dto.advanced.SpacetimeHeatmapDto
import com.duckylife.heritage.modern.core.network.dto.advanced.SpacetimeOverviewDto
import com.duckylife.heritage.modern.core.network.dto.advanced.V3ContentPageDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphNodeType
import com.duckylife.heritage.modern.core.network.dto.advanced.SectionStatus
import com.duckylife.heritage.modern.core.network.dto.advanced.ExportPreviewDto
import kotlinx.serialization.builtins.ListSerializer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 真实后端 payload fixture 的金色反序列化测试。
 *
 * 这些 fixture 不代表任何特定生产数据，但按后端合同构造了各高风险接口的典型字段，
 * 用于在 CI 中验证 DTO 变更不会破坏对真实响应的解码能力。
 */
class ApiFixtureDecodeTest {

    private fun loadFixture(path: String): String {
        val resource = requireNotNull(
            ApiFixtureDecodeTest::class.java.classLoader?.getResource("api-fixtures/$path"),
        ) { "Fixture not found: api-fixtures/$path" }
        return resource.readText(Charsets.UTF_8)
    }

    @Test
    fun `learning routes list fixture decodes`() {
        val json = loadFixture("learning-routes/list.json")
        val list = HeritageJson.decodeFromString(
            ListSerializer(LearningRouteSummaryDto.serializer()),
            json,
        )

        assertEquals(1, list.size)
        val route = list.single()
        assertEquals("route-papercut-101", route.routeId)
        assertEquals("剪纸艺术入门", route.title)
        assertEquals(LearningRouteDifficulty.Beginner, route.difficulty)
        assertEquals(5, route.stepCount)
        assertEquals(3, route.tags.size)
    }

    @Test
    fun `learning route detail fixture decodes with sections and steps`() {
        val json = loadFixture("learning-routes/detail.json")
        val detail = HeritageJson.decodeFromString(
            LearningRouteDetailDto.serializer(),
            json,
        )

        assertEquals("route-papercut-101", detail.routeId)
        assertEquals(2, detail.sections.size)
        assertEquals(2, detail.steps.size)
        val firstStep = detail.steps.first()
        assertEquals("step-001", firstStep.stepId)
        assertEquals(1, firstStep.order)
        assertNotNull(firstStep.content)
        assertEquals("剪纸的起源", firstStep.content?.title)
    }

    @Test
    fun `learning route next fixture decodes next step and related routes`() {
        val json = loadFixture("learning-routes/next.json")
        val next = HeritageJson.decodeFromString(LearningRouteNextDto.serializer(), json)

        assertEquals("route-papercut-101", next.routeId)
        assertFalse(next.completed)
        assertNotNull(next.nextStep)
        assertEquals("step-002", next.nextStep?.stepId)
        assertEquals(1, next.relatedRoutes.size)
        assertEquals("route-papercut-201", next.relatedRoutes.single().routeId)
    }

    @Test
    fun `spacetime overview fixture decodes metrics and timelines`() {
        val json = loadFixture("spacetime/overview.json")
        val overview = HeritageJson.decodeFromString(SpacetimeOverviewDto.serializer(), json)

        assertNotNull(overview.filters)
        assertEquals(1900, overview.filters?.fromYear)
        assertEquals(ContentTargetType.All, overview.filters?.targetType)
        assertEquals(1240, overview.metrics?.total)
        assertEquals(820, overview.metrics?.articleCount)
        assertTrue(overview.topRegions.isNotEmpty())
        assertTrue(overview.yearTimeline.isNotEmpty())
        val firstYear = overview.yearTimeline.first()
        assertEquals(2006, firstYear.year)
        assertTrue(firstYear.count > 0)
    }

    @Test
    fun `spacetime heatmap fixture decodes cells with counts and intensity`() {
        val json = loadFixture("spacetime/heatmap.json")
        val heatmap = HeritageJson.decodeFromString(SpacetimeHeatmapDto.serializer(), json)

        assertEquals(2, heatmap.cells.size)
        val firstCell = heatmap.cells.first()
        assertEquals("浙江", firstCell.xKey)
        assertEquals("剪纸", firstCell.yKey)
        assertTrue(firstCell.total > 0)
        assertTrue(firstCell.intensity > 0.0)
    }

    @Test
    fun `rankings list fixture decodes multiple ranking definitions`() {
        val json = loadFixture("rankings/list.json")
        val list = HeritageJson.decodeFromString(
            ListSerializer(RankingDefinitionDto.serializer()),
            json,
        )

        assertEquals(2, list.size)
        val first = list.first()
        assertEquals("most-connected", first.rankingId)
        assertEquals("最多关联内容", first.title)
        assertEquals(ContentTargetType.All, first.targetType)
    }

    @Test
    fun `ranking detail fixture decodes items with metrics and content refs`() {
        val json = loadFixture("rankings/detail.json")
        val detail = HeritageJson.decodeFromString(RankingDetailDto.serializer(), json)

        assertEquals("most-connected", detail.rankingId)
        assertTrue(detail.items.isNotEmpty())
        val first = detail.items.first()
        assertEquals(1, first.rank)
        assertEquals("剪纸艺术", first.title)
        assertTrue(first.metrics.isNotEmpty())
        assertNotNull(first.content)
        assertEquals("507f1f77bcf86cd799439011", first.content?.id)
    }

    @Test
    fun `graph neighbors fixture decodes nodes edges and generatedAt`() {
        val json = loadFixture("graph/neighbors.json")
        val neighbors = HeritageJson.decodeFromString(GraphNeighborsDto.serializer(), json)

        assertEquals("article:507f1f77bcf86cd799439011", neighbors.center)
        assertEquals(3, neighbors.nodes.size)
        assertEquals(2, neighbors.edges.size)
        val firstEdge = neighbors.edges.first()
        assertTrue(firstEdge.from.isNotBlank())
        assertTrue(firstEdge.to.isNotBlank())
        assertNotNull(firstEdge.type)
    }

    @Test
    fun `graph trail wrapper fixture decodes strategy steps and topic labels`() {
        val json = loadFixture("graph/trail-wrapper.json")
        val trail = HeritageJson.decodeFromString(GraphTrailDto.serializer(), json)

        assertEquals("trail-papercut-to-newyear", trail.trailId)
        assertNotNull(trail.startNode)
        assertNotNull(trail.endNode)
        assertTrue(trail.steps.size >= 2)
        assertTrue(trail.topicLabels.isNotEmpty())
        assertTrue(trail.score > 0.0)
    }

    @Test
    fun `v3 content page fixture decodes mixed section statuses and local state`() {
        val json = loadFixture("v3-content/page.json")
        val page = HeritageJson.decodeFromString(V3ContentPageDto.serializer(), json)

        assertEquals(GraphNodeType.Article, page.pageType)
        assertEquals(true, page.aiCard?.hasAi)
        assertEquals("剪纸艺术", page.content?.get("title")?.toString()?.trim('\"'))
        assertEquals(true, page.localState?.isFavorite)
        assertEquals(5, page.localState?.viewCount)
        assertEquals(1, page.recommendations.size)
        assertEquals(1, page.relatedContent.size)
        assertEquals(1, page.warnings.size)
        assertEquals("ai_partial", page.warnings.first().code)
        val digest = requireNotNull(page.digest)
        assertEquals(2, digest.keyFacts.size)
        assertEquals(3, digest.highlights.size)
        val statusMap = page.sectionStatus.associate { it.section.lowercase() to it.status }
        assertEquals(SectionStatus.Ready, statusMap["aicard"])
        assertEquals(SectionStatus.Ready, statusMap["digest"])
        assertEquals(SectionStatus.Empty, statusMap["graph"])
    }

    @Test
    fun `export preview fixture decodes sample items and warnings`() {
        val json = loadFixture("exports/preview.json")
        val preview = HeritageJson.decodeFromString(ExportPreviewDto.serializer(), json)

        assertEquals(ExportScopeType.Ids, preview.scopeType)
        assertEquals(ExportFormat.Markdown, preview.format)
        assertEquals(3, preview.estimatedItemCount)
        assertTrue(preview.warnings.isNotEmpty())
        assertTrue(preview.sampleItems.isNotEmpty())
        val first = preview.sampleItems.first()
        assertEquals("剪纸艺术", first.title)
        assertTrue(first.sourceUrl?.contains("507f1f77bcf86cd799439011") == true)
    }
}
