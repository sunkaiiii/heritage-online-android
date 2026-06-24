package com.duckylife.heritage.modern.core.profile

/**
 * 服务端镜像表项的同步状态。
 */
enum class ProfileSyncStatus {
    /** 已与服务端同步（或上次同步时的一致状态）。 */
    Synced,

    /** 本地已修改，等待同步到服务端。 */
    Pending,
}

/**
 * 需要重放到服务端的本地写操作类型。
 */
enum class PendingOperationKind {
    AddFavorite,
    RemoveFavorite,
    RecordHistory,
    UpdateProgress,
    ClearHistory,

    /**
     * 数据库中存在无法识别的 kind 字符串时的安全兜底值。
     * 重放时会被当作不可恢复错误直接丢弃，避免崩溃。
     */
    Unknown,
}

/**
 * 本地用户档案状态，供“我的”页顶部展示。
 */
data class SyncedProfileState(
    val profileId: String,
    val displayName: String,
    val favoriteCount: Long,
    val historyCount: Long,
    val learningRouteCount: Long,
    val lastSyncAt: Long?,
    val lastSyncError: String?,
)

/**
 * 收藏条目的本地镜像。
 */
data class ProfileFavorite(
    val id: String,
    val targetType: String,
    val targetId: String,
    val titleSnapshot: String?,
    val coverImageUrlSnapshot: String?,
    val tags: List<String>,
    val note: String?,
    val updatedAt: String?,
    val syncStatus: ProfileSyncStatus,
)

/**
 * 浏览历史的本地镜像。
 */
data class ProfileHistoryItem(
    val id: String,
    val targetType: String,
    val targetId: String,
    val titleSnapshot: String?,
    val viewedAt: String?,
    val viewCount: Int,
    val lastPosition: String?,
    val syncStatus: ProfileSyncStatus,
)

/**
 * 学习进度的本地镜像。
 */
data class ProfileLearningProgress(
    val id: String,
    val routeId: String,
    val routeTitle: String?,
    val completedStepIds: List<String>,
    val currentStepId: String?,
    val percent: Int,
    val updatedAt: String?,
    val completedAt: String?,
    val syncStatus: ProfileSyncStatus,
)

/**
 * 同步过程中遇到的、需要 Worker 决定重试或终止的异常。
 */
class ProfileSyncException(
    message: String,
    val isRetryable: Boolean,
    cause: Throwable? = null,
) : Exception(message, cause)
