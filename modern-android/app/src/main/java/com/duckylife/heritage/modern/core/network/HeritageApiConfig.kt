package com.duckylife.heritage.modern.core.network

const val LocalDevBaseUrl = "https://10.0.2.2:5078"

data class HeritageApiConfig(
    val baseUrl: String = LocalDevBaseUrl,
    val trustSelfSignedCertificates: Boolean = true,
)
