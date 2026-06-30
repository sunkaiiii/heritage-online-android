package com.duckylife.heritage.modern.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.duckylife.heritage.modern.core.database.entity.LocalProfileStateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileStateDao {

    @Query("SELECT * FROM local_profile_state WHERE profileId = :profileId")
    fun observeByProfileId(profileId: String): Flow<LocalProfileStateEntity?>

    @Query("SELECT * FROM local_profile_state WHERE profileId = :profileId")
    suspend fun getByProfileId(profileId: String): LocalProfileStateEntity?

    @Upsert
    suspend fun upsert(entity: LocalProfileStateEntity)

    @Query(
        """
        UPDATE local_profile_state
        SET lastSyncAt = :lastSyncAt, lastSyncError = :lastSyncError
        WHERE profileId = :profileId
        """,
    )
    suspend fun updateSyncStatus(
        profileId: String,
        lastSyncAt: Long?,
        lastSyncError: String?,
    )
}
