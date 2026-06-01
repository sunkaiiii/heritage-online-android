package com.duckylife.heritage.modern.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.duckylife.heritage.modern.core.database.entity.ReadingPathEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingPathDao {

    @Query("SELECT * FROM reading_path_events ORDER BY createdAt DESC LIMIT :limit")
    fun observeRecent(limit: Int = 50): Flow<List<ReadingPathEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(event: ReadingPathEventEntity)

    @Query("DELETE FROM reading_path_events")
    suspend fun deleteAll()
}
