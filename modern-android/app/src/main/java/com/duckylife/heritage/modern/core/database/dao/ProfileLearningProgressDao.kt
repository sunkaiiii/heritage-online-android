package com.duckylife.heritage.modern.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.duckylife.heritage.modern.core.database.entity.ProfileLearningProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileLearningProgressDao {

    @Query("SELECT * FROM profile_learning_progress WHERE profileId = :profileId ORDER BY updatedAt DESC")
    fun observeByProfileId(profileId: String): Flow<List<ProfileLearningProgressEntity>>

    @Query("SELECT * FROM profile_learning_progress WHERE profileId = :profileId ORDER BY updatedAt DESC")
    suspend fun getByProfileId(profileId: String): List<ProfileLearningProgressEntity>

    @Upsert
    suspend fun upsert(entity: ProfileLearningProgressEntity)

    @Query(
        """
        DELETE FROM profile_learning_progress
        WHERE profileId = :profileId AND routeId = :routeId
        """,
    )
    suspend fun deleteByRoute(profileId: String, routeId: String)
}
