package com.duckylife.heritage.modern.core.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.duckylife.heritage.modern.core.database.entity.DirectoryItemEntity

@Dao
interface DirectoryItemDao {
    @Query(
        """
        SELECT * FROM directory_items
        WHERE queryKey = :queryKey
        ORDER BY page ASC, positionInPage ASC
        """,
    )
    fun pagingSource(queryKey: String): PagingSource<Int, DirectoryItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<DirectoryItemEntity>)

    @Query("DELETE FROM directory_items WHERE queryKey = :queryKey")
    suspend fun clearByQuery(queryKey: String)
}
