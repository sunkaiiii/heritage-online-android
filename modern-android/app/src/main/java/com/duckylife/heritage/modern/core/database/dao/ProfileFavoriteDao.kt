package com.duckylife.heritage.modern.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.duckylife.heritage.modern.core.database.entity.ProfileFavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileFavoriteDao {

    @Query("SELECT * FROM profile_favorites WHERE profileId = :profileId ORDER BY updatedAt DESC")
    fun observeByProfileId(profileId: String): Flow<List<ProfileFavoriteEntity>>

    @Query("SELECT * FROM profile_favorites WHERE profileId = :profileId ORDER BY updatedAt DESC")
    suspend fun getByProfileId(profileId: String): List<ProfileFavoriteEntity>

    @Upsert
    suspend fun upsert(entity: ProfileFavoriteEntity)

    @Query(
        """
        DELETE FROM profile_favorites
        WHERE profileId = :profileId AND targetType = :targetType AND targetId = :targetId
        """,
    )
    suspend fun deleteByTarget(profileId: String, targetType: String, targetId: String)

    @Query("DELETE FROM profile_favorites WHERE profileId = :profileId")
    suspend fun deleteAllByProfile(profileId: String)

    @Query(
        """
        SELECT * FROM profile_favorites
        WHERE profileId = :profileId AND targetType = :targetType AND targetId = :targetId
        LIMIT 1
        """,
    )
    suspend fun getByTarget(
        profileId: String,
        targetType: String,
        targetId: String,
    ): ProfileFavoriteEntity?
}
