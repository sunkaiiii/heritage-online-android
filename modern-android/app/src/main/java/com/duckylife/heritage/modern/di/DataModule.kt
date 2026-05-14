package com.duckylife.heritage.modern.di

import com.duckylife.heritage.modern.core.data.DefaultHeritageRepository
import com.duckylife.heritage.modern.core.data.HeritageRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
    @Binds
    @Singleton
    fun bindHeritageRepository(repository: DefaultHeritageRepository): HeritageRepository
}
