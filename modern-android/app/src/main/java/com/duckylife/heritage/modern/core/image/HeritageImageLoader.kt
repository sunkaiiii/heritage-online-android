package com.duckylife.heritage.modern.core.image

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import coil3.ImageLoader
import coil3.compose.LocalPlatformContext
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import okhttp3.OkHttpClient
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

@Composable
fun rememberHeritageImageLoader(): ImageLoader {
    val context = LocalPlatformContext.current
    return remember(context) {
        ImageLoader.Builder(context)
            .components {
                add(
                    OkHttpNetworkFetcherFactory(
                        callFactory = {
                            createLocalDevelopmentImageOkHttpClient()
                        },
                    ),
                )
            }
            .build()
    }
}

private fun createLocalDevelopmentImageOkHttpClient(): OkHttpClient {
    val trustManager = trustAllCertificatesManager()
    val socketFactory = trustAllSslSocketFactory(trustManager)
    return OkHttpClient.Builder()
        .sslSocketFactory(socketFactory, trustManager)
        .hostnameVerifier { _, _ -> true }
        .build()
}

private fun trustAllCertificatesManager(): X509TrustManager =
    object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) = Unit

        override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) = Unit

        override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
    }

private fun trustAllSslSocketFactory(trustManager: X509TrustManager): SSLSocketFactory {
    val sslContext = SSLContext.getInstance("TLS")
    sslContext.init(null, arrayOf(trustManager), SecureRandom())
    return sslContext.socketFactory
}
