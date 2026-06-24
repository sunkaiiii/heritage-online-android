package com.duckylife.heritage.modern.di

import android.content.Context
import com.duckylife.heritage.modern.core.profile.DefaultLocalUserSyncRepository
import com.duckylife.heritage.modern.core.profile.LocalUserSyncRepository
import com.duckylife.heritage.modern.core.profile.ProfileSyncScheduler
import com.duckylife.heritage.modern.core.profile.WorkManagerProfileSyncScheduler
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

    companion object {
        @Provides
        @Singleton
        fun provideProfileSyncScheduler(
            @ApplicationContext context: Context,
        ): ProfileSyncScheduler = WorkManagerProfileSyncScheduler(context)
    }
}
