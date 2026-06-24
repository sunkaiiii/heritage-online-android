package com.duckylife.heritage.modern.core.profile

import com.duckylife.heritage.modern.core.database.dao.PendingProfileOperationDao
import com.duckylife.heritage.modern.core.database.dao.ProfileFavoriteDao
import com.duckylife.heritage.modern.core.database.dao.ProfileHistoryDao
import com.duckylife.heritage.modern.core.database.dao.ProfileLearningProgressDao
import com.duckylife.heritage.modern.core.database.dao.ProfileStateDao
import com.duckylife.heritage.modern.core.database.entity.LocalProfileStateEntity
import com.duckylife.heritage.modern.core.database.entity.PendingProfileOperationEntity
import com.duckylife.heritage.modern.core.database.entity.ProfileFavoriteEntity
import com.duckylife.heritage.modern.core.database.entity.ProfileHistoryEntity
import com.duckylife.heritage.modern.core.database.entity.ProfileLearningProgressEntity
import com.duckylife.heritage.modern.core.network.HeritageJson
import com.duckylife.heritage.modern.core.network.LocalUserFavoritesQuery
import com.duckylife.heritage.modern.core.network.LocalUserHistoryQuery
import com.duckylife.heritage.modern.core.network.api.LocalUserApi
import com.duckylife.heritage.modern.core.network.dto.advanced.FavoriteCreateRequestDto
import com.duckylife.heritage.modern.core.network.dto.advanced.HistoryRecordRequestDto
import com.duckylife.heritage.modern.core.network.dto.advanced.LearningProgressUpdateDto
import com.duckylife.heritage.modern.core.network.dto.advanced.LocalFavoriteDto
import com.duckylife.heritage.modern.core.network.dto.advanced.LocalHistoryDto
import com.duckylife.heritage.modern.core.network.dto.advanced.LocalLearningProgressDto
import com.duckylife.heritage.modern.ui.error.toApiFailure
import com.duckylife.heritage.modern.ui.error.toUiErrorMessage
import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement

/**
 * 本地用户状态同步仓库。
 *
 * 所有写操作先更新本地 Room 镜像并创建 pending operation，再调度 WorkManager 后台同步。
 * 读操作返回本地镜像的 Flow，同步成功后自动更新。
 */
interface LocalUserSyncRepository {

    fun profileState(): Flow<SyncedProfileState?>
    fun favorites(): Flow<List<ProfileFavorite>>
    fun history(): Flow<List<ProfileHistoryItem>>
    fun learningProgress(): Flow<List<ProfileLearningProgress>>
    fun pendingOperationCount(): Flow<Int>

    suspend fun syncNow()

    suspend fun toggleFavorite(
        type: String,
        id: String,
        titleSnapshot: String? = null,
        coverImageUrlSnapshot: String? = null,
    )

    suspend fun removeFavorite(type: String, id: String)

    suspend fun recordHistory(
        type: String,
        id: String,
        titleSnapshot: String? = null,
        lastPosition: String? = null,
    )

    suspend fun updateProgress(
        routeId: String,
        routeTitle: String? = null,
        completedStepIds: List<String> = emptyList(),
        currentStepId: String? = null,
    )

    suspend fun clearHistory()
}

