package com.duckylife.heritage.modern.feature.discovery

import com.duckylife.heritage.modern.core.network.dto.ArticleCategory
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind
import com.duckylife.heritage.modern.core.network.dto.SearchResultType
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphNodeType
import com.duckylife.heritage.modern.core.network.dto.advanced.LearningRouteSeedType
import org.junit.Assert.assertEquals
import org.junit.Test

class DiscoveryRouteSerializerTest {

    @Test
    fun roundTrip_index() {
        val stack = listOf(DiscoveryRouteKey.DiscoveryIndex)
        val restored = deserializeDiscoveryRoutes(serializeDiscoveryRoutes(stack))
        assertEquals(stack, restored)
    }

    @Test
    fun roundTrip_searchWithPipe() {
        val stack = listOf(
            DiscoveryRouteKey.DiscoveryIndex,
            DiscoveryRouteKey.SearchResults(query = "a|b|c"),
        )
        val restored = deserializeDiscoveryRoutes(serializeDiscoveryRoutes(stack))
        assertEquals(stack, restored)
    }

    @Test
    fun roundTrip_articleWithUrl() {
        val url = "https://example.test/page?a=1&b=2"
        val stack = listOf(
            DiscoveryRouteKey.DiscoveryArticleDetail(
                id = "abc123",
                sourceUrl = url,
                category = ArticleCategory.SpecialTopic,
            ),
        )
        val restored = deserializeDiscoveryRoutes(serializeDiscoveryRoutes(stack))
        assertEquals(stack, restored)
    }

    @Test
    fun roundTrip_chineseCharacters() {
        val stack = listOf(
            DiscoveryRouteKey.RegionDetailPage(region = "浙江省"),
            DiscoveryRouteKey.StoryPage(category = "传统技艺"),
        )
        val restored = deserializeDiscoveryRoutes(serializeDiscoveryRoutes(stack))
        assertEquals(stack, restored)
    }

    @Test
    fun roundTrip_specialCharsInSearch() {
        val stack = listOf(
            DiscoveryRouteKey.SearchResults(query = "陶瓷 & 非遗 | 传统"),
        )
        val restored = deserializeDiscoveryRoutes(serializeDiscoveryRoutes(stack))
        assertEquals(stack, restored)
    }

    @Test
    fun roundTrip_emptyStack() {
        val stack = emptyList<DiscoveryRouteKey>()
        val restored = deserializeDiscoveryRoutes(serializeDiscoveryRoutes(stack))
        assertEquals(listOf(DiscoveryRouteKey.DiscoveryIndex), restored)
    }

    @Test
    fun roundTrip_nullValues() {
        val stack = listOf(
            DiscoveryRouteKey.DiscoveryArticleDetail(id = null, sourceId = null, sourceUrl = null),
            DiscoveryRouteKey.DiscoveryInheritorDetail(id = null, sourceId = null),
        )
        val restored = deserializeDiscoveryRoutes(serializeDiscoveryRoutes(stack))
        assertEquals(stack, restored)
    }

    @Test
    fun corruptedJson_fallsBackToIndex() {
        val restored = deserializeDiscoveryRoutes("{invalid json")
        assertEquals(listOf(DiscoveryRouteKey.DiscoveryIndex), restored)
    }

    @Test
    fun legacyStringI_fallsBackToIndex() {
        val restored = deserializeDiscoveryRoutes("I")
        assertEquals(listOf(DiscoveryRouteKey.DiscoveryIndex), restored)
    }

    @Test
    fun emptyString_fallsBackToIndex() {
        val restored = deserializeDiscoveryRoutes("")
        assertEquals(listOf(DiscoveryRouteKey.DiscoveryIndex), restored)
    }

    @Test
    fun roundTrip_allRouteTypes() {
        val allTypes = listOf(
            DiscoveryRouteKey.DiscoveryIndex,
            DiscoveryRouteKey.SearchResults(query = "test"),
            DiscoveryRouteKey.ExploreTopicDetail(type = "category", key = "传统技艺"),
            DiscoveryRouteKey.LearningPathDetail(id = "lp1"),
            DiscoveryRouteKey.CollectionDetail(id = "c1", type = "region", key = "浙江"),
            DiscoveryRouteKey.RegionAtlasPage,
            DiscoveryRouteKey.RegionDetailPage(region = "浙江"),
            DiscoveryRouteKey.TimelinePage,
            DiscoveryRouteKey.KnowledgeGraphHubPage,
            DiscoveryRouteKey.TopicGraphMapPage(topicType = GraphNodeType.Category, topicKey = "传统技艺"),
            DiscoveryRouteKey.GraphTrailPage(GraphTrailSource.Random),
            DiscoveryRouteKey.GraphTrailPage(GraphTrailSource.FromContent(type = SearchResultType.Article, contentId = "a1")),
            DiscoveryRouteKey.GraphTrailPage(GraphTrailSource.FromTopic(topicType = GraphNodeType.Region, topicKey = "浙江")),
            DiscoveryRouteKey.TaxonomyPage,
            DiscoveryRouteKey.TaxonomyDetailPage(type = "category", key = "传统技艺"),
            DiscoveryRouteKey.ComparePage(type = "kind", left = "nationalProject", right = "unescoEntry"),
            DiscoveryRouteKey.StoriesIndexPage,
            DiscoveryRouteKey.StoryPage(region = "云南", year = 2024),
            DiscoveryRouteKey.DeepDivePage(seedType = SearchResultType.Article, seedId = "seed1"),
            DiscoveryRouteKey.GraphExplorePage(
                type = SearchResultType.Article,
                contentId = "a1",
                initialTab = GraphTab.Similar,
            ),
            DiscoveryRouteKey.LearningRoutesPage(seedType = LearningRouteSeedType.Content, seedId = "article:a1"),
            DiscoveryRouteKey.LearningRouteDetailPage(routeId = "route-1"),
            DiscoveryRouteKey.SpacetimePage,
            DiscoveryRouteKey.RankingsPage,
            DiscoveryRouteKey.RankingDetailPage(rankingId = "top-regions"),
            DiscoveryRouteKey.DiscoveryArticleDetail(
                id = "a1",
                sourceUrl = "https://test.test/page?id=1&name=浙江",
                category = ArticleCategory.SpecialTopic,
            ),
            DiscoveryRouteKey.DiscoveryDirectoryDetail(id = "d1", kind = DirectoryItemKind.CulturalEcoZone),
            DiscoveryRouteKey.DiscoveryInheritorDetail(id = "i1", sourceId = "src-i1"),
        )
        val restored = deserializeDiscoveryRoutes(serializeDiscoveryRoutes(allTypes))
        assertEquals(allTypes, restored)
    }

    @Test
    fun navigationToGraphHubAndBackReturnsDiscoveryIndex() {
        val stack = listOf(
            DiscoveryRouteKey.DiscoveryIndex,
            DiscoveryRouteKey.KnowledgeGraphHubPage,
        )
        val serialized = serializeDiscoveryRoutes(stack)
        val restored = deserializeDiscoveryRoutes(serialized)
        // 模拟返回键：移除栈顶后仅剩发现首页。
        val afterBack = restored.dropLast(1)
        assertEquals(listOf(DiscoveryRouteKey.DiscoveryIndex), afterBack)
    }
}
