package com.duckylife.heritage.modern.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.duckylife.heritage.modern.core.database.entity.PendingProfileOperationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PendingProfileOperationDao {

    @Query("SELECT * FROM pending_profile_operations ORDER BY createdAt ASC")
    fun observeAll(): Flow<List<PendingProfileOperationEntity>>

    @Query("SELECT * FROM pending_profile_operations ORDER BY createdAt ASC")
    suspend fun getAll(): List<PendingProfileOperationEntity>

    /**
     * 按 [PendingProfileOperationEntity.deduplicationKey] 替换最后意图。
     *
     * `deduplicationKey` 是唯一索引而不是主键；使用 `@Upsert` 只会处理
     * `operationId` 冲突，第二次写入同一 key 会在 SQLite 中抛唯一约束异常。
     * `REPLACE` 同时覆盖主键和唯一索引冲突，符合“只保留最后一次操作”的队列语义。
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun replace(entity: PendingProfileOperationEntity)

    @Query("DELETE FROM pending_profile_operations WHERE operationId = :operationId")
    suspend fun delete(operationId: String)

    @Query(
        """
        UPDATE pending_profile_operations
        SET attemptCount = attemptCount + 1, lastError = :lastError
        WHERE operationId = :operationId
        """,
    )
    suspend fun incrementAttempt(operationId: String, lastError: String?)

    @Query(
        """
        SELECT * FROM pending_profile_operations
        WHERE deduplicationKey = :deduplicationKey
        LIMIT 1
        """,
    )
    suspend fun getByDedupKey(deduplicationKey: String): PendingProfileOperationEntity?
}
