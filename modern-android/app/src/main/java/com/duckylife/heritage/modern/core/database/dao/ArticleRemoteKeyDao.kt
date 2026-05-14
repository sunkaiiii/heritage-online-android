package com.duckylife.heritage.modern.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.duckylife.heritage.modern.core.database.entity.ArticleRemoteKeyEntity

@Dao
interface ArticleRemoteKeyDao {
    @Query("SELECT * FROM article_remote_keys WHERE queryKey = :queryKey")
    suspend fun remoteKey(queryKey: String): ArticleRemoteKeyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(remoteKey: ArticleRemoteKeyEntity)

    @Query("DELETE FROM article_remote_keys WHERE queryKey = :queryKey")
    suspend fun clearByQuery(queryKey: String)
}
