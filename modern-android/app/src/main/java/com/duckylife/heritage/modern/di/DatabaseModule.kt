package com.duckylife.heritage.modern.di

import android.content.Context
import androidx.room.Room
import com.duckylife.heritage.modern.core.database.HeritageDatabase
import com.duckylife.heritage.modern.core.database.HeritageMigrations
import com.duckylife.heritage.modern.core.saved.RoomSavedContentRepository
import com.duckylife.heritage.modern.core.saved.SavedContentRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideHeritageDatabase(
        @ApplicationContext context: Context,
    ): HeritageDatabase =
        Room.databaseBuilder(
            context,
            HeritageDatabase::class.java,
            "heritage-modern.db",
        )
            .addMigrations(*HeritageMigrations.ALL)
            .build()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class SavedContentModule {
    @Binds
    @Singleton
    abstract fun bindSavedContentRepository(
        impl: RoomSavedContentRepository,
    ): SavedContentRepository
}
