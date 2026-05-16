package com.duckylife.heritage.modern.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.duckylife.heritage.modern.core.database.dao.ArticleDao
import com.duckylife.heritage.modern.core.database.dao.ArticleDetailDao
import com.duckylife.heritage.modern.core.database.dao.ArticleRemoteKeyDao
import com.duckylife.heritage.modern.core.database.entity.ArticleDetailEntity
import com.duckylife.heritage.modern.core.database.entity.ArticleEntity
import com.duckylife.heritage.modern.core.database.entity.ArticleRemoteKeyEntity

@Database(
    entities = [
        ArticleEntity::class,
        ArticleDetailEntity::class,
        ArticleRemoteKeyEntity::class,
    ],
    version = 2,
    exportSchema = false,
)
abstract class HeritageDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
    abstract fun articleDetailDao(): ArticleDetailDao
    abstract fun articleRemoteKeyDao(): ArticleRemoteKeyDao
}
