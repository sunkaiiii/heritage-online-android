package com.duckylife.heritage.modern.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.duckylife.heritage.modern.core.database.entity.InheritorDetailEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InheritorDetailDao {
    @Query("SELECT * FROM inheritor_details WHERE id = :id")
    fun observeById(id: String): Flow<InheritorDetailEntity?>

    @Query(
        """
        SELECT * FROM inheritor_details
        WHERE sourceId = :sourceId
        LIMIT 1
        """,
    )
    fun observeBySourceId(sourceId: String): Flow<InheritorDetailEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(inheritorDetail: InheritorDetailEntity)
}
