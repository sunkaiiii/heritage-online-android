package com.duckylife.heritage.modern.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.duckylife.heritage.modern.core.database.entity.InheritorRemoteKeyEntity

@Dao
interface InheritorRemoteKeyDao {
    @Query("SELECT * FROM inheritor_remote_keys WHERE queryKey = :queryKey")
    suspend fun remoteKey(queryKey: String): InheritorRemoteKeyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(remoteKey: InheritorRemoteKeyEntity)

    @Query("DELETE FROM inheritor_remote_keys WHERE queryKey = :queryKey")
    suspend fun clearByQuery(queryKey: String)
}
