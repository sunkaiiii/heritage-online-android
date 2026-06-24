package com.duckylife.heritage.modern.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.duckylife.heritage.modern.core.database.entity.PendingProfileOperationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PendingProfileOperationDao {

    @Query("SELECT * FROM pending_profile_operations ORDER BY createdAt ASC")
    fun observeAll(): Flow<List<PendingProfileOperationEntity>>

    @Query("SELECT * FROM pending_profile_operations ORDER BY createdAt ASC")
    suspend fun getAll(): List<PendingProfileOperationEntity>

    @Upsert
    suspend fun upsert(entity: PendingProfileOperationEntity)

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
