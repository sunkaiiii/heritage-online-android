package com.duckylife.heritage.modern.core.saved

import com.duckylife.heritage.modern.core.database.entity.SavedContentEntity
import com.duckylife.heritage.modern.core.network.HeritageJson
import com.duckylife.heritage.modern.core.network.dto.MediaAssetDto
import com.duckylife.heritage.modern.feature.my.extractDisplayUrl
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SavedContentRepositoryTest {

    // region extractDisplayUrl

    @Test
    fun extractsDisplayUrlFromValidJson() {
        val json = HeritageJson.encodeToString(
            MediaAssetDto(displayUrl = "https://img.test/display.jpg", thumbnailUrl = "https://img.test/thumb.jpg"),
        )
        assertEquals("https://img.test/display.jpg", extractDisplayUrl(json))
    }

    @Test
    fun fallsBackToThumbnailWhenDisplayUrlMissing() {
        val json = HeritageJson.encodeToString(
            MediaAssetDto(displayUrl = null, thumbnailUrl = "https://img.test/thumb.jpg"),
        )
        assertEquals("https://img.test/thumb.jpg", extractDisplayUrl(json))
    }

    @Test
    fun returnsNullForInvalidJson() {
        assertNull(extractDisplayUrl("not valid json {{{"))
    }

    @Test
    fun returnsNullForNullInput() {
        assertNull(extractDisplayUrl(null))
    }

    @Test
    fun returnsNullForBlankInput() {
        assertNull(extractDisplayUrl("  "))
    }

    @Test
    fun returnsNullWhenBothUrlsNull() {
        val json = HeritageJson.encodeToString(MediaAssetDto(displayUrl = null, thumbnailUrl = null))
        assertNull(extractDisplayUrl(json))
    }

    // endregion

    // region SavedContentRepository behavior

    @Test
    fun toggleFavoriteAddsToFavorites() = runTest {
        val repo = FakeSavedContentRepository()
        val snapshot = testArticleSnapshot(id = "a1", title = "Test Article")

        repo.toggleFavorite(snapshot)

        val favorites = repo.favorites().first()
        assertEquals(1, favorites.size)
        assertEquals("a1", favorites[0].contentKey)
        assertTrue(favorites[0].isFavorite)
        assertNotNull(favorites[0].favoritedAt)
    }

    @Test
    fun toggleFavoriteRemovesFromFavorites() = runTest {
        val repo = FakeSavedContentRepository()
        val snapshot = testArticleSnapshot(id = "a1", title = "Test Article")

        // 收藏
        repo.toggleFavorite(snapshot)
        assertEquals(1, repo.favorites().first().size)

        // 取消收藏
        repo.toggleFavorite(snapshot)
        assertEquals(0, repo.favorites().first().size)
    }

    @Test
    fun recordViewedDoesNotAutoFavorite() = runTest {
        val repo = FakeSavedContentRepository()
        val snapshot = testArticleSnapshot(id = "a1", title = "Test Article")

        repo.recordViewed(snapshot)

        val favorites = repo.favorites().first()
        assertTrue(favorites.isEmpty())
    }

    @Test
    fun recordViewedPreservesExistingFavorite() = runTest {
        val repo = FakeSavedContentRepository()
        val snapshot = testArticleSnapshot(id = "a1", title = "Test Article")

        // 先收藏
        repo.toggleFavorite(snapshot)
        val favAfterToggle = repo.favorites().first()
        assertEquals(1, favAfterToggle.size)

        // 再浏览同一内容
        repo.recordViewed(snapshot)

        // 收藏仍在
        val favorites = repo.favorites().first()
        assertEquals(1, favorites.size)
        assertTrue(favorites[0].isFavorite)
    }

    @Test
    fun favoritesSortedByFavoritedAtDesc() = runTest {
        val repo = FakeSavedContentRepository()
        repo.toggleFavorite(testArticleSnapshot(id = "a1", title = "First"))
        Thread.sleep(1) // ensure different timestamps
        repo.toggleFavorite(testArticleSnapshot(id = "a2", title = "Second"))

        val favorites = repo.favorites().first()
        assertEquals(2, favorites.size)
        // "Second" was favorited later, should be first
        assertEquals("a2", favorites[0].contentKey)
    }

    @Test
    fun recentlyViewedSortedByLastViewedAtDesc() = runTest {
        val repo = FakeSavedContentRepository()
        repo.recordViewed(testArticleSnapshot(id = "a1", title = "First"))
        Thread.sleep(1)
        repo.recordViewed(testArticleSnapshot(id = "a2", title = "Second"))

        val recent = repo.recentlyViewed().first()
        assertEquals(2, recent.size)
        // "Second" was viewed later, should be first
        assertEquals("a2", recent[0].contentKey)
    }

    // endregion

    // region computeKey

    @Test
    fun computeKeyPrefersId() {
        val target = SavedContentTarget(id = "article-1", sourceId = "sid-1", sourceUrl = "https://test.test/1")
        assertEquals("article-1", SavedContentRepository.computeKey(target))
    }

    @Test
    fun computeKeyFallsBackToSourceUrl() {
        val target = SavedContentTarget(id = null, sourceId = "sid-1", sourceUrl = "https://test.test/1")
        assertEquals("https://test.test/1", SavedContentRepository.computeKey(target))
    }

    @Test
    fun computeKeyFallsBackToSourceId() {
        val target = SavedContentTarget(id = null, sourceId = "sid-1", sourceUrl = null)
        assertEquals("sid-1", SavedContentRepository.computeKey(target))
    }

    // endregion

    private fun testArticleSnapshot(id: String, title: String) = SavedContentSnapshot(
        contentType = SavedContentType.Article,
        id = id,
        title = title,
        summary = "Test summary",
        target = SavedContentTarget(id = id),
    )
}
