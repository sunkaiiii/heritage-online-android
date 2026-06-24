package com.duckylife.heritage.modern.feature.my

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duckylife.heritage.modern.core.database.entity.SavedContentEntity
import com.duckylife.heritage.modern.core.network.dto.ArticleCategory
import com.duckylife.heritage.modern.core.network.dto.DirectoryItemKind
import com.duckylife.heritage.modern.core.network.dto.extractCoverImageUrl
import com.duckylife.heritage.modern.core.profile.LocalUserSyncRepository
import com.duckylife.heritage.modern.core.profile.ProfileFavorite
import com.duckylife.heritage.modern.core.profile.ProfileLearningProgress
import com.duckylife.heritage.modern.core.profile.ProfileSyncStatus
import com.duckylife.heritage.modern.core.saved.SavedContentRepository
import com.duckylife.heritage.modern.core.saved.SavedContentTarget
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * “我的学习空间”的 UI 模型。
 *
 * 收藏与浏览优先展示服务端同步镜像；旧版 [SavedContentEntity] 数据作为离线兜底保留。
 */
@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val savedContentRepository: SavedContentRepository,
    private val syncRepository: LocalUserSyncRepository,
) : ViewModel() {

    val favorites: StateFlow<List<FavoriteItem>> = combine(
        syncRepository.favorites(),
        savedContentRepository.favorites(),
    ) { syncFavorites, legacyFavorites ->
        mergeFavorites(syncFavorites, legacyFavorites)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    val history: StateFlow<List<HistoryItem>> = combine(
        syncRepository.history(),
        savedContentRepository.recentlyViewed(),
    ) { syncHistory, legacyRecent ->
        mergeHistory(syncHistory, legacyRecent)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    val profileState = syncRepository.profileState().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    val pendingOperationCount = syncRepository.pendingOperationCount().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = 0,
    )

    val learningProgress = syncRepository.learningProgress().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    fun syncNow() {
        viewModelScope.launch {
            runCatching { syncRepository.syncNow() }
        }
    }

    fun unfavorite(item: FavoriteItem) {
        viewModelScope.launch {
            savedContentRepository.removeFavorite(
                SavedContentTarget(
                    id = item.targetId,
                    sourceId = item.legacySourceId,
                    sourceUrl = item.legacySourceUrl,
                    category = item.legacyCategory,
                    kind = item.legacyKind,
                ),
            )
            syncRepository.removeFavorite(item.targetType, item.targetId)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            syncRepository.clearHistory()
            savedContentRepository.clearRecent()
        }
    }

    private fun mergeFavorites(
        syncFavorites: List<ProfileFavorite>,
        legacyFavorites: List<SavedContentEntity>,
    ): List<FavoriteItem> {
        val syncByKey = syncFavorites.associateBy { it.targetType to it.targetId }
        val legacyByKey = legacyFavorites
            .filter { !it.targetId.isNullOrBlank() }
            .associateBy { it.contentType to it.targetId }
        val keys = (syncByKey.keys + legacyByKey.keys)

        return keys.mapNotNull { (type, targetId) ->
            if (targetId.isNullOrBlank()) return@mapNotNull null
            val sync = syncByKey[type to targetId]
            val legacy = legacyByKey[type to targetId]
            FavoriteItem(
                targetType = type,
                targetId = targetId,
                title = sync?.titleSnapshot ?: legacy?.title,
                coverImageUrl = sync?.coverImageUrlSnapshot
                    ?: legacy?.coverImageJson?.let { extractCoverImageUrl(it) },
                tags = sync?.tags ?: emptyList(),
                syncStatus = sync?.syncStatus,
                legacySourceId = legacy?.targetSourceId,
                legacySourceUrl = legacy?.targetSourceUrl,
                legacyCategory = legacy?.targetCategory,
                legacyKind = legacy?.targetKind,
                navigationTarget = buildNavigationTarget(type, targetId, legacy),
            )
        }.sortedWith(
            compareByDescending<FavoriteItem> { it.syncStatus != null }
                .thenByDescending { it.title.orEmpty() },
        )
    }

    private fun mergeHistory(
        syncHistory: List<com.duckylife.heritage.modern.core.profile.ProfileHistoryItem>,
        legacyRecent: List<SavedContentEntity>,
    ): List<HistoryItem> {
        val syncByKey = syncHistory.associateBy { it.targetType to it.targetId }
        val legacyByKey = legacyRecent
            .filter { !it.targetId.isNullOrBlank() }
            .associateBy { it.contentType to it.targetId }
        val keys = (syncByKey.keys + legacyByKey.keys)

        return keys.mapNotNull { (type, targetId) ->
            if (targetId.isNullOrBlank()) return@mapNotNull null
            val sync = syncByKey[type to targetId]
            val legacy = legacyByKey[type to targetId]
            HistoryItem(
                targetType = type,
                targetId = targetId,
                title = sync?.titleSnapshot ?: legacy?.title,
                viewedAt = sync?.viewedAt,
                viewCount = sync?.viewCount ?: 1,
                syncStatus = sync?.syncStatus,
                legacyLastViewedAt = legacy?.lastViewedAt,
                navigationTarget = buildNavigationTarget(type, targetId, legacy),
            )
        }.sortedByDescending { it.sortKey }
    }

    private fun buildNavigationTarget(
        type: String,
        targetId: String,
        legacy: SavedContentEntity?,
    ): MyPageDestination? = when (type) {
        "article" -> MyPageDestination.Article(
            articleId = targetId,
            sourceId = legacy?.targetSourceId,
            sourceUrl = legacy?.targetSourceUrl,
            category = ArticleCategory.entries.firstOrNull { it.wireName == legacy?.targetCategory }
                ?: ArticleCategory.News,
        )
        "directoryItem" -> MyPageDestination.Directory(
            itemId = targetId,
            sourceId = legacy?.targetSourceId,
            kind = DirectoryItemKind.entries.firstOrNull { it.wireName == legacy?.targetKind }
                ?: DirectoryItemKind.NationalProject,
        )
        "inheritor" -> MyPageDestination.Inheritor(
            inheritorId = targetId,
            sourceId = legacy?.targetSourceId,
        )
        else -> null
    }
}

/**
 * 收藏 tab 的统一项，合并了服务端镜像与旧版本地数据。
 */
data class FavoriteItem(
    val targetType: String,
    val targetId: String,
    val title: String?,
    val coverImageUrl: String?,
    val tags: List<String>,
    val syncStatus: ProfileSyncStatus?,
    val legacySourceId: String?,
    val legacySourceUrl: String?,
    val legacyCategory: String?,
    val legacyKind: String?,
    val navigationTarget: MyPageDestination?,
) {
    val key: String = "$targetType:$targetId"
}

/**
 * 浏览 tab 的历史项，合并了服务端镜像与旧版本地最近浏览。
 */
data class HistoryItem(
    val targetType: String,
    val targetId: String,
    val title: String?,
    val viewedAt: String?,
    val viewCount: Int,
    val syncStatus: ProfileSyncStatus?,
    val legacyLastViewedAt: Long?,
    val navigationTarget: MyPageDestination?,
) {
    val key: String = "$targetType:$targetId"

    /**
     * 用于排序的时间戳；优先使用服务端 ISO 时间，其次使用旧版本地时间戳。
     */
    val sortKey: Long = viewedAt?.let { iso ->
        runCatching { java.time.Instant.parse(iso).toEpochMilli() }.getOrNull()
    } ?: legacyLastViewedAt ?: 0L
}

/**
 * 学习 tab 展示用的进度项。
 */
fun ProfileLearningProgress.toLearningItem(): LearningProgressItem = LearningProgressItem(
    routeId = routeId,
    routeTitle = routeTitle,
    percent = percent,
    completedStepCount = completedStepIds.size,
    completedAt = completedAt,
    syncStatus = syncStatus,
)

data class LearningProgressItem(
    val routeId: String,
    val routeTitle: String?,
    val percent: Int,
    val completedStepCount: Int,
    val completedAt: String?,
    val syncStatus: ProfileSyncStatus,
)
