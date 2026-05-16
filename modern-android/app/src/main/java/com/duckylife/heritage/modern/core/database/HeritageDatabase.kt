package com.duckylife.heritage.modern.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.duckylife.heritage.modern.core.database.dao.ArticleDao
import com.duckylife.heritage.modern.core.database.dao.ArticleDetailDao
import com.duckylife.heritage.modern.core.database.dao.ArticleRemoteKeyDao
import com.duckylife.heritage.modern.core.database.dao.DirectoryItemDao
import com.duckylife.heritage.modern.core.database.dao.DirectoryDetailDao
import com.duckylife.heritage.modern.core.database.dao.DirectoryRemoteKeyDao
import com.duckylife.heritage.modern.core.database.entity.ArticleDetailEntity
import com.duckylife.heritage.modern.core.database.entity.ArticleEntity
import com.duckylife.heritage.modern.core.database.entity.ArticleRemoteKeyEntity
import com.duckylife.heritage.modern.core.database.entity.DirectoryItemEntity
import com.duckylife.heritage.modern.core.database.entity.DirectoryDetailEntity
import com.duckylife.heritage.modern.core.database.entity.DirectoryRemoteKeyEntity

@Database(
    entities = [
        ArticleEntity::class,
        ArticleDetailEntity::class,
        ArticleRemoteKeyEntity::class,
        DirectoryItemEntity::class,
        DirectoryDetailEntity::class,
        DirectoryRemoteKeyEntity::class,
    ],
    version = 4,
    exportSchema = false,
)
abstract class HeritageDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
    abstract fun articleDetailDao(): ArticleDetailDao
    abstract fun articleRemoteKeyDao(): ArticleRemoteKeyDao
    abstract fun directoryItemDao(): DirectoryItemDao
    abstract fun directoryDetailDao(): DirectoryDetailDao
    abstract fun directoryRemoteKeyDao(): DirectoryRemoteKeyDao
}
