package com.duckylife.heritage.modern.di

import com.duckylife.heritage.modern.core.profile.DefaultLocalUserSyncRepository
import com.duckylife.heritage.modern.core.profile.LocalUserSyncRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileSyncModule {

    @Binds
    @Singleton
    abstract fun bindLocalUserSyncRepository(
        impl: DefaultLocalUserSyncRepository,
    ): LocalUserSyncRepository
}
