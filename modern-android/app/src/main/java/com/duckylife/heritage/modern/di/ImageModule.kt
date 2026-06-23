package com.duckylife.heritage.modern.di

import android.content.Context
import coil3.ImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import com.duckylife.heritage.modern.core.network.HeritageApiConfig
import com.duckylife.heritage.modern.core.network.trustAllCertificatesManager
import com.duckylife.heritage.modern.core.network.trustAllSslSocketFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ImageModule {
    @Provides
    @Singleton
    fun provideImageLoader(
        @ApplicationContext context: Context,
        config: HeritageApiConfig,
    ): ImageLoader =
        ImageLoader.Builder(context)
            .components {
                add(
                    OkHttpNetworkFetcherFactory(
                        callFactory = {
                            createImageOkHttpClient(config)
                        },
                    ),
                )
            }
            .build()

    private fun createImageOkHttpClient(config: HeritageApiConfig): OkHttpClient {
        if (!config.trustSelfSignedCertificates) {
            return OkHttpClient.Builder().build()
        }
        val trustManager = trustAllCertificatesManager()
        val socketFactory = trustAllSslSocketFactory(trustManager)
        return OkHttpClient.Builder()
            .sslSocketFactory(socketFactory, trustManager)
            .hostnameVerifier { _, _ -> true }
            .build()
    }
}
