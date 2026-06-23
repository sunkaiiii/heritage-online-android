package com.duckylife.heritage.modern.ui.preview

import com.duckylife.heritage.modern.core.network.HeritageUrlResolver
import com.duckylife.heritage.modern.core.network.dto.ArticleContentBlockDto
import com.duckylife.heritage.modern.core.network.dto.ArticleContentBlockType
import com.duckylife.heritage.modern.core.network.dto.MediaAssetDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ImagePreviewUrlsTest {

    private val resolver = HeritageUrlResolver("https://example.com")

    // ── previewUrl() priority ──

    @Test
    fun `previewUrl returns originalUrl first for highest quality`() {
        val asset = MediaAssetDto(
            displayUrl = "https://example.com/display.jpg",
            thumbnailUrl = "https://example.com/thumb.jpg",
            originalUrl = "https://example.com/original.jpg",
            sourceUrl = "https://example.com/source.jpg",
        )
        assertEquals("https://example.com/original.jpg", asset.previewUrl())
    }

    @Test
    fun `previewUrl falls back to displayUrl when originalUrl is null`() {
        val asset = MediaAssetDto(
            displayUrl = "https://example.com/display.jpg",
            thumbnailUrl = "https://example.com/thumb.jpg",
            originalUrl = null,
            sourceUrl = "https://example.com/source.jpg",
        )
        assertEquals("https://example.com/display.jpg", asset.previewUrl())
    }

    @Test
    fun `previewUrl falls back to thumbnailUrl when originalUrl and displayUrl are null`() {
        val asset = MediaAssetDto(
            displayUrl = null,
            thumbnailUrl = null,
            originalUrl = "https://example.com/original.jpg",
            sourceUrl = "https://example.com/source.jpg",
        )
        assertEquals("https://example.com/original.jpg", asset.previewUrl())
    }

    @Test
    fun `previewUrl falls back to sourceUrl when only sourceUrl is set`() {
        val asset = MediaAssetDto(
            displayUrl = null,
            thumbnailUrl = null,
            originalUrl = null,
            sourceUrl = "https://example.com/source.jpg",
        )
        assertEquals("https://example.com/source.jpg", asset.previewUrl())
    }

    @Test
    fun `previewUrl returns null when all fields are null`() {
        val asset = MediaAssetDto(
            displayUrl = null,
            thumbnailUrl = null,
            originalUrl = null,
            sourceUrl = null,
        )
        assertNull(asset.previewUrl())
    }

    // ── buildPreviewUrls ordering ──

    @Test
    fun `buildPreviewUrls returns empty list when all inputs are empty`() {
        val urls = buildPreviewUrls(resolver = resolver)
        assertEquals(emptyList<String>(), urls)
    }

    @Test
    fun `buildPreviewUrls includes cover image first`() {
        val cover = MediaAssetDto(displayUrl = "https://example.com/cover.jpg")
        val urls = buildPreviewUrls(resolver = resolver, coverImage = cover)
        assertEquals(listOf("https://example.com/cover.jpg"), urls)
    }

    @Test
    fun `buildPreviewUrls orders cover then gallery then content blocks`() {
        val cover = MediaAssetDto(displayUrl = "https://example.com/cover.jpg")
        val gallery = listOf(
            MediaAssetDto(displayUrl = "https://example.com/gallery1.jpg"),
            MediaAssetDto(displayUrl = "https://example.com/gallery2.jpg"),
        )
        val contentBlocks = listOf(
            ArticleContentBlockDto(
                type = ArticleContentBlockType.Image,
                image = MediaAssetDto(displayUrl = "https://example.com/body1.jpg"),
            ),
            ArticleContentBlockDto(
                type = ArticleContentBlockType.Image,
                image = MediaAssetDto(displayUrl = "https://example.com/body2.jpg"),
            ),
        )
        val urls = buildPreviewUrls(resolver = resolver, coverImage = cover, gallery = gallery, contentBlocks = contentBlocks)
        assertEquals(
            listOf(
                "https://example.com/cover.jpg",
                "https://example.com/gallery1.jpg",
                "https://example.com/gallery2.jpg",
                "https://example.com/body1.jpg",
                "https://example.com/body2.jpg",
            ),
            urls,
        )
    }

    @Test
    fun `buildPreviewUrls filters out images with no previewable URL`() {
        val cover = MediaAssetDto(displayUrl = null, thumbnailUrl = null, originalUrl = null, sourceUrl = null)
        val gallery = listOf(
            MediaAssetDto(displayUrl = "https://example.com/gallery1.jpg"),
            MediaAssetDto(displayUrl = null, thumbnailUrl = null, originalUrl = null, sourceUrl = null),
            MediaAssetDto(displayUrl = "https://example.com/gallery3.jpg"),
        )
        val contentBlocks = listOf(
            ArticleContentBlockDto(type = ArticleContentBlockType.Text, text = "Some text"),
            ArticleContentBlockDto(
                type = ArticleContentBlockType.Image,
                image = MediaAssetDto(displayUrl = null, thumbnailUrl = null, originalUrl = null, sourceUrl = null),
            ),
            ArticleContentBlockDto(
                type = ArticleContentBlockType.Image,
                image = MediaAssetDto(displayUrl = "https://example.com/body1.jpg"),
            ),
        )
        val urls = buildPreviewUrls(resolver = resolver, coverImage = cover, gallery = gallery, contentBlocks = contentBlocks)
        assertEquals(
            listOf(
                "https://example.com/gallery1.jpg",
                "https://example.com/gallery3.jpg",
                "https://example.com/body1.jpg",
            ),
            urls,
        )
    }

    @Test
    fun `buildPreviewUrls skips non-image content blocks`() {
        val contentBlocks = listOf(
            ArticleContentBlockDto(type = ArticleContentBlockType.Heading, text = "Heading"),
            ArticleContentBlockDto(type = ArticleContentBlockType.Text, text = "Body text"),
            ArticleContentBlockDto(
                type = ArticleContentBlockType.Image,
                image = MediaAssetDto(displayUrl = "https://example.com/img.jpg"),
            ),
        )
        val urls = buildPreviewUrls(resolver = resolver, contentBlocks = contentBlocks)
        assertEquals(listOf("https://example.com/img.jpg"), urls)
    }

    // ── Directory gallery index positioning ──

    @Test
    fun `directory gallery index starts at 0 when no cover image`() {
        val gallery = listOf(
            MediaAssetDto(displayUrl = "https://example.com/g1.jpg"),
            MediaAssetDto(displayUrl = "https://example.com/g2.jpg"),
        )
        val urls = buildPreviewUrls(resolver = resolver, gallery = gallery)
        // gallery[0] is at index 0, gallery[1] at index 1
        assertEquals("https://example.com/g1.jpg", urls[0])
        assertEquals("https://example.com/g2.jpg", urls[1])
    }

    @Test
    fun `directory gallery index starts at 1 when cover image exists`() {
        val cover = MediaAssetDto(displayUrl = "https://example.com/cover.jpg")
        val gallery = listOf(
            MediaAssetDto(displayUrl = "https://example.com/g1.jpg"),
            MediaAssetDto(displayUrl = "https://example.com/g2.jpg"),
        )
        val urls = buildPreviewUrls(resolver = resolver, coverImage = cover, gallery = gallery)
        // cover = index 0, gallery[0] = index 1, gallery[1] = index 2
        assertEquals("https://example.com/cover.jpg", urls[0])
        assertEquals("https://example.com/g1.jpg", urls[1])
        assertEquals("https://example.com/g2.jpg", urls[2])
    }

    @Test
    fun `content block image index accounts for cover and gallery`() {
        val cover = MediaAssetDto(displayUrl = "https://example.com/cover.jpg")
        val gallery = listOf(
            MediaAssetDto(displayUrl = "https://example.com/g1.jpg"),
            MediaAssetDto(displayUrl = "https://example.com/g2.jpg"),
        )
        val contentBlocks = listOf(
            ArticleContentBlockDto(
                type = ArticleContentBlockType.Image,
                image = MediaAssetDto(displayUrl = "https://example.com/body1.jpg"),
            ),
            ArticleContentBlockDto(
                type = ArticleContentBlockType.Image,
                image = MediaAssetDto(displayUrl = "https://example.com/body2.jpg"),
            ),
        )
        val urls = buildPreviewUrls(resolver = resolver, coverImage = cover, gallery = gallery, contentBlocks = contentBlocks)
        // cover=0, g1=1, g2=2, body1=3, body2=4
        assertEquals(5, urls.size)
        assertEquals("https://example.com/body1.jpg", urls[3])
        assertEquals("https://example.com/body2.jpg", urls[4])
    }

    // ── Smoke: article / directory / inheritor preview URL building ──

    @Test
    fun `smoke test article detail preview URLs`() {
        val cover = MediaAssetDto(displayUrl = "https://example.com/article-cover.jpg")
        val contentBlocks = listOf(
            ArticleContentBlockDto(type = ArticleContentBlockType.Text, text = "Intro paragraph"),
            ArticleContentBlockDto(
                type = ArticleContentBlockType.Image,
                image = MediaAssetDto(displayUrl = "https://example.com/article-img1.jpg"),
            ),
            ArticleContentBlockDto(type = ArticleContentBlockType.Heading, text = "Section 2"),
            ArticleContentBlockDto(
                type = ArticleContentBlockType.Image,
                image = MediaAssetDto(displayUrl = "https://example.com/article-img2.jpg"),
            ),
        )
        val urls = buildPreviewUrls(resolver = resolver, coverImage = cover, contentBlocks = contentBlocks)
        assertEquals(3, urls.size)
        assertEquals("https://example.com/article-cover.jpg", urls[0])
        assertEquals("https://example.com/article-img1.jpg", urls[1])
        assertEquals("https://example.com/article-img2.jpg", urls[2])
    }

    @Test
    fun `smoke test directory detail preview URLs with gallery`() {
        val cover = MediaAssetDto(displayUrl = "https://example.com/dir-cover.jpg")
        val gallery = listOf(
            MediaAssetDto(thumbnailUrl = "https://example.com/dir-g1.jpg"),
            MediaAssetDto(thumbnailUrl = "https://example.com/dir-g2.jpg"),
            MediaAssetDto(thumbnailUrl = "https://example.com/dir-g3.jpg"),
        )
        val contentBlocks = listOf(
            ArticleContentBlockDto(
                type = ArticleContentBlockType.Image,
                image = MediaAssetDto(sourceUrl = "https://example.com/dir-body.jpg"),
            ),
        )
        val urls = buildPreviewUrls(resolver = resolver, coverImage = cover, gallery = gallery, contentBlocks = contentBlocks)
        assertEquals(5, urls.size)
        assertEquals("https://example.com/dir-cover.jpg", urls[0])
        assertEquals("https://example.com/dir-g1.jpg", urls[1])
        assertEquals("https://example.com/dir-g2.jpg", urls[2])
        assertEquals("https://example.com/dir-g3.jpg", urls[3])
        assertEquals("https://example.com/dir-body.jpg", urls[4])
    }

    @Test
    fun `smoke test inheritor detail preview URLs`() {
        val cover = MediaAssetDto(originalUrl = "https://example.com/inheritor-cover.jpg")
        val contentBlocks = listOf(
            ArticleContentBlockDto(type = ArticleContentBlockType.Text, text = "Biography text"),
            ArticleContentBlockDto(
                type = ArticleContentBlockType.Image,
                image = MediaAssetDto(displayUrl = "https://example.com/inheritor-img.jpg"),
            ),
        )
        val urls = buildPreviewUrls(resolver = resolver, coverImage = cover, contentBlocks = contentBlocks)
        assertEquals(2, urls.size)
        assertEquals("https://example.com/inheritor-cover.jpg", urls[0])
        assertEquals("https://example.com/inheritor-img.jpg", urls[1])
    }

    @Test
    fun `smoke test no preview when all images have null URLs`() {
        val cover = MediaAssetDto()
        val gallery = listOf(MediaAssetDto())
        val contentBlocks = listOf(
            ArticleContentBlockDto(
                type = ArticleContentBlockType.Image,
                image = MediaAssetDto(),
            ),
        )
        val urls = buildPreviewUrls(resolver = resolver, coverImage = cover, gallery = gallery, contentBlocks = contentBlocks)
        assertEquals(emptyList<String>(), urls)
    }
}
