package com.duckylife.heritage.modern.core.database.mapper

import com.duckylife.heritage.modern.core.database.entity.ArticleDetailEntity
import com.duckylife.heritage.modern.core.database.entity.ArticleEntity
import com.duckylife.heritage.modern.core.network.ArticleQuery
import com.duckylife.heritage.modern.core.network.HeritageJson
import com.duckylife.heritage.modern.core.network.dto.ArticleCategory
import com.duckylife.heritage.modern.core.network.dto.ArticleDetailDto
import com.duckylife.heritage.modern.core.network.dto.ArticleSummaryDto
import com.duckylife.heritage.modern.core.network.dto.MediaAssetDto
import kotlinx.serialization.encodeToString
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ArticleMapperTest {

    // region queryKey

    @Test
    fun queryKeyIncludesCategory() {
        val newsKey = ArticleQuery(category = ArticleCategory.News).queryKey()
        val forumKey = ArticleQuery(category = ArticleCategory.Forum).queryKey()
        assertNotEquals(newsKey, forumKey)
    }

    @Test
    fun queryKeyIncludesKeywords() {
        val emptyKey = ArticleQuery(keywords = null).queryKey()
        val keyA = ArticleQuery(keywords = "陶瓷").queryKey()
        val keyB = ArticleQuery(keywords = "刺绣").queryKey()
        assertNotEquals(emptyKey, keyA)
        assertNotEquals(keyA, keyB)
    }

    @Test
    fun queryKeyIncludesYear() {
        val noYearKey = ArticleQuery(year = null).queryKey()
        val yearKey = ArticleQuery(year = 2025).queryKey()
        assertNotEquals(noYearKey, yearKey)
    }

    @Test
    fun sameQueryProducesSameKey() {
        val q1 = ArticleQuery(category = ArticleCategory.SpecialTopic, keywords = "非遗", year = 2024)
        val q2 = ArticleQuery(category = ArticleCategory.SpecialTopic, keywords = "非遗", year = 2024)
        assertEquals(q1.queryKey(), q2.queryKey())
    }

    // endregion

    // region ArticleSummaryDto ↔ ArticleEntity

    @Test
    fun toEntityUsesDtoIdWhenPresent() {
        val dto = ArticleSummaryDto(id = "article-1", title = "Test")
        val entity = dto.toEntity(ArticleQuery(), page = 1, positionInPage = 0)
        assertEquals("article-1", entity.id)
    }

    @Test
    fun toEntityFallsBackToSourceUrlWhenIdMissing() {
        val dto = ArticleSummaryDto(id = null, sourceUrl = "https://example.com/1", title = "Test")
        val entity = dto.toEntity(ArticleQuery(), page = 1, positionInPage = 0)
        assertEquals("https://example.com/1", entity.id)
    }

    @Test
    fun toEntityFallsBackToGeneratedIdWhenAllKeysMissing() {
        val dto = ArticleSummaryDto(id = null, sourceUrl = null, title = "Test")
        val query = ArticleQuery(category = ArticleCategory.News, page = 1, pageSize = 20)
        val entity = dto.toEntity(query, page = 2, positionInPage = 5)
        assertEquals("news||-2-5", entity.id)
    }

    @Test
    fun toEntityStoresQueryKey() {
        val query = ArticleQuery(category = ArticleCategory.Forum, keywords = "陶瓷")
        val entity = ArticleSummaryDto(id = "a1", title = "T").toEntity(query, page = 1, positionInPage = 0)
        assertEquals(query.queryKey(), entity.queryKey)
    }

    @Test
    fun toEntitySerializesCoverImage() {
        val dto = ArticleSummaryDto(
            id = "a1",
            title = "T",
            coverImage = MediaAssetDto(displayUrl = "https://img.test/1.jpg", altText = "alt"),
        )
        val entity = dto.toEntity(ArticleQuery(), page = 1, positionInPage = 0)
        val decoded = HeritageJson.decodeFromString<MediaAssetDto>(requireNotNull(entity.coverImageJson))
        assertEquals("https://img.test/1.jpg", decoded.displayUrl)
        assertEquals("alt", decoded.altText)
    }

    @Test
    fun toEntityStoresPageAndPosition() {
        val entity = ArticleSummaryDto(id = "a1").toEntity(ArticleQuery(), page = 3, positionInPage = 7)
        assertEquals(3, entity.page)
        assertEquals(7, entity.positionInPage)
    }

    @Test
    fun toDtoRoundtripPreservesKeyFields() {
        val original = ArticleSummaryDto(
            id = "article-1",
            category = ArticleCategory.SpecialTopic,
            title = "非遗专题",
            summary = "摘要",
            publishedAt = "2025-01-01",
            coverImage = MediaAssetDto(displayUrl = "https://img.test/c.jpg"),
            sourceUrl = "https://src.test/1",
        )
        val entity = original.toEntity(ArticleQuery(category = ArticleCategory.SpecialTopic), page = 1, positionInPage = 0)
        val roundtripped = entity.toDto()
        assertEquals(original.id, roundtripped.id)
        assertEquals(original.category, roundtripped.category)
        assertEquals(original.title, roundtripped.title)
        assertEquals(original.summary, roundtripped.summary)
        assertEquals(original.publishedAt, roundtripped.publishedAt)
        assertEquals(original.coverImage?.displayUrl, roundtripped.coverImage?.displayUrl)
        assertEquals(original.sourceUrl, roundtripped.sourceUrl)
    }

    @Test
    fun toDtoHandlesNullCoverImage() {
        val entity = ArticleEntity(
            id = "a1",
            queryKey = "news||",
            category = "news",
            title = "T",
            summary = null,
            publishedAt = null,
            coverImageJson = null,
            sourceUrl = null,
            page = 1,
            positionInPage = 0,
        )
        val dto = entity.toDto()
        assertNull(dto.coverImage)
    }

    @Test
    fun toDtoHandlesUnknownCategoryGracefully() {
        val entity = ArticleEntity(
            id = "a1",
            queryKey = "unknown||",
            category = "unknownCategory",
            title = "T",
            summary = null,
            publishedAt = null,
            coverImageJson = null,
            sourceUrl = null,
            page = 1,
            positionInPage = 0,
        )
        val dto = entity.toDto()
        assertEquals(ArticleCategory.News, dto.category)
    }

    // endregion

    // region ArticleDetailDto ↔ ArticleDetailEntity

    @Test
    fun detailToEntityUsesDtoIdFirst() {
        val dto = ArticleDetailDto(id = "detail-1", title = "D", category = ArticleCategory.News)
        val entity = dto.toEntity(ArticleCategory.News, sourceId = null, sourceUrl = null, updatedAtEpochMillis = 1000L)
        assertEquals("detail-1", entity.id)
    }

    @Test
    fun detailToEntityFallsBackToSourceId() {
        val dto = ArticleDetailDto(id = null, title = "D")
        val entity = dto.toEntity(ArticleCategory.News, sourceId = "src-123", sourceUrl = null, updatedAtEpochMillis = 1000L)
        assertEquals("src-123", entity.id)
    }

    @Test
    fun detailToEntityFallsBackToSourceUrl() {
        val dto = ArticleDetailDto(id = null, title = "D")
        val entity = dto.toEntity(ArticleCategory.News, sourceId = null, sourceUrl = "https://s.test/1", updatedAtEpochMillis = 1000L)
        assertEquals("https://s.test/1", entity.id)
    }

    @Test
    fun detailToEntityFallsBackToDtoSourceUrl() {
        val dto = ArticleDetailDto(id = null, title = "D", sourceUrl = "https://dto.test/1")
        val entity = dto.toEntity(ArticleCategory.News, sourceId = null, sourceUrl = null, updatedAtEpochMillis = 1000L)
        assertEquals("https://dto.test/1", entity.id)
    }

    @Test
    fun detailToEntityFallsBackToTitle() {
        val dto = ArticleDetailDto(id = null, title = "My Article")
        val entity = dto.toEntity(ArticleCategory.News, sourceId = null, sourceUrl = null, updatedAtEpochMillis = 1000L)
        assertEquals("My Article", entity.id)
    }

    @Test
    fun detailToEntityStoresAllMetadata() {
        val dto = ArticleDetailDto(
            id = "detail-1",
            title = "Title",
            summary = "Summary",
            publishedAt = "2025-06-01",
            sourceUrl = "https://src.test/1",
            sourceName = "Source",
            author = "Author",
            editor = "Editor",
        )
        val entity = dto.toEntity(ArticleCategory.News, sourceId = "src-1", sourceUrl = null, updatedAtEpochMillis = 2000L)
        assertEquals("src-1", entity.sourceId)
        assertEquals("news", entity.category)
        assertEquals("Title", entity.title)
        assertEquals("Summary", entity.summary)
        assertEquals("2025-06-01", entity.publishedAt)
        assertEquals("https://src.test/1", entity.sourceUrl)
        assertEquals("Source", entity.sourceName)
        assertEquals("Author", entity.author)
        assertEquals("Editor", entity.editor)
        assertEquals(2000L, entity.updatedAtEpochMillis)
    }

    @Test
    fun detailToEntitySerializesContentBlocksAndReferences() {
        val dto = ArticleDetailDto(
            id = "d1",
            title = "T",
            contentBlocks = listOf(
                com.duckylife.heritage.modern.core.network.dto.ArticleContentBlockDto(
                    type = com.duckylife.heritage.modern.core.network.dto.ArticleContentBlockType.Text,
                    text = "body",
                ),
            ),
            relatedArticles = listOf(
                com.duckylife.heritage.modern.core.network.dto.ArticleReferenceDto(title = "Related"),
            ),
        )
        val entity = dto.toEntity(ArticleCategory.News, sourceId = null, sourceUrl = null, updatedAtEpochMillis = 0L)
        assertNotNull(entity.contentBlocksJson)
        assertNotNull(entity.relatedArticlesJson)
        // JSON strings should be non-empty
        assert(entity.contentBlocksJson.contains("body"))
        assert(entity.relatedArticlesJson.contains("Related"))
    }

    @Test
    fun detailToDtoRoundtripPreservesContentBlocks() {
        val original = ArticleDetailDto(
            id = "d1",
            title = "T",
            contentBlocks = listOf(
                com.duckylife.heritage.modern.core.network.dto.ArticleContentBlockDto(
                    type = com.duckylife.heritage.modern.core.network.dto.ArticleContentBlockType.Heading,
                    text = "Section",
                ),
                com.duckylife.heritage.modern.core.network.dto.ArticleContentBlockDto(
                    type = com.duckylife.heritage.modern.core.network.dto.ArticleContentBlockType.Text,
                    text = "Paragraph",
                ),
            ),
            relatedArticles = listOf(
                com.duckylife.heritage.modern.core.network.dto.ArticleReferenceDto(
                    title = "Related 1",
                    sourceId = "sid-1",
                ),
            ),
        )
        val entity = original.toEntity(ArticleCategory.SpecialTopic, sourceId = null, sourceUrl = null, updatedAtEpochMillis = 1000L)
        val roundtripped = entity.toDto()
        assertEquals(2, roundtripped.contentBlocks.size)
        assertEquals(com.duckylife.heritage.modern.core.network.dto.ArticleContentBlockType.Heading, roundtripped.contentBlocks[0].type)
        assertEquals("Section", roundtripped.contentBlocks[0].text)
        assertEquals("Paragraph", roundtripped.contentBlocks[1].text)
        assertEquals(1, roundtripped.relatedArticles.size)
        assertEquals("Related 1", roundtripped.relatedArticles[0].title)
        assertEquals("sid-1", roundtripped.relatedArticles[0].sourceId)
    }

    @Test
    fun detailToDtoHandlesNullCoverImage() {
        val entity = ArticleDetailEntity(
            id = "d1",
            sourceId = null,
            category = "news",
            title = "T",
            summary = null,
            publishedAt = null,
            coverImageJson = null,
            sourceUrl = null,
            sourceName = null,
            author = null,
            editor = null,
            contentBlocksJson = "[]",
            relatedArticlesJson = "[]",
            updatedAtEpochMillis = 0L,
        )
        val dto = entity.toDto()
        assertNull(dto.coverImage)
    }

    @Test
    fun detailToDtoDecodesCoverImage() {
        val image = MediaAssetDto(displayUrl = "https://img.test/detail.jpg", altText = "cover")
        val entity = ArticleDetailEntity(
            id = "d1",
            sourceId = null,
            category = "news",
            title = "T",
            summary = null,
            publishedAt = null,
            coverImageJson = HeritageJson.encodeToString(image),
            sourceUrl = null,
            sourceName = null,
            author = null,
            editor = null,
            contentBlocksJson = "[]",
            relatedArticlesJson = "[]",
            updatedAtEpochMillis = 0L,
        )
        val dto = entity.toDto()
        assertNotNull(dto.coverImage)
        assertEquals("https://img.test/detail.jpg", dto.coverImage?.displayUrl)
        assertEquals("cover", dto.coverImage?.altText)
    }

    // endregion
}
