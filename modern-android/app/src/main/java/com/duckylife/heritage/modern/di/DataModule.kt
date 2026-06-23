package com.duckylife.heritage.modern.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.duckylife.heritage.modern.core.data.DefaultHeritageRepository
import com.duckylife.heritage.modern.core.data.HeritageRepository
import com.duckylife.heritage.modern.core.profile.DataStoreLocalProfileRepository
import com.duckylife.heritage.modern.core.profile.LocalProfileRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.profileDataStore by preferencesDataStore(name = "heritage_profile")

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    @Singleton
    abstract fun bindHeritageRepository(repository: DefaultHeritageRepository): HeritageRepository

    @Binds
    @Singleton
    abstract fun bindLocalProfileRepository(
        repository: DataStoreLocalProfileRepository,
    ): LocalProfileRepository

    companion object {
        @Provides
        @Singleton
        fun provideProfileDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
            context.profileDataStore
    }
}
