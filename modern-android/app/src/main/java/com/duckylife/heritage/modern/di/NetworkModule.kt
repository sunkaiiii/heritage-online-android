package com.duckylife.heritage.modern.di

import com.duckylife.heritage.modern.BuildConfig
import com.duckylife.heritage.modern.core.network.HeritageApiClient
import com.duckylife.heritage.modern.core.network.HeritageApiConfig
import com.duckylife.heritage.modern.core.network.KtorHeritageApiClient
import com.duckylife.heritage.modern.core.network.createHeritageHttpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideHeritageApiConfig(): HeritageApiConfig =
        HeritageApiConfig(
            baseUrl = BuildConfig.HERITAGE_API_BASE_URL,
            trustSelfSignedCertificates = BuildConfig.HERITAGE_TRUST_SELF_SIGNED_CERTS,
        )

    @Provides
    @Singleton
    fun provideHttpClient(config: HeritageApiConfig): HttpClient =
        createHeritageHttpClient(config)

    @Provides
    @Singleton
    fun provideHeritageApiClient(
        httpClient: HttpClient,
        config: HeritageApiConfig,
    ): HeritageApiClient =
        KtorHeritageApiClient(httpClient = httpClient, baseUrl = config.baseUrl)
}
