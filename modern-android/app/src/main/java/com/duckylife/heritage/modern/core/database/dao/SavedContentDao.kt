package com.duckylife.heritage.modern.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.duckylife.heritage.modern.core.database.entity.SavedContentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedContentDao {
    @Query("SELECT * FROM saved_content WHERE contentKey = :contentKey")
    suspend fun queryByKey(contentKey: String): SavedContentEntity?

    @Query("SELECT * FROM saved_content WHERE contentKey = :contentKey")
    fun observeByKey(contentKey: String): Flow<SavedContentEntity?>

    @Query("SELECT * FROM saved_content WHERE isFavorite = 1 ORDER BY favoritedAt DESC")
    fun observeFavorites(): Flow<List<SavedContentEntity>>

    @Query("SELECT * FROM saved_content ORDER BY lastViewedAt DESC LIMIT 100")
    fun observeRecentlyViewed(): Flow<List<SavedContentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: SavedContentEntity)

    @Query("UPDATE saved_content SET isFavorite = 0, favoritedAt = NULL WHERE contentKey = :contentKey")
    suspend fun removeFavorite(contentKey: String)

    @Query(
        """
        DELETE FROM saved_content WHERE contentKey NOT IN (
            SELECT contentKey FROM saved_content
            ORDER BY lastViewedAt DESC
            LIMIT :keepCount
        )
        """,
    )
    suspend fun trimToKeep(keepCount: Int)
}
