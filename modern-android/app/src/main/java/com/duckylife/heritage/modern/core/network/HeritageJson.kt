package com.duckylife.heritage.modern.core.network

import kotlinx.serialization.json.Json

val HeritageJson = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
    isLenient = true
}
