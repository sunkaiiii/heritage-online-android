package com.duckylife.heritage.modern.di

import com.duckylife.heritage.modern.core.profile.DefaultJourneyRepository
import com.duckylife.heritage.modern.core.profile.JourneyRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class JourneyModule {

    @Binds
    @Singleton
    abstract fun bindJourneyRepository(
        impl: DefaultJourneyRepository,
    ): JourneyRepository
}