@Singleton
class DefaultLocalUserSyncRepository @Inject constructor(
    private val api: LocalUserApi,
    private val profileRepository: LocalProfileRepository,
    private val stateDao: ProfileStateDao,
    private val favoriteDao: ProfileFavoriteDao,
    private val historyDao: ProfileHistoryDao,
    private val progressDao: ProfileLearningProgressDao,
    private val pendingDao: PendingProfileOperationDao,
    private val scheduler: ProfileSyncScheduler,
) : LocalUserSyncRepository {

    private val allowedTargetTypes = setOf("article", "directoryItem", "inheritor")

    override fun profileState(): Flow<SyncedProfileState?> = flow {
        val profileId = profileRepository.currentProfileId()
        emitAll(stateDao.observeByProfileId(profileId).map { it?.toSyncedState() })
    }

    override fun favorites(): Flow<List<ProfileFavorite>> = flow {
        val profileId = profileRepository.currentProfileId()
        emitAll(favoriteDao.observeByProfileId(profileId).map { list -> list.map { it.toProfileFavorite() } })
    }

    override fun history(): Flow<List<ProfileHistoryItem>> = flow {
        val profileId = profileRepository.currentProfileId()
        emitAll(historyDao.observeByProfileId(profileId).map { list -> list.map { it.toProfileHistoryItem() } })
    }

    override fun learningProgress(): Flow<List<ProfileLearningProgress>> = flow {
        val profileId = profileRepository.currentProfileId()
        emitAll(progressDao.observeByProfileId(profileId).map { list -> list.map { it.toProfileLearningProgress() } })
    }

    override fun pendingOperationCount(): Flow<Int> = pendingDao.observeAll().map { it.size }

    override suspend fun syncNow() {
        currentCoroutineContext().ensureActive()
        val profileId = profileRepository.currentProfileId()
        var lastError: String? = null

        replayPendingOperations(profileId) { error ->
            lastError = error
        }

        currentCoroutineContext().ensureActive()
        val now = System.currentTimeMillis()
        val pullResult = runCatching { pullRemoteState(profileId) }
        if (pullResult.isFailure) {
            val cause = pullResult.exceptionOrNull()!!
            if (cause is kotlinx.coroutines.CancellationException) throw cause

            val failure = runCatching { cause.toApiFailure() }.getOrNull()
                ?: com.duckylife.heritage.modern.ui.error.ApiFailure.Unknown(cause)
            lastError = failure.toUiErrorMessage().kind.name
            stateDao.updateSyncStatus(profileId, now, lastError)
            throw ProfileSyncException(
                "Pull remote state failed",
                isRetryable = failure.isRetryable,
                cause = cause,
            )
        }

        stateDao.updateSyncStatus(profileId, now, lastError)
    }

    override suspend fun toggleFavorite(
        type: String,
        id: String,
        titleSnapshot: String?,
        coverImageUrlSnapshot: String?,
    ) {
        requireValidTargetType(type)
        val profileId = profileRepository.currentProfileId()
        val existing = favoriteDao.getByTarget(profileId, type, id)
        val dedupKey = "favorite:$type:$id"

        if (existing != null) {
            favoriteDao.deleteByTarget(profileId, type, id)
            val payload = RemoveFavoritePayload(type, id)
            pendingDao.replace(
                createPendingOperation(
                    kind = PendingOperationKind.RemoveFavorite,
                    dedupKey = dedupKey,
                    payload = payload,
                ),
            )
        } else {
            val entity = ProfileFavoriteEntity(
                id = "$profileId:$type:$id",
                profileId = profileId,
                targetType = type,
                targetId = id,
                titleSnapshot = titleSnapshot,
                coverImageUrlSnapshot = coverImageUrlSnapshot,
                updatedAt = isoNow(),
                syncStatus = ProfileSyncStatus.Pending,
            )
            favoriteDao.upsert(entity)
            val payload = AddFavoritePayload(type, id)
            pendingDao.replace(
                createPendingOperation(
                    kind = PendingOperationKind.AddFavorite,
                    dedupKey = dedupKey,
                    payload = payload,
                ),
            )
        }
        scheduler.scheduleImmediate()
    }

    override suspend fun removeFavorite(type: String, id: String) {
        requireValidTargetType(type)
        val profileId = profileRepository.currentProfileId()
        favoriteDao.deleteByTarget(profileId, type, id)
        val payload = RemoveFavoritePayload(type, id)
        pendingDao.replace(
            createPendingOperation(
                kind = PendingOperationKind.RemoveFavorite,
                dedupKey = "favorite:$type:$id",
                payload = payload,
            ),
        )
        scheduler.scheduleImmediate()
    }

    override suspend fun recordHistory(
        type: String,
        id: String,
        titleSnapshot: String?,
        lastPosition: String?,
    ) {
        requireValidTargetType(type)
        val profileId = profileRepository.currentProfileId()
        val existing = historyDao.getByTarget(profileId, type, id)
        val entity = ProfileHistoryEntity(
            id = existing?.id ?: "$profileId:$type:$id",
            profileId = profileId,
            targetType = type,
            targetId = id,
            titleSnapshot = titleSnapshot,
            viewedAt = isoNow(),
            viewCount = (existing?.viewCount ?: 0) + 1,
            lastPosition = lastPosition,
            syncStatus = ProfileSyncStatus.Pending,
        )
        historyDao.upsert(entity)
        val payload = RecordHistoryPayload(type, id, lastPosition)
        pendingDao.replace(
            createPendingOperation(
                kind = PendingOperationKind.RecordHistory,
                dedupKey = "history:$type:$id",
                payload = payload,
            ),
        )
        scheduler.scheduleImmediate()
    }

    override suspend fun updateProgress(
        routeId: String,
        routeTitle: String?,
        completedStepIds: List<String>,
        currentStepId: String?,
    ) {
        require(routeId.isNotBlank()) { "routeId must not be blank" }
        val profileId = profileRepository.currentProfileId()
        val existing = progressDao.getByProfileId(profileId).firstOrNull { it.routeId == routeId }
        val entity = ProfileLearningProgressEntity(
            id = existing?.id ?: "$profileId:$routeId",
            profileId = profileId,
            routeId = routeId,
            routeTitle = routeTitle ?: existing?.routeTitle,
            completedStepIds = completedStepIds.distinct(),
            currentStepId = currentStepId,
            percent = existing?.percent ?: 0,
            updatedAt = isoNow(),
            completedAt = existing?.completedAt,
            syncStatus = ProfileSyncStatus.Pending,
        )
        progressDao.upsert(entity)
        val payload = UpdateProgressPayload(
            routeId = routeId,
            completedStepIds = completedStepIds.distinct(),
            currentStepId = currentStepId,
        )
        pendingDao.replace(
            createPendingOperation(
                kind = PendingOperationKind.UpdateProgress,
                dedupKey = "progress:$routeId",
                payload = payload,
            ),
        )
        scheduler.scheduleImmediate()
    }

    override suspend fun clearHistory() {
        val profileId = profileRepository.currentProfileId()
        historyDao.deleteAllByProfile(profileId)
        pendingDao.replace(
            createPendingOperation(
                kind = PendingOperationKind.ClearHistory,
                dedupKey = "history:clear",
                payload = ClearHistoryPayload(),
            ),
        )
        scheduler.scheduleImmediate()
    }

    private suspend fun replayPendingOperations(
        profileId: String,
        onTerminalError: (String?) -> Unit,
    ) {
        val operations = pendingDao.getAll()
        for (operation in operations) {
            currentCoroutineContext().ensureActive()
            val result = runCatching { replayOperation(profileId, operation) }
            if (result.isSuccess) {
                pendingDao.delete(operation.operationId)
                continue
            }

            val cause = result.exceptionOrNull()!!
            if (cause is kotlinx.coroutines.CancellationException) throw cause

            val failure = runCatching { cause.toApiFailure() }.getOrNull()
                ?: com.duckylife.heritage.modern.ui.error.ApiFailure.Unknown(cause)

            if (failure.isRetryable) {
                pendingDao.incrementAttempt(
                    operation.operationId,
                    failure.toUiErrorMessage().kind.name,
                )
                throw ProfileSyncException(
                    "Retryable failure while replaying ${operation.kind}",
                    isRetryable = true,
                    cause = cause,
                )
            }

            onTerminalError(failure.toUiErrorMessage().kind.name)
            pendingDao.delete(operation.operationId)

            // 服务端返回 404 等不可恢复错误时，清除本地乐观镜像，避免用户看到“已收藏但服务端不存在”。
            when (operation.kind) {
                PendingOperationKind.AddFavorite -> {
                    val payload = decodePayload<AddFavoritePayload>(operation.payloadJson)
                    favoriteDao.deleteByTarget(profileId, payload.targetType, payload.targetId)
                }
                PendingOperationKind.RemoveFavorite -> {
                    val payload = decodePayload<RemoveFavoritePayload>(operation.payloadJson)
                    favoriteDao.deleteByTarget(profileId, payload.targetType, payload.targetId)
                }
                else -> Unit
            }
        }
    }

    private suspend fun replayOperation(profileId: String, operation: PendingProfileOperationEntity) {
        when (operation.kind) {
            PendingOperationKind.AddFavorite -> {
                val payload = decodePayload<AddFavoritePayload>(operation.payloadJson)
                val remote = api.addLocalUserFavorite(
                    FavoriteCreateRequestDto(
                        targetType = payload.targetType,
                        targetId = payload.targetId,
                        tags = payload.tags,
                        note = payload.note,
                    ),
                )
                // 用服务端返回的 id 替换本地乐观写入的复合键记录，避免后续 syncFavorites 出现主键/唯一索引冲突。
                favoriteDao.deleteByTarget(profileId, payload.targetType, payload.targetId)
                favoriteDao.upsert(remote.toEntity(profileId, ProfileSyncStatus.Synced))
            }

            PendingOperationKind.RemoveFavorite -> {
                val payload = decodePayload<RemoveFavoritePayload>(operation.payloadJson)
                api.removeLocalUserFavorite(payload.targetType, payload.targetId)
                favoriteDao.deleteByTarget(profileId, payload.targetType, payload.targetId)
            }

            PendingOperationKind.RecordHistory -> {
                val payload = decodePayload<RecordHistoryPayload>(operation.payloadJson)
                val remote = api.recordLocalUserHistory(
                    HistoryRecordRequestDto(
                        targetType = payload.targetType,
                        targetId = payload.targetId,
                        lastPosition = payload.lastPosition,
                    ),
                )
                historyDao.deleteByTarget(profileId, payload.targetType, payload.targetId)
                historyDao.upsert(remote.toEntity(profileId, ProfileSyncStatus.Synced))
            }

            PendingOperationKind.UpdateProgress -> {
                val payload = decodePayload<UpdateProgressPayload>(operation.payloadJson)
                val remote = api.updateLocalUserLearningProgress(
                    routeId = payload.routeId,
                    request = LearningProgressUpdateDto(
                        completedStepIds = payload.completedStepIds,
                        currentStepId = payload.currentStepId,
                    ),
                )
                progressDao.deleteByRoute(profileId, payload.routeId)
                progressDao.upsert(remote.toEntity(profileId, ProfileSyncStatus.Synced))
            }

            PendingOperationKind.ClearHistory -> {
                api.clearLocalUserHistory()
            }

            PendingOperationKind.Unknown -> {
                throw ProfileSyncException(
                    "Unknown pending operation kind, dropping",
                    isRetryable = false,
                )
            }
        }
    }

    private suspend fun pullRemoteState(profileId: String) {
        val profile = api.getLocalUserProfile()
        val summary = api.getLocalUserSummary()

        stateDao.upsert(
            LocalProfileStateEntity(
                profileId = profileId,
                displayName = profile.displayName,
                favoriteCount = profile.favoriteCount,
                historyCount = profile.historyCount,
                learningRouteCount = profile.learningRouteCount,
                generatedAt = profile.generatedAt ?: summary.generatedAt,
            ),
        )

        syncFavorites(profileId)
        syncHistory(profileId)
        syncProgress(profileId)
    }

    private suspend fun syncFavorites(profileId: String) {
        val remoteKeys = mutableSetOf<Pair<String, String>>()
        var page = 1
        var hasMore = true
        while (hasMore && page <= MAX_SYNC_PAGES) {
            currentCoroutineContext().ensureActive()
            val result = api.getLocalUserFavorites(
                LocalUserFavoritesQuery(page = page, pageSize = SYNC_PAGE_SIZE),
            )
            result.items.forEach {
                remoteKeys.add(it.targetType to it.targetId)
                favoriteDao.upsert(it.toEntity(profileId, ProfileSyncStatus.Synced))
            }
            hasMore = result.hasMore
            page++
        }
        // 只有完整拉完服务端分页后才能把不在 remoteKeys 中的项视为已删除。
        // 达到保护上限时 remoteKeys 只是前 MAX_SYNC_PAGES 页，若仍做 reconcile 会误删
        // 大账户中较后的本地镜像。
        if (!hasMore) {
            favoriteDao.getByProfileId(profileId)
                .filter { it.syncStatus == ProfileSyncStatus.Synced && (it.targetType to it.targetId) !in remoteKeys }
                .forEach { favoriteDao.deleteByTarget(profileId, it.targetType, it.targetId) }
        }
    }

    private suspend fun syncHistory(profileId: String) {
        val remoteKeys = mutableSetOf<Pair<String, String>>()
        var page = 1
        var hasMore = true
        while (hasMore && page <= MAX_SYNC_PAGES) {
            currentCoroutineContext().ensureActive()
            val result = api.getLocalUserHistory(
                LocalUserHistoryQuery(page = page, pageSize = SYNC_PAGE_SIZE),
            )
            result.items.forEach {
                remoteKeys.add(it.targetType to it.targetId)
                historyDao.upsert(it.toEntity(profileId, ProfileSyncStatus.Synced))
            }
            hasMore = result.hasMore
            page++
        }
        // 同 favorites：分页被保护上限截断时，不能把未拉到的历史当成远端已删除。
        if (!hasMore) {
            historyDao.getByProfileId(profileId)
                .filter { it.syncStatus == ProfileSyncStatus.Synced && (it.targetType to it.targetId) !in remoteKeys }
                .forEach { historyDao.deleteByTarget(profileId, it.targetType, it.targetId) }
        }
    }

    private suspend fun syncProgress(profileId: String) {
        currentCoroutineContext().ensureActive()
        val items = api.getLocalUserLearningProgress()
        val remoteRouteIds = items.map { it.routeId }.toSet()
        items.forEach { progressDao.upsert(it.toEntity(profileId, ProfileSyncStatus.Synced)) }
        progressDao.getByProfileId(profileId)
            .filter { it.syncStatus == ProfileSyncStatus.Synced && it.routeId !in remoteRouteIds }
            .forEach { progressDao.deleteByRoute(profileId, it.routeId) }
    }

    private fun requireValidTargetType(type: String) {
        require(type in allowedTargetTypes) {
            "targetType must be one of $allowedTargetTypes, was: $type"
        }
    }

    private inline fun <reified T> createPendingOperation(
        kind: PendingOperationKind,
        dedupKey: String,
        payload: T,
    ): PendingProfileOperationEntity = PendingProfileOperationEntity(
        operationId = UUID.randomUUID().toString(),
        kind = kind,
        deduplicationKey = dedupKey,
        payloadJson = HeritageJson.encodeToJsonElement(payload).toString(),
        createdAt = System.currentTimeMillis(),
    )

    private inline fun <reified T> decodePayload(json: String): T =
        HeritageJson.decodeFromJsonElement(HeritageJson.parseToJsonElement(json))

    private fun isoNow(): String = Instant.now().toString()

    private companion object {
        const val SYNC_PAGE_SIZE = 100
        const val MAX_SYNC_PAGES = 20
    }
}

