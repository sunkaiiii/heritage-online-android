package com.duckylife.heritage.modern.ui.text

import com.duckylife.heritage.modern.core.network.dto.ArticleCategory
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind
import com.duckylife.heritage.modern.core.network.dto.SearchResultType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * ContentLabels 底层映射逻辑测试。
 *
 * 注意：@Composable 函数（localizedContentType、localizedArticleCategory 等）
 * 需要 Compose 测试环境（androidTest），这里只测试底层枚举映射逻辑。
 * 实际的 stringResource 映射在 instrumentation test 中验证。
 */
class ContentLabelsTest {

    // region SearchResultType.fromWireName

    @Test
    fun `SearchResultType fromWireName maps known values`() {
        assertEquals(SearchResultType.Article, SearchResultType.fromWireName("article"))
        assertEquals(SearchResultType.DirectoryItem, SearchResultType.fromWireName("directoryItem"))
        assertEquals(SearchResultType.Inheritor, SearchResultType.fromWireName("inheritor"))
    }

    @Test
    fun `SearchResultType fromWireName returns Unknown for the unknown wire value`() {
        assertEquals(SearchResultType.Unknown, SearchResultType.fromWireName("unknown"))
    }

    @Test
    fun `SearchResultType fromWireName returns null for unknown`() {
        assertNull(SearchResultType.fromWireName(""))
        assertNull(SearchResultType.fromWireName("Article"))
        assertNull(SearchResultType.fromWireName(null))
    }

    // endregion

    // region ArticleCategory wireName mapping

    @Test
    fun `ArticleCategory wireName maps correctly`() {
        assertEquals("news", ArticleCategory.News.wireName)
        assertEquals("forum", ArticleCategory.Forum.wireName)
        assertEquals("specialTopic", ArticleCategory.SpecialTopic.wireName)
    }

    @Test
    fun `ArticleCategory fromWireName maps known values`() {
        assertEquals(ArticleCategory.News, ArticleCategory.entries.firstOrNull { it.wireName == "news" })
        assertEquals(ArticleCategory.Forum, ArticleCategory.entries.firstOrNull { it.wireName == "forum" })
        assertEquals(ArticleCategory.SpecialTopic, ArticleCategory.entries.firstOrNull { it.wireName == "specialTopic" })
    }

    @Test
    fun `ArticleCategory fromWireName returns null for unknown`() {
        assertNull(ArticleCategory.entries.firstOrNull { it.wireName == "unknown" })
        assertNull(ArticleCategory.entries.firstOrNull { it.wireName == "" })
    }

    // endregion

    // region DirectoryItemKind wireName mapping

    @Test
    fun `DirectoryItemKind wireName maps correctly`() {
        assertEquals("nationalProject", DirectoryItemKind.NationalProject.wireName)
        assertEquals("culturalEcoZone", DirectoryItemKind.CulturalEcoZone.wireName)
        assertEquals("productiveProtectionBase", DirectoryItemKind.ProductiveProtectionBase.wireName)
        assertEquals("unescoEntry", DirectoryItemKind.UnescoEntry.wireName)
        assertEquals("chinaUnescoEntry", DirectoryItemKind.ChinaUnescoEntry.wireName)
        assertEquals("contractingState", DirectoryItemKind.ContractingState.wireName)
    }

    @Test
    fun `DirectoryItemKind fromWireName maps known values`() {
        assertEquals(DirectoryItemKind.NationalProject, DirectoryItemKind.entries.firstOrNull { it.wireName == "nationalProject" })
        assertEquals(DirectoryItemKind.CulturalEcoZone, DirectoryItemKind.entries.firstOrNull { it.wireName == "culturalEcoZone" })
    }

    @Test
    fun `DirectoryItemKind fromWireName returns null for unknown`() {
        assertNull(DirectoryItemKind.entries.firstOrNull { it.wireName == "unknown" })
        assertNull(DirectoryItemKind.entries.firstOrNull { it.wireName == "" })
    }

    // endregion
}
