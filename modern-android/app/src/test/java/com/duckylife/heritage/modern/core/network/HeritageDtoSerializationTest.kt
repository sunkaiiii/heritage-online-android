package com.duckylife.heritage.modern.core.network

import com.duckylife.heritage.modern.core.network.dto.CollectionDto
import com.duckylife.heritage.modern.core.network.dto.DetailContextDto
import com.duckylife.heritage.modern.core.network.dto.ExploreTopicV2Dto
import com.duckylife.heritage.modern.core.network.dto.RecommendationDto
import com.duckylife.heritage.modern.core.network.dto.RegionAtlasDto
import com.duckylife.heritage.modern.core.network.dto.SearchV2ResponseDto
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HeritageDtoSerializationTest {

    private val json = HeritageJson

    @Test
    fun detailContextDto_defaultsOnEmptyJson() {
        val dto = json.decodeFromString<DetailContextDto>("{}")
        assertEquals(emptyList<Any>(), dto.related)
        assertEquals(null, dto.graph)
        assertEquals(emptyList<Any>(), dto.collections)
        assertEquals(emptyList<Any>(), dto.exploreTopics)
        assertEquals(emptyList<Any>(), dto.recommendations)
        assertEquals(emptyList<Any>(), dto.semanticRecommendations)
    }

    @Test
    fun detailContextDto_parsesFullPayload() {
        val dto = json.decodeFromString<DetailContextDto>(
            """
            {
              "related": [
                { "id": "r1", "type": "article", "title": "关联文章" }
              ],
              "graph": {
                "nodes": [
                  { "id": "n1", "label": "节点1", "type": "article" }
                ],
                "edges": [
                  { "sourceId": "n1", "targetId": "n2", "relationType": "related" }
                ]
              },
              "collections": [
                { "id": "c1", "name": "合集", "items": [{ "id": "i1", "title": "内容" }] }
              ],
              "recommendations": [
                { "id": "rec1", "source": "semantic", "weight": 0.9, "title": "推荐" }
              ],
              "semanticRecommendations": [
                { "id": "sr1", "source": "graph", "weight": 0.7 }
              ]
            }
            """.trimIndent(),
        )

        assertEquals(1, dto.related.size)
        assertEquals("关联文章", dto.related.first().title)
        assertNotNull(dto.graph)
        assertEquals(1, dto.graph!!.nodes.size)
        assertEquals(1, dto.graph!!.edges.size)
        assertEquals(1, dto.collections.size)
        assertEquals(1, dto.collections.first().items.size)
        assertEquals(1, dto.recommendations.size)
        assertEquals(0.9, dto.recommendations.first().weight, 0.001)
        assertEquals(1, dto.semanticRecommendations.size)
    }

    @Test
    fun searchV2ResponseDto_parsesAllFields() {
        val dto = json.decodeFromString<SearchV2ResponseDto>(
            """
            {
              "items": [
                {
                  "id": "s1",
                  "type": "article",
                  "title": "搜索结果",
                  "summary": "摘要",
                  "region": "浙江",
                  "category": "传统技艺",
                  "kind": "nationalProject",
                  "publishedYear": 2020,
                  "score": 0.95,
                  "coverImage": { "displayUrl": "https://example.test/img.jpg" }
                }
              ],
              "facets": {
                "categories": [
                  { "key": "传统技艺", "name": "传统技艺", "count": 50 }
                ],
                "regions": [],
                "kinds": [],
                "years": []
              },
              "page": 1,
              "pageSize": 10,
              "total": 100,
              "hasMore": true
            }
            """.trimIndent(),
        )

        assertEquals(1, dto.items.size)
        assertEquals("搜索结果", dto.items.first().title)
        assertEquals(0.95, dto.items.first().score, 0.001)
        assertEquals("浙江", dto.items.first().region)
        assertNotNull(dto.facets)
        assertEquals(1, dto.facets!!.categories.size)
        assertEquals(50, dto.facets!!.categories.first().count)
        assertEquals(1, dto.page)
        assertEquals(10, dto.pageSize)
        assertEquals(100, dto.total)
        assertTrue(dto.hasMore)
    }

    @Test
    fun searchV2ResponseDto_defaultsOnEmptyJson() {
        val dto = json.decodeFromString<SearchV2ResponseDto>("{}")
        assertEquals(emptyList<Any>(), dto.items)
        assertEquals(null, dto.facets)
        assertEquals(1, dto.page)
        assertEquals(20, dto.pageSize)
        assertEquals(0, dto.total)
        assertFalse(dto.hasMore)
    }

    @Test
    fun exploreTopicV2Dto_parsesNestedSections() {
        val dto = json.decodeFromString<ExploreTopicV2Dto>(
            """
            {
              "type": "category",
              "key": "传统技艺",
              "name": "传统技艺",
              "description": "描述",
              "stats": [
                { "name": "项目数", "value": 120 }
              ],
              "sections": [
                {
                  "heading": "代表性项目",
                  "items": [
                    { "id": "e1", "type": "directoryItem", "title": "景德镇陶瓷" },
                    { "id": "e2", "type": "directoryItem", "title": "宜兴紫砂" }
                  ]
                },
                {
                  "heading": "传承人",
                  "items": [
                    { "id": "h1", "type": "inheritor", "title": "传承人张三" }
                  ]
                }
              ]
            }
            """.trimIndent(),
        )

        assertEquals("传统技艺", dto.name)
        assertEquals(1, dto.stats.size)
        assertEquals(120, dto.stats.first().value)
        assertEquals(2, dto.sections.size)
        assertEquals("代表性项目", dto.sections.first().heading)
        assertEquals(2, dto.sections.first().items.size)
        assertEquals("景德镇陶瓷", dto.sections.first().items.first().title)
        assertEquals(1, dto.sections[1].items.size)
    }

    @Test
    fun regionAtlasDto_parsesRegionsAndTotals() {
        val dto = json.decodeFromString<RegionAtlasDto>(
            """
            {
              "regions": [
                { "region": "浙江省", "count": 42 },
                { "region": "江苏省", "count": 38 }
              ],
              "totals": { "regions": 31, "items": 1500 },
              "generatedAt": "2026-05-20T10:00:00Z"
            }
            """.trimIndent(),
        )

        assertEquals(2, dto.regions.size)
        assertEquals("浙江省", dto.regions.first().region)
        assertEquals(42, dto.regions.first().count)
        assertNotNull(dto.totals)
        assertEquals(31, dto.totals!!.regions)
        assertEquals(1500, dto.totals!!.items)
        assertNotNull(dto.generatedAt)
    }

    @Test
    fun collectionDto_parsesItems() {
        val dto = json.decodeFromString<CollectionDto>(
            """
            {
              "id": "col1",
              "name": "精选集",
              "description": "描述",
              "items": [
                { "id": "ci1", "type": "article", "title": "内容1" },
                { "id": "ci2", "type": "directoryItem", "title": "内容2" }
              ],
              "total": 2,
              "hasMore": false
            }
            """.trimIndent(),
        )

        assertEquals("精选集", dto.name)
        assertEquals(2, dto.items.size)
        assertEquals("内容1", dto.items.first().title)
        assertEquals("内容2", dto.items[1].title)
        assertEquals(2, dto.total)
        assertFalse(dto.hasMore)
    }

    @Test
    fun recommendationDto_parsesAllFields() {
        val dto = json.decodeFromString<RecommendationDto>(
            """
            {
              "id": "rec1",
              "source": "semantic",
              "relationType": "similar",
              "reason": "同属传统技艺",
              "weight": 0.85,
              "title": "推荐项目",
              "summary": "摘要",
              "coverImage": { "displayUrl": "https://example.test/img.jpg" },
              "sourceUrl": "https://example.test/source"
            }
            """.trimIndent(),
        )

        assertEquals("rec1", dto.id)
        assertEquals("semantic", dto.source)
        assertEquals("similar", dto.relationType)
        assertEquals("同属传统技艺", dto.reason)
        assertEquals(0.85, dto.weight, 0.001)
        assertEquals("推荐项目", dto.title)
        assertNotNull(dto.coverImage)
        assertEquals("https://example.test/img.jpg", dto.coverImage!!.displayUrl)
    }
}
