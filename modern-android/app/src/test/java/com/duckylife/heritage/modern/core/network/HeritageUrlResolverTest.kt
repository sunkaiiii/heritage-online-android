package com.duckylife.heritage.modern.core.network

import com.duckylife.heritage.modern.core.network.dto.MediaAssetDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HeritageUrlResolverTest {

    private val resolver = HeritageUrlResolver("https://tuantuan.myds.me:28887")

    @Test
    fun `resolve returns null for null blank or whitespace`() {
        assertNull(resolver.resolve(null))
        assertNull(resolver.resolve(""))
        assertNull(resolver.resolve("   "))
    }

    @Test
    fun `resolve keeps absolute https url unchanged`() {
        val url = "https://cdn.example.com/images/photo.jpg"
        assertEquals(url, resolver.resolve(url))
    }

    @Test
    fun `resolve keeps absolute http url unchanged`() {
        val url = "http://legacy.example.com/images/photo.jpg"
        assertEquals(url, resolver.resolve(url))
    }

    @Test
    fun `resolve joins root relative url with base scheme and authority`() {
        assertEquals(
            "https://tuantuan.myds.me:28887/images/photo.jpg",
            resolver.resolve("/images/photo.jpg"),
        )
    }

    @Test
    fun `resolve joins non root relative url with base root`() {
        assertEquals(
            "https://tuantuan.myds.me:28887/uploads/photo.jpg",
            resolver.resolve("uploads/photo.jpg"),
        )
    }

    @Test
    fun `resolve ignores base path prefix for root relative urls`() {
        val prefixedResolver = HeritageUrlResolver("https://example.com/api/v1")
        assertEquals(
            "https://example.com/images/photo.jpg",
            prefixedResolver.resolve("/images/photo.jpg"),
        )
    }

    @Test
    fun `resolve appends non root relative url to base with path prefix`() {
        val prefixedResolver = HeritageUrlResolver("https://example.com/api/v1")
        assertEquals(
            "https://example.com/api/v1/images/photo.jpg",
            prefixedResolver.resolve("images/photo.jpg"),
        )
    }

    @Test
    fun `resolve returns null for localhost variants`() {
        assertNull(resolver.resolve("http://localhost:5078/images/photo.jpg"))
        assertNull(resolver.resolve("https://localhost/images/photo.jpg"))
        assertNull(resolver.resolve("http://127.0.0.1:5078/images/photo.jpg"))
        assertNull(resolver.resolve("https://127.0.0.1/images/photo.jpg"))
        assertNull(resolver.resolve("http://[::1]:5078/images/photo.jpg"))
        assertNull(resolver.resolve("https://[::1]/images/photo.jpg"))
    }

    @Test
    fun `resolve handles base url with trailing slash`() {
        val trailingSlashResolver = HeritageUrlResolver("https://example.com/api/")
        assertEquals(
            "https://example.com/api/images/photo.jpg",
            trailingSlashResolver.resolve("images/photo.jpg"),
        )
    }

    @Test
    fun `resolvedBestUrl picks display url then thumbnail then original then source`() {
        val asset = MediaAssetDto(
            sourceUrl = "source.jpg",
            originalUrl = "original.jpg",
            displayUrl = "display.jpg",
            thumbnailUrl = "thumbnail.jpg",
        )
        assertEquals(
            "https://tuantuan.myds.me:28887/display.jpg",
            asset.resolvedBestUrl(resolver),
        )
    }

    @Test
    fun `resolvedBestUrl returns null when all candidates are localhost`() {
        val asset = MediaAssetDto(
            displayUrl = "http://localhost/image.jpg",
            thumbnailUrl = "http://127.0.0.1/image.jpg",
        )
        assertNull(asset.resolvedBestUrl(resolver))
    }

    @Test
    fun `resolvedPreviewUrl resolves root relative candidate`() {
        val asset = MediaAssetDto(thumbnailUrl = "/thumbs/photo.jpg")
        assertEquals(
            "https://tuantuan.myds.me:28887/thumbs/photo.jpg",
            asset.resolvedPreviewUrl(resolver),
        )
    }
}
