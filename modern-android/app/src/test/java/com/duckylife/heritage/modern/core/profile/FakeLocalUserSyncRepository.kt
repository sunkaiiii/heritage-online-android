package com.duckylife.heritage.modern.core.profile

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeLocalUserSyncRepository : LocalUserSyncRepository {

    private val _favorites = MutableStateFlow<List<ProfileFavorite>>(emptyList())
    private val _history = MutableStateFlow<List<ProfileHistoryItem>>(emptyList())
    private val _progress = MutableStateFlow<List<ProfileLearningProgress>>(emptyList())
    // 从当前时间开始，避免 ofEpochMilli(1) 生成 1970 年的测试时间戳。
    private var historyCounter = System.currentTimeMillis()

    var syncNowCalled: Boolean = false
        private set

    override fun profileState(): Flow<SyncedProfileState?> = MutableStateFlow(null)

    override fun favorites(): Flow<List<ProfileFavorite>> = _favorites.asStateFlow()

    override fun history(): Flow<List<ProfileHistoryItem>> = _history.asStateFlow()

    override fun learningProgress(): Flow<List<ProfileLearningProgress>> = _progress.asStateFlow()

    override fun pendingOperationCount(): Flow<Int> = MutableStateFlow(0)

    override suspend fun syncNow() {
        syncNowCalled = true
    }

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
