package com.duckylife.heritage.modern.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.duckylife.heritage.modern.core.database.converter.ProfileTypeConverters
import com.duckylife.heritage.modern.core.database.dao.ArticleDao
import com.duckylife.heritage.modern.core.database.dao.ArticleDetailDao
import com.duckylife.heritage.modern.core.database.dao.ArticleRemoteKeyDao
import com.duckylife.heritage.modern.core.database.dao.DirectoryItemDao
import com.duckylife.heritage.modern.core.database.dao.DirectoryDetailDao
import com.duckylife.heritage.modern.core.database.dao.DirectoryRemoteKeyDao
import com.duckylife.heritage.modern.core.database.dao.HomeBannerDao
import com.duckylife.heritage.modern.core.database.dao.InheritorDao
import com.duckylife.heritage.modern.core.database.dao.InheritorDetailDao
import com.duckylife.heritage.modern.core.database.dao.InheritorRemoteKeyDao
import com.duckylife.heritage.modern.core.database.dao.PendingProfileOperationDao
import com.duckylife.heritage.modern.core.database.dao.ProfileFavoriteDao
import com.duckylife.heritage.modern.core.database.dao.ProfileHistoryDao
import com.duckylife.heritage.modern.core.database.dao.ProfileLearningProgressDao
import com.duckylife.heritage.modern.core.database.dao.ProfileStateDao
import com.duckylife.heritage.modern.core.database.dao.ReadingPathDao
import com.duckylife.heritage.modern.core.database.dao.SavedContentDao
import com.duckylife.heritage.modern.core.database.entity.ArticleDetailEntity
import com.duckylife.heritage.modern.core.database.entity.ArticleEntity
import com.duckylife.heritage.modern.core.database.entity.ArticleRemoteKeyEntity
import com.duckylife.heritage.modern.core.database.entity.DirectoryItemEntity
import com.duckylife.heritage.modern.core.database.entity.DirectoryDetailEntity
import com.duckylife.heritage.modern.core.database.entity.DirectoryRemoteKeyEntity
import com.duckylife.heritage.modern.core.database.entity.HomeBannerEntity
import com.duckylife.heritage.modern.core.database.entity.InheritorDetailEntity
import com.duckylife.heritage.modern.core.database.entity.InheritorEntity
import com.duckylife.heritage.modern.core.database.entity.InheritorRemoteKeyEntity
import com.duckylife.heritage.modern.core.database.entity.LocalProfileStateEntity
import com.duckylife.heritage.modern.core.database.entity.PendingProfileOperationEntity
import com.duckylife.heritage.modern.core.database.entity.ProfileFavoriteEntity
import com.duckylife.heritage.modern.core.database.entity.ProfileHistoryEntity
import com.duckylife.heritage.modern.core.database.entity.ProfileLearningProgressEntity
import com.duckylife.heritage.modern.core.database.entity.ReadingPathEventEntity
import com.duckylife.heritage.modern.core.database.entity.SavedContentEntity

@Database(
    entities = [
        ArticleEntity::class,
        ArticleDetailEntity::class,
        ArticleRemoteKeyEntity::class,
        DirectoryItemEntity::class,
        DirectoryDetailEntity::class,
        DirectoryRemoteKeyEntity::class,
        HomeBannerEntity::class,
        InheritorEntity::class,
        InheritorDetailEntity::class,
        InheritorRemoteKeyEntity::class,
        ReadingPathEventEntity::class,
        SavedContentEntity::class,
        LocalProfileStateEntity::class,
        ProfileFavoriteEntity::class,
        ProfileHistoryEntity::class,
        ProfileLearningProgressEntity::class,
        PendingProfileOperationEntity::class,
    ],
    version = 11,
    exportSchema = true,
)
@TypeConverters(ProfileTypeConverters::class)
abstract class HeritageDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
    abstract fun articleDetailDao(): ArticleDetailDao
    abstract fun articleRemoteKeyDao(): ArticleRemoteKeyDao
    abstract fun directoryItemDao(): DirectoryItemDao
    abstract fun directoryDetailDao(): DirectoryDetailDao
    abstract fun directoryRemoteKeyDao(): DirectoryRemoteKeyDao
    abstract fun homeBannerDao(): HomeBannerDao
    abstract fun inheritorDao(): InheritorDao
    abstract fun inheritorDetailDao(): InheritorDetailDao
    abstract fun inheritorRemoteKeyDao(): InheritorRemoteKeyDao
    abstract fun readingPathDao(): ReadingPathDao
    abstract fun savedContentDao(): SavedContentDao
    abstract fun profileStateDao(): ProfileStateDao
    abstract fun profileFavoriteDao(): ProfileFavoriteDao
    abstract fun profileHistoryDao(): ProfileHistoryDao
    abstract fun profileLearningProgressDao(): ProfileLearningProgressDao
    abstract fun pendingProfileOperationDao(): PendingProfileOperationDao
}
