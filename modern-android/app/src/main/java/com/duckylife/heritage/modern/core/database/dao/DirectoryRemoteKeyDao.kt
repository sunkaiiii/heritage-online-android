package com.duckylife.heritage.modern.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.duckylife.heritage.modern.core.database.entity.DirectoryRemoteKeyEntity

@Dao
interface DirectoryRemoteKeyDao {
    @Query("SELECT * FROM directory_remote_keys WHERE queryKey = :queryKey")
    suspend fun remoteKey(queryKey: String): DirectoryRemoteKeyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(remoteKey: DirectoryRemoteKeyEntity)

    @Query("DELETE FROM directory_remote_keys WHERE queryKey = :queryKey")
    suspend fun clearByQuery(queryKey: String)
}
