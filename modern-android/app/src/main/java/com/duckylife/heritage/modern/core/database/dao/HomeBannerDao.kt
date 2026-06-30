package com.duckylife.heritage.modern.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.duckylife.heritage.modern.core.database.entity.HomeBannerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HomeBannerDao {
    @Query("SELECT * FROM home_banners ORDER BY sortOrder ASC")
    fun observeAll(): Flow<List<HomeBannerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(banners: List<HomeBannerEntity>)

    @Query("DELETE FROM home_banners")
    suspend fun deleteAll()
}
