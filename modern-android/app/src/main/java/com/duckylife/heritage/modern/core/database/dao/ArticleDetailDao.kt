package com.duckylife.heritage.modern.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.duckylife.heritage.modern.core.database.entity.ArticleDetailEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDetailDao {
    @Query("SELECT * FROM article_details WHERE id = :id")
    fun observeById(id: String): Flow<ArticleDetailEntity?>

    @Query(
        """
        SELECT * FROM article_details
        WHERE sourceId = :sourceId AND category = :category
        LIMIT 1
        """,
    )
    fun observeBySourceId(sourceId: String, category: String): Flow<ArticleDetailEntity?>

    @Query(
        """
        SELECT * FROM article_details
        WHERE sourceUrl = :sourceUrl AND category = :category
        LIMIT 1
        """,
    )
    fun observeBySourceUrl(sourceUrl: String, category: String): Flow<ArticleDetailEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(articleDetail: ArticleDetailEntity)
}
