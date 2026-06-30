package com.duckylife.heritage.modern.core.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.duckylife.heritage.modern.core.database.entity.ArticleEntity

@Dao
interface ArticleDao {
    @Query(
        """
        SELECT * FROM articles
        WHERE queryKey = :queryKey
        ORDER BY page ASC, positionInPage ASC
        """,
    )
    fun pagingSource(queryKey: String): PagingSource<Int, ArticleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(articles: List<ArticleEntity>)

    @Query("DELETE FROM articles WHERE queryKey = :queryKey")
    suspend fun clearByQuery(queryKey: String)
}
