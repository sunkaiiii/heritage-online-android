package com.duckylife.heritage.modern.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.duckylife.heritage.modern.core.database.entity.DirectoryDetailEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DirectoryDetailDao {
    @Query("SELECT * FROM directory_details WHERE id = :id")
    fun observeById(id: String): Flow<DirectoryDetailEntity?>

    @Query(
        """
        SELECT * FROM directory_details
        WHERE sourceId = :sourceId AND kind = :kind
        LIMIT 1
        """,
    )
    fun observeBySourceId(sourceId: String, kind: String): Flow<DirectoryDetailEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(directoryDetail: DirectoryDetailEntity)
}
