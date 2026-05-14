package com.duckylife.heritage.modern.di

import com.duckylife.heritage.modern.core.network.HeritageApiClient
import com.duckylife.heritage.modern.core.network.HeritageApiConfig
import com.duckylife.heritage.modern.core.network.HeritageJson
import com.duckylife.heritage.modern.core.network.KtorHeritageApiClient
import com.duckylife.heritage.modern.core.network.LocalDevBaseUrl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.accept
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideHeritageApiConfig(): HeritageApiConfig =
        HeritageApiConfig(
            baseUrl = LocalDevBaseUrl,
            trustSelfSignedCertificates = true,
        )

    @Provides
    @Singleton
    fun provideHttpClient(config: HeritageApiConfig): HttpClient =
        HttpClient(OkHttp) {
            expectSuccess = true

            engine {
                if (config.trustSelfSignedCertificates) {
                    val trustManager = trustAllCertificatesManager()
                    val socketFactory = trustAllSslSocketFactory(trustManager)
                    config {
                        sslSocketFactory(socketFactory, trustManager)
                        hostnameVerifier { _, _ -> true }
                    }
                }
            }

            install(ContentNegotiation) {
                json(HeritageJson)
            }

            defaultRequest {
                accept(ContentType.Application.Json)
            }
        }

    @Provides
    @Singleton
    fun provideHeritageApiClient(
        httpClient: HttpClient,
        config: HeritageApiConfig,
    ): HeritageApiClient =
        KtorHeritageApiClient(
            httpClient = httpClient,
            baseUrl = config.baseUrl,
        )
}

fun trustAllCertificatesManager(): X509TrustManager =
    object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) = Unit

        override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) = Unit

        override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
    }

fun trustAllSslSocketFactory(trustManager: X509TrustManager): SSLSocketFactory {
    val sslContext = SSLContext.getInstance("TLS")
    sslContext.init(null, arrayOf(trustManager), SecureRandom())
    return sslContext.socketFactory
}
