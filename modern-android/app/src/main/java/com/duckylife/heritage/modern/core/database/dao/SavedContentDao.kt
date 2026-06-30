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

    // 只返回真正浏览过的记录（lastViewedAt > 0），收藏不会自动进入最近浏览。
    @Query("SELECT * FROM saved_content WHERE lastViewedAt > 0 ORDER BY lastViewedAt DESC LIMIT 100")
    fun observeRecentlyViewed(): Flow<List<SavedContentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: SavedContentEntity)

    @Query("UPDATE saved_content SET isFavorite = 0, favoritedAt = NULL WHERE contentKey = :contentKey")
    suspend fun removeFavorite(contentKey: String)

    // 移除最近浏览：只清空时间戳，保留收藏状态。
    @Query("UPDATE saved_content SET lastViewedAt = 0 WHERE contentKey = :contentKey")
    suspend fun removeRecent(contentKey: String)

    // 清空所有浏览记录，不影响收藏。
    @Query("UPDATE saved_content SET lastViewedAt = 0 WHERE isFavorite = 0")
    suspend fun clearRecent()

    // 只裁剪非收藏且无浏览记录的旧数据。
    @Query(
        """
        DELETE FROM saved_content
        WHERE isFavorite = 0 AND lastViewedAt = 0
        AND contentKey NOT IN (
            SELECT contentKey FROM saved_content
            WHERE lastViewedAt > 0
            ORDER BY lastViewedAt DESC
            LIMIT :keepCount
        )
        """,
    )
    suspend fun trimToKeep(keepCount: Int)
}
