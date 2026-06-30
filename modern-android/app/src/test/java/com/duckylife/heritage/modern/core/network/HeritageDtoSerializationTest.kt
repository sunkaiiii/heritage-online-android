package com.duckylife.heritage.modern.core.network

import com.duckylife.heritage.modern.core.network.dto.BlendedRecommendationResponseDto
import com.duckylife.heritage.modern.core.network.dto.CollectionDto
import com.duckylife.heritage.modern.core.network.dto.CompareResultDto
import com.duckylife.heritage.modern.core.network.dto.ContentDigestDto
import com.duckylife.heritage.modern.core.network.dto.DataStoryDto
import com.duckylife.heritage.modern.core.network.dto.DetailContextDto
import com.duckylife.heritage.modern.core.network.dto.DiscoveryDeepDiveDto
import com.duckylife.heritage.modern.core.network.dto.DiscoveryTodayDto
import com.duckylife.heritage.modern.core.network.dto.DiscoveryTrendingDto
import com.duckylife.heritage.modern.core.network.dto.DiscoveryWeeklyDto
import com.duckylife.heritage.modern.core.network.dto.ExploreIndexDto
import com.duckylife.heritage.modern.core.network.dto.ExploreTopicV2Dto
import com.duckylife.heritage.modern.core.network.dto.LearningPathDetailDto
import com.duckylife.heritage.modern.core.network.dto.LearningPathDto
import com.duckylife.heritage.modern.core.network.dto.RecommendationDto
import com.duckylife.heritage.modern.core.network.dto.RegionAtlasDetailDto
import com.duckylife.heritage.modern.core.network.dto.RegionAtlasDto
import com.duckylife.heritage.modern.core.network.dto.SearchV2ResponseDto
import com.duckylife.heritage.modern.core.network.dto.TaxonomyCategoryDetailDto
import com.duckylife.heritage.modern.core.network.dto.TaxonomyIndexDto
import com.duckylife.heritage.modern.core.network.dto.TaxonomyKindDto
import com.duckylife.heritage.modern.core.network.dto.TaxonomyRegionDetailDto
import com.duckylife.heritage.modern.core.network.dto.TaxonomyTopicDto
import com.duckylife.heritage.modern.core.network.dto.TimelineV2ResponseDto
import com.duckylife.heritage.modern.core.network.dto.TimelineYearBucketDto
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HeritageDtoSerializationTest {

    private val json = HeritageJson

    @Test
    fun searchV2ResponseDto_parsesBackendShape() {
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
                  "score": 95,
                  "coverImage": { "displayUrl": "https://example.test/img.jpg" },
                  "highlights": ["高亮1"],
                  "matchedFields": ["title"]
                }
              ],
              "facets": {
                "types": [{ "key": "article", "count": 50 }],
                "categories": [{ "key": "传统技艺", "count": 50 }],
                "regions": [{ "key": "浙江", "count": 30 }],
                "kinds": [{ "key": "nationalProject", "count": 40 }],
                "years": [{ "key": "2020", "count": 20 }]
              },
              "page": 1,
              "pageSize": 10,
              "total": 100,
              "hasMore": true,
              "query": "陶瓷"
            }
            """.trimIndent(),
        )

        assertEquals(1, dto.items.size)
        assertEquals("搜索结果", dto.items.first().title)
        assertEquals(95, dto.items.first().score)
        assertEquals("浙江", dto.items.first().region)
        assertEquals("传统技艺", dto.items.first().category)
        assertEquals("nationalProject", dto.items.first().kind)
        assertEquals(2020, dto.items.first().publishedYear)
        assertNotNull(dto.items.first().coverImage)
        assertEquals("https://example.test/img.jpg", dto.items.first().coverImage!!.displayUrl)
        assertEquals(1, dto.items.first().highlights.size)
        assertEquals("高亮1", dto.items.first().highlights.first())
        assertEquals(1, dto.items.first().matchedFields.size)
        assertEquals("title", dto.items.first().matchedFields.first())

        assertNotNull(dto.facets)
        assertEquals(1, dto.facets!!.types.size)
        assertEquals("article", dto.facets!!.types.first().key)
        assertEquals(50, dto.facets!!.types.first().count)
        assertEquals(1, dto.facets!!.categories.size)
        assertEquals(1, dto.facets!!.regions.size)
        assertEquals(1, dto.facets!!.kinds.size)
        assertEquals(1, dto.facets!!.years.size)

        assertEquals(1, dto.page)
        assertEquals(10, dto.pageSize)
        assertEquals(100, dto.total)
        assertTrue(dto.hasMore)
        assertEquals("陶瓷", dto.query)
    }

    @Test
    fun timelineV2ResponseDto_parsesBackendShape() {
        val dto = json.decodeFromString<TimelineV2ResponseDto>(
            """
            {
              "items": [
                {
                  "id": "t1",
                  "type": "article",
                  "title": "时间线项目",
                  "summary": "摘要",
                  "category": "传统技艺",
                  "kind": "nationalProject",
                  "region": "浙江",
                  "date": "2020-01-01",
                  "year": 2020,
                  "coverImage": { "displayUrl": "https://example.test/img.jpg" },
                  "sourceUrl": "https://example.test/source"
                }
              ],
              "facets": {
                "types": [{ "key": "article", "count": 5 }],
                "categories": [{ "key": "传统技艺", "count": 10 }],
                "regions": [{ "key": "浙江", "count": 8 }],
                "kinds": [{ "key": "nationalProject", "count": 12 }]
              },
              "page": 1,
              "pageSize": 20,
              "total": 50,
              "hasMore": true
            }
            """.trimIndent(),
        )

        assertEquals(1, dto.items.size)
        assertEquals("时间线项目", dto.items.first().title)
        assertEquals("article", dto.items.first().type)
        assertEquals("传统技艺", dto.items.first().category)
        assertEquals("nationalProject", dto.items.first().kind)
        assertEquals("浙江", dto.items.first().region)
        assertEquals("2020-01-01", dto.items.first().date)
        assertEquals(2020, dto.items.first().year)
        assertNotNull(dto.items.first().coverImage)
        assertEquals("https://example.test/source", dto.items.first().sourceUrl)

        assertNotNull(dto.facets)
        assertEquals(1, dto.facets!!.types.size)
        assertEquals("article", dto.facets!!.types.first().key)
        assertEquals(5, dto.facets!!.types.first().count)
        assertEquals(1, dto.facets!!.categories.size)
        assertEquals(1, dto.facets!!.regions.size)
        assertEquals(1, dto.facets!!.kinds.size)

        assertEquals(1, dto.page)
        assertEquals(20, dto.pageSize)
        assertEquals(50, dto.total)
        assertTrue(dto.hasMore)
    }

    @Test
    fun timelineYearBucketDto_parsesBackendShape() {
        val dto = json.decodeFromString<TimelineYearBucketDto>(
            """
            {
              "year": 2020,
              "total": 100,
              "articleCount": 50,
              "directoryItemCount": 30,
              "inheritorCount": 20
            }
            """.trimIndent(),
        )

        assertEquals(2020, dto.year)
        assertEquals(100, dto.total)
        assertEquals(50, dto.articleCount)
        assertEquals(30, dto.directoryItemCount)
        assertEquals(20, dto.inheritorCount)
    }

    @Test
    fun exploreIndexDto_parsesBackendShape() {
        val dto = json.decodeFromString<ExploreIndexDto>(
            """
            {
              "regions": [
                { "type": "region", "key": "浙江", "title": "浙江", "subtitle": "描述" },
                { "type": "region", "key": "江苏", "title": "江苏", "subtitle": "描述" }
              ],
              "categories": [
                { "type": "category", "key": "传统技艺", "title": "传统技艺", "subtitle": "描述" }
              ],
              "years": [
                { "type": "year", "key": "2020", "title": "2020", "subtitle": "描述" }
              ]
            }
            """.trimIndent(),
        )

        assertEquals(2, dto.regions.size)
        assertEquals("浙江", dto.regions.first().key)
        assertEquals("浙江", dto.regions.first().title)
        assertEquals(1, dto.categories.size)
        assertEquals("传统技艺", dto.categories.first().key)
        assertEquals(1, dto.years.size)
        assertEquals("2020", dto.years.first().key)
    }

    @Test
    fun exploreTopicV2Dto_parsesBackendShape() {
        val dto = json.decodeFromString<ExploreTopicV2Dto>(
            """
            {
              "topic": {
                "type": "category",
                "key": "传统技艺",
                "title": "传统技艺",
                "subtitle": "描述"
              },
              "stats": [
                { "name": "项目数", "value": 120 },
                { "name": "传承人数", "value": 50 }
              ],
              "sections": [
                {
                  "id": "featured",
                  "title": "代表性项目",
                  "subtitle": "精选内容",
                  "items": [
                    { "id": "e1", "type": "directoryItem", "title": "景德镇陶瓷", "count": 10 },
                    { "id": "e2", "type": "directoryItem", "title": "宜兴紫砂", "count": 5 }
                  ]
                },
                {
                  "id": "inheritors",
                  "title": "传承人",
                  "items": [
                    { "id": "h1", "type": "inheritor", "title": "传承人张三" }
                  ]
                }
              ],
              "relatedTopics": [
                { "type": "category", "key": "传统美术", "title": "传统美术" }
              ],
              "timeline": [
                { "id": "t1", "type": "article", "title": "时间线项" }
              ],
              "generatedAt": "2026-05-20T10:00:00Z"
            }
            """.trimIndent(),
        )

        assertNotNull(dto.topic)
        assertEquals("category", dto.topic!!.type)
        assertEquals("传统技艺", dto.topic!!.key)
        assertEquals("传统技艺", dto.topic!!.title)
        assertEquals("描述", dto.topic!!.subtitle)

        assertEquals(2, dto.stats.size)
        assertEquals("项目数", dto.stats.first().name)
        assertEquals(120, dto.stats.first().value)
        assertEquals("传承人数", dto.stats[1].name)
        assertEquals(50, dto.stats[1].value)

        assertEquals(2, dto.sections.size)
        assertEquals("featured", dto.sections.first().id)
        assertEquals("代表性项目", dto.sections.first().title)
        assertEquals("精选内容", dto.sections.first().subtitle)
        assertEquals(2, dto.sections.first().items.size)
        assertEquals("景德镇陶瓷", dto.sections.first().items.first().title)
        assertEquals("宜兴紫砂", dto.sections.first().items[1].title)
        assertEquals(1, dto.sections[1].items.size)
        assertEquals("传承人张三", dto.sections[1].items.first().title)

        assertEquals(1, dto.relatedTopics.size)
        assertEquals("传统美术", dto.relatedTopics.first().title)
        assertEquals(1, dto.timeline.size)
        assertEquals("时间线项", dto.timeline.first().title)
        assertNotNull(dto.generatedAt)
    }

    @Test
    fun learningPathDto_parsesBackendShape() {
        val dto = json.decodeFromString<LearningPathDto>(
            """
            {
              "id": "lp1",
              "title": "非遗入门",
              "subtitle": "学习路径",
              "topics": [
                { "type": "category", "key": "传统技艺", "title": "传统技艺" }
              ],
              "description": "描述",
              "coverImage": "https://example.test/cover.jpg",
              "estimatedItemCount": 20,
              "stepCount": 5,
              "tags": ["入门", "推荐"]
            }
            """.trimIndent(),
        )

        assertEquals("lp1", dto.id)
        assertEquals("非遗入门", dto.title)
        assertEquals("学习路径", dto.subtitle)
        assertEquals(1, dto.topics.size)
        assertEquals("传统技艺", dto.topics.first().title)
        assertEquals("描述", dto.description)
        assertEquals("https://example.test/cover.jpg", dto.coverImage)
        assertEquals(20, dto.estimatedItemCount)
        assertEquals(5, dto.stepCount)
        assertEquals(2, dto.tags.size)
        assertEquals("入门", dto.tags.first())
    }

    @Test
    fun learningPathDetailDto_parsesBackendShape() {
        val dto = json.decodeFromString<LearningPathDetailDto>(
            """
            {
              "id": "lp1",
              "title": "非遗入门",
              "subtitle": "学习路径",
              "description": "描述",
              "tags": ["入门"],
              "steps": [
                {
                  "id": "s1",
                  "title": "第一步",
                  "subtitle": "了解基础",
                  "topic": { "type": "category", "key": "传统技艺", "title": "传统技艺" },
                  "items": [
                    { "id": "li1", "type": "directoryItem", "title": "学习项目" }
                  ]
                }
              ],
              "featuredItems": [
                { "id": "fi1", "type": "article", "title": "精选内容" }
              ],
              "relatedTopics": [
                { "type": "category", "key": "传统美术", "title": "传统美术" }
              ],
              "generatedAt": "2026-05-20T10:00:00Z"
            }
            """.trimIndent(),
        )

        assertEquals("lp1", dto.id)
        assertEquals("非遗入门", dto.title)
        assertEquals("学习路径", dto.subtitle)
        assertEquals("描述", dto.description)
        assertEquals(1, dto.tags.size)

        assertEquals(1, dto.steps.size)
        assertEquals("s1", dto.steps.first().id)
        assertEquals("第一步", dto.steps.first().title)
        assertEquals("了解基础", dto.steps.first().subtitle)
        assertNotNull(dto.steps.first().topic)
        assertEquals("传统技艺", dto.steps.first().topic!!.title)
        assertEquals(1, dto.steps.first().items.size)
        assertEquals("学习项目", dto.steps.first().items.first().title)

        assertEquals(1, dto.featuredItems.size)
        assertEquals("精选内容", dto.featuredItems.first().title)
        assertEquals(1, dto.relatedTopics.size)
        assertEquals("传统美术", dto.relatedTopics.first().title)
        assertNotNull(dto.generatedAt)
    }

    @Test
    fun learningPathDetailDto_parsesBackendShapeWithNullCount() {
        val dto = json.decodeFromString<LearningPathDetailDto>(
            """
            {
              "id": "mixed-starter",
              "title": "随便看看",
              "subtitle": "浏览热门地区、类别与年份",
              "description": "从热门地区、类别和最新年份入手，快速了解非遗文化概貌",
              "tags": ["入门", "综合"],
              "steps": [
                {
                  "id": "hot-region",
                  "title": "热门地区",
                  "subtitle": "浙江",
                  "topic": { "type": "region", "key": "浙江", "title": "热门地区" },
                  "items": [
                    {
                      "type": "directoryItem",
                      "id": "6a13f2039e6a99b63880301f",
                      "title": "Page Test 0",
                      "summary": null,
                      "category": "test",
                      "region": "浙江",
                      "kind": "nationalProject",
                      "year": 2024,
                      "count": null,
                      "coverImage": null,
                      "sourceUrl": "http://test/page_0"
                    }
                  ]
                }
              ],
              "featuredItems": [
                {
                  "id": "fi1",
                  "type": "directoryItem",
                  "title": "精选内容",
                  "count": null,
                  "coverImage": null
                }
              ],
              "relatedTopics": [
                { "type": "region", "key": "江苏", "title": "江苏" }
              ]
            }
            """.trimIndent(),
        )

        assertEquals("mixed-starter", dto.id)
        assertEquals("随便看看", dto.title)
        assertEquals(1, dto.steps.size)
        val item = dto.steps.first().items.first()
        assertEquals("Page Test 0", item.title)
        assertNull(item.count)
        assertNull(item.coverImage)
        assertEquals("test", item.category)
        assertEquals("浙江", item.region)
        assertEquals("nationalProject", item.kind)
        assertEquals(2024, item.year)

        val featured = dto.featuredItems.first()
        assertNull(featured.count)
        assertNull(featured.coverImage)
    }

    @Test
    fun regionAtlasDetailDto_parsesBackendShapeWithNullFacetCount() {
        val dto = json.decodeFromString<RegionAtlasDetailDto>(
            """
            {
              "region": "山西",
              "displayName": "山西",
              "stats": {
                "directoryItemCount": 41,
                "inheritorCount": 197,
                "total": 238
              },
              "categoryBreakdown": [
                { "key": "传统音乐", "name": "传统音乐", "value": 18 }
              ],
              "kindBreakdown": [
                { "key": "nationalProject", "name": "nationalProject", "value": 40 }
              ],
              "featuredDirectoryItems": [
                { "id": "ra1", "kind": "nationalProject", "title": "项目1", "region": "山西省" }
              ],
              "featuredInheritors": [],
              "relatedArticles": [],
              "relatedRegions": []
            }
            """.trimIndent(),
        )

        assertEquals("山西", dto.region)
        assertEquals("山西", dto.displayName)
        assertEquals(41, dto.stats!!.directoryItemCount)
        assertEquals(197, dto.stats!!.inheritorCount)
        assertEquals(238, dto.stats!!.total)
        assertEquals(1, dto.categoryBreakdown.size)
        assertEquals("传统音乐", dto.categoryBreakdown.first().key)
        assertEquals(1, dto.kindBreakdown.size)
        assertEquals("nationalProject", dto.kindBreakdown.first().key)
        assertEquals(1, dto.featuredDirectoryItems.size)
        assertEquals("项目1", dto.featuredDirectoryItems.first().title)
    }

    @Test
    fun regionAtlasDto_parsesBackendShape() {
        val dto = json.decodeFromString<RegionAtlasDto>(
            """
            {
              "regions": [
                {
                  "region": "浙江省",
                  "displayName": "浙江",
                  "directoryItemCount": 30,
                  "inheritorCount": 12,
                  "total": 42,
                  "topCategories": [{ "key": "传统技艺", "count": 15 }],
                  "topKinds": [{ "key": "nationalProject", "count": 20 }],
                  "coverImage": { "displayUrl": "https://example.test/img.jpg" }
                },
                {
                  "region": "江苏省",
                  "displayName": "江苏",
                  "directoryItemCount": 28,
                  "inheritorCount": 10,
                  "total": 38
                }
              ],
              "totals": {
                "directoryItemCount": 1000,
                "inheritorCount": 500,
                "regionCount": 31
              },
              "generatedAt": "2026-05-20T10:00:00Z"
            }
            """.trimIndent(),
        )

        assertEquals(2, dto.regions.size)
        assertEquals("浙江省", dto.regions.first().region)
        assertEquals("浙江", dto.regions.first().displayName)
        assertEquals(42, dto.regions.first().total)
        assertEquals(30, dto.regions.first().directoryItemCount)
        assertEquals(12, dto.regions.first().inheritorCount)
        assertEquals(1, dto.regions.first().topCategories.size)
        assertEquals("传统技艺", dto.regions.first().topCategories.first().key)
        assertEquals(1, dto.regions.first().topKinds.size)
        assertNotNull(dto.regions.first().coverImage)

        assertNotNull(dto.totals)
        assertEquals(31, dto.totals!!.regionCount)
        assertEquals(1000, dto.totals!!.directoryItemCount)
        assertEquals(500, dto.totals!!.inheritorCount)
        assertNotNull(dto.generatedAt)
    }

    @Test
    fun regionAtlasDetailDto_parsesBackendShape() {
        val dto = json.decodeFromString<RegionAtlasDetailDto>(
            """
            {
              "region": "浙江省",
              "displayName": "浙江",
              "stats": {
                "directoryItemCount": 40,
                "inheritorCount": 2,
                "total": 42
              },
              "categoryBreakdown": [
                { "key": "传统技艺", "count": 15 },
                { "key": "传统美术", "count": 10 }
              ],
              "kindBreakdown": [
                { "key": "nationalProject", "count": 20 }
              ],
              "featuredDirectoryItems": [
                { "id": "ra1", "title": "西湖龙井" }
              ],
              "featuredInheritors": [
                { "id": "ri1", "name": "传承人" }
              ],
              "relatedArticles": [
                { "id": "art1", "title": "相关文章" }
              ],
              "timeline": [
                { "id": "t1", "type": "article", "title": "时间线" }
              ],
              "relatedRegions": [
                { "type": "region", "key": "江苏省", "title": "江苏" }
              ],
              "generatedAt": "2026-05-20T10:00:00Z"
            }
            """.trimIndent(),
        )

        assertEquals("浙江省", dto.region)
        assertEquals("浙江", dto.displayName)

        assertNotNull(dto.stats)
        assertEquals(40, dto.stats!!.directoryItemCount)
        assertEquals(2, dto.stats!!.inheritorCount)
        assertEquals(42, dto.stats!!.total)

        assertEquals(2, dto.categoryBreakdown.size)
        assertEquals("传统技艺", dto.categoryBreakdown.first().key)
        assertEquals(15, dto.categoryBreakdown.first().count)
        assertEquals(1, dto.kindBreakdown.size)
        assertEquals("nationalProject", dto.kindBreakdown.first().key)

        assertEquals(1, dto.featuredDirectoryItems.size)
        assertEquals("西湖龙井", dto.featuredDirectoryItems.first().title)
        assertEquals(1, dto.featuredInheritors.size)
        assertEquals("传承人", dto.featuredInheritors.first().name)
        assertEquals(1, dto.relatedArticles.size)
        assertEquals("相关文章", dto.relatedArticles.first().title)
        assertEquals(1, dto.timeline.size)
        assertEquals("时间线", dto.timeline.first().title)
        assertEquals(1, dto.relatedRegions.size)
        assertEquals("江苏", dto.relatedRegions.first().title)
        assertNotNull(dto.generatedAt)
    }

    @Test
    fun collectionDto_parsesBackendShape() {
        val dto = json.decodeFromString<CollectionDto>(
            """
            {
              "id": "col1",
              "title": "精选集",
              "subtitle": "描述",
              "type": "curated",
              "tags": ["精选", "推荐"],
              "items": [
                { "id": "ci1", "type": "article", "title": "内容1", "category": "news", "region": "浙江" },
                { "id": "ci2", "type": "directoryItem", "title": "内容2", "kind": "nationalProject" }
              ],
              "generatedAt": "2026-05-20T10:00:00Z"
            }
            """.trimIndent(),
        )

        assertEquals("col1", dto.id)
        assertEquals("精选集", dto.title)
        assertEquals("描述", dto.subtitle)
        assertEquals("curated", dto.type)
        assertEquals(2, dto.tags.size)
        assertEquals("精选", dto.tags.first())
        assertEquals(2, dto.items.size)
        assertEquals("内容1", dto.items.first().title)
        assertEquals("article", dto.items.first().type)
        assertEquals("news", dto.items.first().category)
        assertEquals("浙江", dto.items.first().region)
        assertEquals("内容2", dto.items[1].title)
        assertEquals("directoryItem", dto.items[1].type)
        assertNotNull(dto.generatedAt)
    }

    @Test
    fun detailContextDto_parsesBackendShape() {
        val dto = json.decodeFromString<DetailContextDto>(
            """
            {
              "related": [
                { "id": "r1", "type": "article", "title": "关联文章", "category": "news", "region": "浙江" }
              ],
              "graph": {
                "nodes": [
                  { "id": "n1", "title": "节点1", "type": "article", "category": "news", "region": "浙江" }
                ],
                "edges": [
                  { "from": "n1", "to": "n2", "relationType": "related", "weight": 0.8 }
                ]
              },
              "collections": [
                { "id": "c1", "title": "合集", "items": [{ "id": "i1", "title": "内容" }] }
              ],
              "exploreTopics": [
                { "type": "category", "key": "传统技艺", "title": "传统技艺" }
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
        assertEquals("article", dto.related.first().type)
        assertEquals("news", dto.related.first().category)
        assertEquals("浙江", dto.related.first().region)

        assertNotNull(dto.graph)
        assertEquals(1, dto.graph!!.nodes.size)
        assertEquals("节点1", dto.graph!!.nodes.first().title)
        assertEquals(1, dto.graph!!.edges.size)
        assertEquals("n1", dto.graph!!.edges.first().fromId)
        assertEquals("n2", dto.graph!!.edges.first().toId)
        assertEquals("related", dto.graph!!.edges.first().relationType)
        assertEquals(0.8, dto.graph!!.edges.first().weight, 0.001)

        assertEquals(1, dto.collections.size)
        assertEquals("合集", dto.collections.first().title)
        assertEquals(1, dto.collections.first().items.size)

        assertEquals(1, dto.exploreTopics.size)
        assertEquals("传统技艺", dto.exploreTopics.first().title)

        assertEquals(1, dto.recommendations.size)
        assertEquals("推荐", dto.recommendations.first().title)
        assertEquals("semantic", dto.recommendations.first().source)
        assertEquals(0.9, dto.recommendations.first().weight, 0.001)

        assertEquals(1, dto.semanticRecommendations.size)
        assertEquals("sr1", dto.semanticRecommendations.first().id)
        assertEquals("graph", dto.semanticRecommendations.first().source)
        assertEquals(0.7, dto.semanticRecommendations.first().weight, 0.001)
    }

    @Test
    fun recommendationDto_parsesAllFields() {
        val dto = json.decodeFromString<RecommendationDto>(
            """
            {
              "id": "rec1",
              "type": "directoryItem",
              "title": "推荐项目",
              "subtitle": "副标题",
              "source": "semantic",
              "relationType": "similar",
              "reason": "同属传统技艺",
              "weight": 0.85,
              "category": "传统技艺",
              "region": "浙江",
              "publishedAt": "2024-01-01",
              "publishedYear": 2024,
              "coverImage": { "displayUrl": "https://example.test/img.jpg" },
              "sourceUrl": "https://example.test/source"
            }
            """.trimIndent(),
        )

        assertEquals("rec1", dto.id)
        assertEquals("directoryItem", dto.type)
        assertEquals("推荐项目", dto.title)
        assertEquals("副标题", dto.subtitle)
        assertEquals("semantic", dto.source)
        assertEquals("similar", dto.relationType)
        assertEquals("同属传统技艺", dto.reason)
        assertEquals(0.85, dto.weight, 0.001)
        assertEquals("传统技艺", dto.category)
        assertEquals("浙江", dto.region)
        assertEquals("2024-01-01", dto.publishedAt)
        assertEquals(2024, dto.publishedYear)
        assertNotNull(dto.coverImage)
        assertEquals("https://example.test/img.jpg", dto.coverImage!!.displayUrl)
        assertEquals("https://example.test/source", dto.sourceUrl)
    }

    // region Discovery v2 DTOs

    @Test
    fun discoveryTodayDto_parsesBackendShape() {
        val dto = json.decodeFromString<DiscoveryTodayDto>(
            """
            {
              "featuredDirectoryItem": {
                "id": "d1",
                "type": "directoryItem",
                "title": "今日推荐名录",
                "summary": "摘要",
                "category": "传统技艺",
                "kind": "nationalProject",
                "region": "浙江",
                "coverImage": { "displayUrl": "https://example.test/img.jpg" },
                "sourceUrl": "https://example.test/source"
              },
              "featuredInheritor": {
                "id": "i1",
                "type": "inheritor",
                "title": "今日推荐传承人"
              },
              "articles": [
                { "id": "a1", "type": "article", "title": "今日文章" }
              ],
              "date": "2026-05-31"
            }
            """.trimIndent(),
        )

        assertNotNull(dto.featuredDirectoryItem)
        assertEquals("d1", dto.featuredDirectoryItem!!.id)
        assertEquals("directoryItem", dto.featuredDirectoryItem!!.type)
        assertEquals("今日推荐名录", dto.featuredDirectoryItem!!.title)
        assertEquals("传统技艺", dto.featuredDirectoryItem!!.category)
        assertEquals("nationalProject", dto.featuredDirectoryItem!!.kind)
        assertEquals("浙江", dto.featuredDirectoryItem!!.region)
        assertNotNull(dto.featuredDirectoryItem!!.coverImage)

        assertNotNull(dto.featuredInheritor)
        assertEquals("i1", dto.featuredInheritor!!.id)
        assertEquals("今日推荐传承人", dto.featuredInheritor!!.title)

        assertEquals(1, dto.articles.size)
        assertEquals("今日文章", dto.articles.first().title)
        assertEquals("2026-05-31", dto.date)
    }

    @Test
    fun discoveryTrendingDto_parsesBackendShape() {
        val dto = json.decodeFromString<DiscoveryTrendingDto>(
            """
            {
              "items": [
                { "id": "t1", "type": "article", "title": "热门文章" },
                { "id": "t2", "type": "directoryItem", "title": "热门名录" }
              ],
              "generatedAt": "2026-05-31T10:00:00Z"
            }
            """.trimIndent(),
        )

        assertEquals(2, dto.items.size)
        assertEquals("热门文章", dto.items.first().title)
        assertEquals("热门名录", dto.items[1].title)
        assertNotNull(dto.generatedAt)
    }

    @Test
    fun discoveryWeeklyDto_parsesBackendShape() {
        val dto = json.decodeFromString<DiscoveryWeeklyDto>(
            """
            {
              "weekId": "2026-W22",
              "sections": [
                {
                  "id": "s1",
                  "title": "本周精选",
                  "subtitle": "不容错过的内容",
                  "items": [
                    { "id": "w1", "type": "article", "title": "本周文章" }
                  ]
                }
              ],
              "generatedAt": "2026-05-31T10:00:00Z"
            }
            """.trimIndent(),
        )

        assertEquals("2026-W22", dto.weekId)
        assertEquals(1, dto.sections.size)
        assertEquals("本周精选", dto.sections.first().title)
        assertEquals("不容错过的内容", dto.sections.first().subtitle)
        assertEquals(1, dto.sections.first().items.size)
        assertEquals("本周文章", dto.sections.first().items.first().title)
        assertNotNull(dto.generatedAt)
    }

    @Test
    fun discoveryDeepDiveDto_parsesBackendShape() {
        val dto = json.decodeFromString<DiscoveryDeepDiveDto>(
            """
            {
              "seed": { "id": "s1", "type": "article", "title": "种子文章" },
              "related": [
                { "id": "r1", "type": "directoryItem", "title": "相关名录" },
                { "id": "r2", "type": "inheritor", "title": "相关传承人" }
              ],
              "generatedAt": "2026-05-31T10:00:00Z"
            }
            """.trimIndent(),
        )

        assertNotNull(dto.seed)
        assertEquals("s1", dto.seed!!.id)
        assertEquals("种子文章", dto.seed!!.title)
        assertEquals(2, dto.related.size)
        assertEquals("相关名录", dto.related.first().title)
        assertEquals("相关传承人", dto.related[1].title)
        assertNotNull(dto.generatedAt)
    }

    // endregion

    // region Data Story DTOs

    @Test
    fun dataStoryDto_parsesBackendShape() {
        val dto = json.decodeFromString<DataStoryDto>(
            """
            {
              "id": "story-1",
              "title": "浙江非遗故事",
              "subtitle": "文化大省的非遗之路",
              "heroImage": { "displayUrl": "https://example.test/hero.jpg" },
              "sections": [
                {
                  "id": "intro",
                  "title": "简介",
                  "type": "intro",
                  "body": "浙江是中国非遗大省..."
                },
                {
                  "id": "items",
                  "title": "代表性项目",
                  "type": "items",
                  "items": [
                    { "type": "directoryItem", "id": "d1", "title": "龙泉青瓷", "summary": "摘要" }
                  ]
                }
              ],
              "relatedTopics": [
                { "type": "category", "key": "传统技艺", "title": "传统技艺" }
              ],
              "generatedAt": "2026-05-31T10:00:00Z"
            }
            """.trimIndent(),
        )

        assertEquals("story-1", dto.id)
        assertEquals("浙江非遗故事", dto.title)
        assertEquals("文化大省的非遗之路", dto.subtitle)
        assertNotNull(dto.heroImage)
        assertEquals(2, dto.sections.size)
        assertEquals("intro", dto.sections.first().type)
        assertEquals("浙江是中国非遗大省...", dto.sections.first().body)
        assertEquals("items", dto.sections[1].type)
        assertEquals(1, dto.sections[1].items.size)
        assertEquals("龙泉青瓷", dto.sections[1].items.first().title)
        assertEquals(1, dto.relatedTopics.size)
        assertEquals("传统技艺", dto.relatedTopics.first().title)
        assertNotNull(dto.generatedAt)
    }

    // endregion

    // region Taxonomy DTOs

    @Test
    fun taxonomyIndexDto_parsesTopicDtoBackendShape() {
        val dto = json.decodeFromString<TaxonomyIndexDto<TaxonomyTopicDto>>(
            """
            {
              "items": [
                {
                  "type": "category",
                  "key": "传统技艺",
                  "title": "传统技艺",
                  "subtitle": "描述",
                  "directoryItemCount": 120,
                  "inheritorCount": 50,
                  "articleCount": 30,
                  "total": 200,
                  "topRegions": [
                    { "region": "浙江", "count": 15 }
                  ],
                  "topCategories": [
                    { "category": "陶瓷", "count": 10 }
                  ],
                  "coverImage": { "displayUrl": "https://example.test/img.jpg" }
                }
              ],
              "generatedAt": "2026-05-31T10:00:00Z"
            }
            """.trimIndent(),
        )

        assertEquals(1, dto.items.size)
        val topic = dto.items.first()
        assertEquals("category", topic.type)
        assertEquals("传统技艺", topic.key)
        assertEquals("传统技艺", topic.title)
        assertEquals("描述", topic.subtitle)
        assertEquals(120, topic.directoryItemCount)
        assertEquals(50, topic.inheritorCount)
        assertEquals(30, topic.articleCount)
        assertEquals(200, topic.total)
        assertEquals(1, topic.topRegions.size)
        assertEquals("浙江", topic.topRegions.first().region)
        assertEquals(1, topic.topCategories.size)
        assertEquals("陶瓷", topic.topCategories.first().category)
        assertNotNull(topic.coverImage)
        assertNotNull(dto.generatedAt)
    }

    @Test
    fun taxonomyIndexDto_parsesKindDtoBackendShape() {
        val dto = json.decodeFromString<TaxonomyIndexDto<TaxonomyKindDto>>(
            """
            {
              "items": [
                {
                  "key": "nationalProject",
                  "title": "国家级项目",
                  "directoryItemCount": 950,
                  "inheritorCount": 300,
                  "total": 1250
                }
              ],
              "generatedAt": "2026-05-31T10:00:00Z"
            }
            """.trimIndent(),
        )

        assertEquals(1, dto.items.size)
        val kind = dto.items.first()
        assertEquals("nationalProject", kind.key)
        assertEquals("国家级项目", kind.title)
        assertEquals(950, kind.directoryItemCount)
        assertEquals(300, kind.inheritorCount)
        assertEquals(1250, kind.total)
    }

    @Test
    fun taxonomyCategoryDetailDto_parsesBackendShape() {
        val dto = json.decodeFromString<TaxonomyCategoryDetailDto>(
            """
            {
              "topic": {
                "type": "category",
                "key": "传统技艺",
                "title": "传统技艺",
                "directoryItemCount": 120,
                "inheritorCount": 50,
                "total": 170
              },
              "stats": {
                "directoryItemCount": 120,
                "inheritorCount": 50,
                "articleCount": 30,
                "total": 200
              },
              "topRegions": [
                { "region": "浙江", "count": 15 }
              ],
              "articles": [
                { "id": "a1", "title": "相关文章" }
              ],
              "directoryItems": [
                { "id": "d1", "title": "相关名录" }
              ],
              "inheritors": [
                { "id": "i1", "name": "相关传承人" }
              ],
              "relatedCategories": ["传统美术", "民俗"],
              "recommendedCollections": [
                { "id": "c1", "title": "推荐合集" }
              ],
              "generatedAt": "2026-05-31T10:00:00Z"
            }
            """.trimIndent(),
        )

        assertEquals("传统技艺", dto.topic.key)
        assertEquals(120, dto.stats.directoryItemCount)
        assertEquals(50, dto.stats.inheritorCount)
        assertEquals(30, dto.stats.articleCount)
        assertEquals(200, dto.stats.total)
        assertEquals(1, dto.topRegions.size)
        assertEquals("浙江", dto.topRegions.first().region)
        assertEquals(1, dto.articles.size)
        assertEquals("相关文章", dto.articles.first().title)
        assertEquals(1, dto.directoryItems.size)
        assertEquals("相关名录", dto.directoryItems.first().title)
        assertEquals(1, dto.inheritors.size)
        assertEquals("相关传承人", dto.inheritors.first().name)
        assertEquals(2, dto.relatedCategories.size)
        assertEquals("传统美术", dto.relatedCategories.first())
        assertEquals(1, dto.recommendedCollections.size)
        assertNotNull(dto.generatedAt)
    }

    @Test
    fun taxonomyRegionDetailDto_parsesBackendShape() {
        val dto = json.decodeFromString<TaxonomyRegionDetailDto>(
            """
            {
              "topic": {
                "type": "region",
                "key": "浙江省",
                "title": "浙江省",
                "directoryItemCount": 40,
                "inheritorCount": 2,
                "total": 42
              },
              "stats": {
                "directoryItemCount": 40,
                "inheritorCount": 2,
                "articleCount": 10,
                "total": 52
              },
              "topCategories": [
                { "category": "传统技艺", "count": 15 }
              ],
              "articles": [
                { "id": "a1", "title": "浙江文章" }
              ],
              "directoryItems": [
                { "id": "d1", "title": "浙江名录" }
              ],
              "inheritors": [
                { "id": "i1", "name": "浙江传承人" }
              ],
              "relatedRegions": ["江苏省", "安徽省"],
              "recommendedCollections": [],
              "generatedAt": "2026-05-31T10:00:00Z"
            }
            """.trimIndent(),
        )

        assertEquals("浙江省", dto.topic.key)
        assertEquals(40, dto.stats.directoryItemCount)
        assertEquals(2, dto.stats.inheritorCount)
        assertEquals(1, dto.topCategories.size)
        assertEquals("传统技艺", dto.topCategories.first().category)
        assertEquals(1, dto.articles.size)
        assertEquals(1, dto.directoryItems.size)
        assertEquals(1, dto.inheritors.size)
        assertEquals(2, dto.relatedRegions.size)
        assertEquals("江苏省", dto.relatedRegions.first())
        assertNotNull(dto.generatedAt)
    }

    // endregion

    // region Compare DTOs

    @Test
    fun compareResultDto_parsesBackendShape() {
        val dto = json.decodeFromString<CompareResultDto>(
            """
            {
              "left": {
                "key": "浙江",
                "title": "浙江",
                "directoryItemCount": 40,
                "inheritorCount": 2,
                "articleCount": 10,
                "total": 52,
                "topCategories": [
                  { "category": "传统技艺", "count": 15 }
                ],
                "topRegions": []
              },
              "right": {
                "key": "江苏",
                "title": "江苏",
                "directoryItemCount": 28,
                "inheritorCount": 10,
                "articleCount": 8,
                "total": 46,
                "topCategories": [],
                "topRegions": []
              },
              "summary": {
                "winnerByDirectoryItems": "浙江",
                "winnerByInheritors": "江苏",
                "winnerByArticles": "浙江",
                "winnerByTotal": "浙江"
              },
              "sharedCategories": ["传统技艺", "传统美术"],
              "leftUniqueCategories": ["民俗"],
              "rightUniqueCategories": ["传统医药"],
              "sharedRegions": [],
              "leftUniqueRegions": [],
              "rightUniqueRegions": [],
              "leftFeaturedItems": [
                { "id": "d1", "title": "浙江项目" }
              ],
              "rightFeaturedItems": [
                { "id": "d2", "title": "江苏项目" }
              ],
              "generatedAt": "2026-05-31T10:00:00Z"
            }
            """.trimIndent(),
        )

        assertEquals("浙江", dto.left.key)
        assertEquals(40, dto.left.directoryItemCount)
        assertEquals(2, dto.left.inheritorCount)
        assertEquals("江苏", dto.right.key)
        assertEquals(28, dto.right.directoryItemCount)
        assertEquals(10, dto.right.inheritorCount)

        assertEquals("浙江", dto.summary.winnerByDirectoryItems)
        assertEquals("江苏", dto.summary.winnerByInheritors)
        assertEquals("浙江", dto.summary.winnerByArticles)
        assertEquals("浙江", dto.summary.winnerByTotal)

        assertEquals(2, dto.sharedCategories.size)
        assertEquals("传统技艺", dto.sharedCategories.first())
        assertEquals(1, dto.leftUniqueCategories.size)
        assertEquals("民俗", dto.leftUniqueCategories.first())
        assertEquals(1, dto.rightUniqueCategories.size)
        assertEquals("传统医药", dto.rightUniqueCategories.first())

        assertEquals(1, dto.leftFeaturedItems.size)
        assertEquals("浙江项目", dto.leftFeaturedItems.first().title)
        assertEquals(1, dto.rightFeaturedItems.size)
        assertEquals("江苏项目", dto.rightFeaturedItems.first().title)
        assertNotNull(dto.generatedAt)
    }

    // endregion

    // region Content Digest DTOs

    @Test
    fun contentDigestDto_parsesBackendShape() {
        val dto = json.decodeFromString<ContentDigestDto>(
            """
            {
              "type": "article",
              "id": "a1",
              "title": "非遗新闻速览",
              "quickRead": "这是一篇关于非遗的新闻...",
              "highlights": ["要点1", "要点2"],
              "keyFacts": [
                { "label": "发布时间", "value": "2026-05-31" },
                { "label": "来源", "value": "人民日报" }
              ],
              "keywords": ["非遗", "传统文化"],
              "readingTimeMinutes": 3,
              "sourceUrl": "https://example.test/source",
              "generatedAt": "2026-05-31T10:00:00Z"
            }
            """.trimIndent(),
        )

        assertEquals("article", dto.type)
        assertEquals("a1", dto.id)
        assertEquals("非遗新闻速览", dto.title)
        assertEquals("这是一篇关于非遗的新闻...", dto.quickRead)
        assertEquals(2, dto.highlights.size)
        assertEquals("要点1", dto.highlights.first())
        assertEquals(2, dto.keyFacts.size)
        assertEquals("发布时间", dto.keyFacts.first().label)
        assertEquals("2026-05-31", dto.keyFacts.first().value)
        assertEquals(2, dto.keywords.size)
        assertEquals("非遗", dto.keywords.first())
        assertEquals(3, dto.readingTimeMinutes)
        assertEquals("https://example.test/source", dto.sourceUrl)
        assertNotNull(dto.generatedAt)
    }

    // endregion

    // region Blended Recommendation DTOs

    @Test
    fun blendedRecommendationResponseDto_parsesBackendShape() {
        val dto = json.decodeFromString<BlendedRecommendationResponseDto>(
            """
            {
              "items": [
                {
                  "id": "r1",
                  "type": "directoryItem",
                  "title": "推荐项目",
                  "subtitle": "副标题",
                  "score": 0.85,
                  "reasons": ["同属传统技艺", "同在浙江"],
                  "scoreBreakdown": {
                    "explicit": 0.3,
                    "inferred": 0.2,
                    "embedding": 0.15,
                    "sameCategory": 0.1,
                    "sameRegion": 0.1
                  },
                  "category": "传统技艺",
                  "region": "浙江",
                  "coverImage": { "displayUrl": "https://example.test/img.jpg" },
                  "sourceUrl": "https://example.test/source"
                }
              ],
              "query": {
                "type": "directoryItem",
                "id": "seed-1",
                "limit": 10,
                "ruleWeight": 1.0,
                "semanticWeight": 0.8,
                "sameCategoryWeight": 0.5,
                "sameRegionWeight": 0.3,
                "diversify": true
              },
              "generatedAt": "2026-05-31T10:00:00Z"
            }
            """.trimIndent(),
        )

        assertEquals(1, dto.items.size)
        val item = dto.items.first()
        assertEquals("r1", item.id)
        assertEquals("directoryItem", item.type)
        assertEquals("推荐项目", item.title)
        assertEquals("副标题", item.subtitle)
        assertEquals(0.85, item.score, 0.001)
        assertEquals(2, item.reasons.size)
        assertEquals("同属传统技艺", item.reasons.first())
        assertEquals(0.3, item.scoreBreakdown.explicit, 0.001)
        assertEquals(0.2, item.scoreBreakdown.inferred, 0.001)
        assertEquals(0.15, item.scoreBreakdown.embedding, 0.001)
        assertEquals(0.1, item.scoreBreakdown.sameCategory, 0.001)
        assertEquals(0.1, item.scoreBreakdown.sameRegion, 0.001)
        assertEquals("传统技艺", item.category)
        assertEquals("浙江", item.region)
        assertNotNull(item.coverImage)
        assertEquals("https://example.test/source", item.sourceUrl)

        assertEquals("directoryItem", dto.query.type)
        assertEquals("seed-1", dto.query.id)
        assertEquals(10, dto.query.limit)
        assertEquals(1.0, dto.query.ruleWeight, 0.001)
        assertEquals(0.8, dto.query.semanticWeight, 0.001)
        assertEquals(0.5, dto.query.sameCategoryWeight, 0.001)
        assertEquals(0.3, dto.query.sameRegionWeight, 0.001)
        assertTrue(dto.query.diversify)
        assertNotNull(dto.generatedAt)
    }

    // endregion
}
