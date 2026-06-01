package com.duckylife.heritage.modern.feature.my

import com.duckylife.heritage.modern.core.data.ReadingPathEvent
import com.duckylife.heritage.modern.core.network.dto.ArticleCategory
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ReadingPathDestinationMapperTest {

    // region Article

    @Test
    fun `article by id only`() {
        val event = ReadingPathEvent(
            toType = "article",
            toId = "article-db-id",
            source = "related",
        )
        val dest = event.toMyPageDestination() as MyPageDestination.Article
        assertEquals("article-db-id", dest.articleId)
        assertNull(dest.sourceId)
        assertNull(dest.sourceUrl)
        assertEquals(ArticleCategory.News, dest.category)
    }

    @Test
    fun `article by sourceId clears articleId`() {
        val event = ReadingPathEvent(
            toType = "article",
            toId = "source-id-1",
            toSourceId = "source-id-1",
            source = "related",
        )
        val dest = event.toMyPageDestination() as MyPageDestination.Article
        assertNull(dest.articleId)
        assertEquals("source-id-1", dest.sourceId)
    }

    @Test
    fun `article by sourceUrl clears articleId`() {
        val event = ReadingPathEvent(
            toType = "article",
            toId = "some-id",
            toSourceUrl = "https://example.test/article",
            source = "related",
        )
        val dest = event.toMyPageDestination() as MyPageDestination.Article
        assertNull(dest.articleId)
        assertEquals("https://example.test/article", dest.sourceUrl)
    }

    @Test
    fun `article with category maps correctly`() {
        val event = ReadingPathEvent(
            toType = "article",
            toId = "a1",
            toCategory = "specialTopic",
            source = "related",
        )
        val dest = event.toMyPageDestination() as MyPageDestination.Article
        assertEquals(ArticleCategory.SpecialTopic, dest.category)
    }

    @Test
    fun `article with unknown category falls back to News`() {
        val event = ReadingPathEvent(
            toType = "article",
            toId = "a1",
            toCategory = "unknown",
            source = "related",
        )
        val dest = event.toMyPageDestination() as MyPageDestination.Article
        assertEquals(ArticleCategory.News, dest.category)
    }

    // endregion

    // region Directory

    @Test
    fun `directory by id only`() {
        val event = ReadingPathEvent(
            toType = "directoryItem",
            toId = "d1",
            source = "related",
        )
        val dest = event.toMyPageDestination() as MyPageDestination.Directory
        assertEquals("d1", dest.itemId)
        assertNull(dest.sourceId)
        assertEquals(DirectoryItemKind.NationalProject, dest.kind)
    }

    @Test
    fun `directory by sourceId clears itemId`() {
        val event = ReadingPathEvent(
            toType = "directoryItem",
            toId = "source-id-1",
            toSourceId = "source-id-1",
            source = "related",
        )
        val dest = event.toMyPageDestination() as MyPageDestination.Directory
        assertNull(dest.itemId)
        assertEquals("source-id-1", dest.sourceId)
    }

    @Test
    fun `directory with kind maps correctly`() {
        val event = ReadingPathEvent(
            toType = "directoryItem",
            toId = "d1",
            toKind = "culturalEcoZone",
            source = "related",
        )
        val dest = event.toMyPageDestination() as MyPageDestination.Directory
        assertEquals(DirectoryItemKind.CulturalEcoZone, dest.kind)
    }

    @Test
    fun `directory with unknown kind falls back to NationalProject`() {
        val event = ReadingPathEvent(
            toType = "directoryItem",
            toId = "d1",
            toKind = "unknown",
            source = "related",
        )
        val dest = event.toMyPageDestination() as MyPageDestination.Directory
        assertEquals(DirectoryItemKind.NationalProject, dest.kind)
    }

    // endregion

    // region Inheritor

    @Test
    fun `inheritor by id only`() {
        val event = ReadingPathEvent(
            toType = "inheritor",
            toId = "i1",
            source = "related",
        )
        val dest = event.toMyPageDestination() as MyPageDestination.Inheritor
        assertEquals("i1", dest.inheritorId)
        assertNull(dest.sourceId)
    }

    @Test
    fun `inheritor by sourceId clears inheritorId`() {
        val event = ReadingPathEvent(
            toType = "inheritor",
            toId = "source-id-1",
            toSourceId = "source-id-1",
            source = "related",
        )
        val dest = event.toMyPageDestination() as MyPageDestination.Inheritor
        assertNull(dest.inheritorId)
        assertEquals("source-id-1", dest.sourceId)
    }

    // endregion

    // region Unsupported types

    @Test
    fun `collection type returns null`() {
        val event = ReadingPathEvent(
            toType = "collection",
            toId = "c1",
            source = "collection",
        )
        assertNull(event.toMyPageDestination())
    }

    @Test
    fun `topic type returns null`() {
        val event = ReadingPathEvent(
            toType = "topic",
            toId = "传统技艺",
            source = "exploreTopic",
        )
        assertNull(event.toMyPageDestination())
    }

    @Test
    fun `unknown type returns null`() {
        val event = ReadingPathEvent(
            toType = "unknown",
            toId = "x1",
            source = "related",
        )
        assertNull(event.toMyPageDestination())
    }

    // endregion
}
