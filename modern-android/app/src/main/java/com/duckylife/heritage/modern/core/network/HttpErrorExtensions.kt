package com.duckylife.heritage.modern.core.network

import io.ktor.client.plugins.ResponseException
import io.ktor.http.HttpStatusCode

fun Throwable.isServiceUnavailable(): Boolean =
    this is ResponseException && response.status == HttpStatusCode.ServiceUnavailable

fun Throwable.isBadRequest(): Boolean =
    this is ResponseException && response.status == HttpStatusCode.BadRequest
