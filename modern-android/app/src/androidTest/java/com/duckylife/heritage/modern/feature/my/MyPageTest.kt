package com.duckylife.heritage.modern.feature.my

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.runner.RunWith
import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.core.data.ReadingPathEvent
import com.duckylife.heritage.modern.core.data.ReadingPathRepository
import com.duckylife.heritage.modern.core.database.entity.SavedContentEntity
import com.duckylife.heritage.modern.core.network.dto.advanced.JourneyResponseDto
import com.duckylife.heritage.modern.core.network.dto.advanced.JourneySignalsDto
import com.duckylife.heritage.modern.core.network.dto.advanced.JourneyStrategy
import com.duckylife.heritage.modern.core.profile.JourneyRepository
import com.duckylife.heritage.modern.core.profile.LocalUserSyncRepository
import com.duckylife.heritage.modern.core.profile.ProfileFavorite
import com.duckylife.heritage.modern.core.profile.ProfileHistoryItem
import com.duckylife.heritage.modern.core.profile.ProfileLearningProgress
import com.duckylife.heritage.modern.core.profile.ProfileSyncStatus
import com.duckylife.heritage.modern.core.profile.SyncedProfileState
import com.duckylife.heritage.modern.core.saved.SavedContentRepository
import com.duckylife.heritage.modern.core.saved.SavedContentSnapshot
import com.duckylife.heritage.modern.core.saved.SavedContentTarget
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MyPageTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createComposeRule()

    @Before
    fun init() {
        hiltRule.inject()
    }

    private val context: Context = ApplicationProvider.getApplicationContext()

    private fun string(resId: Int): String = context.getString(resId)

    @Test
    fun allTabsAreVisibleAndClickable() {
        renderMyPage()

        composeRule.onNodeWithText(string(R.string.favorites_tab)).assertIsDisplayed()
        composeRule.onNodeWithText(string(R.string.browsing_tab)).assertIsDisplayed()
        composeRule.onNodeWithText(string(R.string.learning_tab)).assertIsDisplayed()
        composeRule.onNodeWithText(string(R.string.journeys_tab)).assertIsDisplayed()
        composeRule.onNodeWithText(string(R.string.research_tab)).assertIsDisplayed()

        composeRule.onNodeWithText(string(R.string.journeys_tab)).performClick()
        composeRule.onNodeWithText(string(R.string.journeys_strategy_label)).assertIsDisplayed()
    }

    @Test
    fun clearHistory_showsConfirmationAndClears() {
        val sync = FakeLocalUserSyncRepository()
        val savedContent = FakeSavedContentRepository()
        runBlocking {
            sync.recordHistory("article", "a1", titleSnapshot = "Article One")
            savedContent.recordViewed(
                SavedContentSnapshot(
                    contentType = com.duckylife.heritage.modern.core.saved.SavedContentType.Article,
                    id = "a2",
                    title = "Article Two",
                    target = SavedContentTarget(id = "a2"),
                ),
            )
        }
        renderMyPage(sync = sync, savedContent = savedContent)

        composeRule.onNodeWithText(string(R.string.browsing_tab)).performClick()
        composeRule.onNodeWithText(string(R.string.action_clear_history)).assertIsDisplayed()
        composeRule.onNodeWithText(string(R.string.action_clear_history)).performClick()
        composeRule.onNodeWithText(string(R.string.action_clear_history_confirm)).assertIsDisplayed()
        composeRule.onNodeWithText(string(R.string.confirm)).performClick()

        composeRule.onNodeWithText(string(R.string.action_clear_history)).assertDoesNotExist()
    }

    @Test
    fun favoriteCard_hasUnfavoriteContentDescription() {
        val sync = FakeLocalUserSyncRepository()
        runBlocking {
            sync.toggleFavorite("article", "a1", titleSnapshot = "Article One")
        }
        renderMyPage(sync = sync)

        composeRule.onNodeWithContentDescription(string(R.string.action_unfavorite))
            .assertIsDisplayed()
    }

    @Test
    fun syncRefreshIcon_hasContentDescription() {
        renderMyPage()

        composeRule.onNodeWithContentDescription(string(R.string.action_sync_now))
            .assertIsDisplayed()
    }

    private fun renderMyPage(
        sync: FakeLocalUserSyncRepository = FakeLocalUserSyncRepository(),
        savedContent: FakeSavedContentRepository = FakeSavedContentRepository(),
    ) {
        val viewModel = MyPageViewModel(
            savedContentRepository = savedContent,
            syncRepository = sync,
            journeyRepository = FakeJourneyRepository(),
        )
        val readingPathViewModel = ReadingPathViewModel(FakeReadingPathRepository())

        composeRule.setContent {
            MyPage(
                onBack = {},
                onNavigate = {},
                viewModel = viewModel,
                readingPathViewModel = readingPathViewModel,
            )
        }
    }

    private class FakeJourneyRepository : JourneyRepository {
        override suspend fun loadJourneys(
            strategy: JourneyStrategy,
            limit: Int,
        ): JourneyResponseDto = JourneyResponseDto(strategy = strategy)

        override suspend fun loadSignals(): JourneySignalsDto = JourneySignalsDto()
    }

    private class FakeReadingPathRepository : ReadingPathRepository {
        private val events = MutableStateFlow<List<ReadingPathEvent>>(emptyList())
        override fun observeRecentPath(limit: Int): StateFlow<List<ReadingPathEvent>> = events
        override suspend fun record(event: ReadingPathEvent) { }
        override suspend fun clear() { }
    }

    private class FakeLocalUserSyncRepository : LocalUserSyncRepository {
        private val _favorites = MutableStateFlow<List<ProfileFavorite>>(emptyList())
        private val _history = MutableStateFlow<List<ProfileHistoryItem>>(emptyList())
        private val _progress = MutableStateFlow<List<ProfileLearningProgress>>(emptyList())
        private var historyCounter = System.currentTimeMillis()

        override fun profileState(): Flow<SyncedProfileState?> = MutableStateFlow(null)
        override fun favorites(): Flow<List<ProfileFavorite>> = _favorites.asStateFlow()
        override fun history(): Flow<List<ProfileHistoryItem>> = _history.asStateFlow()
        override fun learningProgress(): Flow<List<ProfileLearningProgress>> = _progress.asStateFlow()
        override fun pendingOperationCount(): Flow<Int> = MutableStateFlow(0)

        override suspend fun syncNow() { }

        override suspend fun toggleFavorite(
            type: String,
            id: String,
            titleSnapshot: String?,
            coverImageUrlSnapshot: String?,
        ) {
            val existing = _favorites.value.find { it.targetType == type && it.targetId == id }
            if (existing != null) {
                _favorites.value = _favorites.value - existing
            } else {
                _favorites.value = _favorites.value + ProfileFavorite(
                    id = "$type:$id",
                    targetType = type,
                    targetId = id,
                    titleSnapshot = titleSnapshot,
                    coverImageUrlSnapshot = coverImageUrlSnapshot,
                    tags = emptyList(),
                    note = null,
                    updatedAt = null,
                    syncStatus = ProfileSyncStatus.Pending,
                )
            }
        }

        override suspend fun removeFavorite(type: String, id: String) {
            _favorites.value = _favorites.value.filterNot { it.targetType == type && it.targetId == id }
        }

        override suspend fun recordHistory(
            type: String,
            id: String,
            titleSnapshot: String?,
            lastPosition: String?,
        ) {
            val now = java.time.Instant.ofEpochMilli(++historyCounter).toString()
            val existing = _history.value.find { it.targetType == type && it.targetId == id }
            if (existing != null) {
                _history.value = _history.value - existing + existing.copy(
                    viewedAt = now,
                    viewCount = existing.viewCount + 1,
                )
            } else {
                _history.value = _history.value + ProfileHistoryItem(
                    id = "$type:$id",
                    targetType = type,
                    targetId = id,
                    titleSnapshot = titleSnapshot,
                    viewedAt = now,
                    viewCount = 1,
                    lastPosition = lastPosition,
                    syncStatus = ProfileSyncStatus.Pending,
                )
            }
        }

        override suspend fun updateProgress(
            routeId: String,
            routeTitle: String?,
            completedStepIds: List<String>,
            currentStepId: String?,
        ) {
            _progress.value = _progress.value + ProfileLearningProgress(
                id = routeId,
                routeId = routeId,
                routeTitle = routeTitle,
                completedStepIds = completedStepIds,
                currentStepId = currentStepId,
                percent = 0,
                updatedAt = null,
                completedAt = null,
                syncStatus = ProfileSyncStatus.Pending,
            )
        }

        override suspend fun clearHistory() {
            _history.value = emptyList()
        }
    }

    private class FakeSavedContentRepository : SavedContentRepository {
        private val allEntities = mutableMapOf<String, SavedContentEntity>()
        private val _favorites = MutableStateFlow<List<SavedContentEntity>>(emptyList())
        private val _recentlyViewed = MutableStateFlow<List<SavedContentEntity>>(emptyList())

        override fun observeFavoriteState(target: SavedContentTarget): Flow<Boolean> =
            MutableStateFlow(false)

        override suspend fun toggleFavorite(snapshot: SavedContentSnapshot) {
            val key = SavedContentRepository.computeKey(snapshot)
            val existing = allEntities[key]
            val entity = if (existing != null) {
                existing.copy(isFavorite = !existing.isFavorite)
            } else {
                SavedContentEntity(
                    contentKey = key,
                    contentType = snapshot.contentType.wireName,
                    title = snapshot.title,
                    summary = null,
                    coverImageJson = null,
                    category = null,
                    region = null,
                    year = null,
                    sourceUrl = null,
                    targetId = null,
                    targetSourceId = null,
                    targetSourceUrl = null,
                    targetCategory = null,
                    targetKind = null,
                    isFavorite = true,
                    favoritedAt = null,
                    lastViewedAt = 0,
                )
            }
            allEntities[key] = entity
            emit()
        }

        override suspend fun recordViewed(snapshot: SavedContentSnapshot) {
            val key = SavedContentRepository.computeKey(snapshot)
            val existing = allEntities[key]
            val entity = if (existing != null) {
                existing.copy(lastViewedAt = System.currentTimeMillis())
            } else {
                SavedContentEntity(
                    contentKey = key,
                    contentType = snapshot.contentType.wireName,
                    title = snapshot.title,
                    summary = null,
                    coverImageJson = null,
                    category = null,
                    region = null,
                    year = null,
                    sourceUrl = null,
                    targetId = null,
                    targetSourceId = null,
                    targetSourceUrl = null,
                    targetCategory = null,
                    targetKind = null,
                    isFavorite = false,
                    favoritedAt = null,
                    lastViewedAt = System.currentTimeMillis(),
                )
            }
            allEntities[key] = entity
            emit()
        }

        override fun favorites(): Flow<List<SavedContentEntity>> = _favorites.asStateFlow()
        override fun recentlyViewed(): Flow<List<SavedContentEntity>> = _recentlyViewed.asStateFlow()

        override suspend fun removeFavorite(target: SavedContentTarget) {
            val key = SavedContentRepository.computeKey(target)
            allEntities[key]?.let {
                allEntities[key] = it.copy(isFavorite = false)
            }
            emit()
        }

        override suspend fun removeRecent(target: SavedContentTarget) {
            val key = SavedContentRepository.computeKey(target)
            allEntities[key]?.let {
                allEntities[key] = it.copy(lastViewedAt = 0)
            }
            emit()
        }

        override suspend fun clearRecent() {
            allEntities.values.forEach { entity ->
                allEntities[entity.contentKey] = entity.copy(lastViewedAt = 0)
            }
            emit()
        }

        private fun emit() {
            _favorites.value = allEntities.values.filter { it.isFavorite }
            _recentlyViewed.value = allEntities.values.filter { it.lastViewedAt > 0 }
        }
    }
}
