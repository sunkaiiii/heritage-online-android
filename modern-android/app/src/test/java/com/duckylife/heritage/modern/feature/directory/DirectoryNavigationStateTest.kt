package com.duckylife.heritage.modern.feature.directory

import com.duckylife.heritage.modern.core.network.dto.ArticleCategory
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind
import org.junit.Assert.assertEquals
import org.junit.Test

class DirectoryNavigationStateTest {

    @Test
    fun roundTrip_list() {
        val stack = listOf(DirectoryRouteKey.DirectoryList)
        val restored = deserializeDirectoryRoutes(serializeDirectoryRoutes(stack))
        assertEquals(stack, restored)
    }

    @Test
    fun roundTrip_directoryDetail() {
        val stack = listOf(
            DirectoryRouteKey.DirectoryList,
            DirectoryRouteKey.DirectoryDetail(id = "d1", sourceId = "src1", kind = DirectoryItemKind.NationalProject),
        )
        val restored = deserializeDirectoryRoutes(serializeDirectoryRoutes(stack))
        assertEquals(stack, restored)
    }

    @Test
    fun roundTrip_inheritorDetail() {
        val stack = listOf(
            DirectoryRouteKey.DirectoryList,
            DirectoryRouteKey.DirectoryInheritorDetail(id = "i1", sourceId = "src1"),
        )
        val restored = deserializeDirectoryRoutes(serializeDirectoryRoutes(stack))
        assertEquals(stack, restored)
    }

    @Test
    fun roundTrip_articleDetail() {
        val stack = listOf(
            DirectoryRouteKey.DirectoryList,
            DirectoryRouteKey.DirectoryTabArticleDetail(
                id = "a1",
                sourceUrl = "https://example.test/page?a=1&b=2",
                category = ArticleCategory.SpecialTopic,
            ),
        )
        val restored = deserializeDirectoryRoutes(serializeDirectoryRoutes(stack))
        assertEquals(stack, restored)
    }

    @Test
    fun roundTrip_collectionDetail() {
        val stack = listOf(
            DirectoryRouteKey.DirectoryList,
            DirectoryRouteKey.DirectoryTabCollectionDetail(id = "c1"),
        )
        val restored = deserializeDirectoryRoutes(serializeDirectoryRoutes(stack))
        assertEquals(stack, restored)
    }

    @Test
    fun roundTrip_topicDetail() {
        val stack = listOf(
            DirectoryRouteKey.DirectoryList,
            DirectoryRouteKey.DirectoryTabTopicDetail(type = "category", key = "传统技艺"),
        )
        val restored = deserializeDirectoryRoutes(serializeDirectoryRoutes(stack))
        assertEquals(stack, restored)
    }

    @Test
    fun roundTrip_chineseCharacters() {
        val stack = listOf(
            DirectoryRouteKey.DirectoryDetail(id = "d1", sourceId = "浙江省"),
            DirectoryRouteKey.DirectoryTabTopicDetail(type = "region", key = "浙江省"),
        )
        val restored = deserializeDirectoryRoutes(serializeDirectoryRoutes(stack))
        assertEquals(stack, restored)
    }

    @Test
    fun roundTrip_specialCharsInUrl() {
        val stack = listOf(
            DirectoryRouteKey.DirectoryTabArticleDetail(
                id = "a1",
                sourceUrl = "https://test.test/page?id=1&name=浙江|非遗",
            ),
        )
        val restored = deserializeDirectoryRoutes(serializeDirectoryRoutes(stack))
        assertEquals(stack, restored)
    }

    @Test
    fun roundTrip_emptyStack() {
        val stack = emptyList<DirectoryRouteKey>()
        val restored = deserializeDirectoryRoutes(serializeDirectoryRoutes(stack))
        assertEquals(listOf(DirectoryRouteKey.DirectoryList), restored)
    }

    @Test
    fun corruptedJson_fallsBackToList() {
        val restored = deserializeDirectoryRoutes("{invalid json")
        assertEquals(listOf(DirectoryRouteKey.DirectoryList), restored)
    }

    @Test
    fun legacyStringL_fallsBackToList() {
        val restored = deserializeDirectoryRoutes("L")
        assertEquals(listOf(DirectoryRouteKey.DirectoryList), restored)
    }

    @Test
    fun emptyString_fallsBackToList() {
        val restored = deserializeDirectoryRoutes("")
        assertEquals(listOf(DirectoryRouteKey.DirectoryList), restored)
    }

    @Test
    fun roundTrip_allRouteTypes() {
        val allTypes = listOf(
            DirectoryRouteKey.DirectoryList,
            DirectoryRouteKey.DirectoryDetail(id = "d1", sourceId = "src1", kind = DirectoryItemKind.CulturalEcoZone),
            DirectoryRouteKey.DirectoryInheritorDetail(id = "i1", sourceId = "src2"),
            DirectoryRouteKey.DirectoryTabArticleDetail(
                id = "a1",
                sourceUrl = "https://test.test/page?id=1&name=浙江",
                category = ArticleCategory.SpecialTopic,
            ),
            DirectoryRouteKey.DirectoryTabCollectionDetail(id = "c1"),
            DirectoryRouteKey.DirectoryTabTopicDetail(type = "category", key = "传统技艺"),
        )
        val restored = deserializeDirectoryRoutes(serializeDirectoryRoutes(allTypes))
        assertEquals(allTypes, restored)
    }
}