private fun LocalProfileStateEntity.toSyncedState() = SyncedProfileState(
    profileId = profileId,
    displayName = displayName,
    favoriteCount = favoriteCount,
    historyCount = historyCount,
    learningRouteCount = learningRouteCount,
    lastSyncAt = lastSyncAt,
    lastSyncError = lastSyncError,
)

private fun ProfileFavoriteEntity.toProfileFavorite() = ProfileFavorite(
    id = id,
    targetType = targetType,
    targetId = targetId,
    titleSnapshot = titleSnapshot,
    coverImageUrlSnapshot = coverImageUrlSnapshot,
    tags = tags,
    note = note,
    updatedAt = updatedAt,
    syncStatus = syncStatus,
)

private fun ProfileHistoryEntity.toProfileHistoryItem() = ProfileHistoryItem(
    id = id,
    targetType = targetType,
    targetId = targetId,
    titleSnapshot = titleSnapshot,
    viewedAt = viewedAt,
    viewCount = viewCount,
    lastPosition = lastPosition,
    syncStatus = syncStatus,
)

private fun ProfileLearningProgressEntity.toProfileLearningProgress() = ProfileLearningProgress(
    id = id,
    routeId = routeId,
    routeTitle = routeTitle,
    completedStepIds = completedStepIds,
    currentStepId = currentStepId,
    percent = percent,
    updatedAt = updatedAt,
    completedAt = completedAt,
    syncStatus = syncStatus,
)

