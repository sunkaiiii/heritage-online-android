package com.duckylife.heritage.modern.feature.my

import com.duckylife.heritage.modern.core.database.entity.SavedContentEntity
import com.duckylife.heritage.modern.core.profile.FakeLocalUserSyncRepository
import com.duckylife.heritage.modern.core.profile.ProfileFavorite
import com.duckylife.heritage.modern.core.profile.ProfileHistoryItem
import com.duckylife.heritage.modern.core.profile.ProfileLearningProgress
import com.duckylife.heritage.modern.core.profile.ProfileSyncStatus
import com.duckylife.heritage.modern.core.saved.FakeSavedContentRepository
import com.duckylife.heritage.modern.core.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MyPageViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun favorites_emitsMergedLegacyAndSyncItems() = runTest {
        val savedContent = FakeSavedContentRepository()
        val sync = FakeLocalUserSyncRepository()
        val viewModel = MyPageViewModel(savedContent, sync)

        savedContent.toggleFavorite(
            testSavedContentSnapshot(id = "a1", type = "article", title = "Legacy Article"),
        )
        sync.toggleFavorite("article", "a2", titleSnapshot = "Synced Article")
        advanceUntilIdle()

        val favorites = viewModel.favorites.first()
        assertEquals(2, favorites.size)
        assertTrue(favorites.any { it.targetId == "a1" && it.title == "Legacy Article" })
        assertTrue(favorites.any { it.targetId == "a2" && it.title == "Synced Article" })
    }

    @Test
    fun favorites_syncItemOverridesLegacyItemWithSameKey() = runTest {
        val savedContent = FakeSavedContentRepository()
        val sync = FakeLocalUserSyncRepository()
        val viewModel = MyPageViewModel(savedContent, sync)

        savedContent.toggleFavorite(
            testSavedContentSnapshot(id = "a1", type = "article", title = "Legacy Title"),
        )
        sync.toggleFavorite("article", "a1", titleSnapshot = "Synced Title")
        advanceUntilIdle()

        val favorites = viewModel.favorites.first()
        assertEquals(1, favorites.size)
        assertEquals("Synced Title", favorites[0].title)
        assertEquals(emptyList<String>(), favorites[0].tags)
    }

    @Test
    fun unfavorite_removesFromBothRepositories() = runTest {
        val savedContent = FakeSavedContentRepository()
        val sync = FakeLocalUserSyncRepository()
        val viewModel = MyPageViewModel(savedContent, sync)

        sync.toggleFavorite("article", "a1", titleSnapshot = "Article")
        savedContent.toggleFavorite(testSavedContentSnapshot(id = "a1", type = "article"))
        advanceUntilIdle()

        val item = viewModel.favorites.first().first()
        viewModel.unfavorite(item)
        advanceUntilIdle()

        assertTrue(viewModel.favorites.first().isEmpty())
        assertTrue(sync.favorites().first().none { it.targetId == "a1" })
        assertTrue(savedContent.allEntities.values.none { it.isFavorite })
    }

    @Test
    fun history_emitsSyncItems() = runTest {
        val savedContent = FakeSavedContentRepository()
        val sync = FakeLocalUserSyncRepository()
        val viewModel = MyPageViewModel(savedContent, sync)

        sync.recordHistory("article", "a1", titleSnapshot = "Article One")
        sync.recordHistory("directoryItem", "d1", titleSnapshot = "Directory One")
        advanceUntilIdle()

        val history = viewModel.history.first()
        assertEquals(2, history.size)
        assertTrue(history.any { it.targetId == "a1" && it.title == "Article One" })
        assertTrue(history.any { it.targetId == "d1" && it.title == "Directory One" })
    }

    @Test
    fun history_mergesLegacyRecentWhenSyncEmpty() = runTest {
        val savedContent = FakeSavedContentRepository()
        val sync = FakeLocalUserSyncRepository()
        val viewModel = MyPageViewModel(savedContent, sync)

        savedContent.recordViewed(
            testSavedContentSnapshot(id = "a1", type = "article", title = "Recent Article"),
        )
        advanceUntilIdle()

        val history = viewModel.history.first()
        assertEquals(1, history.size)
        assertEquals("a1", history[0].targetId)
        assertEquals("Recent Article", history[0].title)
    }

    @Test
    fun history_ordersByViewedAtDescending() = runTest {
        val savedContent = FakeSavedContentRepository()
        val sync = FakeLocalUserSyncRepository()
        val viewModel = MyPageViewModel(savedContent, sync)

        sync.recordHistory("article", "old", titleSnapshot = "Old")
        sync.recordHistory("article", "new", titleSnapshot = "New")
        advanceUntilIdle()

        val history = viewModel.history.first()
        assertEquals("new", history[0].targetId)
        assertEquals("old", history[1].targetId)
    }

    @Test
    fun clearHistory_clearsBothRepositories() = runTest {
        val savedContent = FakeSavedContentRepository()
        val sync = FakeLocalUserSyncRepository()
        val viewModel = MyPageViewModel(savedContent, sync)

        sync.recordHistory("article", "a1", titleSnapshot = "Article")
        savedContent.recordViewed(testSavedContentSnapshot(id = "a2", type = "article"))
        advanceUntilIdle()

        viewModel.clearHistory()
        advanceUntilIdle()

        assertTrue(viewModel.history.first().isEmpty())
        assertTrue(sync.history().first().isEmpty())
        assertTrue(savedContent.recentlyViewed().first().isEmpty())
    }

    @Test
    fun syncNow_callsRepository() = runTest {
        val savedContent = FakeSavedContentRepository()
        val sync = FakeLocalUserSyncRepository()
        val viewModel = MyPageViewModel(savedContent, sync)

        assertFalse(sync.syncNowCalled)
        viewModel.syncNow()
        advanceUntilIdle()

        assertTrue(sync.syncNowCalled)
    }

    @Test
    fun learningProgress_exposesSyncProgress() = runTest {
        val savedContent = FakeSavedContentRepository()
        val sync = FakeLocalUserSyncRepository()
        val viewModel = MyPageViewModel(savedContent, sync)

        sync.updateProgress("route-1", routeTitle = "Route One", completedStepIds = listOf("s1", "s2"))
        advanceUntilIdle()

        val progress = viewModel.learningProgress.first()
        assertEquals(1, progress.size)
        assertEquals("route-1", progress[0].routeId)
        assertEquals("Route One", progress[0].routeTitle)
    }

    private fun testSavedContentSnapshot(
        id: String,
        type: String,
        title: String = "Title",
    ) = com.duckylife.heritage.modern.core.saved.SavedContentSnapshot(
        contentType = when (type) {
            "article" -> com.duckylife.heritage.modern.core.saved.SavedContentType.Article
            "directoryItem" -> com.duckylife.heritage.modern.core.saved.SavedContentType.DirectoryItem
            "inheritor" -> com.duckylife.heritage.modern.core.saved.SavedContentType.Inheritor
            else -> error("unknown type")
        },
        id = id,
        title = title,
        target = com.duckylife.heritage.modern.core.saved.SavedContentTarget(
            id = id,
            category = null,
            kind = null,
        ),
    )
}
