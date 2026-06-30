package com.duckylife.heritage.modern.feature.my

import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.core.database.entity.SavedContentEntity
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphNodeDto
import com.duckylife.heritage.modern.core.network.dto.advanced.GraphNodeType
import com.duckylife.heritage.modern.core.network.dto.advanced.JourneyItemDto
import com.duckylife.heritage.modern.core.network.dto.advanced.JourneyResponseDto
import com.duckylife.heritage.modern.core.network.dto.advanced.JourneySignalsDto
import com.duckylife.heritage.modern.core.network.dto.advanced.JourneyStrategy
import com.duckylife.heritage.modern.core.profile.FakeJourneyRepository
import com.duckylife.heritage.modern.core.profile.FakeLocalUserSyncRepository
import com.duckylife.heritage.modern.core.profile.ProfileFavorite
import com.duckylife.heritage.modern.core.profile.ProfileHistoryItem
import com.duckylife.heritage.modern.core.profile.ProfileLearningProgress
import com.duckylife.heritage.modern.core.profile.ProfileSyncStatus
import com.duckylife.heritage.modern.core.saved.FakeSavedContentRepository
import com.duckylife.heritage.modern.core.testing.MainDispatcherRule
import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceTimeBy
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
        val journeys = FakeJourneyRepository()
        val viewModel = MyPageViewModel(savedContent, sync, journeys)

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
        val journeys = FakeJourneyRepository()
        val viewModel = MyPageViewModel(savedContent, sync, journeys)

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
        val journeys = FakeJourneyRepository()
        val viewModel = MyPageViewModel(savedContent, sync, journeys)

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
        val journeys = FakeJourneyRepository()
        val viewModel = MyPageViewModel(savedContent, sync, journeys)

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
        val journeys = FakeJourneyRepository()
        val viewModel = MyPageViewModel(savedContent, sync, journeys)

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
        val journeys = FakeJourneyRepository()
        val viewModel = MyPageViewModel(savedContent, sync, journeys)

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
        val journeys = FakeJourneyRepository()
        val viewModel = MyPageViewModel(savedContent, sync, journeys)

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
        val journeys = FakeJourneyRepository()
        val viewModel = MyPageViewModel(savedContent, sync, journeys)

        assertFalse(sync.syncNowCalled)
        viewModel.syncNow()
        advanceUntilIdle()

        assertTrue(sync.syncNowCalled)
    }

    @Test
    fun learningProgress_exposesSyncProgress() = runTest {
        val savedContent = FakeSavedContentRepository()
        val sync = FakeLocalUserSyncRepository()
        val journeys = FakeJourneyRepository()
        val viewModel = MyPageViewModel(savedContent, sync, journeys)

        sync.updateProgress("route-1", routeTitle = "Route One", completedStepIds = listOf("s1", "s2"))
        advanceUntilIdle()

        val progress = viewModel.learningProgress.first()
        assertEquals(1, progress.size)
        assertEquals("route-1", progress[0].routeId)
        assertEquals("Route One", progress[0].routeTitle)
    }

    @Test
    fun journeys_emitsLoadingThenSuccessWithItems() = runTest {
        val savedContent = FakeSavedContentRepository()
        val sync = FakeLocalUserSyncRepository()
        val journeys = FakeJourneyRepository()
        journeys.signalsResponse = JourneySignalsDto(signals = listOf("传统工艺"))
        journeys.journeysResponse = JourneyResponseDto(
            items = listOf(
                JourneyItemDto(
                    node = GraphNodeDto(
                        nodeKey = "article:a1",
                        type = GraphNodeType.Article,
                        id = "a1",
                        title = "Article One",
                    ),
                    reasons = listOf("你收藏了同类内容"),
                ),
            ),
        )
        val viewModel = MyPageViewModel(savedContent, sync, journeys)

        advanceUntilIdle()

        val state = viewModel.journeys.value as JourneysUiState.Success
        assertEquals(1, state.items.size)
        assertEquals("a1", state.items[0].targetId)
        assertEquals(listOf("传统工艺"), state.signals)
    }

    @Test
    fun journeys_showsEmptyWhenNoItems() = runTest {
        val savedContent = FakeSavedContentRepository()
        val sync = FakeLocalUserSyncRepository()
        val journeys = FakeJourneyRepository()
        journeys.journeysResponse = JourneyResponseDto(items = emptyList())
        val viewModel = MyPageViewModel(savedContent, sync, journeys)

        advanceUntilIdle()

        val state = viewModel.journeys.value
        assertTrue(state is JourneysUiState.Empty)
    }

    @Test
    fun selectStrategy_cancelsPreviousRequest_andOnlyLatestEmits() = runTest {
        val savedContent = FakeSavedContentRepository()
        val sync = FakeLocalUserSyncRepository()
        val journeys = FakeJourneyRepository()
        journeys.journeysDelayMs = 1_000
        journeys.journeysResponse = JourneyResponseDto(
            items = listOf(
                JourneyItemDto(
                    node = GraphNodeDto(
                        nodeKey = "article:balanced",
                        type = GraphNodeType.Article,
                        id = "balanced",
                        title = "Balanced",
                    ),
                ),
            ),
        )
        val viewModel = MyPageViewModel(savedContent, sync, journeys)
        advanceUntilIdle()
        assertTrue(viewModel.journeys.value is JourneysUiState.Success || viewModel.journeys.value is JourneysUiState.Empty)

        journeys.journeysResponse = JourneyResponseDto(
            strategy = JourneyStrategy.Novelty,
            items = listOf(
                JourneyItemDto(
                    node = GraphNodeDto(
                        nodeKey = "article:novelty",
                        type = GraphNodeType.Article,
                        id = "novelty",
                        title = "Novelty",
                    ),
                ),
            ),
        )
        viewModel.selectStrategy(JourneyStrategy.Novelty)
        advanceTimeBy(50)
        advanceUntilIdle()

        val state = viewModel.journeys.value as JourneysUiState.Success
        assertEquals(JourneyStrategy.Novelty, viewModel.selectedStrategy.first())
        assertEquals("novelty", state.items[0].targetId)
    }

    @Test
    fun retryJourneys_reloadsWithSameStrategy() = runTest {
        val savedContent = FakeSavedContentRepository()
        val sync = FakeLocalUserSyncRepository()
        val journeys = FakeJourneyRepository()
        val viewModel = MyPageViewModel(savedContent, sync, journeys)
        advanceUntilIdle()

        val before = journeys.loadJourneysCallCount
        viewModel.retryJourneys()
        advanceUntilIdle()

        assertEquals(before + 1, journeys.loadJourneysCallCount)
        assertTrue(viewModel.journeys.value is JourneysUiState.Success || viewModel.journeys.value is JourneysUiState.Empty)
    }

    @Test
    fun journeys_errorState_onRepositoryFailure() = runTest {
        val savedContent = FakeSavedContentRepository()
        val sync = FakeLocalUserSyncRepository()
        val journeys = FakeJourneyRepository()
        journeys.journeysException = IOException("network")
        val viewModel = MyPageViewModel(savedContent, sync, journeys)

        advanceUntilIdle()

        val state = viewModel.journeys.value as JourneysUiState.Error
        assertEquals(R.string.error_network_unavailable, state.messageResId)
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