private fun LocalFavoriteDto.toEntity(profileId: String, status: ProfileSyncStatus) = ProfileFavoriteEntity(
    id = id,
    profileId = profileId,
    targetType = targetType,
    targetId = targetId,
    titleSnapshot = titleSnapshot,
    coverImageUrlSnapshot = coverImageUrlSnapshot,
    tags = tags,
    note = note,
    createdAt = createdAt,
    updatedAt = updatedAt,
    syncStatus = status,
)

private fun LocalHistoryDto.toEntity(profileId: String, status: ProfileSyncStatus) = ProfileHistoryEntity(
    id = id,
    profileId = profileId,
    targetType = targetType,
    targetId = targetId,
    titleSnapshot = titleSnapshot,
    viewedAt = viewedAt,
    viewCount = viewCount,
    lastPosition = lastPosition,
    syncStatus = status,
)

private fun LocalLearningProgressDto.toEntity(profileId: String, status: ProfileSyncStatus) = ProfileLearningProgressEntity(
    id = id ?: "$profileId:$routeId",
    profileId = profileId,
    routeId = routeId,
    routeTitle = routeTitle,
    completedStepIds = completedStepIds,
    currentStepId = currentStepId,
    percent = percent,
    startedAt = startedAt,
    updatedAt = updatedAt,
    completedAt = completedAt,
    syncStatus = status,
)
