package com.duckylife.heritage.modern.di

import android.content.Context
import androidx.room.Room
import com.duckylife.heritage.modern.core.database.HeritageDatabase
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
        ).build()
}
