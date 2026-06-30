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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeProfileFavoriteDao : ProfileFavoriteDao {
    private val data = MutableStateFlow<List<ProfileFavoriteEntity>>(emptyList())

    override fun observeByProfileId(profileId: String): Flow<List<ProfileFavoriteEntity>> =
        data.map { list -> list.filter { it.profileId == profileId }.sortedByDescending { it.updatedAt.orEmpty() } }

    override suspend fun getByProfileId(profileId: String): List<ProfileFavoriteEntity> =
        data.value.filter { it.profileId == profileId }

    override suspend fun upsert(entity: ProfileFavoriteEntity) {
        data.value = data.value.filter { it.id != entity.id } + entity
    }

    override suspend fun deleteByTarget(profileId: String, targetType: String, targetId: String) {
        data.value = data.value.filterNot {
            it.profileId == profileId && it.targetType == targetType && it.targetId == targetId
        }
    }

    override suspend fun deleteAllByProfile(profileId: String) {
        data.value = data.value.filterNot { it.profileId == profileId }
    }

    override suspend fun getByTarget(
        profileId: String,
        targetType: String,
        targetId: String,
    ): ProfileFavoriteEntity? = data.value.firstOrNull {
        it.profileId == profileId && it.targetType == targetType && it.targetId == targetId
    }
}

class FakeProfileHistoryDao : ProfileHistoryDao {
    private val data = MutableStateFlow<List<ProfileHistoryEntity>>(emptyList())

    override fun observeByProfileId(profileId: String): Flow<List<ProfileHistoryEntity>> =
        data.map { list -> list.filter { it.profileId == profileId }.sortedByDescending { it.viewedAt.orEmpty() } }

    override suspend fun getByProfileId(profileId: String): List<ProfileHistoryEntity> =
        data.value.filter { it.profileId == profileId }

    override suspend fun upsert(entity: ProfileHistoryEntity) {
        data.value = data.value.filter { it.id != entity.id } + entity
    }

    override suspend fun deleteByTarget(profileId: String, targetType: String, targetId: String) {
        data.value = data.value.filterNot {
            it.profileId == profileId && it.targetType == targetType && it.targetId == targetId
        }
    }

    override suspend fun deleteAllByProfile(profileId: String) {
        data.value = data.value.filterNot { it.profileId == profileId }
    }

    override suspend fun getByTarget(
        profileId: String,
        targetType: String,
        targetId: String,
    ): ProfileHistoryEntity? = data.value.firstOrNull {
        it.profileId == profileId && it.targetType == targetType && it.targetId == targetId
    }
}

class FakeProfileLearningProgressDao : ProfileLearningProgressDao {
    private val data = MutableStateFlow<List<ProfileLearningProgressEntity>>(emptyList())

    override fun observeByProfileId(profileId: String): Flow<List<ProfileLearningProgressEntity>> =
        data.map { list -> list.filter { it.profileId == profileId }.sortedByDescending { it.updatedAt.orEmpty() } }

    override suspend fun getByProfileId(profileId: String): List<ProfileLearningProgressEntity> =
        data.value.filter { it.profileId == profileId }

    override suspend fun upsert(entity: ProfileLearningProgressEntity) {
        data.value = data.value.filter { it.id != entity.id } + entity
    }

    override suspend fun deleteByRoute(profileId: String, routeId: String) {
        data.value = data.value.filterNot { it.profileId == profileId && it.routeId == routeId }
    }
}

class FakeProfileStateDao : ProfileStateDao {
    private val data = MutableStateFlow<Map<String, LocalProfileStateEntity>>(emptyMap())

    override fun observeByProfileId(profileId: String): Flow<LocalProfileStateEntity?> =
        data.map { it[profileId] }

    override suspend fun getByProfileId(profileId: String): LocalProfileStateEntity? =
        data.value[profileId]

    override suspend fun upsert(entity: LocalProfileStateEntity) {
        data.value = data.value + (entity.profileId to entity)
    }

    override suspend fun updateSyncStatus(
        profileId: String,
        lastSyncAt: Long?,
        lastSyncError: String?,
    ) {
        val current = data.value[profileId]
        data.value = if (current != null) {
            data.value + (profileId to current.copy(lastSyncAt = lastSyncAt, lastSyncError = lastSyncError))
        } else {
            data.value + (profileId to LocalProfileStateEntity(
                profileId = profileId,
                lastSyncAt = lastSyncAt,
                lastSyncError = lastSyncError,
            ))
        }
    }
}

class FakePendingProfileOperationDao : PendingProfileOperationDao {
    private val data = MutableStateFlow<List<PendingProfileOperationEntity>>(emptyList())

    override fun observeAll(): Flow<List<PendingProfileOperationEntity>> = data

    override suspend fun getAll(): List<PendingProfileOperationEntity> = data.value.sortedBy { it.createdAt }

    override suspend fun replace(entity: PendingProfileOperationEntity) {
        // 与真实 DAO 的 REPLACE 语义一致：按 deduplicationKey 保留最后意图。
        data.value = data.value.filter { it.deduplicationKey != entity.deduplicationKey } + entity
    }

    override suspend fun delete(operationId: String) {
        data.value = data.value.filterNot { it.operationId == operationId }
    }

    override suspend fun incrementAttempt(operationId: String, lastError: String?) {
        data.value = data.value.map {
            if (it.operationId == operationId) {
                it.copy(attemptCount = it.attemptCount + 1, lastError = lastError)
            } else {
                it
            }
        }
    }

    override suspend fun getByDedupKey(deduplicationKey: String): PendingProfileOperationEntity? =
        data.value.firstOrNull { it.deduplicationKey == deduplicationKey }
}
