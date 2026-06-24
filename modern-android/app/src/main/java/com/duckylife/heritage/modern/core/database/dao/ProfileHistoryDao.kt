package com.duckylife.heritage.modern.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.duckylife.heritage.modern.core.database.entity.ProfileHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileHistoryDao {

    @Query("SELECT * FROM profile_history WHERE profileId = :profileId ORDER BY viewedAt DESC")
    fun observeByProfileId(profileId: String): Flow<List<ProfileHistoryEntity>>

    @Query("SELECT * FROM profile_history WHERE profileId = :profileId ORDER BY viewedAt DESC")
    suspend fun getByProfileId(profileId: String): List<ProfileHistoryEntity>

    @Upsert
    suspend fun upsert(entity: ProfileHistoryEntity)

    @Query(
        """
        DELETE FROM profile_history
        WHERE profileId = :profileId AND targetType = :targetType AND targetId = :targetId
        """,
    )
    suspend fun deleteByTarget(profileId: String, targetType: String, targetId: String)

    @Query("DELETE FROM profile_history WHERE profileId = :profileId")
    suspend fun deleteAllByProfile(profileId: String)

    @Query(
        """
        SELECT * FROM profile_history
        WHERE profileId = :profileId AND targetType = :targetType AND targetId = :targetId
        LIMIT 1
        """,
    )
    suspend fun getByTarget(
        profileId: String,
        targetType: String,
        targetId: String,
    ): ProfileHistoryEntity?
}
