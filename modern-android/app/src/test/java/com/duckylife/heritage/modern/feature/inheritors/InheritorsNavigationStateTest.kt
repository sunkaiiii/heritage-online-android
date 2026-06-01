package com.duckylife.heritage.modern.feature.inheritors

import com.duckylife.heritage.modern.core.network.dto.ArticleCategory
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind
import org.junit.Assert.assertEquals
import org.junit.Test

class InheritorsNavigationStateTest {

    @Test
    fun roundTrip_list() {
        val stack = listOf(InheritorsRouteKey.InheritorsList)
        val restored = deserializeInheritorsRoutes(serializeInheritorsRoutes(stack))
        assertEquals(stack, restored)
    }

    @Test
    fun roundTrip_inheritorDetail() {
        val stack = listOf(
            InheritorsRouteKey.InheritorsList,
            InheritorsRouteKey.InheritorDetail(id = "i1", sourceId = "src1"),
        )
        val restored = deserializeInheritorsRoutes(serializeInheritorsRoutes(stack))
        assertEquals(stack, restored)
    }

    @Test
    fun roundTrip_directoryDetail() {
        val stack = listOf(
            InheritorsRouteKey.InheritorsList,
            InheritorsRouteKey.InheritorDirectoryDetail(id = "d1", sourceId = "src1", kind = DirectoryItemKind.NationalProject),
        )
        val restored = deserializeInheritorsRoutes(serializeInheritorsRoutes(stack))
        assertEquals(stack, restored)
    }

    @Test
    fun roundTrip_articleDetail() {
        val stack = listOf(
            InheritorsRouteKey.InheritorsList,
            InheritorsRouteKey.InheritorTabArticleDetail(
                id = "a1",
                sourceUrl = "https://example.test/page?a=1&b=2",
                category = ArticleCategory.SpecialTopic,
            ),
        )
        val restored = deserializeInheritorsRoutes(serializeInheritorsRoutes(stack))
        assertEquals(stack, restored)
    }

    @Test
    fun roundTrip_collectionDetail() {
        val stack = listOf(
            InheritorsRouteKey.InheritorsList,
            InheritorsRouteKey.InheritorTabCollectionDetail(id = "c1"),
        )
        val restored = deserializeInheritorsRoutes(serializeInheritorsRoutes(stack))
        assertEquals(stack, restored)
    }

    @Test
    fun roundTrip_topicDetail() {
        val stack = listOf(
            InheritorsRouteKey.InheritorsList,
            InheritorsRouteKey.InheritorTabTopicDetail(type = "category", key = "传统技艺"),
        )
        val restored = deserializeInheritorsRoutes(serializeInheritorsRoutes(stack))
        assertEquals(stack, restored)
    }

    @Test
    fun roundTrip_chineseCharacters() {
        val stack = listOf(
            InheritorsRouteKey.InheritorDetail(id = "i1", sourceId = "浙江省"),
            InheritorsRouteKey.InheritorTabTopicDetail(type = "region", key = "浙江省"),
        )
        val restored = deserializeInheritorsRoutes(serializeInheritorsRoutes(stack))
        assertEquals(stack, restored)
    }

    @Test
    fun roundTrip_specialCharsInUrl() {
        val stack = listOf(
            InheritorsRouteKey.InheritorTabArticleDetail(
                id = "a1",
                sourceUrl = "https://test.test/page?id=1&name=浙江|非遗",
            ),
        )
        val restored = deserializeInheritorsRoutes(serializeInheritorsRoutes(stack))
        assertEquals(stack, restored)
    }

    @Test
    fun roundTrip_emptyStack() {
        val stack = emptyList<InheritorsRouteKey>()
        val restored = deserializeInheritorsRoutes(serializeInheritorsRoutes(stack))
        assertEquals(listOf(InheritorsRouteKey.InheritorsList), restored)
    }

    @Test
    fun corruptedJson_fallsBackToList() {
        val restored = deserializeInheritorsRoutes("{invalid json")
        assertEquals(listOf(InheritorsRouteKey.InheritorsList), restored)
    }

    @Test
    fun legacyStringL_fallsBackToList() {
        val restored = deserializeInheritorsRoutes("L")
        assertEquals(listOf(InheritorsRouteKey.InheritorsList), restored)
    }

    @Test
    fun emptyString_fallsBackToList() {
        val restored = deserializeInheritorsRoutes("")
        assertEquals(listOf(InheritorsRouteKey.InheritorsList), restored)
    }

    @Test
    fun roundTrip_allRouteTypes() {
        val allTypes = listOf(
            InheritorsRouteKey.InheritorsList,
            InheritorsRouteKey.InheritorDetail(id = "i1", sourceId = "src1"),
            InheritorsRouteKey.InheritorDirectoryDetail(id = "d1", sourceId = "src2", kind = DirectoryItemKind.CulturalEcoZone),
            InheritorsRouteKey.InheritorTabArticleDetail(
                id = "a1",
                sourceUrl = "https://test.test/page?id=1&name=浙江",
                category = ArticleCategory.SpecialTopic,
            ),
            InheritorsRouteKey.InheritorTabCollectionDetail(id = "c1"),
            InheritorsRouteKey.InheritorTabTopicDetail(type = "category", key = "传统技艺"),
        )
        val restored = deserializeInheritorsRoutes(serializeInheritorsRoutes(allTypes))
        assertEquals(allTypes, restored)
    }
}
